package com.nisr.sauservices.navigation

import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.home.*
import com.nisr.sauservices.ui.pls.*
import com.nisr.sauservices.ui.dashboard.CustomerHomeScreen
import com.nisr.sauservices.ui.viewmodel.BookingsViewModel
import com.nisr.sauservices.ui.viewmodel.ChatViewModel
import com.nisr.sauservices.ui.viewmodel.ProfileViewModel
import com.nisr.sauservices.ui.viewmodel.ResidentialViewModel
import com.nisr.sauservices.ui.viewmodel.HomeViewModel
import com.nisr.sauservices.ui.viewmodel.LocationViewModel
import com.nisr.sauservices.ui.viewmodel.SearchViewModel
import com.nisr.sauservices.ui.viewmodel.VendorsViewModel
import com.nisr.sauservices.ui.viewmodels.PropertyLifestyleViewModel

fun NavGraphBuilder.homeNavGraph(
    navController: NavController,
    sessionManager: SessionManager,
    bookingsViewModel: BookingsViewModel,
    residentialViewModel: ResidentialViewModel,
    homeViewModel: HomeViewModel,
    locationViewModel: LocationViewModel,
    chatViewModel: ChatViewModel,
    profileViewModel: ProfileViewModel
) {
    composable<Screen.Home> {
        SauHomeScreen(
            navController = navController, 
            viewModel = homeViewModel, 
            bookingsViewModel = bookingsViewModel, 
            sessionManager = sessionManager,
            locationViewModel = locationViewModel,
            profileViewModel = profileViewModel
        )
    }

    composable<Screen.Search> {
        val searchViewModel: SearchViewModel = viewModel()
        SearchResultsScreen(navController, "", searchViewModel)
    }

    composable<Screen.Categories> {
        CategoriesScreen(navController)
    }

    composable<Screen.SearchResults> { backStackEntry ->
        val route: Screen.SearchResults = backStackEntry.toRoute()
        val searchViewModel: SearchViewModel = viewModel()
        SearchResultsScreen(navController, route.query, searchViewModel)
    }

    composable<Screen.MerchantShop> { backStackEntry ->
        val route: Screen.MerchantShop = backStackEntry.toRoute()
        val cartViewModel: com.nisr.sauservices.ui.viewmodel.CartViewModel = viewModel()
        MerchantShopScreen(navController, route.vendorId, cartViewModel)
    }

    composable<Screen.Chat> { backStackEntry ->
        val route: Screen.Chat = backStackEntry.toRoute()
        ChatScreen(navController, route.orderId, route.receiverId, route.receiverName, chatViewModel)
    }

    composable<Screen.CategoryVendors> { backStackEntry ->
        val route: Screen.CategoryVendors = backStackEntry.toRoute()
        CategoryVendorListScreen(navController, route.category)
    }

    composable<Screen.SuccessCelebration> { backStackEntry ->
        val route: Screen.SuccessCelebration = backStackEntry.toRoute()
        SuccessAnimationScreen {
            navController.navigate(Screen.OrderTracking(route.orderId)) {
                popUpTo(Screen.Home) { inclusive = false }
            }
        }
    }

    composable<Screen.MerchantMap> {
        val vendorsViewModel: VendorsViewModel = viewModel()
        MerchantMapScreen(navController, vendorsViewModel)
    }
    
    // Property & Lifestyle Services (PLS)
    composable<Screen.PLSMain> { PLSMainScreen(navController) }
    
    composable<Screen.PLSSubcategories> { backStackEntry ->
        val route: Screen.PLSSubcategories = backStackEntry.toRoute()
        PLSSubcategoriesScreen(navController, route.category)
    }

    composable<Screen.PLSServices> { backStackEntry ->
        val route: Screen.PLSServices = backStackEntry.toRoute()
        PLSServicesScreen(navController, route.subcategory, viewModel())
    }

    composable<Screen.PLSBooking> { backStackEntry ->
        val route: Screen.PLSBooking = backStackEntry.toRoute()
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(Screen.PLSMain)
        }
        val plsViewModel: PropertyLifestyleViewModel = viewModel(parentEntry)
        PLSBookingScreen(navController, route.serviceId, plsViewModel, sessionManager)
    }

    composable<Screen.PLSCheckout> { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(Screen.PLSMain)
        }
        val plsViewModel: PropertyLifestyleViewModel = viewModel(parentEntry)
        PLSCheckoutScreen(navController, plsViewModel)
    }

    composable<Screen.PLSSuccess> { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(Screen.PLSMain)
        }
        val plsViewModel: PropertyLifestyleViewModel = viewModel(parentEntry)
        PLSSuccessScreen(navController, plsViewModel)
    }
}
