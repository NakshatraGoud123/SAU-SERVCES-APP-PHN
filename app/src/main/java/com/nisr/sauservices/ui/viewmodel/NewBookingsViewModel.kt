package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.data.repository.RealtimeDatabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class NewBookingsViewModel : ViewModel() {
    private val repository = RealtimeDatabaseRepository()

    private val _bookingStatus = MutableStateFlow<Result<String>?>(null)
    val bookingStatus = _bookingStatus.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun confirmBooking(
        name: String,
        category: String,
        subcategory: String,
        date: String,
        time: String,
        price: String,
        quantity: Int,
        paymentMethod: String,
        address: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            // Parse price string to double
            val priceDouble = price.replace("₹", "").split("–").first().trim().filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 0.0
            
            val booking = OrderModel(
                serviceName = name,
                category = category,
                subcategory = subcategory,
                scheduleDate = date,
                scheduleTime = time,
                totalAmount = priceDouble * quantity,
                paymentMethod = paymentMethod,
                deliveryAddress = address,
                status = "success"
            )
            
            val result = repository.placeOrderDirectly(booking)
            _bookingStatus.value = result
            _isLoading.value = false
        }
    }
    
    fun resetStatus() {
        _bookingStatus.value = null
        _isLoading.value = false
    }
}
