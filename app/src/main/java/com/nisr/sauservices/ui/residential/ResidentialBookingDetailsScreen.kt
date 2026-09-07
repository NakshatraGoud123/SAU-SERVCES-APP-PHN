package com.nisr.sauservices.ui.residential

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.components.*
import com.nisr.sauservices.ui.viewmodel.ResidentialViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResidentialBookingDetailsScreen(
    navController: NavController,
    viewModel: ResidentialViewModel,
    partnerId: String,
    serviceId: String
) {

    val bookingDetails by viewModel.bookingDetails.collectAsState()

    var address by remember {
        mutableStateOf(bookingDetails.address)
    }

    var phone by remember {
        mutableStateOf(bookingDetails.phone)
    }

    var date by remember {
        mutableStateOf(bookingDetails.date)
    }

    var selectedSlot by remember {
        mutableStateOf(bookingDetails.timeSlot)
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    var showValidationError by remember {
        mutableStateOf(false)
    }

    val slots = listOf(
        "Morning: 9AM–12PM",
        "Afternoon: 12PM–3PM",
        "Evening: 3PM–6PM",
        "Night: 6PM–9PM"
    )

    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->

                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = millis

                            val formatter = SimpleDateFormat(
                                "dd/MM/yyyy",
                                Locale.getDefault()
                            )

                            val selectedDate =
                                formatter.format(calendar.time)

                            date = selectedDate
                            viewModel.setDate(selectedDate)
                        }

                        showDatePicker = false
                    }
                ) {
                    Text(
                        "OK",
                        color = LuxuryGold,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("Cancel", color = LuxuryTextSecondary)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = LuxuryCard
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = LuxuryCard,
                    titleContentColor = LuxuryGold,
                    headlineContentColor = LuxuryTextPrimary,
                    weekdayContentColor = LuxuryTextSecondary,
                    subheadContentColor = LuxuryTextSecondary,
                    yearContentColor = LuxuryTextSecondary,
                    currentYearContentColor = LuxuryGold,
                    selectedYearContentColor = LuxuryBackground,
                    selectedYearContainerColor = LuxuryGold,
                    dayContentColor = LuxuryTextPrimary,
                    selectedDayContentColor = LuxuryBackground,
                    selectedDayContainerColor = LuxuryGold,
                    todayContentColor = LuxuryGold,
                    todayDateBorderColor = LuxuryGold
                )
            )
        }
    }

    LuxuryScaffold(
        title = "Booking Details",
        onBackClick = { navController.popBackStack() }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            // --------------------------------------------------
            // SERVICE INFORMATION
            // --------------------------------------------------

            LuxuryCard {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        "Booking Information",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = LuxuryTextPrimary
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        "Service ID: $serviceId",
                        fontSize = 14.sp,
                        color = LuxuryTextSecondary
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        "Partner ID: $partnerId",
                        fontSize = 14.sp,
                        color = LuxuryTextSecondary
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // --------------------------------------------------
            // ADDRESS
            // --------------------------------------------------

            LuxuryCard {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        "Service Location",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = LuxuryTextPrimary
                    )

                    Spacer(Modifier.height(16.dp))

                    LuxuryTextField(
                        value = address,
                        onValueChange = {
                            address = it
                            viewModel.setAddress(it)
                        },
                        label = "FULL ADDRESS",
                        leadingIcon = Icons.Default.LocationOn
                    )

                    Spacer(Modifier.height(12.dp))

                    LuxuryTextField(
                        value = phone,
                        onValueChange = {
                            if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                                phone = it
                                viewModel.setPhone(it)
                            }
                        },
                        label = "CONTACT NUMBER",
                        leadingIcon = Icons.Default.Phone,
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // --------------------------------------------------
            // DATE & TIME
            // --------------------------------------------------

            LuxuryCard {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        "Schedule Service",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = LuxuryTextPrimary
                    )

                    Spacer(Modifier.height(16.dp))

                    Box(modifier = Modifier.clickable { showDatePicker = true }) {
                        LuxuryTextField(
                            value = date,
                            onValueChange = {},
                            enabled = false,
                            label = "PREFERRED DATE",
                            leadingIcon = Icons.Default.CalendarToday,
                            trailingIcon = {
                                Icon(
                                    Icons.Default.CalendarToday,
                                    contentDescription = "Select Date",
                                    tint = LuxuryGold
                                )
                            }
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    LuxuryLabel(text = "PREFERRED TIME SLOT")

                    Spacer(Modifier.height(12.dp))

                    Column(
                        modifier = Modifier.selectableGroup()
                    ) {

                        slots.forEach { slot ->

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(
                                        RoundedCornerShape(12.dp)
                                    )
                                    .selectable(
                                        selected = selectedSlot == slot,
                                        onClick = {
                                            selectedSlot = slot
                                            viewModel.setTimeSlot(slot)
                                        },
                                        role = Role.RadioButton
                                    )
                                    .padding(
                                        vertical = 6.dp
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                RadioButton(
                                    selected = selectedSlot == slot,
                                    onClick = null,
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = LuxuryGold,
                                        unselectedColor = LuxuryBorder
                                    )
                                )

                                Text(
                                    text = slot,
                                    modifier = Modifier.padding(
                                        start = 12.dp
                                    ),
                                    fontSize = 15.sp,
                                    color = if (selectedSlot == slot) LuxuryGold else LuxuryTextPrimary,
                                    fontWeight =
                                        if (selectedSlot == slot) {
                                            FontWeight.Bold
                                        } else {
                                            FontWeight.Normal
                                        }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // --------------------------------------------------
            // VALIDATION MESSAGE
            // --------------------------------------------------

            if (showValidationError) {

                Text(
                    text = "Please enter address, valid phone number, date and time slot.",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(
                        horizontal = 4.dp
                    )
                )

                Spacer(Modifier.height(12.dp))
            }

            // --------------------------------------------------
            // CONTINUE
            // --------------------------------------------------

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
                showArrow = true
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}
