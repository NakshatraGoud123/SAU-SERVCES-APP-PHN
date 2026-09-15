package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.api.SupabaseClient
import com.nisr.sauservices.data.model.Address
import com.nisr.sauservices.data.model.NotificationPreferences
import com.nisr.sauservices.data.model.UserProfile
import com.nisr.sauservices.data.model.Notification
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel : ViewModel() {
    private val auth = SupabaseClient.client.auth
    private val postgrest = SupabaseClient.client.postgrest
    private val storage = SupabaseClient.client.storage

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    private val _addresses = MutableStateFlow<List<Address>>(emptyList())
    val addresses: StateFlow<List<Address>> = _addresses

    private val _notificationPrefs = MutableStateFlow(NotificationPreferences())
    val notificationPrefs: StateFlow<NotificationPreferences> = _notificationPrefs

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading

    init {
        fetchUserProfile()
        fetchAddresses()
        fetchNotificationPreferences()
        fetchNotifications()
    }

    fun fetchUserProfile() {
        val uid = auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val profile = withContext(Dispatchers.IO) {
                    val response = postgrest["profiles"].select {
                        filter { eq("id", uid) }
                    }
                    android.util.Log.d("PROFILE_DEBUG", "Raw Profile Data: ${response.data}")
                    response.decodeSingleOrNull<UserProfile>()
                }
                
                if (profile != null) {
                    _userProfile.value = profile
                    android.util.Log.d("PROFILE_DEBUG", "Loaded Profile: Name=${profile.name}, Pic=${profile.profilePicUrl}")
                } else {
                    android.util.Log.e("PROFILE_DEBUG", "Profile not found in database for ID: $uid")
                    val currentAuthUser = auth.currentUserOrNull()
                    val newProfile = UserProfile(
                        id = uid,
                        name = (currentAuthUser?.userMetadata?.get("full_name") ?: currentAuthUser?.userMetadata?.get("name"))?.toString() ?: "New User",
                        email = currentAuthUser?.email ?: "",
                        phone = currentAuthUser?.phone ?: "",
                        profilePicUrl = (currentAuthUser?.userMetadata?.get("avatar_url") ?: currentAuthUser?.userMetadata?.get("picture"))?.toString()
                    )
                    _userProfile.value = newProfile
                }
            } catch (e: Exception) {
                android.util.Log.e("PROFILE_DEBUG", "Error fetching profile: ${e.message}")
                _userProfile.value = UserProfile(name = "User", email = auth.currentUserOrNull()?.email ?: "")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(name: String, phone: String, profilePicUrl: String? = null, onComplete: (Result<Unit>) -> Unit = {}) {
        val uid = auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                withContext(Dispatchers.IO) {
                    // Create a serializable profile object to avoid 'Any' serialization error
                    val profileToSave = UserProfile(
                        id = uid,
                        name = name,
                        phone = phone,
                        profilePicUrl = profilePicUrl ?: _userProfile.value?.profilePicUrl,
                        email = _userProfile.value?.email ?: auth.currentUserOrNull()?.email ?: ""
                    )

                    // UPSERT using the serializable data class
                    postgrest["profiles"].upsert(profileToSave)
                }
                fetchUserProfile()
                onComplete(Result.success(Unit))
            } catch (e: Exception) {
                android.util.Log.e("DATABASE_ERROR", "Failed to upsert profiles table: ${e.message}")
                onComplete(Result.failure(e))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun uploadProfilePicture(byteArray: ByteArray, onResult: (Result<String>) -> Unit) {
        val uid = auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            _isUploading.value = true
            try {
                val fileName = "profile_$uid.jpg"
                val bucket = storage["avatars"]
                
                withContext(Dispatchers.IO) {
                    bucket.upload(fileName, byteArray) {
                        upsert = true
                    }
                }
                
                // Construct the direct public URL with a timestamp for cache busting
                val supabaseUrl = com.nisr.sauservices.data.api.SupabaseClient.SUPABASE_URL
                val publicUrl = "$supabaseUrl/storage/v1/object/public/avatars/$fileName?t=${System.currentTimeMillis()}"
                
                android.util.Log.d("PHOTO_DEBUG", "Final Avatar URL: $publicUrl")
                
                // Immediately update local state for fast UI feedback
                val currentProfile = _userProfile.value
                if (currentProfile != null) {
                    _userProfile.value = currentProfile.copy(profilePicUrl = publicUrl)
                }
                
                // Now update the users table in database
                updateProfile(
                    name = _userProfile.value?.name ?: "",
                    phone = _userProfile.value?.phone ?: "",
                    profilePicUrl = publicUrl
                ) { dbResult ->
                    if (dbResult.isSuccess) {
                        onResult(Result.success(publicUrl))
                    } else {
                        onResult(Result.failure(Exception("Link failed: ${dbResult.exceptionOrNull()?.message}")))
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("PHOTO_DEBUG", "Upload error: ${e.message}")
                onResult(Result.failure(e))
            } finally {
                _isUploading.value = false
            }
        }
    }

    fun fetchAddresses() {
        val uid = auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            try {
                val list = withContext(Dispatchers.IO) {
                    postgrest["addresses"].select {
                        filter { eq("user_id", uid) }
                    }.decodeList<Address>()
                }
                _addresses.value = list
            } catch (e: Exception) { }
        }
    }

    fun addAddress(address: Address) {
        val uid = auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    // Critical Fix: Attach the User ID to the address
                    val addressWithUser = address.copy(id = "", userId = uid)
                    postgrest["addresses"].insert(addressWithUser)
                }
                fetchAddresses()
            } catch (e: Exception) {
                android.util.Log.e("ADDRESS_ERROR", "Failed to save address: ${e.message}")
            }
        }
    }

    fun deleteAddress(addressId: String) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    postgrest["addresses"].delete {
                        filter { eq("id", addressId) }
                    }
                }
                fetchAddresses()
            } catch (e: Exception) { }
        }
    }

    fun setDefaultAddress(addressId: String) {
        val uid = auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    // First set all to false
                    postgrest["addresses"].update({
                        set("is_default", false)
                    }) {
                        filter { eq("user_id", uid) }
                    }
                    // Then set specific to true
                    postgrest["addresses"].update({
                        set("is_default", true)
                    }) {
                        filter { eq("id", addressId) }
                    }
                }
                fetchAddresses()
            } catch (e: Exception) { }
        }
    }

    fun fetchNotificationPreferences() {
        val uid = auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            try {
                val prefs = withContext(Dispatchers.IO) {
                    postgrest["notification_preferences"].select {
                        filter { eq("user_id", uid) }
                    }.decodeSingleOrNull<NotificationPreferences>()
                }
                if (prefs != null) {
                    _notificationPrefs.value = prefs
                }
            } catch (e: Exception) { }
        }
    }

    fun fetchNotifications() {
        val uid = auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            try {
                val list = withContext(Dispatchers.IO) {
                    postgrest["notifications"].select {
                        filter { eq("user_id", uid) }
                    }.decodeList<Notification>()
                }
                _notifications.value = list
            } catch (e: Exception) { 
                // Fallback to empty list or handle error
            }
        }
    }

    fun updateNotificationPref(key: String, value: Boolean) {
        val uid = auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    postgrest["notification_preferences"].upsert(mapOf(
                        "user_id" to uid,
                        key to value
                    ))
                }
                fetchNotificationPreferences()
            } catch (e: Exception) { }
        }
    }

    fun submitSupportMessage(subject: String, message: String) {
        val uid = auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            try {
                val data = mapOf(
                    "user_id" to uid,
                    "subject" to subject,
                    "message" to message
                )
                withContext(Dispatchers.IO) {
                    postgrest["support_messages"].insert(data)
                }
            } catch (e: Exception) { }
        }
    }

    fun logout() {
        viewModelScope.launch {
            auth.signOut()
        }
    }
}
