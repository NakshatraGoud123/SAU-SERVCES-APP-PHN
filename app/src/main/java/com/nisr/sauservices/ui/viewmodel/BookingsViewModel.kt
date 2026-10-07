package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.BookingModel
import com.nisr.sauservices.data.model.CartModel
import com.nisr.sauservices.data.repository.SupabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private fun getCurrentIsoTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date())
    }

    fun bookService(
        serviceId: String,
        serviceName: String,
        date: String,
        time: String,
        address: String
    ) {
        if (_isSubmitting.value) return
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                val userId = repository.getCurrentUserId()
                if (userId == null) {
                    _bookingResult.value = Result.failure(Exception("User not authenticated"))
                    return@launch
                }
                val profile = repository.getUserProfile(userId).getOrNull()

                val booking = BookingModel(
                    userId = userId,
                    userName = profile?.name ?: "Unknown Customer",
                    userPhone = profile?.phone ?: "",
                    userAddress = address,
                    serviceId = serviceId,
                    serviceName = serviceName,
                    scheduleDate = date,
                    scheduleTime = time,
                    bookingDate = getCurrentIsoTimestamp(),
                    status = "pending",
                    providerId = null,
                    paymentStatus = "pending"
                )
                val result = repository.bookService(booking)
                _bookingResult.value = result
            } catch (e: Exception) {
                _bookingResult.value = Result.failure(e)
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun placeUnifiedOrder(
        serviceId: String,
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
        if (_isSubmitting.value) return
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                val userId = repository.getCurrentUserId()
                if (userId == null) {
                    _bookingResult.value = Result.failure(Exception("User not authenticated"))
                    return@launch
                }
                val profile = repository.getUserProfile(userId).getOrNull()

                val booking = BookingModel(
                    userId = userId,
                    userName = profile?.name ?: "Customer",
                    userPhone = profile?.phone ?: "",
                    userAddress = address,
                    serviceId = serviceId,
                    serviceName = serviceName,
                    category = category,
                    subcategory = subcategory,
                    scheduleDate = date,
                    scheduleTime = time,
                    timeSlot = time,
                    totalAmount = amount,
                    paymentMethod = paymentMethod,
                    paymentStatus = "pending",
                    bookingDate = getCurrentIsoTimestamp(),
                    status = "pending",
                    providerId = null
                )
                
                val result = repository.bookService(booking)
                _bookingResult.value = result
            } catch (e: Exception) {
                _bookingResult.value = Result.failure(e)
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun loadUserBookings() {
        val userId = repository.getCurrentUserId() ?: return
        viewModelScope.launch {
            repository.observeMyBookings(userId).collect { list ->
                _myBookings.value = list
                _bookingsFlow.value = list.map {
                    BookingItem(
                        id = it.id ?: "",
                        serviceName = it.serviceName ?: "",
                        price = "₹0.0",
                        date = it.scheduleDate ?: "",
                        time = it.scheduleTime ?: "",
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
