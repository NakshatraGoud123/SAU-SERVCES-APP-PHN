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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.ThemeManager
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.components.LuxuryButton

// ============================================================
// LUXE BRAND COLORS
// ============================================================
// Brand color mappings already provided by MaterialTheme.colorScheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        val uriHandler = LocalUriHandler.current

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "APPEARANCE",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )

            AppearanceSelector(sessionManager)

            Spacer(Modifier.height(16.dp))

            Text(
                "PREFERENCES",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
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
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )
            
            SettingsItem("Follow on Instagram", Icons.Default.Share) { 
                uriHandler.openUri("https://www.instagram.com/sausolutions") 
            }
            SettingsItem("Rate the App", Icons.Default.Star) { 
                uriHandler.openUri("https://play.google.com/store/apps/details?id=com.nisr.sauservices") 
            }
            SettingsItem("Help & Support", Icons.Default.Help) {
                navController.navigate(Screen.ContactUs)
            }
            
            Spacer(Modifier.height(16.dp))
            
            Text(
                "ABOUT SAU SOLUTIONS",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )
            
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shadowElevation = 2.dp
            ) {
                Column(Modifier.padding(24.dp)) {
                    Text(
                        "SAU Solutions is your premium partner for all lifestyle and home management needs. We connect discerning members with verified professionals and local boutiques for a seamless daily experience.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                    
                    Spacer(Modifier.height(24.dp))
                    
                    InfoRow("Support Email", "care@sausolutions.com")
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(vertical = 12.dp))
                    InfoRow("Concierge Line", "+91 800-SAU-LUXE")
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(vertical = 12.dp))
                    InfoRow("Version", "1.1.0 LUXE")
                    
                    Spacer(Modifier.height(24.dp))
                    
                    Text(
                        "Developed with care for a better tomorrow.\n© 2026 SAU Solutions. All rights reserved.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
private fun AppearanceSelector(sessionManager: SessionManager) {
    val currentTheme = ThemeManager.themeConfig
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            ThemeOptionRow(
                title = "System Default",
                selected = currentTheme == SessionManager.ThemeConfig.SYSTEM,
                onClick = { ThemeManager.updateTheme(SessionManager.ThemeConfig.SYSTEM, sessionManager) }
            )
            ThemeOptionRow(
                title = "Light",
                selected = currentTheme == SessionManager.ThemeConfig.LIGHT,
                onClick = { ThemeManager.updateTheme(SessionManager.ThemeConfig.LIGHT, sessionManager) }
            )
            ThemeOptionRow(
                title = "Dark",
                selected = currentTheme == SessionManager.ThemeConfig.DARK,
                onClick = { ThemeManager.updateTheme(SessionManager.ThemeConfig.DARK, sessionManager) }
            )
        }
    }
}

@Composable
private fun ThemeOptionRow(title: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
        )
    }
}

@Composable
private fun SettingsItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title, 
                color = MaterialTheme.colorScheme.onSurface, 
                fontWeight = FontWeight.Bold, 
                fontSize = 15.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        Text(value, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}
