package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.BookingModel
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.data.model.CartModel
import com.nisr.sauservices.data.repository.SupabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BookingItem(
    val id: String = "",
    val serviceName: String = "",
    val price: String = "",
    val date: String = "",
    val time: String = "",
    val status: String = ""
)

class BookingsViewModel : ViewModel() {
    private val repository = SupabaseRepository()

    private val _bookingResult = MutableStateFlow<Result<String>?>(null)
    val bookingResult = _bookingResult.asStateFlow()

    private val _myBookings = MutableStateFlow<List<BookingModel>>(emptyList())
    val myBookings = _myBookings.asStateFlow()
    
    private val _bookingsFlow = MutableStateFlow<List<BookingItem>>(emptyList())
    val bookingsFlow = _bookingsFlow.asStateFlow()

    init {
        loadUserBookings()
    }

    fun addBooking(item: BookingItem) {
        _bookingsFlow.value = _bookingsFlow.value + item
    }

    fun bookService(
        serviceId: String,
        serviceName: String,
        date: String,
        time: String,
        address: String
    ) {
        viewModelScope.launch {
            val userId = repository.getCurrentUserId() ?: return@launch
            val profile = repository.getUserProfile(userId).getOrNull()

            val booking = BookingModel(
                userId = userId,
                userName = profile?.name ?: "Unknown Customer",
                userPhone = profile?.phone ?: "",
                serviceId = serviceId,
                serviceName = serviceName,
                scheduleDate = date,
                scheduleTime = time,
                address = address,
                status = "pending"
            )
            val result = repository.bookService(booking)
            _bookingResult.value = result
        }
    }

    fun placeUnifiedOrder(
        serviceName: String,
        category: String,
        subcategory: String,
        date: String,
        time: String,
        amount: Double,
        paymentMethod: String,
        address: String,
        items: List<CartModel>
    ) {
        viewModelScope.launch {
            val order = OrderModel(
                userId = repository.getCurrentUserId() ?: "",
                serviceName = serviceName,
                category = category,
                subcategory = subcategory,
                scheduleDate = date,
                scheduleTime = time,
                totalAmount = amount,
                paymentMethod = paymentMethod,
                address = address,
                items = items,
                status = "pending"
            )
            val result = repository.placeOrder(order)
            _bookingResult.value = result
        }
    }

    fun loadUserBookings() {
        val userId = repository.getCurrentUserId() ?: return
        viewModelScope.launch {
            repository.observeMyBookings(userId).collect { list ->
                _myBookings.value = list
                _bookingsFlow.value = list.map {
                    BookingItem(
                        id = it.id,
                        serviceName = it.serviceName,
                        price = "₹0.0",
                        date = it.scheduleDate,
                        time = it.scheduleTime,
                        status = it.status
                    )
                }
            }
        }
    }

    fun resetResult() {
        _bookingResult.value = null
    }
}
