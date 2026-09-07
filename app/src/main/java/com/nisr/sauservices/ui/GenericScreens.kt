package com.nisr.sauservices.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SearchScreen(navController: NavController) {
    GenericPlaceholder("Search Screen", navController)
}

@Composable
fun BookingDetailsScreen(navController: NavController, bookingId: String) {
    GenericPlaceholder("Booking Details Screen\nBooking ID: $bookingId", navController)
}

@Composable
fun ReviewsScreen(navController: NavController, partnerId: String) {
    GenericPlaceholder("Reviews Screen\nPartner ID: $partnerId", navController)
}

@Composable
fun SettingsScreen(navController: NavController) {
    GenericPlaceholder("Settings Screen", navController)
}

@Composable
fun GenericPlaceholder(title: String, navController: NavController) {
    Scaffold { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Go Back")
            }
        }
    }
}
