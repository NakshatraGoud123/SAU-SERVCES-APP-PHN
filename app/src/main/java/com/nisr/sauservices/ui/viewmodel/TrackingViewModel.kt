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
    val partnerPhone: String = "+91 9876543210",
    val etaMinutes: Int = 25,
    val timeline: List<TimelineStatus> = emptyList()
)

data class TimelineStatus(
    val title: String,
    val isDone: Boolean,
    val isCurrent: Boolean
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
                        isCompleted = order.status == "completed",
                        etaMinutes = calculateETA(order.status),
                        timeline = generateTimeline(order.status)
                    )
                }

                // If partner is assigned, listen to their location and fetch details
                val assignedPartnerId = order.partnerId ?: ""
                if (assignedPartnerId.isNotEmpty()) {
                    // Fetch partner profile if not already loaded
                    if (_uiState.value.partnerName == "Assigning...") {
                        viewModelScope.launch {
                            repository.getUserProfile(assignedPartnerId).onSuccess { user ->
                                _uiState.update { it.copy(
                                    partnerName = user.name,
                                    partnerAvatar = user.avatarUrl,
                                    partnerRating = "4.9" // Default mock rating
                                ) }
                            }
                        }
                    }
                    
                    launch {
                        repository.listenToPartnerLocation(assignedPartnerId).collectLatest { locations ->
                            val loc = locations.firstOrNull() ?: return@collectLatest
                            _uiState.update { it.copy(partnerLocation = com.google.android.gms.maps.model.LatLng(loc.latitude, loc.longitude)) }
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
        "placed" -> 0.1f
        "accepted" -> 0.3f
        "on_the_way" -> 0.5f
        "arrived" -> 0.8f
        "started" -> 0.9f
        "completed" -> 1.0f
        else -> 0.1f
    }

    private fun calculateETA(status: String): Int = when(status) {
        "placed" -> 30
        "accepted" -> 25
        "on_the_way" -> 15
        "arrived" -> 5
        "started" -> 2
        else -> 0
    }

    private fun generateTimeline(currentStatus: String): List<TimelineStatus> {
        val statuses = listOf("placed", "accepted", "on_the_way", "arrived", "started", "completed")
        val currentIndex = statuses.indexOf(currentStatus)
        
        return listOf(
            TimelineStatus("Order Placed", currentIndex >= 0, currentIndex == 0),
            TimelineStatus("Partner Assigned", currentIndex >= 1, currentIndex == 1),
            TimelineStatus("Partner Arriving", currentIndex >= 2, currentIndex == 2),
            TimelineStatus("Service Started", currentIndex >= 4, currentIndex == 4),
            TimelineStatus("Delivered", currentIndex >= 5, currentIndex == 5)
        )
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
