package com.nisr.sauservices.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.location.LocationPermissionScreen
import com.nisr.sauservices.ui.location.LocationPickerScreen
import com.nisr.sauservices.ui.location.OrderTrackingScreen
import com.nisr.sauservices.ui.viewmodel.LocationViewModel
import com.nisr.sauservices.ui.viewmodel.TrackingViewModel

/**
 * Modular Navigation Graph for Map and Location features.
 */
fun NavGraphBuilder.locationNavGraph(
    navController: NavHostController,
    locationViewModel: LocationViewModel,
    trackingViewModel: TrackingViewModel
) {
    composable<Screen.LocationPermission> {
        LocationPermissionScreen(navController = navController)
    }

    composable<Screen.MapPicker> {
        LocationPickerScreen(
            navController = navController,
            viewModel = locationViewModel
        )
    }
    
    composable<Screen.OrderTracking> { backStackEntry ->
        val route: Screen.OrderTracking = backStackEntry.toRoute()
        OrderTrackingScreen(
            navController = navController,
            orderId = route.orderId,
            viewModel = trackingViewModel
        )
    }
}
