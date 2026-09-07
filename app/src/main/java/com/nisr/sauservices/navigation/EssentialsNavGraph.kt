package com.nisr.sauservices.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.essentials.*
import com.nisr.sauservices.ui.viewmodel.BookingsViewModel
import com.nisr.sauservices.ui.viewmodel.CartViewModel

fun NavGraphBuilder.essentialsNavGraph(
    navController: NavController,
    cartViewModel: CartViewModel,
    bookingsViewModel: BookingsViewModel
) {
    composable<Screen.HomeEssentialsMain> {
        HomeEssentialsMainScreen(navController, cartViewModel)
    }

    composable<Screen.HomeEssentialsCategory> { backStackEntry ->
        val route: Screen.HomeEssentialsCategory = backStackEntry.toRoute()
        HomeEssentialsCategoryScreen(navController, route.categoryId, cartViewModel)
    }

    composable<Screen.HomeEssentialsItems> { backStackEntry ->
        val route: Screen.HomeEssentialsItems = backStackEntry.toRoute()
        HomeEssentialsCategoryScreen(navController, route.subcategoryId, cartViewModel)
    }

    composable<Screen.HomeEssentialsCheckout> {
        HomeEssentialsCheckoutScreen(navController, cartViewModel)
    }

    composable<Screen.HomeEssentialsSuccess> {
        HomeEssentialsSuccessScreen(navController)
    }
}
