package com.nisr.sauservices.ui.home

import androidx.compose.foundation.background
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
import com.nisr.sauservices.ui.theme.PinkPrimary
import com.nisr.sauservices.ui.viewmodel.*

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
    educationViewModel: EducationCartViewModel
) {
    val bookingDetails = viewModel.bookingDetails.value
    val dbCartItems by homeCartViewModel.dbCartItems.collectAsState()
    
    val resItems = viewModel.cartItems
    val businessItems = businessViewModel.cartItems
    val lifestyleItems = lifestyleViewModel.cartItems
    val techItems = techViewModel.cartItems
    val mensItems = mensGroomingViewModel.cartItems
    val womensItems = womensBeautyViewModel.cartItems
    val healthItems = healthcareViewModel.cartItems
    val foodItems = foodCartViewModel.cartItems
    val eduItems = educationViewModel.cartItems

    val subtotal = viewModel.calculateTotal() + 
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
            
            navController.navigate(Screen.BookingSuccess.route) {
                popUpTo(Screen.Home.route) { inclusive = false }
            }
            bookingsViewModel.resetResult()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Summary", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 16.dp,
                color = Color.White,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Button(
                    onClick = {
                        bookingsViewModel.placeUnifiedOrder(
                            serviceName = "Unified Services",
                            category = "Multi",
                            subcategory = "Unified",
                            date = bookingDetails.date,
                            time = bookingDetails.timeSlot,
                            amount = totalAmount,
                            paymentMethod = bookingDetails.paymentMethod,
                            address = bookingDetails.address,
                            items = dbCartItems
                        )
                    },
                    modifier = Modifier.padding(20.dp).fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
                ) {
                    Text("Confirm Order", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF9FAFB))
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Booking Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Event, null, tint = PinkPrimary)
                        Spacer(Modifier.width(8.dp))
                        Text("Service Schedule", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(Modifier.height(12.dp))
                    SummaryRow("Date", bookingDetails.date)
                    SummaryRow("Time Slot", bookingDetails.timeSlot)
                    Spacer(Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, tint = PinkPrimary)
                        Spacer(Modifier.width(8.dp))
                        Text("Service Location", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(bookingDetails.address, color = Color.Gray, fontSize = 14.sp)
                    Text("Phone: ${bookingDetails.phone}", color = Color.Gray, fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Order Items Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ListAlt, null, tint = PinkPrimary)
                        Spacer(Modifier.width(8.dp))
                        Text("Order Items", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(Modifier.height(12.dp))
                    
                    // Simplified item list display
                    val totalItemsCount = resItems.size + businessItems.size + lifestyleItems.size + 
                                        techItems.size + mensItems.size + womensItems.size + 
                                        healthItems.size + foodItems.size + dbCartItems.size + eduItems.size
                    
                    Text("$totalItemsCount Items in your order", fontSize = 14.sp, color = Color.Gray)
                    
                    // You could add more detailed list here if needed
                }
            }

            Spacer(Modifier.height(16.dp))

            // Payment Method Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Payment, null, tint = PinkPrimary)
                        Spacer(Modifier.width(8.dp))
                        Text("Payment Method", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Text(bookingDetails.paymentMethod, fontWeight = FontWeight.Bold, color = PinkPrimary)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Bill Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Price Details", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(12.dp))
                    SummaryRow("Subtotal", "₹$subtotal")
                    SummaryRow("Delivery Fee", "₹$deliveryFee")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Amount", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        Text("₹$totalAmount", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = PinkPrimary)
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}
