package com.nisr.sauservices.ui.home

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.components.LuxuryButton
import com.nisr.sauservices.ui.viewmodel.*
import com.nisr.sauservices.ui.partner.PartnerData
import com.nisr.sauservices.data.model.ResidentialData

// ============================================================
// LUXE BRAND COLORS (Local for precision)
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
fun ResidentialOrderSummaryScreen(
    navController: NavController,
    viewModel: ResidentialViewModel,
    bookingsViewModel: BookingsViewModel,
    businessViewModel: BusinessViewModel,
    lifestyleViewModel: LifestyleViewModel,
    techViewModel: TechServicesViewModel,
    mensGroomingViewModel: MensGroomingViewModel,
    womensBeautyViewModel: WomensBeautyViewModel,
    healthcareViewModel: HealthcareViewModel,
    foodCartViewModel: FoodCartViewModel,
    homeCartViewModel: CartViewModel,
    educationViewModel: EducationCartViewModel,
    partnerId: String,
    serviceId: String
) {
    val bookingDetails by viewModel.bookingDetails.collectAsState()
    val dbCartItems by homeCartViewModel.dbCartItems.collectAsState()

    val selectedPartner = PartnerData.getPartnerById(partnerId)
    val selectedService = ResidentialData.services.find { it.id == serviceId }
    
    val resItems = viewModel.cartItems
    val businessItems = businessViewModel.cartItems
    val lifestyleItems = lifestyleViewModel.cartItems
    val techItems = techViewModel.cartItems
    val mensItems = mensGroomingViewModel.cartItems
    val womensItems = womensBeautyViewModel.cartItems
    val healthItems = healthcareViewModel.cartItems
    val foodItems = foodCartViewModel.cartItems
    val eduItems = educationViewModel.cartItems

    val subtotal = (selectedService?.price ?: 0.0) +
                   viewModel.calculateTotal() + 
                   businessViewModel.getTotalPrice() +
                   lifestyleViewModel.getTotalPrice() +
                   techViewModel.getTotalPrice() +
                   mensGroomingViewModel.getTotalPrice() +
                   womensBeautyViewModel.calculateTotal() +
                   healthcareViewModel.calculateTotal() +
                   foodCartViewModel.getTotal().toDouble() +
                   educationViewModel.getTotal().toDouble() +
                   dbCartItems.sumOf { it.totalPrice }
    
    val deliveryFee = 30.0
    val totalAmount = subtotal + deliveryFee

    val bookingResult by bookingsViewModel.bookingResult.collectAsState()

    LaunchedEffect(bookingResult) {
        bookingResult?.onSuccess {
            // Clear all carts after success
            viewModel.clearCart()
            businessViewModel.clearCart()
            lifestyleViewModel.clearCart()
            techViewModel.clearCart()
            mensGroomingViewModel.clearCart()
            womensBeautyViewModel.clearCart()
            healthcareViewModel.clearCart()
            foodCartViewModel.clearCart()
            educationViewModel.clearCart()
            homeCartViewModel.clearHomeCart()
            
            navController.navigate(Screen.ResidentialSuccess) {
                popUpTo<Screen.Home> { inclusive = false }
            }
            bookingsViewModel.resetResult()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Review Summary", color = LuxeTextPrimary, fontWeight = FontWeight.Black, fontFamily = FontFamily.Serif) },
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
                    text = "CONFIRM ORDER",
                    onClick = {
                        bookingsViewModel.placeUnifiedOrder(
                            serviceName = selectedService?.name ?: "Unified Services",
                            category = "Residential",
                            subcategory = selectedService?.subcategory ?: "Unified",
                            date = bookingDetails.date,
                            time = bookingDetails.timeSlot,
                            amount = totalAmount,
                            paymentMethod = bookingDetails.paymentMethod,
                            address = bookingDetails.address,
                            items = dbCartItems
                        )
                    },
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
            // Selected Service & Partner Card
            if (selectedService != null || selectedPartner != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = LuxeCardColor,
                    border = BorderStroke(1.dp, LuxeBorder)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Build, null, tint = LuxeAccentSage, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(10.dp))
                            Text("Service & Partner", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LuxeTextPrimary)
                        }
                        Spacer(Modifier.height(16.dp))
                        if (selectedService != null) {
                            SummaryRow("Service", selectedService.name)
                        }
                        if (selectedPartner != null) {
                            SummaryRow("Partner", selectedPartner.name)
                        }
                    }
                }
            }

            // Booking Details Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = LuxeCardColor,
                border = BorderStroke(1.dp, LuxeBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Event, null, tint = LuxeAccentSage, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("Schedule Details", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LuxeTextPrimary)
                    }
                    Spacer(Modifier.height(16.dp))
                    SummaryRow("Date", bookingDetails.date)
                    SummaryRow("Time Slot", bookingDetails.timeSlot)
                    Spacer(Modifier.height(20.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, tint = LuxeAccentSage, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("Delivery Address", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LuxeTextPrimary)
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(bookingDetails.address, color = LuxeTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text("Phone: ${bookingDetails.phone}", color = LuxeTextSecondary, fontSize = 14.sp)
                }
            }

            // Bill Details Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = LuxeCardColor,
                border = BorderStroke(1.dp, LuxeBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Payment Summary", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LuxeTextPrimary)
                    Spacer(Modifier.height(16.dp))
                    SummaryRow("Subtotal", "₹$subtotal")
                    SummaryRow("Delivery Fee", "₹$deliveryFee")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = LuxeBorder)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Amount", fontWeight = FontWeight.Black, fontSize = 18.sp, color = LuxeTextPrimary)
                        Text("₹$totalAmount", fontWeight = FontWeight.Black, fontSize = 18.sp, color = LuxeAccentSage)
                    }
                }
            }
            
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = LuxeTextSecondary, fontSize = 14.sp)
        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = LuxeTextPrimary)
    }
}
