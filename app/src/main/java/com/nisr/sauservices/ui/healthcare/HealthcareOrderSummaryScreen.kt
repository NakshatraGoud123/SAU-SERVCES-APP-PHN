package com.nisr.sauservices.ui.healthcare

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.viewmodel.HealthcareViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthcareOrderSummaryScreen(navController: NavController, viewModel: HealthcareViewModel) {
    val totalAmount = viewModel.calculateTotal()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Review Order", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = { navController.navigate(Screen.HealthcareSuccess) },
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
            ) {
                Text("Confirm & Pay", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Service List
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Services & Items", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1976D2))
                    Spacer(Modifier.height(12.dp))
                    viewModel.cartItems.forEach { item ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${item.name} x${item.quantity}", fontSize = 14.sp, modifier = Modifier.weight(1f))
                            Text("₹${(item.price * item.quantity).toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Amount", fontWeight = FontWeight.Bold)
                        Text("₹${totalAmount.toInt()}", fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
                    }
                }
            }

            // Booking Details
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Appointment & Patient", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1976D2))
                    Spacer(Modifier.height(12.dp))
                    HealthDetailRow("Patient", viewModel.patientName.value)
                    HealthDetailRow("Date", viewModel.selectedDate.value)
                    HealthDetailRow("Time Slot", viewModel.selectedTimeSlot.value)
                    HealthDetailRow("Address", viewModel.customerAddress.value)
                    HealthDetailRow("Emergency Phone", viewModel.phoneNumber.value)
                    HealthDetailRow("Payment", viewModel.selectedPaymentMethod.value)
                    if (viewModel.needsPrescription()) {
                        HealthDetailRow("Prescription", "Uploaded ✅")
                    }
                }
            }
        }
    }
}

@Composable
fun HealthDetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
    }
}
