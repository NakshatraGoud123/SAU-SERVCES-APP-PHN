package com.nisr.sauservices.ui.viewmodel

import android.util.Log
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
                    response.decodeSingleOrNull<UserProfile>()
                }
                
                if (profile != null) {
                    _userProfile.value = profile
                    Log.d("PROFILE_DEBUG", "Loaded Profile: ${profile.name}")
                } else {
                    // Fallback to Auth Metadata if profile row is missing
                    val currentAuthUser = auth.currentUserOrNull()
                    val newProfile = UserProfile(
                        id = uid,
                        name = (currentAuthUser?.userMetadata?.get("full_name") ?: currentAuthUser?.userMetadata?.get("name"))?.toString() ?: "User",
                        email = currentAuthUser?.email ?: "",
                        phone = currentAuthUser?.phone ?: "",
                        profilePicUrl = (currentAuthUser?.userMetadata?.get("avatar_url") ?: currentAuthUser?.userMetadata?.get("picture"))?.toString()
                    )
                    _userProfile.value = newProfile
                }
            } catch (e: Exception) {
                Log.e("PROFILE_DEBUG", "Error: ${e.message}")
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
                    val profileToSave = UserProfile(
                        id = uid,
                        name = name,
                        phone = phone,
                        profilePicUrl = profilePicUrl ?: _userProfile.value?.profilePicUrl,
                        email = _userProfile.value?.email ?: auth.currentUserOrNull()?.email ?: ""
                    )
                    postgrest["profiles"].upsert(profileToSave)
                }
                fetchUserProfile()
                onComplete(Result.success(Unit))
            } catch (e: Exception) {
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
                val fileName = "avatar_$uid.jpg"
                val bucket = storage["avatars"]
                
                withContext(Dispatchers.IO) {
                    bucket.upload(fileName, byteArray) {
                        upsert = true
                    }
                }
                
                val publicUrl = "${SupabaseClient.SUPABASE_URL}/storage/v1/object/public/avatars/$fileName?t=${System.currentTimeMillis()}"
                
                // Update local state immediately
                _userProfile.value = _userProfile.value?.copy(profilePicUrl = publicUrl)
                
                // Save to database
                updateProfile(
                    name = _userProfile.value?.name ?: "",
                    phone = _userProfile.value?.phone ?: "",
                    profilePicUrl = publicUrl
                ) { result ->
                    if (result.isSuccess) onResult(Result.success(publicUrl))
                    else onResult(Result.failure(result.exceptionOrNull() ?: Exception("Profile update failed after upload")))
                }
            } catch (e: Exception) {
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
                    val addressWithUser = address.copy(id = "", userId = uid)
                    postgrest["addresses"].insert(addressWithUser)
                }
                fetchAddresses()
            } catch (e: Exception) { }
        }
    }

    fun deleteAddress(addressId: String) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    postgrest["addresses"].delete { filter { eq("id", addressId) } }
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
                    postgrest["addresses"].update({ set("is_default", false) }) { filter { eq("user_id", uid) } }
                    postgrest["addresses"].update({ set("is_default", true) }) { filter { eq("id", addressId) } }
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
                    postgrest["notification_preferences"].select { filter { eq("user_id", uid) } }.decodeSingleOrNull<NotificationPreferences>()
                }
                if (prefs != null) _notificationPrefs.value = prefs
            } catch (e: Exception) { }
        }
    }

    fun fetchNotifications() {
        val uid = auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            try {
                val list = withContext(Dispatchers.IO) {
                    postgrest["notifications"].select { filter { eq("user_id", uid) } }.decodeList<Notification>()
                }
                _notifications.value = list
            } catch (e: Exception) { }
        }
    }

    fun updateNotificationPref(key: String, value: Boolean) {
        val uid = auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    postgrest["notification_preferences"].upsert(mapOf("user_id" to uid, key to value))
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
