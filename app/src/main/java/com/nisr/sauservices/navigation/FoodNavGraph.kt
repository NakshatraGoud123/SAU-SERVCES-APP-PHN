package com.nisr.sauservices.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.nisr.sauservices.ui.food.*
import com.nisr.sauservices.ui.viewmodel.BookingsViewModel
import com.nisr.sauservices.ui.viewmodel.FoodCartViewModel
import com.nisr.sauservices.ui.Screen

fun NavGraphBuilder.foodNavGraph(
    navController: NavController,
    foodCartViewModel: FoodCartViewModel,
    bookingsViewModel: BookingsViewModel
) {
    composable<Screen.FoodCategories> {
        FoodMainScreen(navController)
    }

    composable<Screen.FoodSubCategory> { backStackEntry ->
        val route: Screen.FoodSubCategory = backStackEntry.toRoute()
        FoodSubCategoryScreen(navController, route.category)
    }

    composable<Screen.FoodTypes> { backStackEntry ->
        val route: Screen.FoodTypes = backStackEntry.toRoute()
        FoodTypeScreen(navController, route.subcategory)
    }

    composable<Screen.FoodSubType> { backStackEntry ->
        val route: Screen.FoodSubType = backStackEntry.toRoute()
        FoodSubTypeScreen(navController, route.typeName)
    }

    composable<Screen.FoodItems> { backStackEntry ->
        val route: Screen.FoodItems = backStackEntry.toRoute()
        FoodItemsScreen(navController, route.restaurantId, foodCartViewModel)
    }

    composable<Screen.FoodCart> {
        FoodCartScreen(navController, foodCartViewModel)
    }

    composable<Screen.FoodBooking> { backStackEntry ->
        val route: Screen.FoodBooking = backStackEntry.toRoute()
        BookingScreen(navController, route.restaurantId)
    }

    composable<Screen.FoodOrderSuccess> {
        FoodSuccessScreen(navController, bookingsViewModel)
    }

    composable<Screen.FoodOrderTracking> { backStackEntry ->
        val route: Screen.FoodOrderTracking = backStackEntry.toRoute()
        FoodOrderTrackingScreen(navController, route.orderId)
    }
}
