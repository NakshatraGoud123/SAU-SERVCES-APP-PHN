package com.nisr.sauservices.ui.lifestyle

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.PinkPrimary
import com.nisr.sauservices.ui.theme.LightPink
import com.nisr.sauservices.ui.viewmodel.LifestyleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifestylePaymentScreen(navController: NavController, viewModel: LifestyleViewModel) {
    val options = listOf(
        LifestylePaymentOption("UPI", "Google Pay, PhonePe, UPI ID", Icons.Default.Payments),
        LifestylePaymentOption("Card", "Credit or Debit Cards", Icons.Default.CreditCard),
        LifestylePaymentOption("Net Banking", "All major banks supported", Icons.Default.AccountBalance),
        LifestylePaymentOption("Cash After Service", "Pay when service is complete", Icons.Default.Payments)
    )
    var selectedOption by remember { mutableStateOf(options[0]) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = { navController.navigate(Screen.LifestyleSuccess) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Confirm & Pay", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        containerColor = Color(0xFFF7F7F7)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Select Payment Method", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))

            options.forEach { option ->
                val isSelected = selectedOption == option
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { selectedOption = option },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) LightPink else Color.White
                    ),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, PinkPrimary) else null
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(option.icon, contentDescription = null, tint = if (isSelected) PinkPrimary else Color.Gray)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(option.title, fontWeight = FontWeight.Bold, color = if (isSelected) PinkPrimary else Color.Black)
                            Text(option.subtitle, fontSize = 12.sp, color = Color.Gray)
                        }
                        RadioButton(
                            selected = isSelected,
                            onClick = { selectedOption = option },
                            colors = RadioButtonDefaults.colors(selectedColor = PinkPrimary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Payable Amount", fontWeight = FontWeight.Medium)
                    val totalWithTax = viewModel.getTotalPrice() * 1.18
                    Text("₹${totalWithTax.toInt()}", fontWeight = FontWeight.ExtraBold, color = PinkPrimary, fontSize = 20.sp)
                }
            }
        }
    }
}

data class LifestylePaymentOption(val title: String, val subtitle: String, val icon: ImageVector)
