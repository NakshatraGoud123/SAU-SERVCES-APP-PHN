package com.nisr.sauservices.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.data.repository.RealtimeDatabaseRepository
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.components.LuxuryButton
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(navController: NavController) {
    val repository = remember { RealtimeDatabaseRepository() }
    val orders by repository.observeUserActivity().collectAsState(initial = emptyList())
    
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Active", "History")
    
    var showReceipt by remember { mutableStateOf<OrderModel?>(null) }

    val activeOrders = orders.filter { (it.status ?: "placed").lowercase() !in listOf("delivered", "completed", "cancelled", "success") }
    val historyOrders = orders.filter { (it.status ?: "placed").lowercase() in listOf("delivered", "completed", "cancelled", "success") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MY ACTIVITY", fontWeight = FontWeight.Black, color = LuxeTextPrimary, letterSpacing = 2.sp, fontFamily = FontFamily.Serif) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LuxeTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxeBackground)
            )
        },
        bottomBar = { BottomNavBar(navController) },
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
                        color = LuxeAccentSage,
                        height = 3.dp
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
                                text = title.uppercase(),
                                fontWeight = if (selectedTab == index) FontWeight.Black else FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp,
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
                    message = if (selectedTab == 0) "Your schedule is currently clear." else "No past luxury experiences found."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    items(displayOrders.sortedByDescending { it.createdAt }, key = { it.orderId }) { order ->
                        var visible by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) { visible = true }
                        
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(tween(800)) + slideInVertically(animationSpec = tween(600), initialOffsetY = { 50 })
                        ) {
                            LuxuryOrderCard(order, onClick = {
                                if (selectedTab == 1) {
                                    showReceipt = order
                                } else {
                                    navController.navigate(Screen.OrderTracking(order.orderId))
                                }
                            })
                        }
                    }
                }
            }
        }
    }
    
    val currentReceipt = showReceipt
    if (currentReceipt != null) {
        LuxeReceiptDialog(order = currentReceipt) { showReceipt = null }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LuxeReceiptDialog(order: OrderModel, onDismiss: () -> Unit) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.padding(24.dp).fillMaxWidth(),
        content = {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                border = BorderStroke(1.dp, LuxeBorder)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(Modifier.size(60.dp).background(LuxeAccentSage.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Check, null, tint = LuxeAccentSage, modifier = Modifier.size(32.dp))
                    }
                    Spacer(Modifier.height(20.dp))
                    Text("Digital Receipt", fontWeight = FontWeight.Black, fontSize = 20.sp, color = LuxeTextPrimary, fontFamily = FontFamily.Serif)
                    Text("Official SAU Transaction", fontSize = 11.sp, color = LuxeAccentSage, letterSpacing = 2.sp, fontWeight = FontWeight.Black)
                    
                    Spacer(Modifier.height(32.dp))
                    
                    HorizontalDivider(color = LuxeBorder, thickness = 1.dp, modifier = Modifier.padding(bottom = 24.dp))
                    
                    ReceiptRow("Order ID", "#${order.orderId.takeLast(6).uppercase()}")
                    ReceiptRow("Status", order.status.uppercase())
                    ReceiptRow("Date", order.createdAt ?: "Today")
                    
                    Spacer(Modifier.height(24.dp))
                    HorizontalDivider(color = LuxeBorder, thickness = 1.dp, modifier = Modifier.padding(bottom = 24.dp))
                    
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("TOTAL AMOUNT", fontWeight = FontWeight.Black, fontSize = 14.sp, color = LuxeTextPrimary)
                        Text("₹${order.totalAmount}", fontWeight = FontWeight.Black, fontSize = 18.sp, color = LuxeAccentSage)
                    }
                    
                    Spacer(Modifier.height(40.dp))
                    
                    LuxuryButton(text = "CLOSE", onClick = onDismiss, modifier = Modifier.height(50.dp))
                }
            }
        }
    )
}

@Composable
private fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = LuxeTextSecondary, fontSize = 13.sp)
        Text(value, color = LuxeTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
