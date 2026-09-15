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

class WalletViewModel(
    private val repository: SupabaseRepository = SupabaseRepository()
) : ViewModel() {

    private val _balance = MutableStateFlow(0.0)
    val balance: StateFlow<Double> = _balance.asStateFlow()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    var isLoading by mutableStateOf(false)
        private set

    init {
        fetchWalletData()
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

    fun processWalletPayment(amount: Double, description: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            val result = repository.updateWalletBalance(amount, "debit")
            if (result.isSuccess) {
                repository.logTransaction(amount, "debit", description)
                fetchWalletData()
                onResult(Result.success(Unit))
            } else {
                onResult(Result.failure(result.exceptionOrNull() ?: Exception("Payment failed")))
            }
            isLoading = false
        }
    }

    fun topUpWallet(amount: Double, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            val result = repository.updateWalletBalance(amount, "credit")
            if (result.isSuccess) {
                repository.logTransaction(amount, "credit", "Wallet Top-up")
                fetchWalletData()
                onResult(Result.success(Unit))
            } else {
                onResult(Result.failure(result.exceptionOrNull() ?: Exception("Top-up failed")))
            }
            isLoading = false
        }
    }
}
