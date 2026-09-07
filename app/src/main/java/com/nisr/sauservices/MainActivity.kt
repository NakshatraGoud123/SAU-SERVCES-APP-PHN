package com.nisr.sauservices

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.nisr.sauservices.data.api.SupabaseClient
import com.nisr.sauservices.navigation.AppNavHost
import com.nisr.sauservices.ui.payment.PaymentEvent
import com.nisr.sauservices.ui.payment.PaymentResultBus
import com.nisr.sauservices.ui.theme.AppTheme
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity(), PaymentResultWithDataListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        
        super.onCreate(savedInstanceState)

        // Supabase Connection Test
        val client = SupabaseClient.client

        setContent {

            val navController = rememberNavController()

            LaunchedEffect(Unit) {
                try {
                    val user = client.auth.currentSessionOrNull()?.user
                    val sessionManager = com.nisr.sauservices.data.local.SessionManager(this@MainActivity)
                    
                    if (user != null && !sessionManager.isLoggedIn()) {
                        sessionManager.saveLoginState(true)
                    }

                    Log.d(
                        "SUPABASE_TEST",
                        "Current User: ${user?.email ?: "Not logged in"}",
                    )
                } catch (e: Exception) {

                    Log.e(
                        "SUPABASE_TEST",
                        "Init Error: ${e.message}",
                    )
                }
            }

            AppTheme {
                // Let AppNavHost handle the Splash screen as its start destination
                AppNavHost(navController)
            }
        }
    }

    override fun onPaymentSuccess(paymentId: String?, data: PaymentData?) {
        lifecycleScope.launch {
            PaymentResultBus.post(PaymentEvent.Success(paymentId, data))
        }
    }

    override fun onPaymentError(code: Int, description: String?, data: PaymentData?) {
        lifecycleScope.launch {
            PaymentResultBus.post(PaymentEvent.Error(code, description, data))
        }
    }
}
