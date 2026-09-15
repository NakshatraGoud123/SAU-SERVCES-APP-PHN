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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.util.Patterns
import kotlinx.coroutines.Job
import java.util.regex.Pattern

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

data class SecurityState(
    val isLockedOut: Boolean = false,
    val remainingLockoutSeconds: Int = 0,
    val loginAttempts: Int = 0
)

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    private val supabase = SupabaseClient.client

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _securityState = MutableStateFlow(SecurityState())
    val securityState: StateFlow<SecurityState> = _securityState.asStateFlow()

    private val emailPattern = Patterns.EMAIL_ADDRESS
    private val passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[A-Z]).{8,}$") // 8+ chars, 1 digit, 1 uppercase

    private var lockoutJob: Job? = null

    val currentUser: UserInfo?
        get() = supabase.auth.currentSessionOrNull()?.user

    /**
     * Secure Login
     */
    fun signIn(email: String, password: String) {
        if (_securityState.value.isLockedOut) {
            _authState.value = AuthState.Error("Too many attempts. Please try again in ${_securityState.value.remainingLockoutSeconds}s")
            return
        }

        // 1. Validation
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Please fill in all fields")
            return
        }

        if (!emailPattern.matcher(email.trim()).matches()) {
            _authState.value = AuthState.Error("Invalid email format")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                supabase.auth.signInWith(Email) {
                    this.email = email.trim()
                    this.password = password
                }
                
                val user = supabase.auth.currentUserOrNull()
                if (user != null) {
                    // Success: Reset attempts
                    _securityState.value = _securityState.value.copy(loginAttempts = 0)
                    
                    val profileResult = userRepository.getUserData(user.id)
                    if (profileResult.isSuccess && profileResult.getOrNull() != null) {
                        _authState.value = AuthState.Success(
                            user = user,
                            userData = profileResult.getOrNull()?.mapValues { it.value as Any? }
                        )
                    } else {
                        _authState.value = AuthState.Error("Account profile not found. Please register.")
                    }
                } else {
                    handleLoginFailure("Session failed")
                }
            } catch (e: Exception) {
                handleLoginFailure(e.message ?: "Login failed")
            }
        }
    }

    private fun handleLoginFailure(rawMessage: String) {
        val attempts = _securityState.value.loginAttempts + 1
        val shouldLock = attempts >= 5
        
        _securityState.value = _securityState.value.copy(
            loginAttempts = attempts,
            isLockedOut = shouldLock,
            remainingLockoutSeconds = if (shouldLock) 30 else 0
        )

        if (shouldLock) startLockoutTimer()

        val friendlyMsg = when {
            rawMessage.contains("invalid", true) -> "Invalid email or password"
            rawMessage.contains("not found", true) -> "Account does not exist"
            rawMessage.contains("timeout", true) || rawMessage.contains("network", true) -> "Server busy. Please try again later."
            rawMessage.contains("confirm", true) || rawMessage.contains("verified", true) -> "Please verify your email before logging in."
            else -> "Access denied. Check your credentials."
        }
        
        _authState.value = AuthState.Error(friendlyMsg)
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
                    _securityState.value = _securityState.value.copy(loginAttempts = 0)
                    val profileResult = userRepository.getUserData(user.id)
                    if (profileResult.isSuccess && profileResult.getOrNull() != null) {
                        _authState.value = AuthState.Success(
                            user = user,
                            userData = profileResult.getOrNull()?.mapValues { it.value as Any? }
                        )
                    } else {
                        val name = (user.userMetadata?.get("full_name") ?: user.userMetadata?.get("name"))?.toString() ?: "User"
                        val avatar = (user.userMetadata?.get("avatar_url") ?: user.userMetadata?.get("picture"))?.toString() ?: ""
                        userRepository.saveUserData(user.id, mapOf<String, Any?>(
                            "name" to name, 
                            "email" to (user.email ?: ""),
                            "avatar_url" to avatar
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
                _authState.value = AuthState.Error("Logout failed")
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


    private fun startLockoutTimer() {
        lockoutJob?.cancel()
        lockoutJob = viewModelScope.launch {
            while (_securityState.value.remainingLockoutSeconds > 0) {
                delay(1000)
                _securityState.value = _securityState.value.copy(
                    remainingLockoutSeconds = _securityState.value.remainingLockoutSeconds - 1
                )
            }
            _securityState.value = _securityState.value.copy(isLockedOut = false, loginAttempts = 0)
        }
    }

    /**
     * Secure Sign Up
     */
    fun signUp(email: String, password: String, userData: Map<String, Any?>) {
        val name = userData["name"] as? String ?: ""
        
        if (name.isBlank() || name.length < 3) {
            _authState.value = AuthState.Error("Name must be at least 3 characters")
            return
        }

        if (!emailPattern.matcher(email.trim()).matches()) {
            _authState.value = AuthState.Error("Invalid email address")
            return
        }

        if (!passwordPattern.matcher(password).matches()) {
            _authState.value = AuthState.Error("Password needs 8+ chars, 1 uppercase, and 1 number")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = userRepository.signUp(email.trim(), password, userData)
            if (result.isSuccess) {
                _authState.value = AuthState.Success(user = supabase.auth.currentUserOrNull())
            } else {
                val error = result.exceptionOrNull()?.message ?: ""
                val msg = if (error.contains("already exists", true)) "This email is already registered" else "Registration failed"
                _authState.value = AuthState.Error(msg)
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
