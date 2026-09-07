 package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.nisr.sauservices.data.repository.SupabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest

/**
 * Professional UI State for Order Tracking
 */
data class TrackingUiState(
    val orderId: String = "",
    val statusTitle: String = "Processing...",
    val statusSubtitle: String = "Fetching order details...",
    val progress: Float = 0.2f,
    val partnerLocation: LatLng = LatLng(20.5937, 78.9629),
    val destinationLocation: LatLng? = null,
    val isCompleted: Boolean = false,
    val isLoading: Boolean = true,
    val partnerName: String = "Assigning...",
    val partnerRating: String = "4.8",
    val partnerAvatar: String? = null,
    val partnerPhone: String = "+91 9876543210"
)

class TrackingViewModel(
    private val repository: SupabaseRepository = SupabaseRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrackingUiState())
    val uiState: StateFlow<TrackingUiState> = _uiState.asStateFlow()

    fun startTracking(orderId: String) {
        _uiState.update { it.copy(orderId = orderId, isLoading = true) }
        
        viewModelScope.launch {
            repository.listenToCustomerOrder(orderId).collectLatest { orders ->
                val order = orders.firstOrNull() ?: return@collectLatest
                
                _uiState.update { 
                    it.copy(
                        statusTitle = getTitleForStatus(order.status),
                        statusSubtitle = getSubtitleForStatus(order.status),
                        progress = getProgressForStatus(order.status),
                        isLoading = false,
                        isCompleted = order.status == "completed"
                    )
                }

                // If partner is assigned, listen to their location and fetch details
                val partnerId = order.deliveryPartnerId ?: ""
                if (partnerId.isNotEmpty()) {
                    // Fetch partner profile if not already loaded
                    if (_uiState.value.partnerName == "Assigning...") {
                        viewModelScope.launch {
                            repository.getUserProfile(partnerId).onSuccess { user ->
                                _uiState.update { it.copy(
                                    partnerName = user.name,
                                    partnerAvatar = user.avatarUrl,
                                    partnerRating = "4.9" // Default mock rating
                                ) }
                            }
                        }
                    }
                    
                    launch {
                        repository.listenToLocation(partnerId, false).collectLatest { locations ->
                            val loc = locations.firstOrNull() ?: return@collectLatest
                            _uiState.update { it.copy(partnerLocation = LatLng(loc.lat, loc.lng)) }
                        }
                    }
                }
            }
        }
    }

    private fun getTitleForStatus(status: String): String = when(status) {
        "placed" -> "Order Placed"
        "accepted" -> "Booking Accepted"
        "on_the_way" -> "On the Way"
        "arrived" -> "Arrived"
        "started" -> "In Progress"
        "completed" -> "Service Completed"
        else -> "Processing..."
    }

    private fun getSubtitleForStatus(status: String): String = when(status) {
        "placed" -> "Waiting for partner assignment"
        "accepted" -> "Partner is preparing to arrive"
        "on_the_way" -> "Partner is heading to your location"
        "arrived" -> "Partner has arrived"
        "started" -> "Work is in progress"
        "completed" -> "Thank you for using SAU Solutions!"
        else -> "Please wait a moment"
    }

    private fun getProgressForStatus(status: String): Float = when(status) {
        "placed" -> 0.2f
        "accepted" -> 0.4f
        "on_the_way" -> 0.6f
        "arrived" -> 0.8f
        "started" -> 0.9f
        "completed" -> 1.0f
        else -> 0.1f
    }

    fun updateDestination(latLng: LatLng, addressName: String) {
        _uiState.update { 
            it.copy(
                destinationLocation = latLng,
                statusTitle = "Out for Delivery",
                statusSubtitle = "Partner is moving towards $addressName",
                progress = 0.7f
            )
        }
    }
}
