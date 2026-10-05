package com.nisr.sauservices.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.ui.home.*
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.education.EducationBottomSheet
import com.nisr.sauservices.ui.business.BusinessBottomSheet
import com.nisr.sauservices.ui.lifestyle.LifestyleBottomSheet
import com.nisr.sauservices.ui.tech.TechBottomSheet
import com.nisr.sauservices.ui.mechanic.MechanicBottomSheet
import com.nisr.sauservices.ui.mobility.MobilityBottomSheet

@Composable
fun CustomerHomeScreen(
    navController: NavController, 
    sessionManager: SessionManager
) {
    var showEduSheet by remember { mutableStateOf(false) }
    var showBizSheet by remember { mutableStateOf(false) }
    var showLifeSheet by remember { mutableStateOf(false) }
    var showTechSheet by remember { mutableStateOf(false) }
    var showMechanicSheet by remember { mutableStateOf(false) }
    var showMobilitySheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBarUI(navController, sessionManager) },
        bottomBar = { BottomNavBar(navController) },
        containerColor = LuxuryBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            HeroBanner(navController)
            
            SearchBarUI(navController)

            Spacer(Modifier.height(24.dp))
            
            CategoriesGrid(navController = navController)

            Spacer(Modifier.height(24.dp))
            
            // Expert & Utility Services Shortcuts
            Text(
                text = "EXPERT & UTILITY SERVICES",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Black,
                color = LuxeTextPrimary,
                letterSpacing = 1.5.sp
            )
            
            Spacer(Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ServiceShortcutCard("Mechanic", Icons.Default.Build, Modifier.weight(1f)) {
                    showMechanicSheet = true
                }
                ServiceShortcutCard("Mobility", Icons.Default.LocalTaxi, Modifier.weight(1f)) {
                    showMobilitySheet = true
                }
                ServiceShortcutCard("Tech", Icons.Default.Devices, Modifier.weight(1f)) {
                    showTechSheet = true
                }
                ServiceShortcutCard("Education", Icons.Default.School, Modifier.weight(1f)) {
                    showEduSheet = true
                }
            }

            Spacer(Modifier.height(24.dp))
            
            ValuePropositionsRow()

            PopularServicesSection(navController)

            HowItWorks()

            OfferBanner()
            
            Spacer(Modifier.height(20.dp))
        }
    }

    if (showEduSheet) {
        EducationBottomSheet(
            navController = navController,
            onDismiss = { showEduSheet = false }
        )
    }

    if (showBizSheet) {
        BusinessBottomSheet(
            navController = navController,
            onDismiss = { showBizSheet = false }
        )
    }

    if (showLifeSheet) {
        LifestyleBottomSheet(
            navController = navController,
            onDismiss = { showLifeSheet = false }
        )
    }

    if (showTechSheet) {
        TechBottomSheet(
            navController = navController,
            onDismiss = { showTechSheet = false }
        )
    }

    if (showMechanicSheet) {
        MechanicBottomSheet(
            navController = navController,
            onDismiss = { showMechanicSheet = false }
        )
    }

    if (showMobilitySheet) {
        MobilityBottomSheet(
            navController = navController,
            onDismiss = { showMobilitySheet = false }
        )
    }
}

@Composable
fun ServiceShortcutCard(title: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .height(90.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = LuxeCard,
        border = BorderStroke(1.dp, LuxeBorder),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(LuxeHighlightChampagne.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = LuxeAccentSage, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = LuxeTextPrimary,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }
}
