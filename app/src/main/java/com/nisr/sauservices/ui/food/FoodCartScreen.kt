package com.nisr.sauservices.ui.food

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.viewmodel.FoodCartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodCartScreen(navController: NavController, viewModel: FoodCartViewModel) {
    val cartItems = viewModel.cartItems
    val deliveryFee = 25
    val subtotal = viewModel.getTotal()
    val grandTotal = if (cartItems.isEmpty()) 0 else subtotal + deliveryFee

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Order (${cartItems.size})", color = LuxuryTextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LuxuryTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxuryBackground)
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 16.dp,
                    color = LuxuryCard,
                    border = BorderStroke(1.dp, LuxuryBorder),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp).navigationBarsPadding()) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("Total Amount", fontSize = 14.sp, color = LuxuryTextSecondary)
                                Text("₹$grandTotal", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = LuxuryGold)
                            }
                            Button(
                                onClick = { navController.navigate(Screen.FoodBooking("res_1")) },
                                modifier = Modifier.height(56.dp).widthIn(min = 180.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = LuxuryGold)
                            ) {
                                Text("Proceed to Checkout", color = LuxuryBackground, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        },
        containerColor = LuxuryBackground
    ) { padding ->
        if (cartItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Your cart is empty", color = LuxuryTextSecondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text("Delivering to Home", fontWeight = FontWeight.Bold, color = LuxuryGold, fontSize = 12.sp)
                    Text("123, Main Road, Hyderabad", color = LuxuryTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(Modifier.height(8.dp))
                }

                items(cartItems) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = LuxuryCard),
                        border = BorderStroke(1.dp, LuxuryBorder),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.name, color = LuxuryTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("₹${item.price}", color = LuxuryTextSecondary, fontSize = 14.sp)
                            }
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(LuxuryGold, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 4.dp)
                            ) {
                                IconButton(onClick = { viewModel.decreaseQty(item.id) }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.Remove, null, tint = LuxuryBackground, modifier = Modifier.size(16.dp))
                                }
                                Text(item.quantity.toString(), color = LuxuryBackground, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 8.dp))
                                IconButton(onClick = { viewModel.increaseQty(item.id) }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.Add, null, tint = LuxuryBackground, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(LuxuryCard)
                            .border(BorderStroke(1.dp, LuxuryBorder), RoundedCornerShape(16.dp))
                            .padding(20.dp)
                    ) {
                        Text("Bill Details", color = LuxuryTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(Modifier.height(16.dp))
                        LuxuryBillRow("Item Subtotal", "₹$subtotal")
                        LuxuryBillRow("Delivery Fee", "₹$deliveryFee")
                        HorizontalDivider(Modifier.padding(vertical = 12.dp), color = LuxuryBorder)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Grand Total", color = LuxuryTextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                            Text("₹$grandTotal", color = LuxuryGold, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LuxuryBillRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = LuxuryTextSecondary, fontSize = 14.sp)
        Text(value, color = LuxuryTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}
