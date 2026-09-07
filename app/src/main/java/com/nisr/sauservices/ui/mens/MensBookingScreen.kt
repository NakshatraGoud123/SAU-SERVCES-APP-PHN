package com.nisr.sauservices.ui.mens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Schedule
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
fun MensBookingScreen(navController: NavController, viewModel: MensGroomingViewModel) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking Details", fontWeight = FontWeight.Bold) },
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
                onClick = {
                    if (viewModel.selectedDate.value.isNotEmpty() && 
                        viewModel.selectedTime.value.isNotEmpty() &&
                        viewModel.customerAddress.value.isNotEmpty() &&
                        viewModel.phoneNumber.value.isNotEmpty()) {
                        navController.navigate(Screen.MensCheckout)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = viewModel.selectedDate.value.isNotEmpty() && 
                          viewModel.selectedTime.value.isNotEmpty() &&
                          viewModel.customerAddress.value.isNotEmpty() &&
                          viewModel.phoneNumber.value.isNotEmpty()
            ) {
                Text("Confirm Details", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color(0xFFF7F7F7)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Schedule Your Service", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            // Date Selection
            BookingField(
                label = "Select Date",
                value = viewModel.selectedDate.value,
                icon = Icons.Default.CalendarMonth,
                placeholder = "DD/MM/YYYY",
                onValueChange = { viewModel.selectedDate.value = it }
            )

            // Time Selection
            BookingField(
                label = "Select Time",
                value = viewModel.selectedTime.value,
                icon = Icons.Default.Schedule,
                placeholder = "HH:MM AM/PM",
                onValueChange = { viewModel.selectedTime.value = it }
            )

            Text("Service Location", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            // Address
            BookingField(
                label = "Full Address",
                value = viewModel.customerAddress.value,
                icon = Icons.Default.LocationOn,
                placeholder = "Enter your complete address",
                onValueChange = { viewModel.customerAddress.value = it },
                singleLine = false
            )

            // Phone
            BookingField(
                label = "Contact Number",
                value = viewModel.phoneNumber.value,
                icon = Icons.Default.Phone,
                placeholder = "Enter 10-digit number",
                onValueChange = { viewModel.phoneNumber.value = it }
            )
        }
    }
}

@Composable
fun BookingField(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    placeholder: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true
) {
    Column {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, fontSize = 14.sp) },
            leadingIcon = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            singleLine = singleLine,
            minLines = if (singleLine) 1 else 3
        )
    }
}
