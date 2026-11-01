import { serve } from "https://deno.land/std@0.168.0/http/server.ts"
import { createClient } from 'https://esm.sh/@supabase/supabase-js@2'
import { HmacSha256 } from "https://deno.land/std@0.160.0/hash/sha256.ts";

const RAZORPAY_KEY_ID = Deno.env.get('RAZORPAY_KEY_ID')
const RAZORPAY_KEY_SECRET = Deno.env.get('RAZORPAY_KEY_SECRET')
const SUPABASE_URL = Deno.env.get('SUPABASE_URL')
const SUPABASE_SERVICE_ROLE_KEY = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY')

serve(async (req) => {
  const corsHeaders = {
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
  }

  if (req.method === 'OPTIONS') {
    return new Response('ok', { headers: corsHeaders })
  }

  try {
    if (req.method !== 'POST') {
      throw new Error('Method not allowed')
    }

    const { razorpay_payment_id, razorpay_order_id, razorpay_signature, description } = await req.json()

    if (!razorpay_payment_id || !razorpay_order_id || !razorpay_signature) {
      throw new Error('Missing required payment verification parameters')
    }

    if (!RAZORPAY_KEY_ID || !RAZORPAY_KEY_SECRET || !SUPABASE_URL || !SUPABASE_SERVICE_ROLE_KEY) {
      throw new Error('Server environment variables not fully configured')
    }

    // 1. Authenticate caller from JWT
    const authHeader = req.headers.get('Authorization')
    if (!authHeader) {
      throw new Error('Missing authorization header')
    }

    const supabaseAdmin = createClient(SUPABASE_URL, SUPABASE_SERVICE_ROLE_KEY)
    const { data: { user }, error: userError } = await supabaseAdmin.auth.getUser(authHeader.replace('Bearer ', ''))

    if (userError || !user) {
      throw new Error('User authentication failed')
    }

    // 2. Fetch expected order from trusted server-side table
    const { data: expectedOrder, error: orderError } = await supabaseAdmin
      .from('pending_wallet_orders')
      .select('*')
      .eq('order_id', razorpay_order_id)
      .single()

    if (orderError || !expectedOrder) {
      throw new Error('Associated pending wallet order not found')
    }

    if (expectedOrder.user_id !== user.id) {
      throw new Error('Unauthorized: Order user mismatch')
    }

    if (expectedOrder.status === 'completed') {
      if (!expectedOrder.payment_id || expectedOrder.payment_id !== razorpay_payment_id) {
        throw new Error('Submitted payment ID does not match the payment already bound to this completed order')
      }

      // Verify confirmed ledger credit exists
      const { data: ledgerTx, error: ledgerError } = await supabaseAdmin
        .from('wallet_transactions')
        .select('*')
        .eq('reference_id', expectedOrder.payment_id)
        .eq('user_id', user.id)
        .eq('type', 'credit')
        .eq('order_id', razorpay_order_id)
        .single()

      if (ledgerError || !ledgerTx || Number(ledgerTx.amount) !== Number(expectedOrder.amount)) {
        throw new Error('Order marked completed but lacks verified ledger confirmation')
      }

      return new Response(JSON.stringify({
        is_success: true,
        message: 'Payment already verified and credited'
      }), {
        headers: { ...corsHeaders, 'Content-Type': 'application/json' },
        status: 200,
      })
    }

    // 3. Verify HMAC Signature
    const hmac = new HmacSha256(RAZORPAY_KEY_SECRET);
    hmac.update(`${razorpay_order_id}|${razorpay_payment_id}`);
    const generated_signature = hmac.toString();

    if (generated_signature !== razorpay_signature) {
      throw new Error('Payment verification failed: Invalid cryptographic signature')
    }

    // 4. Fetch actual payment details directly from Razorpay API
    const payResponse = await fetch(`https://api.razorpay.com/v1/payments/${razorpay_payment_id}`, {
      method: 'GET',
      headers: {
        'Authorization': 'Basic ' + btoa(`${RAZORPAY_KEY_ID}:${RAZORPAY_KEY_SECRET}`)
      }
    })

    const paymentData = await payResponse.json()
    if (!payResponse.ok || !paymentData) {
      throw new Error('Failed to fetch payment details from Razorpay gateway')
    }

    // 5. Validate payment properties
    if (paymentData.status !== 'captured') {
      throw new Error(`Payment is not captured. Current status: ${paymentData.status}`)
    }

    if (paymentData.currency !== 'INR') {
      throw new Error(`Invalid payment currency: ${paymentData.currency}`)
    }

    if (paymentData.order_id !== razorpay_order_id) {
      throw new Error('Payment order ID does not match expected Razorpay order')
    }

    const expectedAmountInPaise = Math.round(Number(expectedOrder.amount) * 100)
    if (Number(paymentData.amount) !== expectedAmountInPaise) {
      throw new Error('Payment amount mismatch against expected order')
    }

    // 6. Execute atomic credit via secure RPC using service_role
    const { data: rpcData, error: rpcError } = await supabaseAdmin.rpc('credit_wallet_balance', {
      p_user_id: user.id,
      p_amount: expectedOrder.amount,
      p_reference_id: razorpay_payment_id,
      p_description: description || 'Wallet Top-up',
      p_order_id: razorpay_order_id
    })

    if (rpcError) {
      throw new Error(`Database transaction error: ${rpcError.message}`)
    }

    if (!rpcData || rpcData.is_success !== true) {
      throw new Error(`Wallet credit rejected: ${rpcData?.message || 'Unknown database error'}`)
    }

    return new Response(JSON.stringify({
        is_success: true,
        message: 'Payment verified and credited successfully',
        data: rpcData
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
