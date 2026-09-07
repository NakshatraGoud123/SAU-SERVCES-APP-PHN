package com.nisr.sauservices.ui.food

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(navController: NavController, restaurantId: String) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout", color = LuxuryTextPrimary, fontWeight = FontWeight.Bold) },
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
                    onClick = { navController.navigate(Screen.FoodOrderSuccess) },
                    modifier = Modifier.padding(20.dp).fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LuxuryGold)
                ) {
                    Text("Place Order", color = LuxuryBackground, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Delivery Address
            LuxuryCheckoutSection("Delivery Address") {
                Column {
                    Text("Home", color = LuxuryTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("123, Main Road, Hyderabad", color = LuxuryTextSecondary, fontSize = 14.sp)
                }
            }

            // Delivery Time
            LuxuryCheckoutSection("Delivery Time") {
                Text("Today, 7:00 PM - 7:30 PM", color = LuxuryTextPrimary, fontWeight = FontWeight.Medium)
            }

            // Summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LuxuryCard),
                border = BorderStroke(1.dp, LuxuryBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Bill Details", color = LuxuryTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(16.dp))
                    LuxuryFoodBillRow("Item Total", "₹239")
                    LuxuryFoodBillRow("Delivery Fee", "₹25")
                    HorizontalDivider(Modifier.padding(vertical = 12.dp), color = LuxuryBorder)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Amount", color = LuxuryTextPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        Text("₹264", color = LuxuryGold, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun LuxuryCheckoutSection(title: String, content: @Composable () -> Unit) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(title, color = LuxuryTextSecondary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("Change", color = LuxuryGold, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.clickable { })
        }
        Spacer(Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = LuxuryCard),
            border = BorderStroke(1.dp, LuxuryBorder)
        ) {
            Box(Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun LuxuryFoodBillRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = LuxuryTextSecondary, fontSize = 14.sp)
        Text(value, color = LuxuryTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}
