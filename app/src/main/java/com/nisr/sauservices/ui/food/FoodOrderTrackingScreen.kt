package com.nisr.sauservices.ui.food

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodOrderTrackingScreen(navController: NavController, orderId: String) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Tracking", color = LuxuryTextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LuxuryTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxuryBackground)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 16.dp,
                color = LuxuryCard,
                border = BorderStroke(1.dp, LuxuryBorder),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Button(
                    onClick = { /* TODO: Call support */ },
                    modifier = Modifier.padding(20.dp).fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LuxuryCard),
                    border = BorderStroke(1.dp, LuxuryGold)
                ) {
                    Icon(Icons.Default.Phone, null, tint = LuxuryGold)
                    Spacer(Modifier.width(12.dp))
                    Text("Call Delivery Partner", color = LuxuryGold, fontWeight = FontWeight.ExtraBold)
                }
            }
        },
        containerColor = LuxuryBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text("Order ID: #$orderId", fontSize = 14.sp, color = LuxuryTextSecondary)

            // Restaurant Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(50.dp).clip(CircleShape).background(LuxuryGold.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Restaurant, null, tint = LuxuryGold, modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("Pizza House", color = LuxuryTextPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    Text("Order Placed", color = LuxuryTextSecondary, fontSize = 14.sp)
                }
                Spacer(Modifier.weight(1f))
                Text("07:05 PM", fontSize = 12.sp, color = LuxuryTextSecondary)
            }

            // Progress List
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                LuxuryTrackingStep("Order Confirmed", "07:06 PM", true)
                LuxuryTrackingStep("Preparing Your Order", "07:12 PM", true)
                LuxuryTrackingStep("Out for Delivery", "07:25 PM", true)
                LuxuryTrackingStep("Delivered", "--:--", false)
            }

            Spacer(Modifier.height(24.dp))
            Text("You will be notified when your order arrives.", textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = LuxuryTextSecondary, fontSize = 14.sp, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun LuxuryTrackingStep(title: String, time: String, isDone: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isDone) LuxuryGold else LuxuryBorder,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(16.dp))
        Text(title, fontWeight = if (isDone) FontWeight.Bold else FontWeight.Normal, color = if (isDone) LuxuryTextPrimary else LuxuryTextSecondary, fontSize = 15.sp)
        Spacer(Modifier.weight(1f))
        Text(time, fontSize = 12.sp, color = LuxuryTextSecondary)
    }
}
