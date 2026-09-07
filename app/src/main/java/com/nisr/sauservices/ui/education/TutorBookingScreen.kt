package com.nisr.sauservices.ui.education

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.nisr.sauservices.ui.viewmodel.EducationCartViewModel

private val EduPrimary = Color(0xFF1565C0)
private val EduAccent = Color(0xFFE3F2FD)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorBookingScreen(navController: NavController, viewModel: EducationCartViewModel) {
    var studentName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var preferredTiming by remember { mutableStateOf("Morning") }
    var mode by remember { mutableStateOf("Online") }
    
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tutor Booking", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EduAccent)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Selected Courses", fontWeight = FontWeight.Bold, color = EduPrimary)
                    viewModel.cartItems.forEach { item ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${item.name} x${item.quantity}", fontSize = 14.sp)
                            Text("₹${item.price * item.quantity}", fontWeight = FontWeight.Bold)
                        }
                    }
                    Divider(Modifier.padding(vertical = 8.dp), color = EduPrimary.copy(alpha = 0.2f))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Amount", fontWeight = FontWeight.Bold)
                        Text("₹${viewModel.getTotal()}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = EduPrimary)
                    }
                }
            }

            Text("Contact Details", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            
            OutlinedTextField(
                value = studentName,
                onValueChange = { studentName = it },
                label = { Text("Student Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = EduPrimary) }
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = EduPrimary) }
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = EduPrimary) },
                minLines = 3
            )

            Text("Preferences", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Timing:", modifier = Modifier.width(100.dp), fontWeight = FontWeight.Medium)
                AssistChip(
                    onClick = { preferredTiming = "Morning" },
                    label = { Text("Morning") },
                    leadingIcon = { if(preferredTiming == "Morning") Icon(Icons.Default.Check, null, Modifier.size(18.dp)) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if(preferredTiming == "Morning") EduAccent else Color.Transparent,
                        labelColor = if(preferredTiming == "Morning") EduPrimary else Color.Gray
                    )
                )
                Spacer(Modifier.width(8.dp))
                AssistChip(
                    onClick = { preferredTiming = "Evening" },
                    label = { Text("Evening") },
                    leadingIcon = { if(preferredTiming == "Evening") Icon(Icons.Default.Check, null, Modifier.size(18.dp)) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if(preferredTiming == "Evening") EduAccent else Color.Transparent,
                        labelColor = if(preferredTiming == "Evening") EduPrimary else Color.Gray
                    )
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Mode:", modifier = Modifier.width(100.dp), fontWeight = FontWeight.Medium)
                AssistChip(
                    onClick = { mode = "Online" },
                    label = { Text("Online") },
                    leadingIcon = { if(mode == "Online") Icon(Icons.Default.Check, null, Modifier.size(18.dp)) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if(mode == "Online") EduAccent else Color.Transparent,
                        labelColor = if(mode == "Online") EduPrimary else Color.Gray
                    )
                )
                Spacer(Modifier.width(8.dp))
                AssistChip(
                    onClick = { mode = "Offline" },
                    label = { Text("Offline") },
                    leadingIcon = { if(mode == "Offline") Icon(Icons.Default.Check, null, Modifier.size(18.dp)) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if(mode == "Offline") EduAccent else Color.Transparent,
                        labelColor = if(mode == "Offline") EduPrimary else Color.Gray
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { 
                    navController.navigate(Screen.EducationSuccess) 
                    viewModel.clearCart()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EduPrimary),
                enabled = studentName.isNotBlank() && phoneNumber.isNotBlank()
            ) {
                Text("Confirm Booking", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
