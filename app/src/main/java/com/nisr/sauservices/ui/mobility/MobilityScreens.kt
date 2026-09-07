package com.nisr.sauservices.ui.mobility

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.nisr.sauservices.data.model.MobilityData
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.viewmodel.MobilityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobilityMainScreen(navController: NavController, viewModel: MobilityViewModel) {
    val uiState by viewModel.uiState
    var step by remember { mutableIntStateOf(1) }
    
    val mobilityBlue = Color(0xFF2196F3)
    val lightBlue = Color(0xFFE3F2FD)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mobility Services", fontWeight = FontWeight.Bold) },
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
        ) {
            when (step) {
                1 -> { // Select Service Type
                    Text(
                        "Where are you going?", 
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.Bold, 
                        fontSize = 22.sp
                    )
                    
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(MobilityData.serviceTypes) { type ->
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable { 
                                    viewModel.selectServiceType(type)
                                    step = 2
                                },
                                colors = CardDefaults.cardColors(containerColor = lightBlue),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(type.icon, null, tint = mobilityBlue, modifier = Modifier.size(32.dp))
                                    Spacer(Modifier.width(16.dp))
                                    Column {
                                        Text(type.name, fontWeight = FontWeight.Bold)
                                        Text(type.description, fontSize = 12.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> { // Set Locations
                    Column(Modifier.padding(16.dp)) {
                        Text("Plan your ride", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(Modifier.height(16.dp))
                        
                        OutlinedTextField(
                            value = uiState.pickupLocation,
                            onValueChange = { viewModel.setLocations(it, uiState.destinationLocation) },
                            label = { Text("Pickup Location") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.MyLocation, null, tint = mobilityBlue) },
                            shape = RoundedCornerShape(12.dp)
                        )
                        
                        Spacer(Modifier.height(12.dp))
                        
                        OutlinedTextField(
                            value = uiState.destinationLocation,
                            onValueChange = { viewModel.setLocations(uiState.pickupLocation, it) },
                            label = { Text("Destination Location") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = Color.Red) },
                            shape = RoundedCornerShape(12.dp)
                        )
                        
                        Spacer(Modifier.height(24.dp))
                        
                        if (uiState.pickupLocation.isNotBlank() && uiState.destinationLocation.isNotBlank()) {
                            Text("Fare Estimate: ₹${uiState.fareEstimate}", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = mobilityBlue)
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = { step = 3 },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = mobilityBlue)
                            ) {
                                Text("Find Drivers")
                            }
                        }
                    }
                }
                3 -> { // Driver Selection & Confirm
                    Column(Modifier.padding(16.dp)) {
                        Text("Nearby Drivers", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        
                        // Mock Driver List
                        listOf("Rahul (4.9 ★)", "Amit (4.7 ★)", "Suresh (4.8 ★)").forEach { driver ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                                    .clickable { viewModel.startTracking() },
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Person, null, tint = mobilityBlue, modifier = Modifier.size(40.dp))
                                    Spacer(Modifier.width(16.dp))
                                    Column {
                                        Text(driver, fontWeight = FontWeight.Bold)
                                        Text("2 mins away", fontSize = 12.sp, color = Color.Gray)
                                    }
                                    Spacer(Modifier.weight(1f))
                                    Text("₹${uiState.fareEstimate}", fontWeight = FontWeight.Bold, color = mobilityBlue)
                                }
                            }
                        }
                        
                        Spacer(Modifier.height(24.dp))
                        Button(
                            onClick = { navController.navigate(Screen.MobilitySuccess) },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = mobilityBlue)
                        ) {
                            Text("Confirm Booking")
                        }
                    }
                }
            }
        }
    }
}
