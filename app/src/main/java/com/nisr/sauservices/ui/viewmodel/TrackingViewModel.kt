package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.nisr.sauservices.data.model.BookingModel
import com.nisr.sauservices.data.repository.SupabaseRepository
import kotlinx.coroutines.Job
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
    val statusSubtitle: String = "Fetching booking details...",
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
    val timeline: List<TimelineStatus> = emptyList(),
    val partnerId: String = ""
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

    private var trackingJob: Job? = null

    fun startTracking(bookingId: String) {
        if (bookingId.isBlank()) return
        
        // Cancel any existing tracking session to prevent duplicate listeners
        trackingJob?.cancel()
        
        _uiState.update { it.copy(orderId = bookingId, isLoading = true) }
        
        trackingJob = viewModelScope.launch {
            // 1. Fetch immediately so status appears right away
            repository.getBookingById(bookingId).onSuccess { booking ->
                updateStateWithBooking(booking)
            }

            // 2. Listen to realtime stream updates
            repository.listenToCustomerBooking(bookingId).collectLatest { bookings ->
                val booking = bookings.firstOrNull() ?: return@collectLatest
                updateStateWithBooking(booking)
            }
        }
    }

    private suspend fun updateStateWithBooking(booking: BookingModel) {
        _uiState.update { 
            it.copy(
                statusTitle = getTitleForStatus(booking.status),
                statusSubtitle = getSubtitleForStatus(booking.status),
                progress = getProgressForStatus(booking.status),
                isLoading = false,
                isCompleted = booking.status == "completed",
                etaMinutes = calculateETA(booking.status),
                timeline = generateTimeline(booking.status)
            )
        }

        // If partner is assigned, listen to their location and fetch details
        val assignedPartnerId = booking.providerId ?: ""
        if (assignedPartnerId.isNotEmpty()) {
            _uiState.update { it.copy(partnerId = assignedPartnerId) }
            // Fetch partner profile if not already loaded
            if (_uiState.value.partnerName == "Assigning..." || _uiState.value.partnerName == "Service Expert") {
                repository.getUserProfile(assignedPartnerId).onSuccess { user ->
                    _uiState.update { it.copy(
                        partnerName = user.name,
                        partnerAvatar = user.avatarUrl,
                        partnerRating = "4.9"
                    ) }
                }.onFailure {
                    _uiState.update { it.copy(partnerName = "Service Expert") }
                }
            }
            
            // Use a coroutine tied to viewModelScope
            viewModelScope.launch {
                repository.listenToPartnerLocation(assignedPartnerId).collectLatest { locations ->
                    val loc = locations.firstOrNull() ?: return@collectLatest
                    _uiState.update { it.copy(partnerLocation = LatLng(loc.latitude, loc.longitude)) }
                }
            }
        }
    }

    fun stopTracking() {
        trackingJob?.cancel()
        trackingJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopTracking()
    }

    private fun getTitleForStatus(status: String): String = when(status.lowercase()) {
        "pending" -> "Searching for Expert"
        "accepted" -> "Booking Accepted"
        "on_the_way" -> "On the Way"
        "arrived" -> "Arrived"
        "started" -> "In Progress"
        "completed" -> "Service Completed"
        "cancelled" -> "Booking Cancelled"
        else -> "Processing..."
    }

    private fun getSubtitleForStatus(status: String): String = when(status.lowercase()) {
        "pending" -> "Waiting for a partner to accept your booking"
        "accepted" -> "Partner is preparing to arrive"
        "on_the_way" -> "Partner is heading to your location"
        "arrived" -> "Partner has arrived"
        "started" -> "Work is in progress"
        "completed" -> "Thank you for using SAU Services!"
        "cancelled" -> "This booking has been cancelled"
        else -> "Please wait a moment"
    }

    private fun getProgressForStatus(status: String): Float = when(status.lowercase()) {
        "pending" -> 0.1f
        "accepted" -> 0.3f
        "on_the_way" -> 0.5f
        "arrived" -> 0.8f
        "started" -> 0.9f
        "completed" -> 1.0f
        else -> 0.1f
    }

    private fun calculateETA(status: String): Int = when(status.lowercase()) {
        "pending" -> 30
        "accepted" -> 25
        "on_the_way" -> 15
        "arrived" -> 5
        "started" -> 2
        else -> 0
    }

    private fun generateTimeline(currentStatus: String): List<TimelineStatus> {
        val statuses = listOf("pending", "accepted", "on_the_way", "arrived", "started", "completed")
        val currentIndex = statuses.indexOf(currentStatus.lowercase())
        
        return listOf(
            TimelineStatus("Booking Placed", currentIndex >= 0, currentIndex == 0),
            TimelineStatus("Partner Assigned", currentIndex >= 1, currentIndex == 1),
            TimelineStatus("Partner Arriving", currentIndex >= 2, currentIndex == 2),
            TimelineStatus("Service Started", currentIndex >= 4, currentIndex == 4),
            TimelineStatus("Completed", currentIndex >= 5, currentIndex == 5)
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
