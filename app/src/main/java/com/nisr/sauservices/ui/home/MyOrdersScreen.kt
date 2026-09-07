package com.nisr.sauservices.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.data.repository.RealtimeDatabaseRepository
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.PinkPrimary
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(navController: NavController) {
    val repository = remember { RealtimeDatabaseRepository() }
    val orders by repository.observeUserActivity().collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Orders & Bookings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F8F8)
    ) { padding ->
        if (orders.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ShoppingBag, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Text("No orders yet", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orders.sortedByDescending { it.timestamp }) { order ->
                    OrderCard(order, onClick = {
                        navController.navigate(Screen.BookingDetails(order.orderId))
                    })
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: OrderModel, onClick: () -> Unit) {
    val sdf = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    val dateString = sdf.format(Date(order.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(
                        text = if (order.scheduleDate != null) "Booking: ${order.serviceName}" else "Order: ${order.serviceName.ifEmpty { "Essential Supplies" }}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(text = "ID: ${order.orderId}", fontSize = 12.sp, color = Color.Gray)
                }
                Surface(
                    color = if (order.status == "success" || order.orderStatus == "delivered") Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = (if (order.orderStatus.isNotEmpty()) order.orderStatus else order.status).uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (order.status == "success" || order.orderStatus == "delivered") Color(0xFF2E7D32) else Color(0xFFEF6C00)
                    )
                }
            }
            
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(Modifier.height(12.dp))

            if (order.scheduleDate != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Scheduled for: ", fontSize = 14.sp, color = Color.Gray)
                    Text("${order.scheduleDate} at ${order.scheduleTime}", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            } else if (order.items != null) {
                Text(text = "${order.items.size} Items", fontSize = 14.sp, color = Color.Gray)
                order.items.take(2).forEach { item ->
                    Text(text = "• ${item.itemName} x ${item.quantity}", fontSize = 13.sp)
                }
                if (order.items.size > 2) Text("... and ${order.items.size - 2} more", fontSize = 12.sp, color = PinkPrimary)
            }

            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Text(text = dateString, fontSize = 12.sp, color = Color.Gray)
                Text(text = "₹${order.totalPrice.coerceAtLeast(order.amount.toDouble())}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = PinkPrimary)
            }
        }
    }
}
