package com.nisr.sauservices.ui.healthcare

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontFamily
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.components.*
import com.nisr.sauservices.ui.theme.*

data class HealthCategory(val id: String, val name: String, val icon: String, val color: Color)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthcareCategoryScreen(navController: NavController) {
    val categories = listOf(
        HealthCategory("lab", "Lab Tests & Diagnostics", "🔬", SuccessGreen),
        HealthCategory("doctor", "Doctor Consultation", "👨‍⚕️", Color(0xFF4D8DFF)),
        HealthCategory("pharmacy", "Pharmacy & Medicines", "💊", Color(0xFFFF8A3D)),
        HealthCategory("homecare", "Home Healthcare", "🏠", LuxuryGold)
    )

    LuxuryScaffold(
        title = "Healthcare",
        onBackClick = { navController.popBackStack() }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Luxury Search Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                color = LuxuryCard,
                border = BorderStroke(1.dp, LuxuryBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Search, null, tint = LuxuryGold)
                    Spacer(Modifier.width(12.dp))
                    Text("Search medicines, tests...", color = LuxuryTextSecondary, fontSize = 14.sp)
                }
            }

            // Luxury Hero Card for Pharmacy (since user asked for better pharmacy)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { navController.navigate(Screen.HealthcareSubcategories("Pharmacy & Medicines")) },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A18)),
                border = BorderStroke(1.dp, LuxuryGold.copy(alpha = 0.3f))
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "SAU PHARMACY", 
                            color = LuxuryGold, 
                            fontSize = 12.sp, 
                            fontWeight = FontWeight.Black, 
                            letterSpacing = 2.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Order Medicines", 
                            color = White, 
                            fontSize = 20.sp, 
                            fontWeight = FontWeight.ExtraBold, 
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            "Genuine medicines delivered in 60 mins", 
                            color = LuxuryTextSecondary, 
                            fontSize = 11.sp, 
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFFE8C66A).copy(alpha = 0.1f), CircleShape), 
                        contentAlignment = Alignment.Center
                    ) {
                        Text("💊", fontSize = 28.sp)
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            Text(
                "CURATED CARE", 
                style = MaterialTheme.typography.labelSmall, 
                fontWeight = FontWeight.Black, 
                color = LuxuryGold, 
                letterSpacing = 2.sp,
                modifier = Modifier.padding(horizontal = 18.dp)
            )
            Spacer(Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(categories) { category ->
                    HealthLuxuryCategoryCard(category) {
                        navController.navigate(Screen.HealthcareSubcategories(category.name))
                    }
                }
            }
        }
    }
}

@Composable
fun HealthLuxuryCategoryCard(category: HealthCategory, onClick: () -> Unit) {
    LuxuryCard(onClick = onClick) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = category.color.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(category.icon, fontSize = 24.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                category.name, 
                fontSize = 14.sp, 
                fontWeight = FontWeight.ExtraBold, 
                color = LuxuryTextPrimary,
                lineHeight = 18.sp,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Explore Services", 
                fontSize = 10.sp, 
                fontWeight = FontWeight.Bold, 
                color = LuxuryGold
            )
        }
    }
}
