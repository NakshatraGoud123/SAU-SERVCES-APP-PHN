package com.nisr.sauservices.ui.womens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
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
import com.nisr.sauservices.ui.viewmodel.WomensBeautyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeautyBookingScreen(navController: NavController, viewModel: WomensBeautyViewModel) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val timeSlots = listOf("Morning (9AM-12PM)", "Afternoon (12PM-3PM)", "Evening (3PM-6PM)", "Night (6PM-9PM)")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = { navController.navigate(Screen.WomensBeautyPayment) },
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC2185B)),
                enabled = viewModel.selectedDate.value.isNotEmpty() && 
                          viewModel.selectedTimeSlot.value.isNotEmpty() && 
                          viewModel.customerAddress.value.isNotEmpty() && 
                          viewModel.phoneNumber.value.isNotEmpty()
            ) {
                Text("Proceed to Payment", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Date Selection
            Column {
                Text("Select Date", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
                OutlinedCard(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color(0xFFC2185B))
                        Spacer(Modifier.width(12.dp))
                        Text(if (viewModel.selectedDate.value.isEmpty()) "Choose a date" else viewModel.selectedDate.value)
                    }
                }
            }

            // Time Slot Selection
            Column {
                Text("Select Time Slot", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    timeSlots.chunked(2).forEach { rowSlots ->
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowSlots.forEach { slot ->
                                val isSelected = viewModel.selectedTimeSlot.value == slot
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(if (isSelected) Color(0xFFFCE4EC) else Color.White, RoundedCornerShape(8.dp))
                                        .border(1.dp, if (isSelected) Color(0xFFC2185B) else Color.LightGray, RoundedCornerShape(8.dp))
                                        .clickable { viewModel.selectedTimeSlot.value = slot }
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(slot, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) Color(0xFFC2185B) else Color.Black)
                                }
                            }
                        }
                    }
                }
            }

            // Address
            Column {
                Text("Service Address", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = viewModel.customerAddress.value,
                    onValueChange = { viewModel.customerAddress.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter your full address") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFC2185B)) },
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Phone
            Column {
                Text("Phone Number", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = viewModel.phoneNumber.value,
                    onValueChange = { if (it.length <= 10) viewModel.phoneNumber.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter 10-digit mobile number") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFFC2185B)) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val date = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date(it))
                        viewModel.selectedDate.value = date
                    }
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
