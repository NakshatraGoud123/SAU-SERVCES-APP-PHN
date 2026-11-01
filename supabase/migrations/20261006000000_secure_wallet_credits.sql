-- =====================================================================
-- SECURE WALLET CREDITS MIGRATION (Phase 6K - Final Hardened & Constraint-Aware)
-- =====================================================================

-- 1. Create pending_wallet_orders table to store trusted server-side order expectations
CREATE TABLE IF NOT EXISTS public.pending_wallet_orders (
    order_id TEXT PRIMARY KEY,
    user_id UUID REFERENCES auth.users(id) NOT NULL,
    amount NUMERIC NOT NULL CHECK (amount > 0),
    currency TEXT NOT NULL DEFAULT 'INR',
    payment_id TEXT UNIQUE,
    status TEXT NOT NULL DEFAULT 'pending' CHECK (status IN ('pending', 'processing', 'completed', 'failed')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Enable RLS on pending_wallet_orders
ALTER TABLE public.pending_wallet_orders ENABLE ROW LEVEL SECURITY;

-- Revoke direct table access from clients; only service_role (Edge Functions) can access
REVOKE ALL ON public.pending_wallet_orders FROM PUBLIC;
REVOKE ALL ON public.pending_wallet_orders FROM anon;
REVOKE ALL ON public.pending_wallet_orders FROM authenticated;
GRANT ALL ON public.pending_wallet_orders TO service_role;

-- 2. Add nullable order_id foreign key to wallet_transactions safely (non-destructive)
ALTER TABLE public.wallet_transactions
ADD COLUMN IF NOT EXISTS order_id TEXT REFERENCES public.pending_wallet_orders(order_id);

-- 3. Secure Server-Side Credit RPC Function with Strict Constraint-Aware Exception Handling
CREATE OR REPLACE FUNCTION public.credit_wallet_balance(
    p_user_id UUID,
    p_amount NUMERIC,
    p_reference_id TEXT,
    p_description TEXT,
    p_order_id TEXT
)
RETURNS JSON
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, pg_temp
AS $$
DECLARE
    v_current_balance NUMERIC;
    v_new_balance NUMERIC;
    v_order_user_id UUID;
    v_order_amount NUMERIC;
    v_order_status TEXT;
    v_order_payment_id TEXT;
    v_existing_tx_user UUID;
    v_existing_tx_amount NUMERIC;
    v_existing_tx_type TEXT;
    v_existing_tx_order_id TEXT;
    v_ledger_match BOOLEAN := FALSE;
    v_constraint_name TEXT;
BEGIN
    -- A. Strict Parameter Validation (Reject NULL, zero, or negative amounts)
    IF p_user_id IS NULL OR p_amount IS NULL OR p_amount <= 0 OR p_reference_id IS NULL OR p_reference_id = '' OR p_order_id IS NULL OR p_order_id = '' THEN
        RETURN json_build_object('is_success', false, 'message', 'Invalid or null credit parameters');
    END IF;

    -- B. Lock and inspect pending order row
    SELECT user_id, amount, status, payment_id
    INTO v_order_user_id, v_order_amount, v_order_status, v_order_payment_id
    FROM public.pending_wallet_orders
    WHERE order_id = p_order_id AND user_id = p_user_id
    FOR UPDATE;

    IF NOT FOUND THEN
        RETURN json_build_object('is_success', false, 'message', 'Pending order not found');
    END IF;

    -- C. Validate User & Amount Binding against trusted pending order
    IF v_order_user_id <> p_user_id OR v_order_amount <> p_amount THEN
        RETURN json_build_object('is_success', false, 'message', 'Order user or amount mismatch');
    END IF;

    -- D. Check Ledger for existing transaction with this payment reference (Strict binding: require exact order_id match)
    SELECT user_id, amount, type, order_id
    INTO v_existing_tx_user, v_existing_tx_amount, v_existing_tx_type, v_existing_tx_order_id
    FROM public.wallet_transactions
    WHERE reference_id = p_reference_id;

    IF FOUND THEN
        -- Verify that the existing ledger entry matches exact user, amount, order_id, and is a credit
        IF v_existing_tx_user = p_user_id AND v_existing_tx_amount = p_amount AND v_existing_tx_type = 'credit' AND v_existing_tx_order_id = p_order_id THEN
            v_ledger_match := TRUE;
        ELSE
            -- Conflict: payment reference already used by different user, amount, or order
            RETURN json_build_object('is_success', false, 'message', 'Payment reference ID conflict or order mismatch');
        END IF;
    END IF;

    -- E. Check Order Status
    IF v_order_status = 'completed' THEN
        -- If order is marked completed, we MUST verify a confirmed matching ledger transaction exists bound to this exact order
        IF v_ledger_match AND v_order_payment_id = p_reference_id THEN
            SELECT balance INTO v_current_balance FROM public.wallet_balances WHERE user_id = p_user_id;
            RETURN json_build_object(
                'is_success', true,
                'message', 'Order already completed with confirmed ledger credit',
                'balance', COALESCE(v_current_balance, 0)
            );
        ELSE
            -- Order marked completed without a valid confirmed ledger entry -> Fail safely and flag reconciliation
            RETURN json_build_object('is_success', false, 'message', 'Order marked completed but lacks verified ledger confirmation');
        END IF;
    ELSIF v_order_status <> 'pending' AND v_order_status <> 'processing' THEN
        RETURN json_build_object('is_success', false, 'message', 'Order is in an invalid state');
    END IF;

    -- If a valid ledger match already exists but order was still pending, mark order completed and return success
    IF v_ledger_match THEN
        UPDATE public.pending_wallet_orders
        SET status = 'completed', payment_id = p_reference_id
        WHERE order_id = p_order_id;

        SELECT balance INTO v_current_balance FROM public.wallet_balances WHERE user_id = p_user_id;
        RETURN json_build_object(
            'is_success', true,
            'message', 'Transaction already processed (Idempotent Ledger Match)',
            'balance', COALESCE(v_current_balance, 0)
        );
    END IF;

    -- F. Ensure wallet balance row exists & lock row for atomicity
    INSERT INTO public.wallet_balances (user_id, balance)
    VALUES (p_user_id, 0)
    ON CONFLICT (user_id) DO NOTHING;

    SELECT balance INTO v_current_balance
    FROM public.wallet_balances
    WHERE user_id = p_user_id
    FOR UPDATE;

    v_current_balance := COALESCE(v_current_balance, 0);
    v_new_balance := v_current_balance + p_amount;

    -- G. Atomic Balance Update & Ledger Insertion
    UPDATE public.wallet_balances
    SET balance = v_new_balance,
        updated_at = now()
    WHERE user_id = p_user_id;

    INSERT INTO public.wallet_transactions (
        user_id,
        amount,
        type,
        reference_id,
        balance_after,
        description,
        order_id
    )
    VALUES (
        p_user_id,
        p_amount,
        'credit',
        p_reference_id,
        v_new_balance,
        p_description,
        p_order_id
    );

    -- H. Mark Order Completed and bind payment_id uniquely
    UPDATE public.pending_wallet_orders
    SET status = 'completed',
        payment_id = p_reference_id
    WHERE order_id = p_order_id;

    RETURN json_build_object(
        'is_success', true,
        'message', 'Wallet credited successfully',
        'balance', v_new_balance
    );

EXCEPTION
    WHEN unique_violation THEN
        GET STACKED DIAGNOSTICS v_constraint_name = CONSTRAINT_NAME;

        -- Handle specifically only wallet_transactions reference_id unique violation
        IF v_constraint_name = 'wallet_transactions_reference_id_key' THEN
            SELECT user_id, amount, type, order_id INTO v_existing_tx_user, v_existing_tx_amount, v_existing_tx_type, v_existing_tx_order_id
            FROM public.wallet_transactions
            WHERE reference_id = p_reference_id;

            IF FOUND AND v_existing_tx_user = p_user_id AND v_existing_tx_amount = p_amount AND v_existing_tx_type = 'credit' AND v_existing_tx_order_id = p_order_id THEN
                UPDATE public.pending_wallet_orders SET status = 'completed', payment_id = p_reference_id WHERE order_id = p_order_id AND user_id = p_user_id;
                SELECT balance INTO v_current_balance FROM public.wallet_balances WHERE user_id = p_user_id;
                RETURN json_build_object(
                    'is_success', true,
                    'message', 'Transaction already processed (Constraint Idempotent Match)',
                    'balance', COALESCE(v_current_balance, 0)
                );
            ELSE
                RETURN json_build_object('is_success', false, 'message', 'Payment reference ID conflict during concurrent execution');
            END IF;
        ELSE
            -- Fail closed for all other unique constraints (e.g. pending_wallet_orders payment_id collision)
            RETURN json_build_object('is_success', false, 'message', 'Payment ID already claimed or unique constraint conflict');
        END IF;
    WHEN OTHERS THEN
        -- Sanitize internal database errors without exposing raw SQL stack traces
        RETURN json_build_object('is_success', false, 'message', 'An internal database error occurred while processing the transaction');
END;
$$;

-- Strict Security Grants for Credit Function (service_role only)
REVOKE EXECUTE ON FUNCTION public.credit_wallet_balance(UUID, NUMERIC, TEXT, TEXT, TEXT) FROM PUBLIC;
REVOKE EXECUTE ON FUNCTION public.credit_wallet_balance(UUID, NUMERIC, TEXT, TEXT, TEXT) FROM anon;
REVOKE EXECUTE ON FUNCTION public.credit_wallet_balance(UUID, NUMERIC, TEXT, TEXT, TEXT) FROM authenticated;
GRANT EXECUTE ON FUNCTION public.credit_wallet_balance(UUID, NUMERIC, TEXT, TEXT, TEXT) TO service_role;
