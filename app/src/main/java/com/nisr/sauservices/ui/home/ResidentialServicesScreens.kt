package com.nisr.sauservices.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.data.model.*
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.viewmodel.*

data class PaymentOptionData(val name: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResidentialCategoryScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Residential Services", color = LuxuryTextPrimary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LuxuryTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxuryBackground)
            )
        },
        containerColor = LuxuryBackground
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
                    navController.navigate(Screen.ResidentialSubcategories(category.id))
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
        colors = CardDefaults.cardColors(containerColor = LuxuryCard),
        border = BorderStroke(1.dp, LuxuryBorder)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (category.imageRes != null) {
                Image(
                    painter = painterResource(id = category.imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.4f
                )
                // Bottom Gradient for text readability
                Box(modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                        startY = 100f
                    )
                ))
            }
            
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (category.imageRes == null) {
                    Surface(
                        modifier = Modifier.size(60.dp),
                        shape = CircleShape,
                        color = LuxuryGold.copy(alpha = 0.05f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(category.icon, contentDescription = null, tint = LuxuryGold, modifier = Modifier.size(30.dp))
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
                
                Text(
                    category.name, 
                    fontSize = 15.sp, 
                    textAlign = TextAlign.Center, 
                    fontWeight = FontWeight.Black, 
                    color = Color.White
                )
            }
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
                title = { Text(category?.name ?: "Subcategories", color = LuxuryTextPrimary, fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LuxuryTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxuryBackground)
            )
        },
        containerColor = LuxuryBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(subcategories) { sub ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable {
                        navController.navigate(Screen.ResidentialServices(categoryId, sub.id))
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = LuxuryCard),
                    border = BorderStroke(1.dp, LuxuryBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(sub.name, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = LuxuryTextPrimary)
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = LuxuryGold)
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
    val categories = listOf("All", "AC Repair", "Installation", "Others")
    var selectedCategory by remember { mutableStateOf("All") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(sub?.name ?: "Services", color = LuxuryTextPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LuxuryTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxuryBackground)
            )
        },
        containerColor = LuxuryBackground
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Horizontal Category Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LuxuryGold,
                            selectedLabelColor = LuxuryBackground,
                            containerColor = LuxuryCard,
                            labelColor = LuxuryTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = LuxuryBorder,
                            selectedBorderColor = LuxuryGold,
                            borderWidth = 1.dp,
                            selectedBorderWidth = 1.dp,
                            enabled = true,
                            selected = selectedCategory == category
                        )
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(services) { service ->
                    ResidentialServiceCardDesign(
                        service = service,
                        onAdd = { 
                            viewModel.selectService(service.id)
                            navController.navigate(Screen.PartnerList(service.id))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ResidentialServiceCardDesign(service: ResidentialServiceItem, onAdd: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LuxuryCard),
        border = BorderStroke(1.dp, LuxuryBorder)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image on the left
            Surface(
                modifier = Modifier.size(100.dp),
                shape = RoundedCornerShape(12.dp),
                color = LuxuryBackground
            ) {
                Icon(
                    Icons.Default.Air, 
                    contentDescription = null, 
                    modifier = Modifier.padding(24.dp),
                    tint = LuxuryGold.copy(alpha = 0.2f)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(service.name, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = LuxuryTextPrimary)
                Spacer(Modifier.height(4.dp))
                Text("Starting from", fontSize = 12.sp, color = LuxuryTextSecondary)
                Text("₹${service.price.toInt()}", fontWeight = FontWeight.Bold, color = LuxuryGold, fontSize = 16.sp)
            }

            Button(
                onClick = onAdd,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LuxuryGold),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("ADD", color = LuxuryBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResidentialPaymentScreen(
    navController: NavController, 
    viewModel: ResidentialViewModel,
    partnerId: String,
    serviceId: String
) {
    val bookingDetails by viewModel.bookingDetails.collectAsState()
    var selectedOption by remember { mutableStateOf(bookingDetails.paymentMethod.ifBlank { "UPI" }) }
    val options = listOf(
        PaymentOptionData("Cash on Delivery", Icons.Default.Payments),
        PaymentOptionData("UPI", Icons.Default.AccountBalanceWallet),
        PaymentOptionData("Debit/Credit Card", Icons.Default.CreditCard),
        PaymentOptionData("Wallet", Icons.Default.AccountBalance)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment Methods", color = LuxuryTextPrimary, fontWeight = FontWeight.ExtraBold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = LuxuryTextPrimary) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxuryBackground)
            )
        },
        containerColor = LuxuryBackground
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text("Select Payment Option", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = LuxuryTextPrimary)
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
                        color = if (selectedOption == option.name) LuxuryGold.copy(alpha = 0.05f) else LuxuryCard,
                        border = if (selectedOption == option.name) BorderStroke(1.dp, LuxuryGold) else BorderStroke(1.dp, LuxuryBorder),
                    ) {
                        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(option.icon, contentDescription = null, tint = if (selectedOption == option.name) LuxuryGold else LuxuryTextSecondary)
                            Spacer(Modifier.width(16.dp))
                            Text(text = option.name, color = LuxuryTextPrimary, modifier = Modifier.weight(1f), fontWeight = if (selectedOption == option.name) FontWeight.Bold else FontWeight.Medium)
                            RadioButton(
                                selected = selectedOption == option.name, 
                                onClick = null,
                                colors = RadioButtonDefaults.colors(selectedColor = LuxuryGold, unselectedColor = LuxuryTextSecondary)
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            Button(
                onClick = { 
                    navController.navigate(
                        Screen.ResidentialOrderSummary(
                            partnerId = partnerId,
                            serviceId = serviceId
                        )
                    ) 
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LuxuryGold),
            ) {
                Text("Proceed to Summary", color = LuxuryBackground, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = LuxuryBackground, modifier = Modifier.size(18.dp))
            }
        }
    }
}
