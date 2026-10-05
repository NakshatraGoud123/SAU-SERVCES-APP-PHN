package com.nisr.sauservices.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.Transaction
import com.nisr.sauservices.data.repository.SupabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.app.Activity
import android.util.Log
import com.nisr.sauservices.data.repository.RazorpayRepository
import com.nisr.sauservices.ui.payment.PaymentEvent
import com.nisr.sauservices.ui.payment.PaymentResultBus
import org.json.JSONObject
import com.razorpay.Checkout

class WalletViewModel(
    private val repository: SupabaseRepository = SupabaseRepository(),
    private val razorpayRepository: RazorpayRepository = RazorpayRepository()
) : ViewModel() {

    private val _balance = MutableStateFlow(0.0)
    val balance: StateFlow<Double> = _balance.asStateFlow()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    var isLoading by mutableStateOf(false)
        private set

    private var pendingTopupAmount: Double = 0.0

    init {
        fetchWalletData()
        observePaymentResults()
    }

    private fun observePaymentResults() {
        viewModelScope.launch {
            PaymentResultBus.events.collect { event ->
                if (event is PaymentEvent.Success) {
                    if (pendingTopupAmount > 0) {
                        verifyTopupOnServer(event)
                    } else {
                        fetchWalletData()
                    }
                } else if (event is PaymentEvent.Error) {
                    isLoading = false
                    pendingTopupAmount = 0.0
                }
            }
        }
    }

    fun startRazorpayTopup(activity: Activity, amount: Double, email: String, contact: String) {
        isLoading = true
        pendingTopupAmount = amount
        val checkout = Checkout()
        checkout.setKeyID("rzp_test_TYEz9RMOAJ2nmV")

        try {
            val options = JSONObject()
            options.put("name", "SAU SOLUTIONS")
            options.put("description", "Wallet Top-up")
            options.put("currency", "INR")
            options.put("amount", (amount * 100).toInt())
            options.put("prefill.email", email)
            options.put("prefill.contact", contact)
            options.put("theme.color", "#96A68F") // Luxe Sage

            checkout.open(activity, options)
        } catch (e: Exception) {
            isLoading = false
        }
    }

    fun fetchWalletData() {
        viewModelScope.launch {
            isLoading = true
            val balanceResult = repository.getWalletBalance()
            val txResult = repository.getTransactions()

            if (balanceResult.isSuccess) {
                _balance.value = balanceResult.getOrDefault(0.0)
            }
            if (txResult.isSuccess) {
                _transactions.value = txResult.getOrDefault(emptyList())
            }
            isLoading = false
        }
    }

    fun processWalletPayment(
        amount: Double, 
        description: String, 
        referenceId: String? = null,
        onResult: (Result<Unit>) -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            // Secure: Use server-side RPC for atomic transaction
            val finalReferenceId = referenceId ?: "DEBIT_${System.currentTimeMillis()}"
            val result = repository.processWalletTransaction(amount, "debit", finalReferenceId, description)
            
            if (result.isSuccess) {
                fetchWalletData()
                onResult(Result.success(Unit))
            } else {
                onResult(Result.failure(result.exceptionOrNull() ?: Exception("Payment failed")))
            }
            isLoading = false
        }
    }

    private fun verifyTopupOnServer(event: PaymentEvent.Success) {
        val paymentId = event.paymentId ?: ""
        val orderId = event.data?.orderId ?: ""
        val signature = event.data?.signature ?: ""
        
        viewModelScope.launch {
            isLoading = true
            val result = razorpayRepository.verifyPaymentOnServer(
                paymentId = paymentId,
                orderId = orderId,
                signature = signature,
                amount = pendingTopupAmount,
                description = "Wallet Top-up"
            )
            
            if (result.isSuccess) {
                pendingTopupAmount = 0.0
                fetchWalletData()
            } else {
                // Handle error
                Log.e("WALLET_VM", "Server verification failed: ${result.exceptionOrNull()?.message}")
            }
            isLoading = false
        }
    }
}
