package com.nisr.sauservices.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.nisr.sauservices.ui.home.*
import com.nisr.sauservices.ui.payment.*
import com.nisr.sauservices.ui.viewmodel.*
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.BookingDetailsScreen
import com.nisr.sauservices.ui.residential.ResidentialBookingDetailsScreen
import com.nisr.sauservices.ui.essentials.*

fun NavGraphBuilder.bookingNavGraph(
    navController: NavController,
    residentialViewModel: ResidentialViewModel,
    businessViewModel: BusinessViewModel,
    lifestyleViewModel: LifestyleViewModel,
    techViewModel: TechServicesViewModel,
    mensGroomingViewModel: MensGroomingViewModel,
    womensBeautyViewModel: WomensBeautyViewModel,
    healthcareViewModel: HealthcareViewModel,
    bookingsViewModel: BookingsViewModel,
    foodCartViewModel: FoodCartViewModel,
    homeCartViewModel: CartViewModel,
    educationCartViewModel: EducationCartViewModel
) {
    // Unified Bookings List
    composable<Screen.Bookings> {
        BookingsScreen(navController, bookingsViewModel)
    }

    // Unified Cart for all Services
    composable<Screen.Cart> {
        UnifiedCartScreen(
            navController = navController,
            residentialViewModel = residentialViewModel,
            businessViewModel = businessViewModel,
            lifestyleViewModel = lifestyleViewModel,
            techViewModel = techViewModel,
            mensGroomingViewModel = mensGroomingViewModel,
            womensBeautyViewModel = womensBeautyViewModel,
            healthcareViewModel = healthcareViewModel,
            foodCartViewModel = foodCartViewModel,
            homeCartViewModel = homeCartViewModel,
            educationViewModel = educationCartViewModel
        )
    }

    // Essentials Checkout
    composable<Screen.HomeEssentialsCheckout> {
        HomeEssentialsCheckoutScreen(navController, homeCartViewModel)
    }

    composable<Screen.HomeEssentialsSuccess> {
        HomeEssentialsSuccessScreen(navController)
    }
    composable<Screen.ResidentialBookingDetails> { backStackEntry ->
        val route: Screen.ResidentialBookingDetails = backStackEntry.toRoute()
        ResidentialBookingDetailsScreen(
            navController = navController, 
            viewModel = residentialViewModel,
            partnerId = route.partnerId,
            serviceId = route.serviceId
        )
    }

    composable<Screen.ResidentialPayment> { backStackEntry ->
        val route: Screen.ResidentialPayment = backStackEntry.toRoute()
        ResidentialPaymentScreen(
            navController = navController, 
            viewModel = residentialViewModel,
            partnerId = route.partnerId,
            serviceId = route.serviceId
        )
    }

    composable<Screen.ResidentialOrderSummary> { backStackEntry ->
        val route: Screen.ResidentialOrderSummary = backStackEntry.toRoute()
        ResidentialOrderSummaryScreen(
            navController = navController,
            viewModel = residentialViewModel,
            bookingsViewModel = bookingsViewModel,
            businessViewModel = businessViewModel,
            lifestyleViewModel = lifestyleViewModel,
            techViewModel = techViewModel,
            mensGroomingViewModel = mensGroomingViewModel,
            womensBeautyViewModel = womensBeautyViewModel,
            healthcareViewModel = healthcareViewModel,
            foodCartViewModel = foodCartViewModel,
            homeCartViewModel = homeCartViewModel,
            educationViewModel = educationCartViewModel,
            partnerId = route.partnerId,
            serviceId = route.serviceId
        )
    }

    composable<Screen.ResidentialSuccess> {
        BookingSuccessScreen(navController)
    }

    // Booking Details & Success Flow
    composable<Screen.BookingConfirmation> {
        BookingSuccessScreen(navController, "Your booking is confirmed!")
    }

    composable<Screen.MyBookings> {
        MyOrdersScreen(navController)
    }

    composable<Screen.BookingDetails> { backStackEntry ->
        val route: Screen.BookingDetails = backStackEntry.toRoute()
        BookingDetailsScreen(navController, route.bookingId)
    }

    // --- PAYMENT FLOW ---
    composable<Screen.PaymentMethod> { backStackEntry ->
        val dest: Screen.PaymentMethod = backStackEntry.toRoute()
        PaymentMethodScreen(navController, dest.bookingId, dest.customerId, dest.partnerId, dest.amount)
    }

    composable<Screen.CashSuccess> { backStackEntry ->
        val dest: Screen.CashSuccess = backStackEntry.toRoute()
        CashBookingSuccessScreen(navController, dest.paymentId, dest.amount)
    }

    composable<Screen.CashCollection> { backStackEntry ->
        val dest: Screen.CashCollection = backStackEntry.toRoute()
        CashCollectionScreen(navController, dest.paymentId, dest.bookingId, dest.amount)
    }

    composable<Screen.CustomerOtp> { backStackEntry ->
        val dest: Screen.CustomerOtp = backStackEntry.toRoute()
        CustomerOtpScreen(navController, dest.paymentId, dest.bookingId, dest.amount)
    }

    composable<Screen.PaidSuccess> { backStackEntry ->
        val dest: Screen.PaidSuccess = backStackEntry.toRoute()
        DigitalPaymentSuccessScreen(navController, dest.amount)
    }
}
