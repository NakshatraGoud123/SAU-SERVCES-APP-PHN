package com.nisr.sauservices.data.repository

import com.nisr.sauservices.data.api.SupabaseClient
import com.nisr.sauservices.data.model.RazorpayPaymentModel
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.util.UUID

class RazorpayRepository {
    private val postgrest = SupabaseClient.client.postgrest
    private val functions = SupabaseClient.client.functions

    suspend fun createRazorpayOrder(amount: Double): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = functions.invoke(
                "create-razorpay-order",
                body = buildJsonObject {
                    put("amount", (amount * 100).toInt()) // Amount in paise
                    put("currency", "INR")
                }
            )
            
            // FunctionResponse delegates to HttpResponse, we can use bodyAsText() from Ktor
            // or decodeAs<T>() if we have the right imports.
            // Let's try the Ktor way as a safe bet
            val responseString = response.toString() 
            // If toString doesn't work, we need the correct body access.
            // In Supabase 3.0.x it's often response.body<T>() or response.decodeAs<T>()
            
            val orderId = if (responseString.contains("order_id")) {
                responseString.substringAfter("order_id\":\"").substringBefore("\"")
            } else {
                // Fallback: search for order_id in a more flexible way
                val idMatch = Regex("\"order_id\"\\s*:\\s*\"([^\"]+)\"").find(responseString)
                idMatch?.groupValues?.get(1) ?: throw Exception("Order creation failed: $responseString")
            }
            
            Result.success(orderId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun savePaymentResult(
        bookingId: String,
        customerId: String,
        partnerId: String,
        amount: Double,
        razorpayPaymentId: String,
        razorpayOrderId: String,
        razorpaySignature: String
    ): Result<Unit> = try {
        val payment = RazorpayPaymentModel(
            id = UUID.randomUUID().toString(),
            bookingId = bookingId,
            customerId = customerId,
            partnerId = partnerId,
            amount = amount,
            razorpayPaymentId = razorpayPaymentId,
            razorpayOrderId = razorpayOrderId,
            razorpaySignature = razorpaySignature,
            status = "paid"
        )

        withContext(Dispatchers.IO) {
            postgrest["razorpay_payments"].insert(payment)
            
            // Update booking status
            postgrest["bookings"].update(
                update = {
                    set("payment_method", "Digital")
                    set("payment_status", "paid")
                    set("status", "completed")
                }
            ) {
                filter {
                    eq("id", bookingId)
                }
            }
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
