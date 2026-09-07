package com.nisr.sauservices.ui.education

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.LightPink
import com.nisr.sauservices.ui.theme.PinkPrimary
import com.nisr.sauservices.ui.viewmodel.EduCartItem
import com.nisr.sauservices.ui.viewmodel.EducationCartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationCartScreen(navController: NavController, viewModel: EducationCartViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Education Cart", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            if (viewModel.cartItems.isNotEmpty()) {
                EducationCartBottomBar(viewModel) {
                    navController.navigate(Screen.Cart)
                }
            }
        },
        containerColor = Color(0xFFF7F7F7) // BackgroundGray from theme
    ) { padding ->
        if (viewModel.cartItems.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.LightGray
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("Your cart is empty", color = Color.Gray, fontSize = 16.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(viewModel.cartItems) { item ->
                    EduCartItemCard(item, viewModel)
                }
            }
        }
    }
}

@Composable
fun EduCartItemCard(item: EduCartItem, viewModel: EducationCartViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LightPink),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MenuBook, contentDescription = null, tint = PinkPrimary)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = item.duration, fontSize = 12.sp, color = Color.Gray)
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
fun EducationCartBottomBar(viewModel: EducationCartViewModel, onProceed: () -> Unit) {
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
                Text(text = "Total Payable", fontSize = 12.sp, color = Color.Gray)
                Text(text = "₹${viewModel.getTotal()}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = PinkPrimary)
            }
            
            Button(
                onClick = onProceed,
                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(48.dp).padding(horizontal = 8.dp)
            ) {
                Text("Go to Unified Cart", fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
            }
        }
    }
}
