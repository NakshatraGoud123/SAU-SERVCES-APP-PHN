package com.nisr.sauservices.ui.womens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WomensBeautySubcategoryScreen(navController: NavController, categoryName: String) {
    val subcategories = when (categoryName) {
        "Hair Services" -> listOf("Hair Cut & Styling", "Hair Treatments", "Hair Coloring")
        "Facial & Cleanup" -> listOf("Facials", "Cleanup")
        "Waxing" -> listOf("Body Wax", "Special Wax")
        "Threading & Face" -> listOf("Threading", "Face Services")
        "Manicure & Pedicure" -> listOf("Manicure", "Pedicure")
        "Makeup & Styling" -> listOf("Makeup", "Hair Styling")
        "Spa & Massage" -> listOf("Massage", "Spa")
        "Bridal & Premium" -> listOf("Bridal", "Premium Packages")
        else -> emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(categoryName, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFFFFBFD)
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(subcategories) { sub ->
                SubcategoryItem(sub) {
                    navController.navigate(Screen.WomensBeautyServices(sub))
                }
            }
        }
    }
}

@Composable
fun SubcategoryItem(name: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(name, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
        }
    }
}
