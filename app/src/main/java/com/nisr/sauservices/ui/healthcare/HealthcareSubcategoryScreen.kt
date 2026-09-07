package com.nisr.sauservices.ui.healthcare

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
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
import com.nisr.sauservices.ui.components.*
import com.nisr.sauservices.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthcareSubcategoryScreen(navController: NavController, categoryName: String) {
    val subcategories = when (categoryName) {
        "Lab Tests & Diagnostics" -> listOf("Blood Tests", "Health Profiles", "Vitamin Tests")
        "Doctor Consultation" -> listOf("General Doctors", "Consultation Modes")
        "Pharmacy & Medicines" -> listOf("Medicine Orders", "Health Products", "Personal Care", "Medical Devices")
        "Home Healthcare", "Home Healthcare Services" -> listOf("Care Services", "Medical Support", "Equipment Rental")
        else -> emptyList()
    }

    LuxuryScaffold(
        title = categoryName.uppercase(),
        onBackClick = { navController.popBackStack() }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Select a sub-category",
                    style = MaterialTheme.typography.labelSmall,
                    color = LuxuryTextSecondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
            
            items(subcategories) { sub ->
                HealthSubcategoryLuxuryCard(sub) {
                    navController.navigate(Screen.HealthcareServices(sub))
                }
            }
            
            if (subcategories.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text("No subcategories found for $categoryName", color = LuxuryTextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
fun HealthSubcategoryLuxuryCard(name: String, onClick: () -> Unit) {
    LuxuryCard(onClick = onClick) {
        Row(
            modifier = Modifier.padding(22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name, 
                fontSize = 16.sp, 
                fontWeight = FontWeight.ExtraBold, 
                color = LuxuryTextPrimary
            )
            Icon(
                imageVector = Icons.Default.ChevronRight, 
                contentDescription = null, 
                tint = LuxuryGold,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
