package com.nisr.sauservices.ui.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nisr.sauservices.ui.viewmodel.ProfileViewModel
import com.nisr.sauservices.ui.components.LuxuryButton

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactUsScreen(navController: NavController, viewModel: ProfileViewModel = viewModel()) {
    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contact Us", fontWeight = FontWeight.Black, color = LuxeTextPrimary) },
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
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Support Info Cards
            ContactInfoCard(
                icon = Icons.Default.Email,
                title = "Support Email",
                content = "support@sauservices.com"
            )
            ContactInfoCard(
                icon = Icons.Default.Phone,
                title = "Support Phone",
                content = "+91 98765 43210"
            )
            ContactInfoCard(
                icon = Icons.Default.Schedule,
                title = "Working Hours",
                content = "Mon - Sat: 9:00 AM - 8:00 PM"
            )

            Spacer(Modifier.height(16.dp))

            Text("Submit a Request", fontWeight = FontWeight.Black, fontSize = 18.sp, color = LuxeTextPrimary)

            OutlinedTextField(
                value = subject,
                onValueChange = { subject = it },
                label = { Text("Subject", color = LuxeTextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LuxeAccentSage,
                    unfocusedBorderColor = LuxeBorder,
                    focusedLabelColor = LuxeAccentSage
                )
            )

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Message", color = LuxeTextSecondary) },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LuxeAccentSage,
                    unfocusedBorderColor = LuxeBorder,
                    focusedLabelColor = LuxeAccentSage
                )
            )

            LuxuryButton(
                text = "SUBMIT REQUEST",
                onClick = {
                    if (subject.isNotEmpty() && message.isNotEmpty()) {
                        viewModel.submitSupportMessage(subject, message)
                        Toast.makeText(context, "Request submitted successfully!", Toast.LENGTH_SHORT).show()
                        subject = ""
                        message = ""
                    } else {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.height(50.dp)
            )
        }
    }
}

@Composable
fun ContactInfoCard(icon: ImageVector, title: String, content: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = LuxeCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, LuxeBorder)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(LuxeHighlightChampagne.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = LuxeTextPrimary, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, fontSize = 12.sp, color = LuxeTextSecondary, fontWeight = FontWeight.Bold)
                Text(content, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = LuxeTextPrimary)
            }
        }
    }
}
