package com.nisr.sauservices.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Wallet
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
import com.nisr.sauservices.ui.theme.PinkPrimary
import com.nisr.sauservices.ui.viewmodel.NewBookingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingSummaryScreen(
    navController: NavController,
    viewModel: NewBookingsViewModel,
    name: String,
    date: String,
    time: String,
    qty: Int,
    price: String,
    cat: String,
    sub: String
) {
    var selectedPayment by remember { mutableStateOf("UPI") }
    var address by remember { mutableStateOf("") }
    
    val priceDouble = price.replace("₹", "").split("–").first().trim().filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 0.0
    val totalAmount = priceDouble * qty
    
    val bookingStatus by viewModel.bookingStatus.collectAsState()
    var showSuccess by remember { mutableStateOf(false) }
    var generatedId by remember { mutableStateOf("") }

    LaunchedEffect(bookingStatus) {
        bookingStatus?.let {
            if (it.isSuccess) {
                generatedId = it.getOrNull() ?: "ORD${System.currentTimeMillis() % 1000000}"
                showSuccess = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout Booking", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    if (address.isNotBlank()) {
                        viewModel.confirmBooking(name, cat, sub, date, time, price, qty, selectedPayment, address)
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                enabled = address.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
            ) {
                Text("Pay & Confirm Booking", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            Text("Service Summary", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(text = name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(text = "$cat > $sub", color = Color.Gray, fontSize = 14.sp)
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Date:")
                        Text(date, fontWeight = FontWeight.SemiBold)
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Time:")
                        Text(time, fontWeight = FontWeight.SemiBold)
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Quantity:")
                        Text(qty.toString(), fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Delivery/Service Address", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                placeholder = { Text("Enter full address") },
                leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = PinkPrimary) },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(16.dp))
            Text("Payment Options", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            
            PaymentOption("UPI", Icons.Default.Payments, selectedPayment == "UPI") { selectedPayment = "UPI" }
            PaymentOption("Credit/Debit Card", Icons.Default.CreditCard, selectedPayment == "Card") { selectedPayment = "Card" }
            PaymentOption("Wallet", Icons.Default.Wallet, selectedPayment == "Wallet") { selectedPayment = "Wallet" }

            Spacer(Modifier.height(24.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total Amount", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("₹$totalAmount", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = PinkPrimary)
            }
        }
    }

    if (showSuccess) {
        OrderSuccessDialog(
            orderId = generatedId,
            onViewOrder = {
                showSuccess = false
                viewModel.resetStatus()
                navController.navigate(Screen.MyBookings) {
                    popUpTo<Screen.Home> { inclusive = false }
                }
            },
            onGoHome = {
                showSuccess = false
                viewModel.resetStatus()
                navController.navigate(Screen.Home) {
                    popUpTo(0) { inclusive = true }
                }
            }
        )
    }
}

@Composable
fun PaymentOption(name: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isSelected: Boolean, onSelect: () -> Unit) {
    OutlinedCard(
        onClick = onSelect,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (isSelected) PinkPrimary.copy(alpha = 0.1f) else Color.Transparent
        ),
        border = CardDefaults.outlinedCardBorder(enabled = true).copy(
            brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) PinkPrimary else Color.LightGray)
        )
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = if (isSelected) PinkPrimary else Color.Gray)
            Spacer(Modifier.width(16.dp))
            Text(name, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
            Spacer(Modifier.weight(1f))
            RadioButton(selected = isSelected, onClick = null, colors = RadioButtonDefaults.colors(selectedColor = PinkPrimary))
        }
    }
}
