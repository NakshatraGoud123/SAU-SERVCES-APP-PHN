package com.nisr.sauservices.ui.tech

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import java.net.URLDecoder
import java.net.URLEncoder

private val TechBlue = Color(0xFF2196F3)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechSubCategoryScreen(navController: NavController, category: String) {
    val decodedCategory = URLDecoder.decode(category, "UTF-8")
    
    val subCategories = when (decodedCategory) {
        "Mobile Repair" -> listOf("Screen Issues", "Battery Issues", "Charging Issues", "Software Issues")
        "Laptop & Computer Repair" -> listOf("Hardware Issues", "Software Issues", "Performance", "Accessories")
        "TV & Home Appliance Repair" -> listOf("TV Repair", "Refrigerator", "Washing Machine", "AC Repair")
        "Internet & WiFi Setup" -> listOf("New Setup", "Repair", "Upgrades")
        "CCTV & Security Systems" -> listOf("CCTV Installation", "Maintenance", "Upgrades")
        "Smart Home Installation" -> listOf("Smart Devices", "Security", "Automation")
        else -> emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(decodedCategory, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF7F7F7)
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text(
                text = "Select Sub-Category",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TechBlue
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(subCategories) { sub ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val encoded = URLEncoder.encode(sub, "UTF-8")
                                navController.navigate(Screen.TechServices(encoded))
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                                .heightIn(min = 60.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = sub,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.Black,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
