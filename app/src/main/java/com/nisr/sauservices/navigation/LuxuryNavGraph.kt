package com.nisr.sauservices.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.luxury.*
import com.nisr.sauservices.ui.location.ManualLocationScreen
import com.nisr.sauservices.ui.viewmodel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.ui.auth.ForgotPasswordScreen
import com.nisr.sauservices.ui.auth.LoginScreen
import com.nisr.sauservices.ui.auth.ResetPasswordScreen
import com.nisr.sauservices.ui.auth.SignUpScreen

fun NavGraphBuilder.luxuryNavGraph(navController: NavController) {
    composable<Screen.LuxurySplash> {
        val context = LocalContext.current
        val sessionManager = remember { SessionManager(context) }
        
        LuxurySplashScreen(onFinished = {
            if (sessionManager.isLoggedIn()) {
                val hasPermission = ContextCompat.checkSelfPermission(
                    context, 
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                                 locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                
                val currentAddress = sessionManager.getAddress()
                val hasSavedAddress = currentAddress != "Fetching location..." && currentAddress.isNotEmpty()

                if ((hasPermission && isGpsEnabled) || hasSavedAddress) {
                    navController.navigate(Screen.Home) {
                        popUpTo<Screen.LuxurySplash> { inclusive = true }
                    }
                } else {
                    navController.navigate(Screen.LocationPermission) {
                        popUpTo<Screen.LuxurySplash> { inclusive = true }
                    }
                }
            } else {
                navController.navigate(Screen.LuxuryOnboarding1) {
                    popUpTo<Screen.LuxurySplash> { inclusive = true }
                }
            }
        })
    }
    
    composable<Screen.LuxuryOnboarding1> {
        LuxuryOnboardingScreen(
            onFinished = { navController.navigate(Screen.LuxuryLogin) }
        )
    }
    
    // Legacy onboarding routes - redirecting to the new unified screen
    composable<Screen.LuxuryOnboarding2> { navController.navigate(Screen.LuxuryOnboarding1) }
    composable<Screen.LuxuryOnboarding3> { navController.navigate(Screen.LuxuryOnboarding1) }
    
    composable<Screen.LuxuryLogin> {
        LoginScreen(navController)
    }
    
    composable<Screen.LuxurySignUp> {
        SignUpScreen(navController, "customer")
    }

    composable<Screen.ManualLocation> {
        ManualLocationScreen(navController)
    }
    
    composable<Screen.LuxuryForgotPassword> {
        ForgotPasswordScreen(navController)
    }
    
    composable<Screen.ResetPassword> { backStackEntry ->
        val route: Screen.ResetPassword = backStackEntry.toRoute()
        ResetPasswordScreen(navController, route.email)
    }

    composable<Screen.LuxuryProfile> {
        LuxuryProfileScreen(
            navController = navController,
            onSignOut = { navController.navigate(Screen.LuxurySignOut) },
            onBack = { navController.popBackStack() }
        )
    }

    composable<Screen.LuxurySignOut> {
        val context = LocalContext.current
        val sessionManager = remember { SessionManager(context) }
        val authViewModel: AuthViewModel = viewModel()
        
        LuxurySignOutConfirmation(
            onConfirm = { 
                // SECURE: Sign out from Supabase AND local state
                authViewModel.signOut()
                sessionManager.saveLoginState(false)
                sessionManager.logout() // Clear all encrypted data
                
                navController.navigate(Screen.LuxuryLogin) {
                    popUpTo(0) { inclusive = true }
                }
            },
            onCancel = { navController.popBackStack() }
        )
    }
}
