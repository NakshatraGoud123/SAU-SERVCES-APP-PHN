import { serve } from "https://deno.land/std@0.168.0/http/server.ts"
import { createClient } from 'https://esm.sh/@supabase/supabase-js@2'
import { HmacSha256 } from "https://deno.land/std@0.160.0/hash/sha256.ts";

const RAZORPAY_KEY_SECRET = Deno.env.get('RAZORPAY_KEY_SECRET')
const SUPABASE_URL = Deno.env.get('SUPABASE_URL')
const SUPABASE_SERVICE_ROLE_KEY = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY')

serve(async (req) => {
  // CORS Headers
  const corsHeaders = {
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
  }

  if (req.method === 'OPTIONS') {
    return new Response('ok', { headers: corsHeaders })
  }

  try {
    const { razorpay_payment_id, razorpay_order_id, razorpay_signature, amount, description } = await req.json()

    if (!RAZORPAY_KEY_SECRET) {
      throw new Error('Razorpay secret not configured')
    }

    // 1. Verify Signature
    // Signature format: hmac_sha256(order_id + "|" + payment_id, secret)
    const hmac = new HmacSha256(RAZORPAY_KEY_SECRET);
    hmac.update(`${razorpay_order_id}|${razorpay_payment_id}`);
    const generated_signature = hmac.toString();

    if (generated_signature !== razorpay_signature) {
        // Fallback or detailed error for debugging (remove in production)
        throw new Error('Payment verification failed: Invalid signature')
    }

    // 2. Initialize Supabase Client (Service Role for admin actions)
    const supabase = createClient(SUPABASE_URL!, SUPABASE_SERVICE_ROLE_KEY!)

    // 3. Verify user identity from JWT
    const authHeader = req.headers.get('Authorization')!
    const { data: { user }, error: userError } = await supabase.auth.getUser(authHeader.replace('Bearer ', ''))

    if (userError || !user) {
      throw new Error('User authentication failed')
    }

    // 4. Atomic Credit via RPC
    // We call the RPC as the user to leverage auth.uid() inside the function
    const userClient = createClient(SUPABASE_URL!, Deno.env.get('SUPABASE_ANON_KEY')!, {
        global: { headers: { Authorization: authHeader } }
    })

    const { data, error: rpcError } = await userClient.rpc('process_wallet_transaction', {
      p_amount: amount / 100, // Razorpay amount is in paise
      p_type: 'credit',
      p_reference_id: razorpay_payment_id,
      p_description: description || 'Wallet Top-up'
    })

    if (rpcError) {
        throw new Error(`Wallet update failed: ${rpcError.message}`)
    }

    return new Response(JSON.stringify({
        is_success: true,
        message: 'Payment verified and wallet credited',
        data: data
    }), {
      headers: { ...corsHeaders, 'Content-Type': 'application/json' },
      status: 200,
    })

  } catch (error) {
    return new Response(JSON.stringify({ is_success: false, error: error.message }), {
      headers: { ...corsHeaders, 'Content-Type': 'application/json' },
      status: 400,
    })
  }
})
