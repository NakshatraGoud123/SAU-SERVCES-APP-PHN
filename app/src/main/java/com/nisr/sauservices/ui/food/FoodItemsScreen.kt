package com.nisr.sauservices.ui.food

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.nisr.sauservices.data.model.FoodItem
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.viewmodel.FoodCartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodItemsScreen(navController: NavController, restaurantId: String, viewModel: FoodCartViewModel) {
    val restaurant = FoodData.restaurants.find { it.id == restaurantId }
    val menuItems = FoodData.getMenuByRestaurant(restaurantId)
    val categories = restaurant?.categories ?: listOf("All")
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull() ?: "All") }

    val filteredItems = if (selectedCategory == "All") menuItems else menuItems.filter { it.category == selectedCategory }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(restaurant?.name ?: "Menu", color = LuxuryTextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LuxuryTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxuryBackground)
            )
        },
        containerColor = LuxuryBackground,
        bottomBar = {
            if (viewModel.cartItems.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 16.dp,
                    color = LuxuryCard,
                    border = BorderStroke(1.dp, LuxuryBorder),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp).navigationBarsPadding(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            val count = viewModel.cartItems.sumOf { it.quantity }
                            Text("$count items added", fontSize = 13.sp, color = LuxuryTextSecondary)
                            Text("₹${viewModel.getTotal()}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = LuxuryGold)
                        }
                        Button(
                            onClick = { navController.navigate(Screen.FoodCart) },
                            colors = ButtonDefaults.buttonColors(containerColor = LuxuryGold),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("View Cart", color = LuxuryBackground, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Luxury Category Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category, fontWeight = FontWeight.SemiBold) },
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
                            selectedBorderWidth = 1.dp,
                            enabled = true,
                            selected = selectedCategory == category
                        )
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(filteredItems) { item ->
                    FoodLuxuryItemCard(item, viewModel)
                }
            }
        }
    }
}

@Composable
fun FoodLuxuryItemCard(item: FoodItem, viewModel: FoodCartViewModel) {
    val cartItem = viewModel.cartItems.find { it.id == item.id }
    val quantity = cartItem?.quantity ?: 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LuxuryCard),
        border = BorderStroke(1.dp, LuxuryBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .border(1.dp, if (item.isVeg) Color(0xFF2E7D32) else Color(0xFFE11D48))
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(if (item.isVeg) Color(0xFF2E7D32) else Color(0xFFE11D48))
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(item.name, color = LuxuryTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(Modifier.height(4.dp))
                Text("₹${item.price}", color = LuxuryGold, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                if (item.description.isNotEmpty()) {
                    Text(item.description, fontSize = 12.sp, color = LuxuryTextSecondary, maxLines = 2)
                }
            }

            Spacer(Modifier.width(16.dp))

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LuxuryBorder),
                contentAlignment = Alignment.Center
            ) {
                if (item.imageRes != null) {
                    Image(
                        painter = painterResource(id = item.imageRes),
                        contentDescription = item.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.Restaurant, 
                        null, 
                        tint = LuxuryGold.copy(alpha = 0.2f),
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                // ADD / Qty Button Overlay
                Box(
                    modifier = Modifier.align(Alignment.BottomCenter).offset(y = 10.dp)
                ) {
                    if (quantity == 0) {
                        Button(
                            onClick = { viewModel.addItem(item.id, item.name, item.price) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LuxuryBackground),
                            border = BorderStroke(1.dp, LuxuryGold),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("ADD", color = LuxuryGold, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .height(30.dp)
                                .background(LuxuryGold, RoundedCornerShape(8.dp))
                                .padding(horizontal = 2.dp)
                        ) {
                            IconButton(onClick = { viewModel.decreaseQty(item.id) }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Remove, null, tint = LuxuryBackground, modifier = Modifier.size(14.dp))
                            }
                            Text(
                                quantity.toString(), 
                                color = LuxuryBackground, 
                                fontWeight = FontWeight.ExtraBold, 
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            IconButton(onClick = { viewModel.increaseQty(item.id) }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Add, null, tint = LuxuryBackground, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
