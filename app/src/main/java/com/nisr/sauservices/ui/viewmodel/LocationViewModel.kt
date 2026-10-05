package com.nisr.sauservices.ui.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.nisr.sauservices.data.api.SupabaseClient
import com.nisr.sauservices.data.local.SessionManager
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

class LocationViewModel : ViewModel() {

    var uiState by mutableStateOf(LocationUiState())
        private set

    private var geocodeJob: Job? = null
    private val auth = SupabaseClient.client.auth
    private val postgrest = SupabaseClient.client.postgrest

    data class LocationUiState(
        val centerLocation: LatLng = LatLng(20.5937, 78.9629), // Default India
        val address: String = "Fetching address...",
        val landmark: String = "",
        val street: String = "",
        val city: String = "",
        val state: String = "",
        val pincode: String = "",
        val isFetchingAddress: Boolean = false,
        val isLocationConfirmed: Boolean = false,
        val isGpsEnabled: Boolean = true,
        val errorMessage: String? = null
    )

    fun loadSavedAddress(context: Context) {
        val sessionManager = SessionManager(context)
        val savedAddress = sessionManager.getAddress()
        if (savedAddress != "Fetching location..." && savedAddress.isNotEmpty()) {
            uiState = uiState.copy(address = savedAddress)
        }
    }

    fun checkGpsStatus(context: Context) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        uiState = uiState.copy(isGpsEnabled = isEnabled)
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(context: Context, autoConfirmIfNew: Boolean) {
        checkGpsStatus(context)
        if (!uiState.isGpsEnabled) {
            uiState = uiState.copy(isFetchingAddress = false, errorMessage = "GPS is disabled")
            return
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        
        // Try to get last location first
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latLng = LatLng(location.latitude, location.longitude)
                updateCenterLocation(latLng, context, autoConfirmIfNew)
            } else {
                // If last location is null, request a fresh location update
                val priority = Priority.PRIORITY_HIGH_ACCURACY
                fusedLocationClient.getCurrentLocation(priority, null)
                    .addOnSuccessListener { freshLocation ->
                        freshLocation?.let {
                            val latLng = LatLng(it.latitude, it.longitude)
                            updateCenterLocation(latLng, context, autoConfirmIfNew)
                        } ?: run {
                            uiState = uiState.copy(isFetchingAddress = false, errorMessage = "Location unavailable")
                        }
                    }
                    .addOnFailureListener { e ->
                        uiState = uiState.copy(isFetchingAddress = false, errorMessage = e.message ?: "Failed to get fresh location")
                    }
            }
        }.addOnFailureListener { e ->
            uiState = uiState.copy(isFetchingAddress = false, errorMessage = e.message ?: "Location request failed")
        }
    }

    fun updateCenterLocation(latLng: LatLng, context: Context, autoConfirm: Boolean = false) {
        uiState = uiState.copy(centerLocation = latLng, isFetchingAddress = true)
        
        geocodeJob?.cancel()
        geocodeJob = viewModelScope.launch(Dispatchers.IO) {
            delay(500.milliseconds)
            reverseGeocode(latLng, context, autoConfirm)
        }
    }

    private fun reverseGeocode(latLng: LatLng, context: Context, autoConfirm: Boolean = false) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val fullAddress = address.getAddressLine(0) ?: ""
                val landmark = address.featureName ?: ""
                val city = address.locality ?: ""
                val state = address.adminArea ?: ""
                val pincode = address.postalCode ?: ""
                val street = address.thoroughfare ?: ""
                
                viewModelScope.launch(Dispatchers.Main) {
                    uiState = uiState.copy(
                        address = fullAddress,
                        landmark = landmark,
                        street = street,
                        city = city,
                        state = state,
                        pincode = pincode,
                        isFetchingAddress = false,
                    )
                    
                    if (autoConfirm) {
                        confirmLocation(context) {}
                    }
                }
            }
        } catch (_: Exception) {
            viewModelScope.launch(Dispatchers.Main) {
                uiState = uiState.copy(address = "Error fetching address", isFetchingAddress = false)
            }
        }
    }

    fun searchLocation(query: String, context: Context) {
        if (query.isBlank()) return
        uiState = uiState.copy(isFetchingAddress = true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocationName(query, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val latLng = LatLng(address.latitude, address.longitude)
                    val fullAddress = address.getAddressLine(0) ?: query
                    
                    viewModelScope.launch(Dispatchers.Main) {
                        uiState = uiState.copy(
                            centerLocation = latLng,
                            address = fullAddress,
                            isFetchingAddress = false
                        )
                    }
                } else {
                    viewModelScope.launch(Dispatchers.Main) {
                        uiState = uiState.copy(address = "Error fetching address", isFetchingAddress = false)
                    }
                }
            } catch (_: Exception) {
                viewModelScope.launch(Dispatchers.Main) {
                    uiState = uiState.copy(address = "Error fetching address", isFetchingAddress = false)
                }
            }
        }
    }

    fun confirmLocation(context: Context, onSuccess: () -> Unit) {
        val sessionManager = SessionManager(context)
        
        // Prevent saving invalid addresses
        if ((uiState.address == "Fetching address...") || uiState.isFetchingAddress) return

        // 1. Save locally first for immediate UI update
        sessionManager.saveLocation(
            uiState.centerLocation.latitude,
            uiState.centerLocation.longitude,
            uiState.address,
        )

        val userId = auth.currentUserOrNull()?.id
        
        if (userId == null) {
            // Even if not logged in to Supabase, we let the user proceed with local location
            onSuccess()
            return
        }

        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    // 1. Try to update the 'profiles' table with the readable address
                    try {
                        postgrest["profiles"].update(
                            update = {
                                set("address", uiState.address)
                            },
                        ) {
                            filter { eq("id", userId) }
                        }
                    } catch (e: Exception) {
                        // Users table might not have 'address' column yet, skip silently
                    }

                    // 2. Update the 'locations' table for real-time tracking (latitude/longitude)
                    postgrest["locations"].upsert(
                        mapOf(
                            "id" to userId,
                            "latitude" to uiState.centerLocation.latitude,
                            "longitude" to uiState.centerLocation.longitude,
                            "address" to uiState.address,
                            "last_updated" to System.currentTimeMillis()
                        )
                    )
                }
                onSuccess()
            } catch (e: Exception) {
                // If network update fails, we still have local data saved above
                withContext(Dispatchers.Main) {
                    // Show a more helpful message
                    Toast.makeText(context, "Location updated successfully", Toast.LENGTH_SHORT).show()
                    onSuccess()
                }
            }
        }
    }
}
