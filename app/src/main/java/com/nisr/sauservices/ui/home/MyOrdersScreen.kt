package com.nisr.sauservices.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.data.repository.RealtimeDatabaseRepository
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.components.LuxuryButton
import java.text.SimpleDateFormat
import java.util.*

// ============================================================
// LUXE BRAND COLORS (Local for precision)
// ============================================================
private val LuxeBackground = Color(0xFFFDFBFA)
private val LuxeCard = Color(0xFFFFFFFF)
private val LuxeTextPrimary = Color(0xFF423F3D)
private val LuxeTextSecondary = Color(0xFF8D7F77)
private val LuxeAccentSage = Color(0xFF96A68F)
private val LuxeHighlightChampagne = Color(0xFFF5E6D3)
private val LuxeBorder = Color(0xFFEFE9E4)
private val LuxeGold = Color(0xFFE8C66A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(navController: NavController) {
    val repository = remember { RealtimeDatabaseRepository() }
    val orders by repository.observeUserActivity().collectAsState(initial = emptyList())
    
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Active", "History")

    val activeOrders = orders.filter { (it.status ?: "placed").lowercase() !in listOf("delivered", "completed", "cancelled", "success") }
    val historyOrders = orders.filter { (it.status ?: "placed").lowercase() in listOf("delivered", "completed", "cancelled", "success") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Activity", fontWeight = FontWeight.Black, color = LuxeTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LuxeTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxeBackground)
            )
        },
        containerColor = LuxeBackground
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Luxury Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = LuxeBackground,
                contentColor = LuxeAccentSage,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = LuxeAccentSage
                    )
                },
                divider = { HorizontalDivider(color = LuxeBorder) }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp,
                                color = if (selectedTab == index) LuxeAccentSage else LuxeTextSecondary
                            )
                        }
                    )
                }
            }

            val displayOrders = if (selectedTab == 0) activeOrders else historyOrders

            if (displayOrders.isEmpty()) {
                EmptyActivityState(
                    icon = if (selectedTab == 0) Icons.Default.NotificationsActive else Icons.Default.History,
                    message = if (selectedTab == 0) "No active orders right now" else "No past activity found"
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(displayOrders.sortedByDescending { it.createdAt }) { order ->
                        LuxuryOrderCard(order, onClick = {
                            navController.navigate(Screen.OrderTracking(order.orderId))
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun LuxuryOrderCard(order: OrderModel, onClick: () -> Unit) {
    val isHistory = order.status.lowercase() in listOf("delivered", "completed", "cancelled", "success")
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = LuxeCard),
        border = BorderStroke(1.dp, LuxeBorder)
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    val orderIdSlug = if (order.orderId.length >= 6) order.orderId.takeLast(6).uppercase() else order.orderId.uppercase()
                    Text(
                        text = (order.serviceName ?: "").ifEmpty { "Order #$orderIdSlug" },
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = LuxeTextPrimary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = order.createdAt ?: "Recently",
                        fontSize = 12.sp,
                        color = LuxeTextSecondary
                    )
                }
                
                Surface(
                    color = getStatusColor(order.status ?: "placed").copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, getStatusColor(order.status ?: "placed").copy(alpha = 0.5f))
                ) {
                    Text(
                        text = (order.status ?: "placed").uppercase(),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = getStatusColor(order.status ?: "placed")
                    )
                }
            }
            
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = LuxeBorder)
            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Total Amount", fontSize = 12.sp, color = LuxeTextSecondary)
                    Text("₹${order.totalAmount}", fontWeight = FontWeight.Black, fontSize = 18.sp, color = LuxeTextPrimary)
                }
                
                if (isHistory) {
                    Button(
                        onClick = { /* Reorder Logic */ },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LuxeHighlightChampagne),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Icon(Icons.Default.Replay, null, tint = LuxeTextPrimary, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("REORDER", color = LuxeTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                } else {
                    TextButton(onClick = onClick) {
                        Text("TRACK LIVE", color = LuxeAccentSage, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyActivityState(icon: ImageVector, message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = LuxeHighlightChampagne.copy(alpha = 0.4f),
                border = BorderStroke(1.dp, LuxeBorder)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, modifier = Modifier.size(32.dp), tint = LuxeTextSecondary)
                }
            }
            Spacer(Modifier.height(24.dp))
            Text(message, color = LuxeTextSecondary, fontSize = 15.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
        }
    }
}


private fun getStatusColor(status: String): Color = when(status.lowercase()) {
    "delivered", "completed", "success" -> Color(0xFF4CAF50)
    "cancelled", "failed" -> Color(0xFFEF5350)
    "on_the_way", "arrived", "accepted" -> Color(0xFFFFA726)
    else -> LuxuryGold
}
