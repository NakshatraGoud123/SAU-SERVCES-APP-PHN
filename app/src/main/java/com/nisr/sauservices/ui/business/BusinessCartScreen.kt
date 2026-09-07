package com.nisr.sauservices.ui.business

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
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
import com.nisr.sauservices.ui.theme.PinkPrimary
import com.nisr.sauservices.ui.theme.LightPink
import com.nisr.sauservices.ui.viewmodel.BusinessViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessCartScreen(navController: NavController, viewModel: BusinessViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Business Cart", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            if (viewModel.cartItems.isNotEmpty()) {
                BusinessCartBottomBar(viewModel) {
                    navController.navigate(Screen.Cart)
                }
            }
        },
        containerColor = Color(0xFFF7F7F7)
    ) { padding ->
        if (viewModel.cartItems.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Your cart is empty", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(viewModel.cartItems) { item ->
                    BusinessCartItemCard(item, viewModel)
                }
            }
        }
    }
}

@Composable
fun BusinessCartItemCard(item: BusinessCartItem, viewModel: BusinessViewModel) {
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
                Text(text = item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = "₹${item.price}", fontWeight = FontWeight.Bold, color = PinkPrimary)
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.background(LightPink, RoundedCornerShape(8.dp))
            ) {
                IconButton(onClick = { viewModel.decreaseQty(item.id) }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Remove, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(16.dp))
                }
                Text(text = item.quantity.toString(), fontWeight = FontWeight.Bold, color = PinkPrimary)
                IconButton(onClick = { viewModel.increaseQty(item.id) }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun BusinessCartBottomBar(viewModel: BusinessViewModel, onProceed: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "Total Amount", fontSize = 12.sp, color = Color.Gray)
                Text(text = "₹${viewModel.getTotalPrice()}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = PinkPrimary)
            }
            
            Button(
                onClick = onProceed,
                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text("Go to Unified Cart", fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
            }
        }
    }
}
