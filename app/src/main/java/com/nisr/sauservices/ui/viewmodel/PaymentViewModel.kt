package com.nisr.sauservices.ui.viewmodel

import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.repository.CashPaymentRepository
import com.nisr.sauservices.data.repository.RazorpayRepository
import com.nisr.sauservices.ui.payment.PaymentEvent
import com.nisr.sauservices.ui.payment.PaymentResultBus
import com.razorpay.Checkout
import kotlinx.coroutines.launch
import org.json.JSONObject

class PaymentViewModel : ViewModel() {
    private val cashRepository = CashPaymentRepository()
    private val razorpayRepository = RazorpayRepository()

    var isLoading by mutableStateOf(false)
        private set

    var paymentError by mutableStateOf<String?>(null)
        private set

    var cashPaymentId by mutableStateOf<String?>(null)
        private set

    var isOtpVerified by mutableStateOf(false)
        private set

    private var currentBookingData: BookingData? = null

    data class BookingData(
        val bookingId: String,
        val customerId: String,
        val partnerId: String,
        val amount: Double,
        val onSuccess: () -> Unit
    )

    init {
        observePaymentResults()
    }

    private fun observePaymentResults() {
        viewModelScope.launch {
            PaymentResultBus.events.collect { event ->
                when (event) {
                    is PaymentEvent.Success -> {
                        handlePaymentSuccess(event)
                    }
                    is PaymentEvent.Error -> {
                        isLoading = false
                        paymentError = event.description ?: "Payment Failed"
                    }
                }
            }
        }
    }

    private fun handlePaymentSuccess(event: PaymentEvent.Success) {
        val data = currentBookingData ?: return
        viewModelScope.launch {
            val paymentId = event.paymentId ?: ""
            val razorpayOrderId = event.data?.orderId ?: ""
            val signature = event.data?.signature ?: ""

            val result = razorpayRepository.savePaymentResult(
                bookingId = data.bookingId,
                customerId = data.customerId,
                partnerId = data.partnerId,
                amount = data.amount,
                razorpayPaymentId = paymentId,
                razorpayOrderId = razorpayOrderId,
                razorpaySignature = signature
            )

            result.onSuccess {
                isLoading = false
                data.onSuccess()
            }.onFailure {
                isLoading = false
                paymentError = it.message ?: "Failed to save payment details"
            }
        }
    }

    fun startRazorpayPayment(
        activity: Activity,
        bookingId: String,
        customerId: String,
        partnerId: String,
        amount: Double,
        customerEmail: String,
        customerContact: String,
        onSuccess: () -> Unit
    ) {
        isLoading = true
        paymentError = null
        currentBookingData = BookingData(bookingId, customerId, partnerId, amount, onSuccess)

        viewModelScope.launch {
            val orderResult = razorpayRepository.createRazorpayOrder(amount)
            
            orderResult.onSuccess { orderId ->
                val checkout = Checkout()
                // TODO: Replace with your actual Razorpay Key ID
                checkout.setKeyID("rzp_test_YOUR_KEY_HERE") 

                try {
                    val options = JSONObject()
                    options.put("name", "SAU SERVICES")
                    options.put("description", "Booking Payment")
                    options.put("theme.color", "#00E5FF")
                    options.put("currency", "INR")
                    options.put("order_id", orderId) // VERY IMPORTANT: Use the order_id from backend
                    options.put("amount", (amount * 100).toInt()) // Amount in paise
                    options.put("prefill.email", customerEmail)
                    options.put("prefill.contact", customerContact)

                    checkout.open(activity, options)
                } catch (e: Exception) {
                    isLoading = false
                    paymentError = "Error starting Razorpay: ${e.message}"
                }
            }.onFailure {
                isLoading = false
                paymentError = "Failed to create order on server: ${it.message}"
            }
        }
    }

    fun createCashPayment(bookingId: String, customerId: String, partnerId: String, amount: Double, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            paymentError = null
            val result = cashRepository.createCashPayment(bookingId, customerId, partnerId, amount)
            result.onSuccess { id ->
                cashPaymentId = id
                onSuccess(id)
            }.onFailure {
                paymentError = it.message ?: "Failed to create cash payment"
            }
            isLoading = false
        }
    }

    fun markCashCollected(paymentId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            paymentError = null
            val result = cashRepository.partnerCollectedCash(paymentId)
            result.onSuccess {
                onSuccess()
            }.onFailure {
                paymentError = it.message ?: "Failed to mark cash as collected"
            }
            isLoading = false
        }
    }

    fun verifyOtp(paymentId: String, otp: String, bookingId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            paymentError = null
            val result = cashRepository.verifyCustomerOtp(paymentId, otp, bookingId)
            result.onSuccess { verified ->
                if (verified) {
                    isOtpVerified = true
                    onSuccess()
                } else {
                    paymentError = "Invalid OTP"
                }
            }.onFailure {
                paymentError = it.message ?: "Failed to verify OTP"
            }
            isLoading = false
        }
    }
    
    fun clearError() {
        paymentError = null
    }
}
