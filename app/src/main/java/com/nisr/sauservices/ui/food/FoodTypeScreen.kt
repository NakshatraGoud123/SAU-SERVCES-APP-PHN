package com.nisr.sauservices.ui.food

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.Screen
import java.net.URLDecoder
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodTypeScreen(navController: NavController, subcategory: String) {
    val decodedSubcategory = URLDecoder.decode(subcategory, "UTF-8")
    
    val types = when (decodedSubcategory) {
        // Home Delivery
        "Veg Meals" -> listOf("South Indian Meals", "North Indian Thali", "Mini Meals", "Festival Meals", "Premium Meals")
        "Non Veg Meals" -> listOf("Chicken Meals", "Mutton Meals", "Fish Meals", "Egg Meals", "Mixed Meals")
        "Fast Food" -> listOf("Burgers", "Pizzas", "Rolls", "Sandwiches", "Snacks")
        
        // Bakery
        "Cakes" -> listOf("Chocolate", "Vanilla", "Fruit", "Red Velvet", "Designer Cakes")
        
        // Beverages
        "Fresh Juices" -> listOf("Orange", "Apple", "Pineapple", "Watermelon", "Mixed Fruit")
        
        // Restaurant
        "Fine Dining" -> listOf("Starters", "Main Course", "Desserts", "Drinks", "Combos")
        
        // Catering
        "Wedding Catering", "Birthday Catering", "Corporate Catering", "Outdoor Catering", "Small Party Catering" -> 
            listOf(decodedSubcategory)

        // Tiffin
        "Daily Veg Tiffin", "Daily Non Veg Tiffin", "Weekly Plan", "Monthly Plan", "Diet Tiffin" -> 
            listOf("Veg Plan", "Non Veg Plan", "Diet Plan", "Office Plan", "Student Plan")

        else -> listOf("Standard", "Premium", "Deluxe")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(decodedSubcategory, color = LuxuryTextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = LuxuryTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxuryBackground)
            )
        },
        containerColor = LuxuryBackground
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text(
                text = "Select Type",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleMedium,
                color = LuxuryTextSecondary,
                fontWeight = FontWeight.Bold
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(types) { type ->
                    FoodTypeCard(type) {
                        val encoded = URLEncoder.encode(type, "UTF-8")
                        navController.navigate(Screen.FoodItems(encoded))
                    }
                }
            }
        }
    }
}

@Composable
fun FoodTypeCard(name: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LuxuryCard),
        border = BorderStroke(1.dp, LuxuryBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LuxuryGold.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Fastfood, contentDescription = null, tint = LuxuryGold, modifier = Modifier.size(30.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = name,
                color = LuxuryTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}
