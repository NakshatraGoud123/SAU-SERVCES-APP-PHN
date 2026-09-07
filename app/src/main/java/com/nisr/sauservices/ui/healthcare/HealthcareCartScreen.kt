package com.nisr.sauservices.ui.healthcare

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
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
import com.nisr.sauservices.ui.viewmodel.HealthcareCartItem
import com.nisr.sauservices.ui.viewmodel.HealthcareViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthcareCartScreen(navController: NavController, viewModel: HealthcareViewModel) {
    val cartItems = viewModel.cartItems

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Healthcare Cart", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(12.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp).navigationBarsPadding()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Grand Total", fontSize = 16.sp, color = Color.Gray)
                            Text("₹${viewModel.calculateTotal().toInt()}", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1976D2))
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { 
                                navController.navigate(Screen.Cart)
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                        ) {
                            Text("Go to Unified Cart", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFFF8FBFF)
    ) { padding ->
        if (cartItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(100.dp), tint = Color.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No services selected", fontSize = 20.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { navController.popBackStack() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))) {
                        Text("Explore Services")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(cartItems) { item ->
                    HealthCartItemCard(
                        item = item,
                        onIncrease = { viewModel.updateQty(item.id, true) },
                        onDecrease = { viewModel.updateQty(item.id, false) },
                        onRemove = { viewModel.removeFromCart(item.id) }
                    )
                }
                if (viewModel.needsPrescription()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "⚠️ One or more items in your cart requires a prescription upload. You can upload it in the next step.",
                                modifier = Modifier.padding(12.dp),
                                fontSize = 12.sp,
                                color = Color(0xFFF57F17),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(120.dp)) }
            }
        }
    }
}

@Composable
fun HealthCartItemCard(
    item: HealthcareCartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                Text("₹${item.price.toInt()}", color = Color(0xFF1976D2), fontWeight = FontWeight.Bold)
                if (item.requiresPrescription) {
                    Text("Prescription required", fontSize = 10.sp, color = Color.Red)
                }
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.background(Color(0xFFE3F2FD), RoundedCornerShape(8.dp))
            ) {
                IconButton(onClick = onDecrease) {
                    Text("-", fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
                }
                Text(item.quantity.toString(), fontWeight = FontWeight.Bold, color = Color(0xFF1976D2), modifier = Modifier.padding(horizontal = 4.dp))
                IconButton(onClick = onIncrease) {
                    Text("+", fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Gray)
            }
        }
    }
}
