import { serve } from "https://deno.land/std@0.168.0/http/server.ts"
import { createClient } from 'https://esm.sh/@supabase/supabase-js@2'

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

    const { amount } = await req.json()

    // Strict validation: must be a finite positive integer number of paise within limit (<= ₹100,000)
    if (amount === undefined || amount === null || typeof amount !== 'number' || !Number.isInteger(amount) || !Number.isFinite(amount) || amount <= 0 || amount > 10000000) {
      throw new Error('Invalid amount specified (must be a positive integer in paise)')
    }

    if (!RAZORPAY_KEY_ID || !RAZORPAY_KEY_SECRET || !SUPABASE_URL || !SUPABASE_SERVICE_ROLE_KEY) {
      throw new Error('Server environment variables not fully configured')
    }

    // 1. Authenticate caller
    const authHeader = req.headers.get('Authorization')
    if (!authHeader) {
      throw new Error('Missing authorization header')
    }

    const supabaseAdmin = createClient(SUPABASE_URL, SUPABASE_SERVICE_ROLE_KEY)
    const { data: { user }, error: userError } = await supabaseAdmin.auth.getUser(authHeader.replace('Bearer ', ''))

    if (userError || !user) {
      throw new Error('User authentication failed')
    }

    // 2. Create Razorpay Order
    const response = await fetch('https://api.razorpay.com/v1/orders', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Basic ' + btoa(`${RAZORPAY_KEY_ID}:${RAZORPAY_KEY_SECRET}`)
      },
      body: JSON.stringify({
        amount: amount, // paise
        currency: 'INR',
        receipt: `receipt_${Date.now()}`
      })
    })

    const orderData = await response.json()
    if (!response.ok || !orderData.id) {
      throw new Error(orderData.error?.description || 'Failed to create Razorpay order')
    }

    // 3. Save expected order server-side in pending_wallet_orders (amount converted from paise to rupees)
    const { error: insertError } = await supabaseAdmin
      .from('pending_wallet_orders')
      .insert({
        order_id: orderData.id,
        user_id: user.id,
        amount: amount / 100.0,
        currency: 'INR',
        status: 'pending'
      })

    if (insertError) {
      throw new Error(`Failed to save pending order: ${insertError.message}`)
    }

    return new Response(JSON.stringify(orderData), {
      headers: { ...corsHeaders, 'Content-Type': 'application/json' },
      status: 200,
    })
  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), {
      headers: { ...corsHeaders, 'Content-Type': 'application/json' },
      status: 400,
    })
  }
})
