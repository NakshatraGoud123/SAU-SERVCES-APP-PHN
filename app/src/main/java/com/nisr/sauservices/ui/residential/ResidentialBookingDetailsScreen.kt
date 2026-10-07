package com.nisr.sauservices.ui.residential

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.components.LuxuryButton
import com.nisr.sauservices.ui.viewmodel.ResidentialViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// ============================================================
// LUXE BRAND COLORS (Local precision match with Order Summary)
// ============================================================
private val LuxeBackground = Color(0xFFFDFBFA)
private val LuxeCardColor = Color(0xFFFFFFFF)
private val LuxeTextPrimary = Color(0xFF423F3D)
private val LuxeTextSecondary = Color(0xFF8D7F77)
private val LuxeAccentSage = Color(0xFF96A68F)
private val LuxeHighlightChampagne = Color(0xFFF5E6D3)
private val LuxeBorder = Color(0xFFEFE9E4)
private val LuxeGold = Color(0xFFE8C66A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResidentialBookingDetailsScreen(
    navController: NavController,
    viewModel: ResidentialViewModel,
    partnerId: String,
    serviceId: String
) {
    val bookingDetails by viewModel.bookingDetails.collectAsState()

    var address by remember { mutableStateOf(bookingDetails.address) }
    var phone by remember { mutableStateOf(bookingDetails.phone) }
    var date by remember { mutableStateOf(bookingDetails.date) }
    var selectedSlot by remember { mutableStateOf(bookingDetails.timeSlot) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showValidationError by remember { mutableStateOf(false) }

    val slots = listOf(
        "Morning: 9AM–12PM",
        "Afternoon: 12PM–3PM",
        "Evening: 3PM–6PM",
        "Night: 6PM–9PM"
    )

    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val calendar = Calendar.getInstance().apply { timeInMillis = millis }
                            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            val selectedDate = formatter.format(calendar.time)
                            date = selectedDate
                            viewModel.setDate(selectedDate)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK", color = LuxeGold, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = LuxeTextSecondary)
                }
            },
            colors = DatePickerDefaults.colors(containerColor = LuxeCardColor)
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = LuxeCardColor,
                    titleContentColor = LuxeGold,
                    headlineContentColor = LuxeTextPrimary,
                    weekdayContentColor = LuxeTextSecondary,
                    subheadContentColor = LuxeTextSecondary,
                    yearContentColor = LuxeTextSecondary,
                    currentYearContentColor = LuxeGold,
                    selectedYearContentColor = LuxeBackground,
                    selectedYearContainerColor = LuxeGold,
                    dayContentColor = LuxeTextPrimary,
                    selectedDayContentColor = LuxeBackground,
                    selectedDayContainerColor = LuxeGold,
                    todayContentColor = LuxeGold,
                    todayDateBorderColor = LuxeGold
                )
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Booking Details", 
                        color = LuxeTextPrimary, 
                        fontWeight = FontWeight.Black, 
                        fontFamily = FontFamily.Serif 
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LuxeTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxeBackground)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = LuxeCardColor,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                border = BorderStroke(1.dp, LuxeBorder)
            ) {
                LuxuryButton(
                    text = "Proceed to Payment",
                    onClick = {
                        val validPhone = phone.length == 10
                        if (address.isNotBlank() && validPhone && date.isNotBlank() && selectedSlot.isNotBlank()) {
                            showValidationError = false
                            navController.navigate(
                                Screen.ResidentialPayment(
                                    partnerId = partnerId,
                                    serviceId = serviceId
                                )
                            )
                        } else {
                            showValidationError = true
                        }
                    },
                    showArrow = true,
                    modifier = Modifier.padding(24.dp).height(56.dp)
                )
            }
        },
        containerColor = LuxeBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // --------------------------------------------------
            // SERVICE INFORMATION CARD
            // --------------------------------------------------
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = LuxeCardColor,
                border = BorderStroke(1.dp, LuxeBorder)
            ) {
                Row(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(LuxeHighlightChampagne),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CalendarToday, null, tint = LuxeGold)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Professional Home Service",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = LuxeTextPrimary
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            if (partnerId.isBlank()) "Will be assigned to first available expert" else "Assigned Expert Partner",
                            fontSize = 13.sp,
                            color = LuxeTextSecondary
                        )
                    }
                }
            }

            // --------------------------------------------------
            // SERVICE LOCATION CARD
            // --------------------------------------------------
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = LuxeCardColor,
                border = BorderStroke(1.dp, LuxeBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Service Location",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = LuxeTextPrimary
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = {
                            address = it
                            viewModel.setAddress(it)
                        },
                        label = { Text("FULL ADDRESS", color = LuxeTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = LuxeAccentSage) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LuxeAccentSage,
                            unfocusedBorderColor = LuxeBorder,
                            focusedContainerColor = LuxeCardColor,
                            unfocusedContainerColor = LuxeCardColor,
                            focusedTextColor = LuxeTextPrimary,
                            unfocusedTextColor = LuxeTextPrimary
                        )
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = {
                            if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                                phone = it
                                viewModel.setPhone(it)
                            }
                        },
                        label = { Text("CONTACT NUMBER", color = LuxeTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        leadingIcon = { Icon(Icons.Default.Phone, null, tint = LuxeAccentSage) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LuxeAccentSage,
                            unfocusedBorderColor = LuxeBorder,
                            focusedContainerColor = LuxeCardColor,
                            unfocusedContainerColor = LuxeCardColor,
                            focusedTextColor = LuxeTextPrimary,
                            unfocusedTextColor = LuxeTextPrimary
                        )
                    )
                }
            }

            // --------------------------------------------------
            // SCHEDULE SERVICE CARD
            // --------------------------------------------------
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = LuxeCardColor,
                border = BorderStroke(1.dp, LuxeBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Schedule Service",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = LuxeTextPrimary
                    )

                    Spacer(Modifier.height(16.dp))

                    Box(modifier = Modifier.clickable { showDatePicker = true }) {
                        OutlinedTextField(
                            value = date,
                            onValueChange = {},
                            enabled = false,
                            label = { Text("PREFERRED DATE", color = LuxeTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            leadingIcon = { Icon(Icons.Default.CalendarToday, null, tint = LuxeAccentSage) },
                            trailingIcon = { Icon(Icons.Default.CalendarToday, null, tint = LuxeAccentSage) },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledBorderColor = LuxeBorder,
                                disabledContainerColor = LuxeCardColor,
                                disabledTextColor = LuxeTextPrimary,
                                disabledLabelColor = LuxeTextSecondary
                            )
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    Text(
                        "PREFERRED TIME SLOT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = LuxeTextSecondary
                    )

                    Spacer(Modifier.height(10.dp))

                    Column(
                        modifier = Modifier.selectableGroup(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        slots.forEach { slot ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .selectable(
                                        selected = selectedSlot == slot,
                                        onClick = {
                                            selectedSlot = slot
                                            viewModel.setTimeSlot(slot)
                                        },
                                        role = Role.RadioButton
                                    )
                                    .background(if (selectedSlot == slot) LuxeHighlightChampagne.copy(alpha = 0.5f) else Color.Transparent)
                                    .padding(horizontal = 12.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedSlot == slot,
                                    onClick = null,
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = LuxeAccentSage,
                                        unselectedColor = LuxeBorder
                                    )
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = slot,
                                    fontSize = 15.sp,
                                    color = LuxeTextPrimary,
                                    fontWeight = if (selectedSlot == slot) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // --------------------------------------------------
            // VALIDATION MESSAGE
            // --------------------------------------------------
            if (showValidationError) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Text(
                        text = "Please enter your address, a valid 10-digit phone number, date, and time slot.",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}
