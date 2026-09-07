package com.nisr.sauservices.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.data.repository.UserRepository
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.auth.SplashScreen
import com.nisr.sauservices.ui.viewmodel.*
import com.nisr.sauservices.ui.viewmodels.BookingViewModel
import com.nisr.sauservices.ui.viewmodels.CustomerViewModel

@Composable
fun AppNavHost(navController: NavHostController) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    
    val profileViewModel: ProfileViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel()
    val residentialViewModel: ResidentialViewModel = viewModel()
    val businessViewModel: BusinessViewModel = viewModel()
    val lifestyleViewModel: LifestyleViewModel = viewModel()
    val techViewModel: TechServicesViewModel = viewModel()
    val mensGroomingViewModel: MensGroomingViewModel = viewModel()
    val womensBeautyViewModel: WomensBeautyViewModel = viewModel()
    val healthViewModel: HealthcareViewModel = viewModel()
    val educationCartViewModel: EducationCartViewModel = viewModel()
    val bookingsViewModel: BookingsViewModel = viewModel()
    val foodCartViewModel: FoodCartViewModel = viewModel()
    val locationViewModel: LocationViewModel = viewModel()
    val trackingViewModel: TrackingViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()

    NavHost(navController, startDestination = Screen.LuxurySplash) {
        
        composable<Screen.Splash> {
            SplashScreen(onFinished = {
                if (sessionManager.isLoggedIn()) {
                    navController.navigate(Screen.Home) {
                        popUpTo<Screen.Splash> { inclusive = true }
                    }
                } else {
                    navController.navigate(Screen.Login()) {
                        popUpTo<Screen.Splash> { inclusive = true }
                    }
                }
            })
        }

        // Modular Navigation Graphs
        authNavGraph(navController)
        
        homeNavGraph(
            navController = navController,
            sessionManager = sessionManager,
            bookingsViewModel = bookingsViewModel,
            residentialViewModel = residentialViewModel,
            homeViewModel = homeViewModel,
            locationViewModel = locationViewModel
        )
        
        servicesNavGraph(
            navController = navController,
            residentialViewModel = residentialViewModel,
            businessViewModel = businessViewModel,
            lifestyleViewModel = lifestyleViewModel,
            techViewModel = techViewModel,
            mensGroomingViewModel = mensGroomingViewModel,
            womensBeautyViewModel = womensBeautyViewModel,
            healthViewModel = healthViewModel,
            homeCartViewModel = cartViewModel,
            educationCartViewModel = educationCartViewModel
        )
        
        bookingNavGraph(
            navController = navController,
            residentialViewModel = residentialViewModel,
            businessViewModel = businessViewModel,
            lifestyleViewModel = lifestyleViewModel,
            techViewModel = techViewModel,
            mensGroomingViewModel = mensGroomingViewModel,
            womensBeautyViewModel = womensBeautyViewModel,
            healthcareViewModel = healthViewModel,
            bookingsViewModel = bookingsViewModel,
            foodCartViewModel = foodCartViewModel,
            homeCartViewModel = cartViewModel,
            educationCartViewModel = educationCartViewModel
        )
        
        profileNavGraph(navController, profileViewModel)
        
        locationNavGraph(navController, locationViewModel, trackingViewModel)
        
        essentialsNavGraph(navController, cartViewModel, bookingsViewModel)
        
        foodNavGraph(navController, foodCartViewModel, bookingsViewModel)

        luxuryNavGraph(navController)
    }
}
