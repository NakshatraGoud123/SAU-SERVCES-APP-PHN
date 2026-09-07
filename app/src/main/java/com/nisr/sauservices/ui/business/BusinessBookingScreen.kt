package com.nisr.sauservices.ui.business

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.nisr.sauservices.ui.theme.PinkPrimary
import com.nisr.sauservices.ui.viewmodel.BusinessViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessBookingScreen(navController: NavController, viewModel: BusinessViewModel) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val scrollState = rememberScrollState()

    val timeSlots = listOf("Morning (9AM-12PM)", "Evening (3PM-6PM)", "Afternoon (12PM-3PM)", "Night (6PM-9PM)")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking Details", fontWeight = FontWeight.Bold) },
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
                    navController.navigate(
                        Screen.ResidentialBookingDetails(
                            partnerId = "business",
                            serviceId = "business"
                        )
                    )
                },
                enabled = viewModel.selectedDate.value.isNotEmpty() && 
                          viewModel.selectedTime.value.isNotEmpty() && 
                          viewModel.customerAddress.value.isNotEmpty() && 
                          viewModel.phoneNumber.value.length >= 10,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PinkPrimary,
                    disabledContainerColor = Color(0xFFE0E0E0)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Review Order", 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        },
        containerColor = Color(0xFFFBFBFB)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Date Selection
            Column {
                Text("Select Date", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(12.dp))
                OutlinedCard(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder(true).copy(width = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = PinkPrimary)
                        Spacer(Modifier.width(16.dp))
                        Text(
                            text = if (viewModel.selectedDate.value.isEmpty()) "Choose a date" else viewModel.selectedDate.value,
                            fontSize = 16.sp,
                            color = if (viewModel.selectedDate.value.isEmpty()) Color.Gray else Color.Black
                        )
                    }
                }
            }

            // Time Slot Selection
            Column {
                Text("Select Time Slot", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    timeSlots.chunked(2).forEach { rowSlots ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            rowSlots.forEach { slot ->
                                val isSelected = viewModel.selectedTime.value == slot
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp)
                                        .background(if (isSelected) PinkPrimary.copy(alpha = 0.1f) else Color.White, RoundedCornerShape(8.dp))
                                        .border(1.dp, if (isSelected) PinkPrimary else Color.LightGray, RoundedCornerShape(8.dp))
                                        .clickable { viewModel.selectedTime.value = slot }
                                        .padding(horizontal = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = slot,
                                        fontSize = 12.sp,
                                        color = if (isSelected) PinkPrimary else Color.Black,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Address
            Column {
                Text("Service Address", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = viewModel.customerAddress.value,
                    onValueChange = { viewModel.customerAddress.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter your full address", fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = PinkPrimary) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PinkPrimary,
                        focusedLeadingIconColor = PinkPrimary
                    )
                )
            }

            // Phone
            Column {
                Text("Phone Number", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = viewModel.phoneNumber.value,
                    onValueChange = { if (it.length <= 10) viewModel.phoneNumber.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter 10-digit mobile number", fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = PinkPrimary) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PinkPrimary,
                        focusedLeadingIconColor = PinkPrimary
                    )
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
                        val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it))
                        viewModel.selectedDate.value = date
                    }
                    showDatePicker = false
                }) { Text("Confirm") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
