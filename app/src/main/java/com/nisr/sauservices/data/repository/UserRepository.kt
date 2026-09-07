package com.nisr.sauservices.data.repository

import com.nisr.sauservices.data.api.SupabaseClient
import com.nisr.sauservices.data.model.User
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository {
    private val client = SupabaseClient.client

    fun getCurrentUser() = client.auth.currentSessionOrNull()?.user

    suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(email: String, password: String, userData: Map<String, Any?>): Result<Unit> {
        return try {
            val authUser = client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            
            authUser?.let {
                val userId = it.id
                val profileData = userData.toMutableMap()
                profileData["id"] = userId
                
                withContext(Dispatchers.IO) {
                    // Using postgrest to insert profile data
                    client.postgrest["users"].insert(profileData)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerUser(user: User): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                client.postgrest["users"].insert(user)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserData(uid: String): Result<Map<String, String>?> {
        return try {
            val response = withContext(Dispatchers.IO) {
                client.postgrest["users"].select(Columns.ALL) {
                    filter {
                        eq("id", uid)
                    }
                }.decodeSingleOrNull<Map<String, String>>()
            }
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveUserData(uid: String, userData: Map<String, Any?>): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                client.postgrest["users"].upsert(userData) {
                    filter {
                        eq("id", uid)
                    }
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            client.auth.resetPasswordForEmail(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyPasswordResetOtp(email: String, token: String): Result<Unit> {
        return try {
            client.auth.verifyEmailOtp(
                type = OtpType.Email.RECOVERY,
                email = email,
                token = token
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePassword(newPassword: String): Result<Unit> {
        return try {
            client.auth.updateUser {
                password = newPassword
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        client.auth.signOut()
    }
}
