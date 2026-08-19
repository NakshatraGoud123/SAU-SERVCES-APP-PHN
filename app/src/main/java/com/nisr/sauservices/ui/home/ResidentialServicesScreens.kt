package com.nisr.sauservices.ui.home

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.data.api.SupabaseClient
import com.nisr.sauservices.data.model.*
import com.nisr.sauservices.data.model.toSafeUuid
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.PinkPrimary
import com.nisr.sauservices.ui.viewmodel.*
import io.github.jan.supabase.auth.auth

data class PaymentOptionData(val name: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResidentialCategoryScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Residential Services", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF9FAFB)
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(padding)
        ) {
            items(ResidentialData.categories) { category ->
                ResidentialCategoryCardProfessional(category) {
                    navController.navigate(Screen.ResidentialSubcategories.createRoute(category.id))
                }
            }
        }
    }
}

@Composable
fun ResidentialCategoryCardProfessional(category: ResidentialCategory, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(60.dp),
                shape = CircleShape,
                color = PinkPrimary.copy(alpha = 0.05f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(category.icon, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(30.dp))
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                category.name, 
                fontSize = 15.sp, 
                textAlign = TextAlign.Center, 
                fontWeight = FontWeight.Bold, 
                color = Color.Black
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResidentialSubcategoryScreen(navController: NavController, categoryId: String) {
    val category = ResidentialData.categories.find { it.id == categoryId }
    val subcategories = ResidentialData.subcategories.filter { it.categoryId == categoryId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(category?.name ?: "Subcategories", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF9FAFB)
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(subcategories) { sub ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable {
                        navController.navigate(Screen.ResidentialServiceList.createRoute(categoryId, sub.id))
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(sub.name, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color.Black)
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = PinkPrimary)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResidentialServiceListScreen(
    navController: NavController, 
    categoryId: String, 
    subcategoryId: String, 
    viewModel: ResidentialViewModel,
    cartViewModel: CartViewModel
) {
    val sub = ResidentialData.subcategories.find { it.id == subcategoryId }
    val services = ResidentialData.services.filter { it.subcategory == subcategoryId }
    val dbCartItems by cartViewModel.dbCartItems.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(sub?.name ?: "Services", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF9FAFB),
        bottomBar = {
            val totalCount = dbCartItems.sumOf { it.quantity }
            if (totalCount > 0) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 16.dp,
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    Button(
                        onClick = { navController.navigate(Screen.Cart.route) },
                        modifier = Modifier.padding(20.dp).fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
                    ) {
                        val totalPrice = dbCartItems.sumOf { it.totalPrice }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("$totalCount Items | ₹$totalPrice", fontWeight = FontWeight.Bold)
                            Text("View Cart", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(services) { service ->
                val cartItem = dbCartItems.find { it.productId == service.id.toSafeUuid() }
                val quantity = cartItem?.quantity ?: 0
                
                ResidentialServiceCardProfessional(
                    service = service,
                    quantity = quantity,
                    onAdd = { 
                        cartViewModel.addItemToCart(
                            name = service.name,
                            price = service.price,
                            category = "Residential",
                            subcategory = subcategoryId,
                            unit = "Service",
                            productId = service.id
                        )
                    },
                    onIncrease = { 
                        cartItem?.let { cartViewModel.updateQuantity(it.itemId, it.quantity + 1) }
                    },
                    onDecrease = { 
                        cartItem?.let { cartViewModel.updateQuantity(it.itemId, it.quantity - 1) }
                    }
                )
            }
        }
    }
}

@Composable
fun ResidentialServiceCardProfessional(service: ResidentialServiceItem, quantity: Int, onAdd: () -> Unit, onIncrease: () -> Unit, onDecrease: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(PinkPrimary.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Build, 
                    contentDescription = null, 
                    tint = PinkPrimary.copy(alpha = 0.4f), 
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(service.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                Text("${service.durationMinutes} mins", fontSize = 13.sp, color = Color.Gray)
                Spacer(Modifier.height(4.dp))
                Text("₹${service.price}", fontWeight = FontWeight.ExtraBold, color = PinkPrimary, fontSize = 16.sp)
            }
            
            if (quantity == 0) {
                OutlinedButton(
                    onClick = onAdd, 
                    shape = RoundedCornerShape(12.dp), 
                    border = BorderStroke(1.dp, PinkPrimary),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PinkPrimary),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("ADD", fontWeight = FontWeight.Bold)
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically, 
                    modifier = Modifier
                        .background(PinkPrimary, RoundedCornerShape(12.dp))
                        .height(40.dp)
                        .padding(horizontal = 4.dp)
                ) {
                    IconButton(onClick = onDecrease, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.Remove, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp)) }
                    Text(quantity.toString(), color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                    IconButton(onClick = onIncrease, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp)) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResidentialBookingDetailsScreen(navController: NavController, viewModel: ResidentialViewModel) {
    var address by remember { mutableStateOf(viewModel.bookingDetails.value.address) }
    var phone by remember { mutableStateOf(viewModel.bookingDetails.value.phone) }
    var date by remember { mutableStateOf(viewModel.bookingDetails.value.date) }
    var selectedSlot by remember { mutableStateOf(viewModel.bookingDetails.value.timeSlot) }

    val slots = listOf("Morning: 9AM–12PM", "Afternoon: 12PM–3PM", "Evening: 3PM–6PM", "Night: 6PM–9PM")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking Details", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF9FAFB)
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(24.dp)) {
                    Text("Service Location", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = address, 
                        onValueChange = { address = it; viewModel.setAddress(it) }, 
                        label = { Text("Full Address") }, 
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PinkPrimary, focusedLabelColor = PinkPrimary),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = phone, 
                        onValueChange = { phone = it; viewModel.setPhone(it) }, 
                        label = { Text("Contact Number") }, 
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PinkPrimary, focusedLabelColor = PinkPrimary),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
            
            Spacer(Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(24.dp)) {
                    Text("Schedule Service", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = date, 
                        onValueChange = { date = it; viewModel.setDate(it) }, 
                        label = { Text("Preferred Date (DD/MM/YYYY)") }, 
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PinkPrimary, focusedLabelColor = PinkPrimary),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = { Icon(Icons.Default.CalendarToday, null, tint = PinkPrimary) }
                    )
                    Spacer(Modifier.height(24.dp))
                    Text("Preferred Time Slot", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Spacer(Modifier.height(12.dp))
                    Column(Modifier.selectableGroup()) {
                        slots.forEach { slot ->
                            Row(
                                Modifier.fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .selectable(selected = selectedSlot == slot, onClick = { selectedSlot = slot; viewModel.setTimeSlot(slot) }, role = Role.RadioButton)
                                    .padding(vertical = 4.dp), 
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedSlot == slot, 
                                    onClick = null,
                                    colors = RadioButtonDefaults.colors(selectedColor = PinkPrimary)
                                )
                                Text(slot, modifier = Modifier.padding(start = 12.dp), fontSize = 15.sp, fontWeight = if(selectedSlot == slot) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                }
            }
            
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = { if(address.isNotBlank() && phone.isNotBlank()) navController.navigate(Screen.ResidentialPayment.route) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) { 
                Text("Proceed to Payment", fontWeight = FontWeight.Bold, fontSize = 16.sp) 
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, null, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResidentialPaymentScreen(navController: NavController, viewModel: ResidentialViewModel) {
    var selectedOption by remember { mutableStateOf(viewModel.bookingDetails.value.paymentMethod.ifBlank { "UPI" }) }
    val options = listOf(
        PaymentOptionData("Cash on Delivery", Icons.Default.Payments),
        PaymentOptionData("UPI", Icons.Default.AccountBalanceWallet),
        PaymentOptionData("Debit/Credit Card", Icons.Default.CreditCard),
        PaymentOptionData("Wallet", Icons.Default.AccountBalance)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment Methods", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF9FAFB)
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text("Select Payment Option", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
            Spacer(Modifier.height(16.dp))
            Column(Modifier.selectableGroup()) {
                options.forEach { option ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .selectable(
                                selected = selectedOption == option.name, 
                                onClick = { selectedOption = option.name; viewModel.setPaymentMethod(option.name) }, 
                                role = Role.RadioButton
                            ),
                        shape = RoundedCornerShape(16.dp),
                        color = if (selectedOption == option.name) PinkPrimary.copy(alpha = 0.05f) else Color.White,
                        border = if (selectedOption == option.name) BorderStroke(1.dp, PinkPrimary) else null,
                        shadowElevation = if (selectedOption == option.name) 0.dp else 1.dp
                    ) {
                        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(option.icon, contentDescription = null, tint = if (selectedOption == option.name) PinkPrimary else Color.Gray)
                            Spacer(Modifier.width(16.dp))
                            Text(text = option.name, modifier = Modifier.weight(1f), fontWeight = if (selectedOption == option.name) FontWeight.Bold else FontWeight.Medium)
                            RadioButton(
                                selected = selectedOption == option.name, 
                                onClick = null,
                                colors = RadioButtonDefaults.colors(selectedColor = PinkPrimary)
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            Button(
                onClick = { navController.navigate(Screen.ResidentialOrderSummary.route) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text("Proceed to Summary", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, null, modifier = Modifier.size(18.dp))
            }
        }
    }
}
