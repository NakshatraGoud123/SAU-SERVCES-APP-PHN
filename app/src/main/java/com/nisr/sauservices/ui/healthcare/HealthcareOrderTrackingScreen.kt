package com.nisr.sauservices.ui.healthcare

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.viewmodel.HealthcareViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthcareOrderTrackingScreen(navController: NavController, viewModel: HealthcareViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Track Appointment", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Service Status", fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
                    Spacer(Modifier.height(16.dp))
                    
                    TrackingStep("Order Confirmed", "Your request has been accepted", true)
                    TrackingStep("Professional Assigned", "Dr. Sameer (General Physician)", true)
                    TrackingStep("In Transit", "Estimated arrival in 15 mins", false)
                    TrackingStep("Service Started", "Waiting for professional to arrive", false)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(50.dp).background(Color.LightGray, CircleShape))
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Dr. Sameer", fontWeight = FontWeight.Bold)
                        Text("General Physician • 4.8 ⭐", fontSize = 12.sp, color = Color.Gray)
                    }
                    IconButton(onClick = { /* Call logic */ }, colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFFE8F5E9))) {
                        Icon(Icons.Default.Call, contentDescription = null, tint = Color(0xFF43A047))
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = { navController.navigate(Screen.Home) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
            ) {
                Text("Back to Dashboard", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun TrackingStep(title: String, subtitle: String, isCompleted: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.Top) {
        Icon(
            Icons.Default.CheckCircle, 
            contentDescription = null, 
            tint = if (isCompleted) Color(0xFF43A047) else Color.LightGray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, color = if (isCompleted) Color.Black else Color.Gray)
            Text(subtitle, fontSize = 12.sp, color = Color.Gray)
        }
    }
}
