package com.nisr.sauservices.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.profile.*
import com.nisr.sauservices.ui.viewmodel.ProfileViewModel

/**
 * Navigation Graph for Profile and Settings related screens.
 */
fun NavGraphBuilder.profileNavGraph(
    navController: NavHostController,
    profileViewModel: ProfileViewModel,
    locationViewModel: com.nisr.sauservices.ui.viewmodel.LocationViewModel
) {
    composable<Screen.Profile> {
        ProfileScreen(navController, profileViewModel)
    }

    composable<Screen.EditProfile> {
        EditProfileScreen(navController, profileViewModel)
    }

    composable<Screen.Notifications> {
        NotificationsScreen(navController, profileViewModel)
    }

    composable<Screen.ShippingAddress> {
        ShippingAddressScreen(navController, profileViewModel, locationViewModel)
    }

    composable<Screen.ChangePassword> {
        ChangePasswordScreen(navController)
    }

    composable<Screen.AddAccounts> {
        AddAccountsScreen(navController)
    }

    composable<Screen.ContactUs> {
        ContactUsScreen(navController, profileViewModel)
    }

    composable<Screen.FAQ> {
        FAQScreen(navController)
    }

    composable<Screen.Wallet> {
        WalletScreen(navController)
    }

    composable<Screen.Settings> {
        SettingsScreen(navController)
    }
}
