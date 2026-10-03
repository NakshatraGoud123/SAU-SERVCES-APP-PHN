package com.nisr.sauservices.data.repository

import com.nisr.sauservices.data.api.SupabaseClient
import com.nisr.sauservices.data.model.RazorpayPaymentModel
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.postgrest
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import java.util.UUID

@Serializable
private data class CreateOrderResponse(
    val id: String? = null,
    @SerialName("order_id") val orderId: String? = null,
    val error: JsonElement? = null
)

@Serializable
private data class VerifyPaymentResponse(
    @SerialName("is_success") val isSuccess: Boolean = false,
    val message: String? = null,
    val error: JsonElement? = null
)

class RazorpayRepository {
    private val postgrest = SupabaseClient.client.postgrest
    private val functions = SupabaseClient.client.functions

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    suspend fun createRazorpayOrder(amount: Double): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = functions.invoke(
                "create-razorpay-order",
                body = buildJsonObject {
                    put("amount", (amount * 100).toInt()) // Amount in paise
                    put("currency", "INR")
                }
            )
            
            val responseString = response.bodyAsText()
            if (responseString.isBlank()) {
                return@withContext Result.failure(Exception("Empty response received from order creation server"))
            }

            val parsedResponse = json.decodeFromString<CreateOrderResponse>(responseString)
            val resolvedOrderId = parsedResponse.id ?: parsedResponse.orderId

            if (!resolvedOrderId.isNullOrBlank()) {
                Result.success(resolvedOrderId)
            } else {
                val errorDetails = extractErrorMessage(parsedResponse.error) ?: "Order creation failed: $responseString"
                Result.failure(Exception(errorDetails))
            }
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
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
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
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyPaymentOnServer(
        paymentId: String,
        orderId: String,
        signature: String,
        amount: Double,
        description: String = "Wallet Top-up"
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = functions.invoke(
                "verify-razorpay-payment",
                body = buildJsonObject {
                    put("razorpay_payment_id", paymentId)
                    put("razorpay_order_id", orderId)
                    put("razorpay_signature", signature)
                    put("amount", (amount * 100).toInt()) // paise
                    put("description", description)
                }
            )
            
            val responseBody = response.bodyAsText()
            if (responseBody.isBlank()) {
                return@withContext Result.failure(Exception("Empty response received from verification server"))
            }

            val parsedResponse = json.decodeFromString<VerifyPaymentResponse>(responseBody)
            if (parsedResponse.isSuccess) {
                Result.success(Unit)
            } else {
                val errorMsg = extractErrorMessage(parsedResponse.error) ?: "Verification failed"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun extractErrorMessage(errorElement: JsonElement?): String? {
        if (errorElement == null) return null
        return try {
            when (errorElement) {
                is JsonPrimitive -> errorElement.content
                is JsonObject -> {
                    errorElement["message"]?.jsonPrimitive?.content
                        ?: errorElement["description"]?.jsonPrimitive?.content
                        ?: errorElement["error"]?.jsonPrimitive?.content
                        ?: errorElement.toString()
                }
                else -> errorElement.toString()
            }
        } catch (_: Exception) {
            null
        }
    }
}
