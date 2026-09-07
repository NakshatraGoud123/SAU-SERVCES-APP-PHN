package com.nisr.sauservices.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.nisr.sauservices.ui.auth.*
import com.nisr.sauservices.ui.onboarding.OnboardingScreen
import com.nisr.sauservices.ui.Screen

fun NavGraphBuilder.authNavGraph(navController: NavController) {
    composable<Screen.Onboarding> {
        OnboardingScreen(navController)
    }
    
    composable<Screen.RoleSelection> {
        // Redirect to Login as customer
        LaunchedEffect(Unit) {
            navController.navigate(Screen.Login("customer")) {
                popUpTo<Screen.RoleSelection> { inclusive = true }
            }
        }
    }
    
    composable<Screen.AuthOptions> { backStackEntry ->
        val route: Screen.AuthOptions = backStackEntry.toRoute()
        SignInScreen(navController, route.role)
    }
    
    composable<Screen.Login> {
        LoginScreen(navController) 
    }
    
    composable<Screen.Register> { backStackEntry ->
        val route: Screen.Register = backStackEntry.toRoute()
        SignUpScreen(navController, route.role)
    }
    
    composable<Screen.ForgotPassword> {
        ForgotPasswordScreen(navController)
    }

    composable<Screen.ResetPassword> { backStackEntry ->
        val route: Screen.ResetPassword = backStackEntry.toRoute()
        ResetPasswordScreen(navController, route.email)
    }
}
