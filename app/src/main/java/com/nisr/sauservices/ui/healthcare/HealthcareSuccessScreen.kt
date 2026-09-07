package com.nisr.sauservices.ui.healthcare

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.viewmodel.HealthcareViewModel

@Composable
fun HealthcareSuccessScreen(navController: NavController, viewModel: HealthcareViewModel) {
    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(100.dp).background(Color(0xFFE8F5E9), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(60.dp), tint = Color(0xFF43A047))
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text("Booking Confirmed!", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1976D2))
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                "Your healthcare request for ${viewModel.patientName.value} has been successfully placed. Our professional will visit your location on ${viewModel.selectedDate.value} during the ${viewModel.selectedTimeSlot.value} slot.",
                fontSize = 15.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Button(
                onClick = { navController.navigate(Screen.HealthcareOrderTracking) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
            ) {
                Text("Track Your Appointment", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedButton(
                onClick = { 
                    viewModel.clearCart()
                    navController.navigate(Screen.Home) {
                        popUpTo<Screen.Home> { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1976D2))
            ) {
                Text("Back to Home", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
            }
        }
    }
}
