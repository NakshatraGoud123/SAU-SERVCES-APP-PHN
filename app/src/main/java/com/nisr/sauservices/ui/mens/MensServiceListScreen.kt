package com.nisr.sauservices.ui.mens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.PinkPrimary
import com.nisr.sauservices.ui.viewmodel.MensGroomingService
import com.nisr.sauservices.ui.viewmodel.MensGroomingViewModel
import java.net.URLDecoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MensServiceListScreen(navController: NavController, subcategory: String, viewModel: MensGroomingViewModel) {
    val decodedSub = URLDecoder.decode(subcategory, "UTF-8")
    val services = getServicesForSubcategory(decodedSub)
    val cartItems = viewModel.cartItems

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(decodedSub, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    BadgedBox(
                        badge = {
                            if (cartItems.isNotEmpty()) {
                                Badge(containerColor = PinkPrimary) { Text(cartItems.size.toString(), color = Color.White) }
                            }
                        },
                        modifier = Modifier.padding(end = 16.dp).clickable { navController.navigate(Screen.Cart) }
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp,
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).navigationBarsPadding(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            val count = cartItems.sumOf { it.quantity }
                            Text("$count items added", fontSize = 14.sp, color = Color.Gray)
                            Text("₹${viewModel.getTotalPrice().toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PinkPrimary)
                        }
                        Button(
                            onClick = { navController.navigate(Screen.Cart) },
                            colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("View Cart", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFFF7F7F7)
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(services) { service ->
                val quantity = cartItems.find { it.id == service.id }?.quantity ?: 0
                MensServiceCard(
                    service = service,
                    quantity = quantity,
                    onAdd = { viewModel.addToCart(service) },
                    onIncrease = { viewModel.increaseQty(service.id) },
                    onDecrease = { viewModel.decreaseQty(service.id) }
                )
            }
        }
    }
}

@Composable
fun MensServiceCard(
    service: MensGroomingService,
    quantity: Int,
    onAdd: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(service.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("₹${service.price.toInt()}", fontWeight = FontWeight.ExtraBold, color = PinkPrimary, fontSize = 18.sp)
            }
            
            if (quantity == 0) {
                Button(
                    onClick = onAdd,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add")
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.background(PinkPrimary.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                ) {
                    IconButton(onClick = onDecrease) {
                        Icon(Icons.Default.Remove, contentDescription = null, tint = PinkPrimary)
                    }
                    Text(quantity.toString(), fontWeight = FontWeight.Bold, color = PinkPrimary, modifier = Modifier.padding(horizontal = 8.dp))
                    IconButton(onClick = onIncrease) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = PinkPrimary)
                    }
                }
            }
        }
    }
}

fun getServicesForSubcategory(sub: String): List<MensGroomingService> {
    return when (sub) {
        "Haircut" -> listOf(
            MensGroomingService("h1", "Basic Haircut", 150.0, "Hair Services", sub),
            MensGroomingService("h2", "Stylish Haircut", 250.0, "Hair Services", sub),
            MensGroomingService("h3", "Premium Haircut", 400.0, "Hair Services", sub)
        )
        "Hair Styling" -> listOf(
            MensGroomingService("hs1", "Hair Styling", 200.0, "Hair Services", sub),
            MensGroomingService("hs2", "Hair Straightening", 600.0, "Hair Services", sub),
            MensGroomingService("hs3", "Hair Coloring", 800.0, "Hair Services", sub)
        )
        "Hair Treatment" -> listOf(
            MensGroomingService("ht1", "Dandruff Treatment", 500.0, "Hair Services", sub),
            MensGroomingService("ht2", "Hair Fall Control", 700.0, "Hair Services", sub),
            MensGroomingService("ht3", "Scalp Treatment", 600.0, "Hair Services", sub)
        )
        "Shaving" -> listOf(
            MensGroomingService("s1", "Clean Shave", 100.0, "Beard & Shaving", sub),
            MensGroomingService("s2", "Premium Shave", 180.0, "Beard & Shaving", sub)
        )
        "Beard Grooming" -> listOf(
            MensGroomingService("bg1", "Beard Trimming", 120.0, "Beard & Shaving", sub),
            MensGroomingService("bg2", "Beard Styling", 200.0, "Beard & Shaving", sub),
            MensGroomingService("bg3", "Beard Coloring", 300.0, "Beard & Shaving", sub)
        )
        "Beard Care" -> listOf(
            MensGroomingService("bc1", "Beard Spa", 400.0, "Beard & Shaving", sub),
            MensGroomingService("bc2", "Beard Smoothening", 350.0, "Beard & Shaving", sub)
        )
        "Facials" -> listOf(
            MensGroomingService("f1", "Basic Facial", 400.0, "Facial & Skin Care", sub),
            MensGroomingService("f2", "Gold Facial", 700.0, "Facial & Skin Care", sub),
            MensGroomingService("f3", "Diamond Facial", 900.0, "Facial & Skin Care", sub)
        )
        "Skin Care" -> listOf(
            MensGroomingService("sc1", "Face Cleanup", 300.0, "Facial & Skin Care", sub),
            MensGroomingService("sc2", "Blackhead Removal", 250.0, "Facial & Skin Care", sub),
            MensGroomingService("sc3", "Detan Treatment", 500.0, "Facial & Skin Care", sub)
        )
        "Advanced" -> listOf(
            MensGroomingService("adv1", "Anti-Acne Treatment", 800.0, "Facial & Skin Care", sub),
            MensGroomingService("adv2", "Skin Brightening", 750.0, "Facial & Skin Care", sub)
        )
        "Massage" -> listOf(
            MensGroomingService("m1", "Head Massage", 200.0, "Spa & Relaxation", sub),
            MensGroomingService("m2", "Body Massage", 800.0, "Spa & Relaxation", sub),
            MensGroomingService("m3", "Shoulder Massage", 300.0, "Spa & Relaxation", sub)
        )
        "Relaxation" -> listOf(
            MensGroomingService("r1", "Aroma Therapy", 900.0, "Spa & Relaxation", sub),
            MensGroomingService("r2", "Stress Relief Therapy", 850.0, "Spa & Relaxation", sub)
        )
        "Packages" -> listOf(
            MensGroomingService("p1", "Basic Package (Haircut + Shave)", 220.0, "Grooming Packages", sub),
            MensGroomingService("p2", "Standard Package (Haircut + Beard + Facial)", 600.0, "Grooming Packages", sub),
            MensGroomingService("p3", "Premium Package (Haircut + Beard + Facial + Massage)", 1200.0, "Grooming Packages", sub),
            MensGroomingService("p4", "Wedding Package (Full Groom)", 2500.0, "Grooming Packages", sub)
        )
        "Events" -> listOf(
            MensGroomingService("e1", "Party Grooming", 800.0, "Special Occasion Grooming", sub),
            MensGroomingService("e2", "Date Night Grooming", 600.0, "Special Occasion Grooming", sub)
        )
        "Wedding" -> listOf(
            MensGroomingService("w1", "Groom Wedding Styling", 3000.0, "Special Occasion Grooming", sub),
            MensGroomingService("w2", "Pre-Wedding Grooming", 2000.0, "Special Occasion Grooming", sub)
        )
        else -> emptyList()
    }
}
