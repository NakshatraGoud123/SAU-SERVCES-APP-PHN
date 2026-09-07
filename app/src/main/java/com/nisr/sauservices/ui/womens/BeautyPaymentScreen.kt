package com.nisr.sauservices.ui.womens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
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
import com.nisr.sauservices.ui.viewmodel.WomensBeautyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeautyPaymentScreen(navController: NavController, viewModel: WomensBeautyViewModel) {
    val paymentMethods = listOf("Cash on Delivery", "UPI", "Debit/Credit Card", "Wallet")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment Method", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = { navController.navigate(Screen.WomensBeautyOrderSummary) },
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC2185B)),
                enabled = viewModel.selectedPaymentMethod.value.isNotEmpty()
            ) {
                Text("Review Order", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Choose how you'd like to pay", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
            
            paymentMethods.forEach { method ->
                val isSelected = viewModel.selectedPaymentMethod.value == method
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectedPaymentMethod.value = method }
                        .border(1.dp, if (isSelected) Color(0xFFC2185B) else Color.Transparent, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFFCE4EC) else Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(method, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) Color(0xFFC2185B) else Color.Black)
                        if (isSelected) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFFC2185B))
                        } else {
                            Box(modifier = Modifier.size(24.dp).border(2.dp, Color.LightGray, RoundedCornerShape(12.dp)))
                        }
                    }
                }
            }
        }
    }
}
