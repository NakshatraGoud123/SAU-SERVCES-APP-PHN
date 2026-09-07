package com.nisr.sauservices.ui.healthcare

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.viewmodel.HealthcareViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionUploadScreen(navController: NavController, viewModel: HealthcareViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upload Prescription", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = { navController.navigate(Screen.HealthcareBooking) },
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                enabled = viewModel.prescriptionImageUri.value != null
            ) {
                Text("Continue to Booking", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(80.dp), tint = Color(0xFF1976D2))
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Prescription Required", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                "A valid doctor's prescription is required for one or more items in your cart. Please upload a clear photo of your prescription.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFFF1F8E9), RoundedCornerShape(16.dp))
                    .border(2.dp, Color(0xFF1976D2), RoundedCornerShape(16.dp))
                    .clickable { 
                        // Simulate file pick
                        viewModel.prescriptionImageUri.value = "dummy_path_to_image"
                    },
                contentAlignment = Alignment.Center
            ) {
                if (viewModel.prescriptionImageUri.value == null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Tap to Upload", fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
                        Text("JPEG, PNG or PDF", fontSize = 12.sp, color = Color.Gray)
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF43A047), modifier = Modifier.size(40.dp))
                        Text("Prescription Uploaded Successfully", fontWeight = FontWeight.Bold, color = Color(0xFF43A047))
                        Text("Tap to change", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Your data is secure and will only be shared with verified pharmacies.", fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}
