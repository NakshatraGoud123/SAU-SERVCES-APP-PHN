package com.nisr.sauservices.ui.food

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.data.model.FoodData
import com.nisr.sauservices.data.model.Restaurant
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodMainScreen(navController: NavController) {
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Top Rated", "Offers", "Near Me")
    val restaurants = FoodData.getRestaurantsByFilter(selectedFilter)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Hyderabad, Telangana", color = LuxuryTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LuxuryTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxuryBackground)
            )
        },
        containerColor = LuxuryBackground
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Luxury Search Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(54.dp)
                    .clickable { navController.navigate(Screen.Search) },
                shape = RoundedCornerShape(12.dp),
                color = LuxuryCard,
                border = BorderStroke(1.dp, LuxuryBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, null, tint = LuxuryGold)
                    Spacer(Modifier.width(12.dp))
                    Text("Search for restaurants or food...", color = LuxuryTextSecondary, fontSize = 14.sp, modifier = Modifier.weight(1f))
                    Icon(Icons.Default.FilterList, null, tint = LuxuryGold)
                }
            }

            // Luxury Filters
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter, fontWeight = FontWeight.SemiBold) },
                        shape = RoundedCornerShape(10.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = LuxuryCard,
                            selectedContainerColor = LuxuryGold,
                            labelColor = LuxuryTextSecondary,
                            selectedLabelColor = LuxuryBackground
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = LuxuryBorder,
                            selectedBorderColor = LuxuryGold,
                            borderWidth = 1.dp,
                            enabled = true,
                            selected = selectedFilter == filter
                        )
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(restaurants) { restaurant ->
                    RestaurantLuxuryCard(restaurant) {
                        navController.navigate(Screen.FoodItems(restaurant.id))
                    }
                }
            }
        }
    }
}

@Composable
fun RestaurantLuxuryCard(restaurant: Restaurant, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = LuxuryCard),
        border = BorderStroke(1.dp, LuxuryBorder)
    ) {
        Column {
            // Placeholder for cinematic image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(LuxuryCard),
                contentAlignment = Alignment.Center
            ) {
                if (restaurant.imageRes != null) {
                    Image(
                        painter = painterResource(id = restaurant.imageRes),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Gradient overlay for cinematic look
                    Box(modifier = Modifier.fillMaxSize().background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(Color.Transparent, LuxuryBackground.copy(alpha = 0.6f)),
                            startY = 100f
                        )
                    ))
                } else {
                    Icon(
                        Icons.Default.Restaurant, 
                        null, 
                        tint = LuxuryGold.copy(alpha = 0.2f), 
                        modifier = Modifier.size(60.dp)
                    )
                }
                if (restaurant.offers.isNotEmpty()) {
                    Surface(
                        modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
                        color = LuxuryGold,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            restaurant.offers, 
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = LuxuryBackground,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        restaurant.name, 
                        color = LuxuryTextPrimary, 
                        fontWeight = FontWeight.ExtraBold, 
                        fontSize = 20.sp
                    )
                    Surface(
                        color = LuxuryGold.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, null, tint = LuxuryGold, modifier = Modifier.size(12.dp))
                            Text(restaurant.rating.toString(), color = LuxuryGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                Spacer(Modifier.height(4.dp))
                
                Text(
                    restaurant.cuisine, 
                    color = LuxuryTextSecondary, 
                    fontSize = 13.sp,
                    maxLines = 1
                )
                
                Spacer(Modifier.height(12.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timer, null, tint = LuxuryGold, modifier = Modifier.size(14.dp))
                    Text(" ${restaurant.deliveryTime}", color = LuxuryTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Text(" • ", color = LuxuryBorder)
                    Text(restaurant.costForTwo, color = LuxuryTextSecondary, fontSize = 12.sp)
                }
            }
        }
    }
}
