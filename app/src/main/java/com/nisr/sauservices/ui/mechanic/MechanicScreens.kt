package com.nisr.sauservices.ui.mechanic

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import android.widget.Toast
import com.nisr.sauservices.data.model.MechanicData
import com.nisr.sauservices.data.model.MechanicServiceItem
import com.nisr.sauservices.data.model.MechanicSubcategory
import com.nisr.sauservices.service.NotificationHelper
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.viewmodel.BookingsViewModel
import com.nisr.sauservices.ui.viewmodel.MechanicViewModel
import java.net.URLDecoder
import java.net.URLEncoder

// ============================================================
// LUXE BRAND COLORS
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
fun MechanicSubcategoryScreen(navController: NavController, categoryName: String, viewModel: MechanicViewModel) {
    val category = MechanicData.categories.find { it.name == categoryName }
    val showAll = categoryName == "Mechanic Services" || categoryName == "Mechanical Kit" || categoryName.isEmpty()
    
    val subcategories = if (showAll) {
        emptyList()
    } else {
        MechanicData.subcategories.filter { it.categoryId == category?.id }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (showAll) "Mechanic Services" else categoryName, 
                        fontWeight = FontWeight.Black, 
                        color = LuxeTextPrimary, 
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
        containerColor = LuxeBackground
    ) { padding ->
        if (showAll || subcategories.isEmpty()) {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(MechanicData.categories) { cat ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .clickable {
                                navController.navigate(Screen.MechanicSubcategories(cat.name))
                            },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = LuxeCard),
                        border = BorderStroke(1.dp, LuxeBorder),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(cat.imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = cat.name,
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
                                                .data(cat.imageUrl)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                    Spacer(Modifier.width(16.dp))
                                    Column {
                                        Text(cat.name, fontWeight = FontWeight.Black, fontSize = 16.sp, color = LuxeTextPrimary)
                                        Spacer(Modifier.height(2.dp))
                                        Text("Expert inspection & repair", fontSize = 12.sp, color = LuxeTextSecondary, fontWeight = FontWeight.Medium)
                                    }
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = LuxeAccentSage)
                            }
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(subcategories) { sub ->
                    MechanicSubcategoryCard(sub) {
                        viewModel.updateSubcategoryId(sub.id)
                        val encoded = URLEncoder.encode(sub.name, "UTF-8")
                        navController.navigate(Screen.MechanicServices(sub.id, encoded))
                    }
                }
            }
        }
    }
}

@Composable
fun MechanicSubcategoryCard(subcategory: MechanicSubcategory, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = LuxeCard),
        border = BorderStroke(1.dp, LuxeBorder),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(subcategory.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = subcategory.name,
                modifier = Modifier.fillMaxSize().alpha(0.25f),
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(subcategory.name, fontWeight = FontWeight.Black, fontSize = 16.sp, color = LuxeTextPrimary)
                    Spacer(Modifier.height(2.dp))
                    Text("Reliable doorstep service", fontSize = 12.sp, color = LuxeTextSecondary, fontWeight = FontWeight.Medium)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = LuxeAccentSage)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MechanicServiceListScreen(navController: NavController, subcategoryId: String, subcategoryName: String, viewModel: MechanicViewModel) {
    val decodedName = URLDecoder.decode(subcategoryName, "UTF-8")
    val services = MechanicData.services.filter { it.subcategoryId == subcategoryId }
    val displayServices = if (services.isNotEmpty()) services else listOf(
        MechanicServiceItem("def_1", "$decodedName Standard", 499.0, "", subcategoryId, 45),
        MechanicServiceItem("def_2", "$decodedName Complete Package", 899.0, "", subcategoryId, 60)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = decodedName, fontWeight = FontWeight.Black, color = LuxeTextPrimary, fontFamily = FontFamily.Serif) },
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
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(displayServices) { service ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = LuxeCard),
                    border = BorderStroke(1.dp, LuxeBorder),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(service.name, fontWeight = FontWeight.Black, fontSize = 16.sp, color = LuxeTextPrimary)
                            Spacer(Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccessTime, null, tint = LuxeAccentSage, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("${service.estimatedMinutes} mins", fontSize = 12.sp, color = LuxeTextSecondary)
                            }
                            Spacer(Modifier.height(8.dp))
                            Text("₹${service.price.toInt()}", fontWeight = FontWeight.Black, fontSize = 18.sp, color = LuxeGold)
                        }

                        Button(
                            onClick = {
                                viewModel.selectService(service)
                                navController.navigate(Screen.MechanicBooking)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LuxeAccentSage)
                        ) {
                            Text("Book", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MechanicBookingScreen(
    navController: NavController, 
    viewModel: MechanicViewModel,
    bookingsViewModel: BookingsViewModel = viewModel()
) {
    val uiState by viewModel.bookingState
    var step by remember { mutableIntStateOf(1) }
    val context = LocalContext.current
    val bookingResult by bookingsViewModel.bookingResult.collectAsState()
    val isSubmitting by bookingsViewModel.isSubmitting.collectAsState()

    LaunchedEffect(bookingResult) {
        bookingResult?.onSuccess {
            val selectedService = uiState.selectedService
            val serviceName = selectedService?.name ?: "Mechanic Service"
            NotificationHelper.showNotification(
                context,
                "Mechanic Booked! 🔧",
                "Your $serviceName has been confirmed."
            )
            navController.navigate(Screen.MechanicSuccess) {
                popUpTo<Screen.Home> { inclusive = false }
            }
            bookingsViewModel.resetResult()
        }?.onFailure { err ->
            Toast.makeText(context, "Booking failed: ${err.message}", Toast.LENGTH_LONG).show()
            bookingsViewModel.resetResult()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Mechanic", fontWeight = FontWeight.Black, color = LuxeTextPrimary, fontFamily = FontFamily.Serif) },
                navigationIcon = {
                    IconButton(onClick = { 
                        if (step > 1) step-- else navController.popBackStack()
                    }) {
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
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Progress Indicator
            LinearProgressIndicator(
                progress = { step / 3f },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = LuxeAccentSage,
                trackColor = LuxeHighlightChampagne
            )

            Spacer(Modifier.height(24.dp))

            when (step) {
                1 -> { // Vehicle & Issue
                    Text("Vehicle Details", fontWeight = FontWeight.Black, fontSize = 20.sp, color = LuxeTextPrimary, fontFamily = FontFamily.Serif)
                    Spacer(Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = uiState.vehicleType,
                        onValueChange = { viewModel.updateVehicleType(it) },
                        label = { Text("Vehicle Type (e.g., Honda Activa, Swift)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LuxeAccentSage,
                            unfocusedBorderColor = LuxeBorder
                        )
                    )
                    
                    Spacer(Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = uiState.description,
                        onValueChange = { viewModel.updateDescription(it) },
                        label = { Text("Describe the issue or service needed") },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LuxeAccentSage,
                            unfocusedBorderColor = LuxeBorder
                        )
                    )
                    
                    Spacer(Modifier.height(24.dp))
                    
                    Button(
                        onClick = { step = 2 },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LuxeAccentSage),
                        enabled = uiState.vehicleType.isNotBlank()
                    ) {
                        Text("Next: Select Mechanic", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
                2 -> { // Location & Selection
                    Text("Select Garage / Mechanic", fontWeight = FontWeight.Black, fontSize = 20.sp, color = LuxeTextPrimary, fontFamily = FontFamily.Serif)
                    Spacer(Modifier.height(16.dp))
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = LuxeHighlightChampagne.copy(alpha = 0.4f)),
                        border = BorderStroke(1.dp, LuxeBorder)
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null, tint = LuxeAccentSage)
                            Spacer(Modifier.width(12.dp))
                            Text(uiState.location.ifBlank { "Current Location (Hyderabad)" }, fontWeight = FontWeight.Medium, color = LuxeTextPrimary)
                        }
                    }
                    
                    Spacer(Modifier.height(20.dp))
                    
                    listOf("Expert Garage", "Speedy Repairs", "Moto Masters").forEach { name ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                                .clickable { viewModel.updateDescription("$name selected") },
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, if(uiState.description.contains(name)) LuxeAccentSage else LuxeBorder),
                            colors = CardDefaults.cardColors(containerColor = LuxeCard),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(48.dp).clip(CircleShape).background(LuxeHighlightChampagne),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Build, null, tint = LuxeAccentSage, modifier = Modifier.size(24.dp))
                                }
                                Spacer(Modifier.width(16.dp))
                                Column {
                                    Text(name, fontWeight = FontWeight.Bold, color = LuxeTextPrimary)
                                    Spacer(Modifier.height(2.dp))
                                    Text("4.8 ★ | 2.5 km away • Open Now", fontSize = 12.sp, color = LuxeTextSecondary)
                                }
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = { step = 3 },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LuxeAccentSage)
                    ) {
                        Text("Next: Payment & Review", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
                3 -> { // Payment & Review
                    Text("Service Estimate", fontWeight = FontWeight.Black, fontSize = 20.sp, color = LuxeTextPrimary, fontFamily = FontFamily.Serif)
                    Spacer(Modifier.height(8.dp))
                    val price = uiState.selectedService?.price ?: 750.0
                    Text("₹${price.toInt()}", fontWeight = FontWeight.Black, fontSize = 32.sp, color = LuxeGold)
                    
                    Spacer(Modifier.height(24.dp))
                    Text("Payment Method", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LuxeTextPrimary)
                    Spacer(Modifier.height(8.dp))
                    
                    listOf("Cash After Service", "UPI (GPay / PhonePe)", "Credit / Debit Card").forEach { method ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { viewModel.updatePaymentMethod(method) },
                            shape = RoundedCornerShape(14.dp),
                            color = LuxeCard,
                            border = BorderStroke(1.dp, if (uiState.paymentMethod == method) LuxeAccentSage else LuxeBorder)
                        ) {
                            Row(
                                Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = uiState.paymentMethod == method, 
                                    onClick = { viewModel.updatePaymentMethod(method) },
                                    colors = RadioButtonDefaults.colors(selectedColor = LuxeAccentSage)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(method, fontWeight = FontWeight.Bold, color = LuxeTextPrimary)
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(32.dp))
                    Button(
                        onClick = {
                            if (!isSubmitting) {
                                val selectedService = uiState.selectedService
                                val serviceId = selectedService?.id ?: ""
                                val serviceName = selectedService?.name ?: "Mechanic Service (${uiState.vehicleType})"
                                val amount = selectedService?.price ?: 750.0
                                
                                bookingsViewModel.placeUnifiedOrder(
                                    serviceId = serviceId,
                                    serviceName = serviceName,
                                    category = "Mechanic",
                                    subcategory = uiState.vehicleType,
                                    date = "Today",
                                    time = "As soon as possible",
                                    amount = amount,
                                    paymentMethod = uiState.paymentMethod,
                                    address = uiState.location.ifBlank { "Hyderabad" },
                                    items = emptyList()
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LuxeAccentSage),
                        enabled = !isSubmitting
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Text("Confirm Booking", fontWeight = FontWeight.Black, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }
}
