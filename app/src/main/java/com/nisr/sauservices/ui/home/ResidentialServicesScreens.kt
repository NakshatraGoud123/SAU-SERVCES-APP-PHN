package com.nisr.sauservices.ui.home

import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.alpha
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
                title = { Text("Home Services", color = LuxeTextPrimary, fontWeight = FontWeight.Black, fontFamily = FontFamily.Serif) },
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
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(600)) + slideInVertically(initialOffsetY = { 40 }, animationSpec = tween(600))
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = LuxeCard),
            border = BorderStroke(1.dp, LuxeBorder),
            elevation = CardDefaults.cardElevation(2.dp)
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
                        alpha = 0.5f
                    )
                    // Bottom Gradient
                    Box(modifier = Modifier.fillMaxSize().background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f)),
                            startY = 80f
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
                    Spacer(Modifier.height(2.dp))
                    Text(
                        category.description ?: "Professional service",
                        fontSize = 11.sp,
                        color = if (category.imageUrl != null) Color.White.copy(alpha = 0.8f) else LuxeTextSecondary,
                        maxLines = 1
                    )
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
                    val id = sub["id"] ?: "sub_1"
                    val name = sub["name"] ?: "Service"
                    val imageUrl = sub["imageUrl"] ?: "https://images.unsplash.com/photo-1581578731548-c64695cc6952?w=600&auto=format&fit=crop&q=80"
                    var visible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) { visible = true }

                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(500)) + slideInVertically(initialOffsetY = { 40 }, animationSpec = tween(500))
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .clickable {
                                    navController.navigate(Screen.ResidentialServices(categoryId, id))
                                },
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = LuxeCard),
                            border = BorderStroke(1.dp, LuxeBorder),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(imageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = name,
                                    modifier = Modifier.fillMaxSize().alpha(0.3f),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.horizontalGradient(
                                                colors = listOf(Color.White, Color.White.copy(alpha = 0.85f), Color.Transparent),
                                                startX = 0f,
                                                endX = Float.POSITIVE_INFINITY
                                            )
                                        )
                                )

                                Row(
                                    modifier = Modifier.fillMaxSize().padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Surface(
                                            modifier = Modifier.size(52.dp),
                                            shape = CircleShape,
                                            border = BorderStroke(1.5.dp, LuxeBorder),
                                            color = LuxeHighlightChampagne
                                        ) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(imageUrl)
                                                    .crossfade(true)
                                                    .build(),
                                                contentDescription = null,
                                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                        Spacer(Modifier.width(16.dp))
                                        Column {
                                            Text(name, fontWeight = FontWeight.Black, fontSize = 16.sp, color = LuxeTextPrimary)
                                            Spacer(Modifier.height(2.dp))
                                            Text("Expert professional task", fontSize = 12.sp, color = LuxeTextSecondary, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = LuxeAccentSage)
                                }
                            }
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
                                    viewModel.selectService(service)
                                    navController.navigate(Screen.ResidentialBookingDetails("", service.id))
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
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(500)) + slideInVertically(initialOffsetY = { 40 }, animationSpec = tween(500))
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = LuxeCard,
            border = BorderStroke(1.dp, LuxeBorder),
            shadowElevation = 2.dp
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
                    Text(service.displayDescription, fontSize = 12.sp, color = LuxeTextSecondary, maxLines = 1)
                    Spacer(Modifier.height(4.dp))
                    Text("₹${service.price.toInt()}", fontWeight = FontWeight.Black, color = LuxeGold, fontSize = 16.sp)
                }

                LuxuryButton(
                    text = "BOOK",
                    onClick = onAdd,
                    modifier = Modifier.width(80.dp).height(36.dp)
                )
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
        PaymentOptionData("UPI (GPay / PhonePe / Paytm)", Icons.Default.Payments),
        PaymentOptionData("Credit / Debit Card", Icons.Default.CreditCard),
        PaymentOptionData("Cash After Service", Icons.Default.LocalAtm)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment Method", color = LuxeTextPrimary, fontWeight = FontWeight.Black, fontFamily = FontFamily.Serif) },
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
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Select Payment Option", fontWeight = FontWeight.Black, fontSize = 20.sp, color = LuxeTextPrimary)
                Spacer(Modifier.height(16.dp))

                options.forEach { option ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { 
                                selectedOption = option.name
                                viewModel.setPaymentMethod(option.name)
                            },
                        shape = RoundedCornerShape(16.dp),
                        color = LuxeCard,
                        border = BorderStroke(1.dp, if (selectedOption == option.name) LuxeAccentSage else LuxeBorder),
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedOption == option.name,
                                onClick = { 
                                    selectedOption = option.name
                                    viewModel.setPaymentMethod(option.name)
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = LuxeAccentSage)
                            )
                            Spacer(Modifier.width(12.dp))
                            Icon(option.icon, null, tint = LuxeAccentSage)
                            Spacer(Modifier.width(12.dp))
                            Text(option.name, fontWeight = FontWeight.Bold, color = LuxeTextPrimary)
                        }
                    }
                }
            }

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
