package com.nisr.sauservices.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nisr.sauservices.ui.essentials.*
import com.nisr.sauservices.ui.viewmodel.BookingsViewModel
import com.nisr.sauservices.ui.viewmodel.CartViewModel

fun NavGraphBuilder.essentialsNavGraph(
    navController: NavController,
    cartViewModel: CartViewModel,
    bookingsViewModel: BookingsViewModel
) {
    composable(Routes.ESSENTIALS_MAIN) {
        HomeEssentialsMainScreen(navController, cartViewModel)
    }

    composable(
        route = Routes.ESSENTIALS_CATEGORY,
        arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
    ) { backStackEntry ->
        val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
        HomeEssentialsCategoryScreen(navController, categoryId)
    }

    composable(
        route = Routes.ESSENTIALS_ITEMS,
        arguments = listOf(navArgument("subcategoryId") { type = NavType.StringType })
    ) { backStackEntry ->
        val subcategoryId = backStackEntry.arguments?.getString("subcategoryId") ?: ""
        HomeEssentialsItemsScreen(navController, subcategoryId, cartViewModel)
    }

    composable(Routes.ESSENTIALS_CART) {
         HomeEssentialsCartScreen(navController, cartViewModel)
    }

    composable(Routes.ESSENTIALS_CHECKOUT) {
        HomeEssentialsCheckoutScreen(navController, cartViewModel)
    }

    composable(Routes.ESSENTIALS_SUCCESS) {
        HomeEssentialsSuccessScreen(navController, cartViewModel, bookingsViewModel)
    }
}
