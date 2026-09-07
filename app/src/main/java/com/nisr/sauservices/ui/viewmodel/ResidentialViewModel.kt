package com.nisr.sauservices.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.nisr.sauservices.data.model.BookingDetails
import com.nisr.sauservices.data.model.ResidentialCartItem
import com.nisr.sauservices.data.model.ResidentialServiceItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ResidentialViewModel : ViewModel() {
    private val _cartItems = mutableStateListOf<ResidentialCartItem>()
    val cartItems: List<ResidentialCartItem> get() = _cartItems

    private val _bookingDetails = MutableStateFlow(BookingDetails())
    val bookingDetails = _bookingDetails.asStateFlow()

    var selectedPartnerId = mutableStateOf<String?>(null)
        private set

    var selectedServiceId = mutableStateOf<String?>(null)
        private set

    fun selectPartner(partnerId: String) {
        selectedPartnerId.value = partnerId
    }

    fun selectService(serviceId: String) {
        selectedServiceId.value = serviceId
    }

    fun addToCart(service: ResidentialServiceItem) {
        val index = _cartItems.indexOfFirst { it.service.id == service.id }
        if (index != -1) {
            _cartItems[index] = _cartItems[index].copy(quantity = _cartItems[index].quantity + 1)
        } else {
            _cartItems.add(ResidentialCartItem(service, 1))
        }
    }

    fun removeFromCart(serviceId: String) {
        _cartItems.removeAll { it.service.id == serviceId }
    }

    fun updateQty(serviceId: String, increment: Boolean) {
        val index = _cartItems.indexOfFirst { it.service.id == serviceId }
        if (index != -1) {
            val item = _cartItems[index]
            if (increment) {
                _cartItems[index] = item.copy(quantity = item.quantity + 1)
            } else {
                if (item.quantity > 1) {
                    _cartItems[index] = item.copy(quantity = item.quantity - 1)
                } else {
                    _cartItems.removeAt(index)
                }
            }
        }
    }

    fun getItemQuantity(serviceId: String): Int {
        return _cartItems.find { it.service.id == serviceId }?.quantity ?: 0
    }

    fun calculateTotal(): Double {
        return _cartItems.sumOf { it.service.price * it.quantity }
    }

    fun setDate(date: String) {
        _bookingDetails.value = _bookingDetails.value.copy(date = date)
    }

    fun setTimeSlot(slot: String) {
        _bookingDetails.value = _bookingDetails.value.copy(timeSlot = slot)
    }

    fun setAddress(address: String) {
        _bookingDetails.value = _bookingDetails.value.copy(address = address)
    }

    fun setPhone(phone: String) {
        _bookingDetails.value = _bookingDetails.value.copy(phone = phone)
    }

    fun setPaymentMethod(method: String) {
        _bookingDetails.value = _bookingDetails.value.copy(paymentMethod = method)
    }

    fun clearCart() {
        _cartItems.clear()
        _bookingDetails.value = BookingDetails()
    }
}
