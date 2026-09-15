package com.nisr.sauservices.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.luxury.*
import com.nisr.sauservices.ui.viewmodel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun NavGraphBuilder.luxuryNavGraph(navController: NavController) {
    composable<Screen.LuxurySplash> {
        val context = androidx.compose.ui.platform.LocalContext.current
        val sessionManager = remember { com.nisr.sauservices.data.local.SessionManager(context) }
        
        LuxurySplashScreen(onFinished = {
            if (sessionManager.isLoggedIn()) {
                val hasPermission = ContextCompat.checkSelfPermission(
                    context, 
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                
                if (hasPermission) {
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
        com.nisr.sauservices.ui.auth.LoginScreen(navController)
    }
    
    composable<Screen.LuxurySignUp> {
        com.nisr.sauservices.ui.auth.SignUpScreen(navController, "customer")
    }
    
    composable<Screen.LuxuryForgotPassword> {
        com.nisr.sauservices.ui.auth.ForgotPasswordScreen(navController)
    }
    
    composable<Screen.ResetPassword> { backStackEntry ->
        val route: Screen.ResetPassword = backStackEntry.toRoute()
        com.nisr.sauservices.ui.auth.ResetPasswordScreen(navController, route.email)
    }

    composable<Screen.LuxuryProfile> {
        LuxuryProfileScreen(
            navController = navController,
            onSignOut = { navController.navigate(Screen.LuxurySignOut) },
            onBack = { navController.popBackStack() }
        )
    }

    composable<Screen.LuxurySignOut> {
        val context = androidx.compose.ui.platform.LocalContext.current
        val sessionManager = remember { com.nisr.sauservices.data.local.SessionManager(context) }
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
