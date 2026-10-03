-- 1. Create wallet_balances table
CREATE TABLE IF NOT EXISTS public.wallet_balances (
    user_id UUID REFERENCES auth.users(id) NOT NULL PRIMARY KEY,
    balance NUMERIC NOT NULL DEFAULT 0 CHECK (balance >= 0),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 2. Create wallet_transactions table
CREATE TABLE IF NOT EXISTS public.wallet_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES auth.users(id) NOT NULL,
    amount NUMERIC NOT NULL CHECK (amount > 0),
    type TEXT NOT NULL CHECK (type IN ('credit', 'debit')),
    reference_id TEXT NOT NULL UNIQUE,
    balance_after NUMERIC NOT NULL CHECK (balance_after >= 0),
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 3. Enable Row Level Security (RLS)
ALTER TABLE public.wallet_balances ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.wallet_transactions ENABLE ROW LEVEL SECURITY;

-- 4. Set up Policies
-- Customers can only view their own balance
CREATE POLICY "Users can view their own wallet balance"
ON public.wallet_balances FOR SELECT
USING (auth.uid() = user_id);

-- Customers can only view their own transaction history
CREATE POLICY "Users can view their own wallet transactions"
ON public.wallet_transactions FOR SELECT
USING (auth.uid() = user_id);

-- 5. Secure RPC to process transactions
-- This function handles both credits (top-ups) and debits (order payments)
-- It uses auth.uid() to ensure the operation is performed for the logged-in user
CREATE OR REPLACE FUNCTION public.process_wallet_transaction(
    p_amount NUMERIC,
    p_type TEXT,
    p_reference_id TEXT,
    p_description TEXT
)
RETURNS JSON
LANGUAGE plpgsql
SECURITY DEFINER -- Runs with service_role privileges to bypass RLS restrictions on write
AS $$
DECLARE
    v_user_id UUID;
    v_current_balance NUMERIC;
    v_new_balance NUMERIC;
BEGIN
    -- A. Identification: Determine user from JWT (Session)
    v_user_id := auth.uid();
    IF v_user_id IS NULL THEN
        RETURN json_build_object('is_success', false, 'message', 'Authentication required');
    END IF;

    -- B. Validation: Basic sanity checks
    IF p_amount <= 0 THEN
        RETURN json_build_object('is_success', false, 'message', 'Amount must be positive');
    END IF;

    IF p_type NOT IN ('credit', 'debit') THEN
        RETURN json_build_object('is_success', false, 'message', 'Invalid transaction type');
    END IF;

    IF p_reference_id IS NULL OR p_reference_id = '' THEN
        RETURN json_build_object('is_success', false, 'message', 'Reference ID is required');
    END IF;

    -- C. Idempotency: Check if reference_id was already used
    -- This prevents double-crediting or double-debiting on retries
    IF EXISTS (SELECT 1 FROM public.wallet_transactions WHERE reference_id = p_reference_id) THEN
        SELECT balance INTO v_current_balance FROM public.wallet_balances WHERE user_id = v_user_id;
        RETURN json_build_object(
            'is_success', true,
            'message', 'Transaction already processed',
            'balance', v_current_balance
        );
    END IF;

    -- D. Locking & Preparation: Lock the wallet row to prevent race conditions
    -- Ensure a wallet exists for the user
    INSERT INTO public.wallet_balances (user_id, balance)
    VALUES (v_user_id, 0)
    ON CONFLICT (user_id) DO NOTHING;

    -- SELECT FOR UPDATE locks the row until the function ends (end of transaction)
    SELECT balance INTO v_current_balance
    FROM public.wallet_balances
    WHERE user_id = v_user_id
    FOR UPDATE;

    -- E. Logic: Calculate new balance and verify funds for debits
    IF p_type = 'credit' THEN
        v_new_balance := v_current_balance + p_amount;
    ELSE
        IF v_current_balance < p_amount THEN
            RETURN json_build_object('is_success', false, 'message', 'Insufficient wallet balance');
        END IF;
        v_new_balance := v_current_balance - p_amount;
    END IF;

    -- F. Execution: Update the balance table
    UPDATE public.wallet_balances
    SET balance = v_new_balance,
        updated_at = now()
    WHERE user_id = v_user_id;

    -- G. Ledger: Record the transaction for history/audit
    INSERT INTO public.wallet_transactions (
        user_id,
        amount,
        type,
        reference_id,
        balance_after,
        description
    )
    VALUES (
        v_user_id,
        p_amount,
        p_type,
        p_reference_id,
        v_new_balance,
        p_description
    );

    -- H. Return success with the updated balance
    RETURN json_build_object(
        'is_success', true,
        'message', 'Transaction successful',
        'balance', v_new_balance
    );

EXCEPTION
    WHEN OTHERS THEN
        -- Safely catch database errors (like unique constraint violations)
        RETURN json_build_object('is_success', false, 'message', SQLERRM);
END;
$$;
