package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.repository.UserRepository
import com.nisr.sauservices.data.supabase.AuthRepository
import com.nisr.sauservices.data.api.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.auth.OtpType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class Success(
        val user: UserInfo? = null,
        val userData: Map<String, Any?>? = null
    ) : AuthState()
    data class Error(
        val message: String
    ) : AuthState()
}

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    private val supabase = SupabaseClient.client

    private val _authState =
        MutableStateFlow<AuthState>(AuthState.Idle)

    val authState: StateFlow<AuthState> =
        _authState.asStateFlow()

    val currentUser: UserInfo?
        get() = supabase.auth.currentSessionOrNull()?.user

    /**
     * Login using email and password
     */
    fun signIn(
        email: String,
        password: String
    ) {
        if (email.isBlank()) {
            _authState.value =
                AuthState.Error("Please enter your email")
            return
        }

        if (password.isBlank()) {
            _authState.value =
                AuthState.Error("Please enter your password")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(email.trim())
                .matches()
        ) {
            _authState.value =
                AuthState.Error("Please enter a valid email address")
            return
        }

        if (password.length < 6) {
            _authState.value =
                AuthState.Error(
                    "Password must contain at least 6 characters"
                )
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // 1. Authenticate with Supabase Auth
                supabase.auth.signInWith(Email) {
                    this.email = email.trim()
                    this.password = password
                }
                
                val user = supabase.auth.currentUserOrNull()
                if (user != null) {
                    // 2. Verify they have a "real" profile in our database
                    val profileResult = userRepository.getUserData(user.id)
                    if (profileResult.isSuccess && profileResult.getOrNull() != null) {
                        _authState.value = AuthState.Success(
                            user = user,
                            userData = profileResult.getOrNull()?.mapValues { it.value as Any? }
                        )
                    } else {
                        // User exists in Auth but not in our Users table (possible incomplete registration)
                        _authState.value = AuthState.Error("User profile not found. Please register again.")
                    }
                } else {
                    _authState.value = AuthState.Error("Login successful, but session could not be established.")
                }
            } catch (e: Exception) {
                val errorMsg = e.message ?: "Login failed"
                val finalMsg = when {
                    errorMsg.contains("invalid", ignoreCase = true) -> 
                        "Invalid email or password. Have you registered yet?"
                    errorMsg.contains("confirm", ignoreCase = true) || errorMsg.contains("verified", ignoreCase = true) ->
                        "Please check your email and confirm your account before logging in."
                    else -> errorMsg
                }
                _authState.value = AuthState.Error(finalMsg)
            }
        }
    }

    /**
     * Sign in with Google ID Token
     */
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                supabase.auth.signInWith(IDToken) {
                    this.idToken = idToken
                    this.provider = Google
                }
                
                val user = supabase.auth.currentUserOrNull()
                if (user != null) {
                    // Check for profile or create one if it doesn't exist
                    val profileResult = userRepository.getUserData(user.id)
                    if (profileResult.isSuccess && profileResult.getOrNull() != null) {
                        _authState.value = AuthState.Success(
                            user = user,
                            userData = profileResult.getOrNull()?.mapValues { it.value as Any? }
                        )
                    } else {
                        // Create basic profile for Google users
                        val name = (user.userMetadata?.get("full_name") ?: user.userMetadata?.get("name"))?.toString() ?: "Google User"
                        val avatar = (user.userMetadata?.get("avatar_url") ?: user.userMetadata?.get("picture"))?.toString() ?: ""
                        userRepository.saveUserData(user.id, mapOf<String, Any?>(
                            "full_name" to name, 
                            "email" to (user.email ?: ""),
                            "profile_pic_url" to avatar
                        ))
                        _authState.value = AuthState.Success(user = user)
                    }
                } else {
                    _authState.value = AuthState.Error("Google authentication failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Google login failed")
            }
        }
    }

    /**
     * Check whether a user is already logged in.
     */
    fun isUserLoggedIn(): Boolean {
        return supabase.auth.currentSessionOrNull() != null
    }

    /**
     * Logout
     */
    fun signOut() {
        viewModelScope.launch {
            try {
                supabase.auth.signOut()
                _authState.value = AuthState.Idle
            } catch (e: Exception) {
                _authState.value =
                    AuthState.Error(
                        e.message ?: "Logout failed"
                    )
            }
        }
    }

    fun logout() = signOut()

    /**
     * Reset state back to Idle.
     */
    fun resetState() {
        _authState.value = AuthState.Idle
    }

    fun signUp(email: String, password: String, userData: Map<String, Any?>) {
        val fullName = userData["full_name"] as? String ?: ""
        
        if (fullName.isBlank() || fullName.length < 3) {
            _authState.value = AuthState.Error("Please enter a valid full name")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            _authState.value = AuthState.Error("Please enter a valid email address")
            return
        }

        if (password.length < 6) {
            _authState.value = AuthState.Error("Password must be at least 6 characters")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = userRepository.signUp(email.trim(), password, userData)
            if (result.isSuccess) {
                _authState.value = AuthState.Success(user = supabase.auth.currentUserOrNull())
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                supabase.auth.resetPasswordForEmail(email)
                _authState.value = AuthState.Idle
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Failed to send reset email")
            }
        }
    }

    fun verifyPasswordResetOtp(email: String, token: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                supabase.auth.verifyEmailOtp(
                    type = OtpType.Email.RECOVERY,
                    email = email,
                    token = token
                )
                _authState.value = AuthState.Idle
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Invalid OTP")
            }
        }
    }

    fun updatePassword(newPassword: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                supabase.auth.updateUser {
                    password = newPassword
                }
                _authState.value = AuthState.Idle
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Failed to update password")
            }
        }
    }
}
