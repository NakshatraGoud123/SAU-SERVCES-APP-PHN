package com.nisr.sauservices.ui.food

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.text.font.FontFamily
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.components.*
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.viewmodel.BookingItem
import com.nisr.sauservices.ui.viewmodel.BookingsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FoodSuccessScreen(navController: NavController, bookingsViewModel: BookingsViewModel) {
    val orderId = "FOOO${(1000..9999).random()}"
    
    LaunchedEffect(Unit) {
        bookingsViewModel.addBooking(
            BookingItem(
                id = orderId,
                serviceName = "Pizza House",
                date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
                time = "Today",
                status = "Upcoming",
                price = "₹264"
            )
        )
    }

    Surface(modifier = Modifier.fillMaxSize(), color = LuxuryBackground) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(100.dp).background(LuxuryGold.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, null, modifier = Modifier.size(60.dp), tint = LuxuryGold)
            }
            Spacer(Modifier.height(32.dp))
            Text(
                text = "Order Confirmed!", 
                color = LuxuryTextPrimary, 
                fontSize = 28.sp, 
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Serif
            )
            Text("Your food has been ordered successfully.", color = LuxuryTextSecondary, textAlign = TextAlign.Center)
            
            Spacer(Modifier.height(32.dp))
            LuxuryCard(modifier = Modifier.padding(horizontal = 8.dp)) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Order ID", color = LuxuryTextSecondary)
                        Text("#$orderId", color = LuxuryTextPrimary, fontWeight = FontWeight.Bold)
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Expected Time", color = LuxuryTextSecondary)
                        Text("30-40 mins", color = LuxuryTextPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(60.dp))
            LuxuryButton(
                text = "TRACK ORDER",
                onClick = { navController.navigate(Screen.FoodOrderTracking(orderId)) }
            )
            Spacer(Modifier.height(16.dp))
            TextButton(
                onClick = { 
                    navController.navigate(Screen.Home) { popUpTo(0) { inclusive = true } } 
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to Home", color = LuxuryTextSecondary, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
        }
    }
}
