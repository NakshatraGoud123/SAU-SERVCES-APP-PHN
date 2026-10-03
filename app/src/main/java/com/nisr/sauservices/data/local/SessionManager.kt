package com.nisr.sauservices.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.io.File

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = createEncryptedSharedPreferences(context)

    companion object {
        private const val PREFS_FILE_NAME = "sau_secure_prefs"
        const val KEY_LOGIN_STATE = "login_state"
        const val KEY_USER_ROLE = "user_role"
        const val KEY_LAT = "latitude"
        const val KEY_LNG = "longitude"
        const val KEY_ADDRESS = "address"
        const val KEY_THEME = "app_theme"

        private fun createEncryptedSharedPreferences(context: Context): SharedPreferences {
            return try {
                val masterKey = MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()

                EncryptedSharedPreferences.create(
                    context,
                    PREFS_FILE_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            } catch (e: Exception) {
                Log.e("SessionManager", "EncryptedSharedPreferences initialization failed. Clearing corrupted data.", e)
                clearCorruptedPreferences(context)

                try {
                    val masterKey = MasterKey.Builder(context)
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                        .build()

                    EncryptedSharedPreferences.create(
                        context,
                        PREFS_FILE_NAME,
                        masterKey,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    )
                } catch (retryException: Exception) {
                    Log.e("SessionManager", "Retry initializing EncryptedSharedPreferences failed.", retryException)
                    context.getSharedPreferences("${PREFS_FILE_NAME}_fallback", Context.MODE_PRIVATE)
                }
            }
        }

        private fun clearCorruptedPreferences(context: Context) {
            try {
                context.deleteSharedPreferences(PREFS_FILE_NAME)

                val sharedPrefsDir = File(context.filesDir.parentFile, "shared_prefs")
                if (sharedPrefsDir.exists() && sharedPrefsDir.isDirectory) {
                    File(sharedPrefsDir, "${PREFS_FILE_NAME}.xml").delete()
                    File(sharedPrefsDir, "${PREFS_FILE_NAME}_keyset.xml").delete()
                    File(sharedPrefsDir, "__androidx_security_crypto_encrypted_file_pref__.xml").delete()
                }
            } catch (e: Exception) {
                Log.e("SessionManager", "Error clearing corrupted preference files", e)
            }
        }
    }

    enum class ThemeConfig {
        SYSTEM, LIGHT, DARK
    }

    fun saveTheme(theme: ThemeConfig) {
        prefs.edit { putString(KEY_THEME, theme.name) }
    }

    fun getTheme(): ThemeConfig {
        val name = prefs.getString(KEY_THEME, ThemeConfig.SYSTEM.name)
        return try {
            ThemeConfig.valueOf(name ?: ThemeConfig.SYSTEM.name)
        } catch (e: Exception) {
            ThemeConfig.SYSTEM
        }
    }

    fun saveLoginState(isLoggedIn: Boolean) {
        prefs.edit { putBoolean(KEY_LOGIN_STATE, isLoggedIn) }
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_LOGIN_STATE, false)

    fun saveUserRole(role: String) {
        prefs.edit { putString(KEY_USER_ROLE, role) }
    }

    fun getUserRole(): String? = prefs.getString(KEY_USER_ROLE, null)

    fun saveLocation(lat: Double, lng: Double, address: String) {
        prefs.edit {
            putFloat(KEY_LAT, lat.toFloat())
            putFloat(KEY_LNG, lng.toFloat())
            putString(KEY_ADDRESS, address)
        }
    }

    fun getAddress(): String = prefs.getString(KEY_ADDRESS, "Fetching location...") ?: "Fetching location..."

    fun logout() {
        prefs.edit { clear() }
    }
}
