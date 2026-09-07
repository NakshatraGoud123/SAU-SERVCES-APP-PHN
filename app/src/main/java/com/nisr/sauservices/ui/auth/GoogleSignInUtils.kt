package com.nisr.sauservices.ui.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.nisr.sauservices.R

object GoogleSignInUtils {
    suspend fun launchGoogleSignIn(context: Context): String? {
        val credentialManager = CredentialManager.create(context)
        
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.google_client_id))
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = credentialManager.getCredential(context, request)
            handleSignIn(result)
        } catch (e: Exception) {
            // Forward the exception or handle specific ones like GetCredentialCancellationException
            throw e
        }
    }

    private fun handleSignIn(result: GetCredentialResponse): String? {
        val credential = result.credential
        if (credential is GoogleIdTokenCredential) {
            return credential.idToken
        }
        return null
    }
}
