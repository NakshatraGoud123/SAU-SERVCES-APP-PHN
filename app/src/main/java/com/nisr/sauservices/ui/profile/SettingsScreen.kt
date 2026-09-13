package com.nisr.sauservices.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Black, color = LuxeTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = LuxeTextPrimary)
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
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "PREFERENCES",
                style = MaterialTheme.typography.labelSmall,
                color = LuxeTextSecondary,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )

            SettingsItem("Notifications", Icons.Default.Notifications) {
                navController.navigate(Screen.Notifications)
            }
            SettingsItem("Password & Security", Icons.Default.Lock) {
                navController.navigate(Screen.ChangePassword)
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "CONNECT WITH US",
                style = MaterialTheme.typography.labelSmall,
                color = LuxeTextSecondary,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )
            
            SettingsItem("Follow on Instagram", Icons.Default.Share) { /* Open Link */ }
            SettingsItem("Rate the App", Icons.Default.Star) { /* Open Store */ }
            SettingsItem("Help & Support", Icons.Default.Help) {
                navController.navigate(Screen.ContactUs)
            }
            
            Spacer(Modifier.height(16.dp))
            
            Text(
                "LEGAL & INFO",
                style = MaterialTheme.typography.labelSmall,
                color = LuxeTextSecondary,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )
            
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = LuxeCard,
                border = BorderStroke(1.dp, LuxeBorder)
            ) {
                Column(Modifier.padding(20.dp)) {
                    InfoRow("Version", "1.0.8 LUXE")
                    HorizontalDivider(color = LuxeBorder, modifier = Modifier.padding(vertical = 12.dp))
                    InfoRow("Build", "2026.09.13")
                    HorizontalDivider(color = LuxeBorder, modifier = Modifier.padding(vertical = 12.dp))
                    Text(
                        "Developed with care by SAU Solutions. All rights reserved.",
                        fontSize = 11.sp,
                        color = LuxeTextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
private fun SettingsItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = LuxeCard,
        border = BorderStroke(1.dp, LuxeBorder)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                color = LuxeHighlightChampagne.copy(alpha = 0.5f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = LuxeTextPrimary, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title, 
                color = LuxeTextPrimary, 
                fontWeight = FontWeight.Bold, 
                fontSize = 15.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.ChevronRight, null, tint = LuxeTextSecondary, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = LuxeTextSecondary, fontSize = 14.sp)
        Text(value, color = LuxeTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}
