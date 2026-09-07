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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.components.*
import com.nisr.sauservices.ui.viewmodel.*
import com.nisr.sauservices.ui.partner.PartnerData
import com.nisr.sauservices.data.model.ResidentialData

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

    LuxuryScaffold(
        title = "Order Summary",
        onBackClick = { navController.popBackStack() },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 16.dp,
                color = LuxuryBackground,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                border = BorderStroke(1.dp, LuxuryBorder)
            ) {
                LuxuryButton(
                    text = "Confirm Order",
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
                    modifier = Modifier.padding(20.dp)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Selected Service & Partner Card
            if (selectedService != null || selectedPartner != null) {
                LuxuryCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Build, null, tint = LuxuryGold, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(10.dp))
                            Text("Service & Partner", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = LuxuryTextPrimary)
                        }
                        Spacer(Modifier.height(14.dp))
                        if (selectedService != null) {
                            SummaryRow("Service", selectedService.name)
                        }
                        if (selectedPartner != null) {
                            SummaryRow("Partner", selectedPartner.name)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // Booking Details Card
            LuxuryCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Event, null, tint = LuxuryGold)
                        Spacer(Modifier.width(8.dp))
                        Text("Service Schedule", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LuxuryTextPrimary)
                    }
                    Spacer(Modifier.height(12.dp))
                    SummaryRow("Date", bookingDetails.date)
                    SummaryRow("Time Slot", bookingDetails.timeSlot)
                    Spacer(Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, tint = LuxuryGold)
                        Spacer(Modifier.width(8.dp))
                        Text("Service Location", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LuxuryTextPrimary)
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(bookingDetails.address, color = LuxuryTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    Text("Phone: ${bookingDetails.phone}", color = LuxuryTextSecondary, fontSize = 15.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Order Items Summary Card
            LuxuryCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ListAlt, null, tint = LuxuryGold)
                        Spacer(Modifier.width(8.dp))
                        Text("Order Items", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LuxuryTextPrimary)
                    }
                    Spacer(Modifier.height(12.dp))
                    
                    val totalItemsCount = resItems.size + businessItems.size + lifestyleItems.size + 
                                        techItems.size + mensItems.size + womensItems.size + 
                                        healthItems.size + foodItems.size + dbCartItems.size + eduItems.size
                    
                    Text("$totalItemsCount Items in your order", fontSize = 15.sp, color = LuxuryTextSecondary, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Payment Method Card
            LuxuryCard {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Payment, null, tint = LuxuryGold)
                        Spacer(Modifier.width(8.dp))
                        Text("Payment Method", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LuxuryTextPrimary)
                    }
                    Text(bookingDetails.paymentMethod, fontWeight = FontWeight.Bold, color = LuxuryGold)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Bill Details Card
            LuxuryCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Price Details", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LuxuryTextPrimary)
                    Spacer(Modifier.height(12.dp))
                    SummaryRow("Subtotal", "₹$subtotal")
                    SummaryRow("Delivery Fee", "₹$deliveryFee")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = LuxuryBorder)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Amount", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = LuxuryTextPrimary)
                        Text("₹$totalAmount", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = LuxuryGold)
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = LuxuryTextSecondary, fontSize = 15.sp)
        Text(value, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = LuxuryTextPrimary)
    }
}
