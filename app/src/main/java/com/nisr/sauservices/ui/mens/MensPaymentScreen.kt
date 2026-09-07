package com.nisr.sauservices.ui.mens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.nisr.sauservices.ui.viewmodel.MensGroomingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MensPaymentScreen(navController: NavController, viewModel: MensGroomingViewModel) {
    val options = listOf(
        PaymentMethod("UPI", "Google Pay, PhonePe, UPI ID", Icons.Default.AccountBalance),
        PaymentMethod("Card", "Credit or Debit Cards", Icons.Default.CreditCard),
        PaymentMethod("Net Banking", "All major banks supported", Icons.Default.Language),
        PaymentMethod("Cash After Service", "Pay when service is complete", Icons.Default.Payments)
    )
    var selectedOption by remember { mutableStateOf(options[0]) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Button(
                onClick = { navController.navigate(Screen.MensSuccess) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Confirm Payment ₹${viewModel.getTotalPrice().toInt()}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
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
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.White
                    ),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(option.icon, contentDescription = null, tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(option.title, fontWeight = FontWeight.Bold, color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black)
                            Text(option.subtitle, fontSize = 12.sp, color = Color.Gray)
                        }
                        RadioButton(
                            selected = isSelected,
                            onClick = { selectedOption = option },
                            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
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
                    Text("Total to Pay", fontWeight = FontWeight.Medium)
                    Text("₹${viewModel.getTotalPrice().toInt()}", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, fontSize = 20.sp)
                }
            }
        }
    }
}

data class PaymentMethod(val title: String, val subtitle: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
