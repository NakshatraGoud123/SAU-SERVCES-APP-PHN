package com.nisr.sauservices.data.supabase

import com.nisr.sauservices.data.api.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

class AuthRepository {

    private val supabase = SupabaseClient.client

    /**
     * Login user using email and password.
     */
    suspend fun login(
        email: String,
        password: String
    ): Result<String> {
        return try {

            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            val user = supabase.auth.currentUserOrNull()

            if (user != null) {
                Result.success(user.id)
            } else {
                Result.failure(
                    Exception("Login successful, but user information was not found.")
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Logout current user.
     */
    suspend fun logout(): Result<Unit> {
        return try {
            supabase.auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get currently logged-in user's ID.
     */
    fun getCurrentUserId(): String? {
        return supabase.auth.currentUserOrNull()?.id
    }

    /**
     * Check whether a user is currently logged in.
     */
    fun isUserLoggedIn(): Boolean {
        return supabase.auth.currentUserOrNull() != null
    }
}
