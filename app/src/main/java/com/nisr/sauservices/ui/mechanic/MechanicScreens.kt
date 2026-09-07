package com.nisr.sauservices.ui.mechanic

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.nisr.sauservices.data.model.MechanicData
import com.nisr.sauservices.data.model.MechanicSubcategory
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.viewmodel.MechanicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MechanicSubcategoryScreen(navController: NavController, categoryName: String, viewModel: MechanicViewModel) {
    val category = MechanicData.categories.find { it.name == categoryName }
    val showAll = categoryName == "Mechanic Services" || categoryName == "Mechanical Kit"
    
    val subcategories = if (showAll) {
        emptyList() // We'll show categories instead
    } else {
        MechanicData.subcategories.filter { it.categoryId == category?.id }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (showAll) "Mechanic Services" else categoryName, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF7F7F7)
    ) { padding ->
        if (showAll) {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(MechanicData.categories) { cat ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable {
                            navController.navigate(Screen.MechanicSubcategories(cat.name))
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(cat.icon, null, tint = Color(0xFF1E6355), modifier = Modifier.size(24.dp))
                                Spacer(Modifier.width(16.dp))
                                Text(cat.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E6355))
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF2D9F88))
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(subcategories) { sub ->
                    MechanicSubcategoryCard(sub) {
                        viewModel.updateSubcategoryId(sub.id)
                        navController.navigate(Screen.MechanicBooking)
                    }
                }
            }
        }
    }
}

@Composable
fun MechanicSubcategoryCard(subcategory: MechanicSubcategory, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(subcategory.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E6355))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF2D9F88))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MechanicBookingScreen(navController: NavController, viewModel: MechanicViewModel) {
    val uiState by viewModel.bookingState
    var step by remember { mutableIntStateOf(1) }
    
    val garageGreen = Color(0xFF1E6355)
    val lightGreen = Color(0xFFE8F3F1)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Mechanic", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { 
                        if (step > 1) step-- else navController.popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Progress Indicator
            LinearProgressIndicator(
                progress = { step / 4f },
                modifier = Modifier.fillMaxWidth().height(8.dp).padding(bottom = 24.dp),
                color = garageGreen,
                trackColor = lightGreen
            )

            when (step) {
                1 -> { // Vehicle & Issue
                    Text("Vehicle Details", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = garageGreen)
                    Spacer(Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = uiState.vehicleType,
                        onValueChange = { viewModel.updateVehicleType(it) },
                        label = { Text("Vehicle Type (e.g., Honda Activa, Swift)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    Spacer(Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = uiState.description,
                        onValueChange = { viewModel.updateDescription(it) },
                        label = { Text("Describe the issue") },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    Spacer(Modifier.height(16.dp))
                    
                    Button(
                        onClick = { step = 2 },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = garageGreen),
                        enabled = uiState.vehicleType.isNotBlank()
                    ) {
                        Text("Next: Location & Mechanics")
                    }
                }
                2 -> { // Location & Selection
                    Text("Confirm Location", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = garageGreen)
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = lightGreen)
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null, tint = garageGreen)
                            Spacer(Modifier.width(12.dp))
                            Text(uiState.location, fontWeight = FontWeight.Medium)
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    Text("Select Mechanic", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = garageGreen)
                    
                    // Mock Mechanic Selection
                    listOf("Expert Garage", "Speedy Repairs", "Moto Masters").forEach { name ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                                .clickable { viewModel.updateDescription("$name selected") },
                            border = BorderStroke(1.dp, if(uiState.description.contains(name)) garageGreen else Color.Transparent),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Build, null, tint = garageGreen, modifier = Modifier.size(40.dp))
                                Spacer(Modifier.width(16.dp))
                                Column {
                                    Text(name, fontWeight = FontWeight.Bold)
                                    Text("4.8 ★ | 2.5 km away", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = { step = 3 },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = garageGreen)
                    ) {
                        Text("Next: Payment & Review")
                    }
                }
                3 -> { // Payment & Review
                    Text("Service Estimate", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = garageGreen)
                    Text("Estimated Cost: ₹750", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = garageGreen)
                    
                    Spacer(Modifier.height(24.dp))
                    Text("Payment Method", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    
                    listOf("Cash", "UPI", "Card").forEach { method ->
                        Row(
                            Modifier.fillMaxWidth().clickable { viewModel.updatePaymentMethod(method) }.padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = uiState.paymentMethod == method, onClick = null, colors = RadioButtonDefaults.colors(selectedColor = garageGreen))
                            Spacer(Modifier.width(12.dp))
                            Text(method)
                        }
                    }
                    
                    Spacer(Modifier.height(32.dp))
                    Button(
                        onClick = { navController.navigate(Screen.MechanicSuccess) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = garageGreen)
                    ) {
                        Text("Confirm Booking")
                    }
                }
            }
        }
    }
}
