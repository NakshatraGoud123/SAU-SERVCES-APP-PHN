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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.nisr.sauservices.data.model.*
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.viewmodel.*
import com.nisr.sauservices.ui.components.LuxuryButton

data class PaymentOptionData(val name: String, val icon: ImageVector)

// ============================================================
// LUXE BRAND COLORS (Local for precision)
// ============================================================
private val LuxeBackground = Color(0xFFFDFBFA)
private val LuxeCard = Color(0xFFFFFFFF)
private val LuxeTextPrimary = Color(0xFF423F3D)
private val LuxeTextSecondary = Color(0xFF8D7F77)
private val LuxeAccentSage = Color(0xFF96A68F)
private val LuxeHighlightChampagne = Color(0xFFF5E6D3)
private val LuxeBorder = Color(0xFFEFE9E4)
private val LuxeGold = Color(0xFFE8C66A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResidentialCategoryScreen(navController: NavController, viewModel: ResidentialViewModel = viewModel()) {
    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Service Categories", color = LuxeTextPrimary, fontWeight = FontWeight.Black, fontFamily = FontFamily.Serif) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LuxeTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxeBackground)
            )
        },
        containerColor = LuxeBackground
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LuxeAccentSage)
            }
        } else if (categories.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No categories found.", color = LuxeTextSecondary)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(padding)
            ) {
                items(categories) { category ->
                    LuxeCategoryCard(category) {
                        navController.navigate(Screen.ResidentialSubcategories(category.id))
                    }
                }
            }
        }
    }
}

@Composable
fun LuxeCategoryCard(category: Category, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = LuxeCard),
        border = BorderStroke(1.dp, LuxeBorder)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (category.imageUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(category.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.6f
                )
                // Bottom Gradient
                Box(modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f)),
                        startY = 100f
                    )
                ))
            }
            
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    category.name, 
                    fontSize = 16.sp, 
                    fontWeight = FontWeight.Black, 
                    color = if (category.imageUrl != null) Color.White else LuxeTextPrimary,
                    lineHeight = 20.sp
                )
                if (category.imageUrl == null) {
                    Spacer(Modifier.height(4.dp))
                    Text("Explore", color = LuxeAccentSage, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResidentialSubcategoryScreen(
    navController: NavController, 
    categoryId: String,
    viewModel: ResidentialViewModel = viewModel()
) {
    val subcategories by viewModel.subcategories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(categoryId) {
        viewModel.fetchSubcategories(categoryId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Subcategories", color = LuxeTextPrimary, fontWeight = FontWeight.Black, fontFamily = FontFamily.Serif) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LuxeTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxeBackground)
            )
        },
        containerColor = LuxeBackground
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LuxeAccentSage)
            }
        } else if (subcategories.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No services available here.", color = LuxeTextSecondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(subcategories) { sub ->
                    val id = sub["id"] ?: ""
                    val name = sub["name"] ?: ""
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable {
                            navController.navigate(Screen.MerchantShop(id))
                        },
                        shape = RoundedCornerShape(20.dp),
                        color = LuxeCard,
                        border = BorderStroke(1.dp, LuxeBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(24.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(name, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = LuxeTextPrimary)
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = LuxeAccentSage)
                        }
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
    val services by viewModel.services.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val categories = listOf("All", "Top Rated", "New")
    var selectedCategory by remember { mutableStateOf("All") }

    LaunchedEffect(subcategoryId) {
        viewModel.fetchServices(subcategoryId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Services", color = LuxeTextPrimary, fontWeight = FontWeight.Black, fontFamily = FontFamily.Serif) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LuxeTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxeBackground)
            )
        },
        containerColor = LuxeBackground
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LuxeAccentSage)
            }
        } else {
            Column(modifier = Modifier.padding(padding)) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category) },
                            shape = RoundedCornerShape(16.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LuxeAccentSage,
                                selectedLabelColor = Color.White,
                                containerColor = LuxeCard,
                                labelColor = LuxeTextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = LuxeBorder,
                                selectedBorderColor = LuxeAccentSage,
                                borderWidth = 1.dp,
                                selectedBorderWidth = 1.dp,
                                enabled = true,
                                selected = selectedCategory == category
                            )
                        )
                    }
                }

                if (services.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No specific tasks found.", color = LuxeTextSecondary)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(services) { service ->
                            ResidentialServiceCardDesign(
                                service = service,
                                onAdd = { 
                                    // Map ServiceModel to the booking flow
                                    // For now, navigate to partner list
                                    navController.navigate(Screen.PartnerList(service.id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResidentialServiceCardDesign(service: ServiceModel, onAdd: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = LuxeCard,
        border = BorderStroke(1.dp, LuxeBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(16.dp),
                color = LuxeHighlightChampagne.copy(alpha = 0.5f)
            ) {
                if (!service.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(service.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.Build, 
                        contentDescription = null, 
                        modifier = Modifier.padding(24.dp),
                        tint = LuxeAccentSage
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(service.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LuxeTextPrimary)
                Spacer(Modifier.height(4.dp))
                Text("Standard Service", fontSize = 12.sp, color = LuxeTextSecondary)
                Text("₹${service.price.toInt()}", fontWeight = FontWeight.Black, color = LuxeTextPrimary, fontSize = 16.sp)
            }

            LuxuryButton(
                text = "ADD",
                onClick = onAdd,
                modifier = Modifier.width(80.dp).height(36.dp)
            )
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
                title = { Text("Payment", color = LuxeTextPrimary, fontWeight = FontWeight.Black, fontFamily = FontFamily.Serif) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = LuxeTextPrimary) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxeBackground)
            )
        },
        containerColor = LuxeBackground
    ) { padding ->
        Column(Modifier.padding(padding).padding(20.dp)) {
            Text("Select Method", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = LuxeTextPrimary)
            Spacer(Modifier.height(20.dp))
            Column(Modifier.selectableGroup()) {
                options.forEach { option ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .selectable(
                                selected = selectedOption == option.name, 
                                onClick = { selectedOption = option.name; viewModel.setPaymentMethod(option.name) }, 
                                role = Role.RadioButton
                            ),
                        shape = RoundedCornerShape(20.dp),
                        color = LuxeCard,
                        border = BorderStroke(1.dp, if (selectedOption == option.name) LuxeAccentSage else LuxeBorder),
                    ) {
                        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(option.icon, contentDescription = null, tint = if (selectedOption == option.name) LuxeAccentSage else LuxeTextSecondary)
                            Spacer(Modifier.width(16.dp))
                            Text(text = option.name, color = LuxeTextPrimary, modifier = Modifier.weight(1f), fontWeight = if (selectedOption == option.name) FontWeight.Bold else FontWeight.Medium)
                            RadioButton(
                                selected = selectedOption == option.name, 
                                onClick = null,
                                colors = RadioButtonDefaults.colors(selectedColor = LuxeAccentSage)
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            LuxuryButton(
                text = "PROCEED TO SUMMARY",
                onClick = { 
                    navController.navigate(
                        Screen.ResidentialOrderSummary(
                            partnerId = partnerId,
                            serviceId = serviceId
                        )
                    ) 
                },
                modifier = Modifier.height(56.dp)
            )
        }
    }
}
