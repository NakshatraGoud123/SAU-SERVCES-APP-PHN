package com.nisr.sauservices.ui.home

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.components.*

@Composable
fun BookingSuccessScreen(
    navController: NavController,
    message: String = "Your service booking has been confirmed. Our professional will reach out to you shortly."
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LuxuryBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(LuxuryGold.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = LuxuryGold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Booking Successful!",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = LuxuryTextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            fontSize = 16.sp,
            color = LuxuryTextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        LuxuryButton(
            text = "Back to Home",
            onClick = {
                navController.navigate(Screen.Home) {
                    popUpTo<Screen.Home> { inclusive = true }
                }
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedButton(
            onClick = {
                navController.navigate(Screen.Bookings) {
                    popUpTo<Screen.Home> { inclusive = false }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryGold),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = LuxuryGold)
        ) {
            Text("View My Bookings", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}
