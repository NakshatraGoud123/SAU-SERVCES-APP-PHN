package com.nisr.sauservices.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.theme.PinkPrimary
import com.nisr.sauservices.ui.viewmodel.BookingItem
import com.nisr.sauservices.ui.viewmodel.BookingsViewModel

import com.nisr.sauservices.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingsScreen(navController: NavController, viewModel: BookingsViewModel) {
    val bookings by viewModel.bookingsFlow.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Upcoming, 1: Completed

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Bookings", fontWeight = FontWeight.Bold, color = LuxuryTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LuxuryTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxuryBackground)
            )
        },
        containerColor = LuxuryBackground
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = LuxuryBackground,
                contentColor = LuxuryGold,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = LuxuryGold
                    )
                },
                divider = { HorizontalDivider(color = LuxuryBorder) }
            ) {
                Tab(
                    selected = selectedTab == 0, 
                    onClick = { selectedTab = 0 }, 
                    text = { Text("Upcoming", color = if (selectedTab == 0) LuxuryGold else LuxuryTextSecondary) }
                )
                Tab(
                    selected = selectedTab == 1, 
                    onClick = { selectedTab = 1 }, 
                    text = { Text("Completed", color = if (selectedTab == 1) LuxuryGold else LuxuryTextSecondary) }
                )
            }

            val filteredBookings = if (selectedTab == 0) {
                bookings.filter { it.status == "Upcoming" || it.status == "pending" || it.status == "success" }
            } else {
                bookings.filter { it.status == "Completed" }
            }

            if (filteredBookings.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CalendarMonth, null, modifier = Modifier.size(64.dp), tint = LuxuryBorder)
                        Text("No bookings found", color = LuxuryTextSecondary)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredBookings) { booking ->
                        BookingCard(booking)
                    }
                }
            }
        }
    }
}

@Composable
fun BookingCard(booking: BookingItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LuxuryCard),
        border = BorderStroke(1.dp, LuxuryBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(booking.serviceName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = LuxuryTextPrimary, modifier = Modifier.weight(1f))
                Text(booking.price, fontWeight = FontWeight.ExtraBold, color = LuxuryGold)
            }
            
            Spacer(Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarMonth, null, tint = LuxuryTextSecondary, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text(booking.date, fontSize = 14.sp, color = LuxuryTextSecondary)
                
                Spacer(Modifier.width(16.dp))
                
                Icon(Icons.Default.Schedule, null, tint = LuxuryTextSecondary, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text(booking.time, fontSize = 14.sp, color = LuxuryTextSecondary)
            }
            
            Spacer(Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                val statusColor = when(booking.status.lowercase()) {
                    "completed" -> Color(0xFF2E7D32)
                    "upcoming", "success", "pending" -> Color(0xFFEF6C00)
                    else -> LuxuryTextSecondary
                }
                val bgColor = when(booking.status.lowercase()) {
                    "completed" -> Color(0xFFE8F5E9).copy(alpha = 0.1f)
                    "upcoming", "success", "pending" -> Color(0xFFFFF3E0).copy(alpha = 0.1f)
                    else -> LuxuryCard
                }

                Box(
                    modifier = Modifier
                        .background(bgColor, RoundedCornerShape(8.dp))
                        .border(1.dp, statusColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        booking.status.uppercase(),
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
