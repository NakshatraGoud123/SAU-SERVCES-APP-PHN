package com.nisr.sauservices.ui.healthcare

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.PinkPrimary

data class HealthCategory(val name: String, val icon: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthcareCategoryScreen(navController: NavController) {
    val categories = listOf(
        HealthCategory("Lab Tests & Diagnostics", "🔬"),
        HealthCategory("Doctor Consultation", "👨‍⚕️"),
        HealthCategory("Pharmacy & Medicines", "💊"),
        HealthCategory("Home Healthcare Services", "🏠")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Healthcare & Pharmacy", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF9FAFB)
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Search Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Search, null, tint = Color.Gray)
                    Spacer(Modifier.width(12.dp))
                    Text("Search medicines, tests...", color = Color.Gray, fontSize = 14.sp)
                }
            }

            // Hero Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = PinkPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Healthcare", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Medicines & care at your doorstep", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                    }
                    Box(modifier = Modifier.size(56.dp).background(Color.White.copy(alpha = 0.2f), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.MedicalServices, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "What are you looking for?", 
                style = MaterialTheme.typography.titleMedium, 
                fontWeight = FontWeight.Bold, 
                color = Color.Black, 
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(categories) { category ->
                    HealthCategoryCardProfessional(category) {
                        navController.navigate(Screen.HealthcareSubcategories.createRoute(category.name))
                    }
                }
            }
        }
    }
}

@Composable
fun HealthCategoryCardProfessional(category: HealthCategory, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(150.dp).clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                color = PinkPrimary.copy(alpha = 0.05f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(category.icon, fontSize = 32.sp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                category.name, 
                fontSize = 14.sp, 
                fontWeight = FontWeight.Bold, 
                textAlign = TextAlign.Center, 
                color = Color.Black,
                lineHeight = 18.sp
            )
        }
    }
}
