package com.nisr.sauservices.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.luxury.*

fun NavGraphBuilder.luxuryNavGraph(navController: NavController) {
    composable<Screen.LuxurySplash> {
        val context = androidx.compose.ui.platform.LocalContext.current
        val sessionManager = remember { com.nisr.sauservices.data.local.SessionManager(context) }
        
        LuxurySplashScreen(onFinished = {
            if (sessionManager.isLoggedIn()) {
                navController.navigate(Screen.Home) {
                    popUpTo<Screen.LuxurySplash> { inclusive = true }
                }
            } else {
                navController.navigate(Screen.LuxuryOnboarding1) {
                    popUpTo<Screen.LuxurySplash> { inclusive = true }
                }
            }
        })
    }
    
    composable<Screen.LuxuryOnboarding1> {
        LuxuryOnboardingScreen1(
            onNext = { navController.navigate(Screen.LuxuryOnboarding2) },
            onSkip = { navController.navigate(Screen.LuxuryLogin) }
        )
    }
    
    composable<Screen.LuxuryOnboarding2> {
        LuxuryOnboardingScreen2(
            onNext = { navController.navigate(Screen.LuxuryOnboarding3) },
            onSkip = { navController.navigate(Screen.LuxuryLogin) }
        )
    }
    
    composable<Screen.LuxuryOnboarding3> {
        LuxuryOnboardingScreen3(
            onNext = { navController.navigate(Screen.LuxuryLogin) },
            onSkip = { navController.navigate(Screen.LuxuryLogin) }
        )
    }
    
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
            onSignOut = { navController.navigate(Screen.LuxurySignOut) },
            onBack = { navController.popBackStack() }
        )
    }

    composable<Screen.LuxurySignOut> {
        val context = androidx.compose.ui.platform.LocalContext.current
        val sessionManager = remember { com.nisr.sauservices.data.local.SessionManager(context) }
        
        LuxurySignOutConfirmation(
            onConfirm = { 
                sessionManager.saveLoginState(false)
                navController.navigate(Screen.LuxuryLogin) {
                    popUpTo(0)
                }
            },
            onCancel = { navController.popBackStack() }
        )
    }
}
