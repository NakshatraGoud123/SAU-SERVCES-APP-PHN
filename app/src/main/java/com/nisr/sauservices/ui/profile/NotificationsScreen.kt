package com.nisr.sauservices.ui.profile

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nisr.sauservices.data.model.Notification
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.viewmodel.ProfileViewModel

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavController, viewModel: ProfileViewModel = viewModel()) {
    val notifications by viewModel.notifications.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NOTIFICATIONS", fontWeight = FontWeight.Black, color = LuxeTextPrimary, fontFamily = FontFamily.Serif, letterSpacing = 2.sp) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (notifications.isEmpty()) {
                EmptyNotificationsState()
            } else {
                NotificationList(notifications)
            }
        }
    }
}

@Composable
private fun EmptyNotificationsState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = LuxeHighlightChampagne.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, LuxeBorder)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Notifications, null, tint = LuxeAccentSage, modifier = Modifier.size(32.dp))
                }
            }
            Spacer(Modifier.height(24.dp))
            Text("Your sanctuary is quiet", color = LuxeTextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp, fontFamily = FontFamily.Serif)
            Text("We will notify you here for any updates.", color = LuxeTextSecondary, fontSize = 14.sp)
        }
    }
}

@Composable
private fun NotificationList(notifications: List<Notification>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(notifications) { notification ->
            var visible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { visible = true }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(800)) + slideInHorizontally(animationSpec = tween(500), initialOffsetX = { 100 })
            ) {
                NotificationLuxuryCard(notification)
            }
        }
    }
}

@Composable
private fun NotificationLuxuryCard(notification: Notification) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = LuxeCard,
        border = BorderStroke(1.dp, LuxeBorder),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(LuxeHighlightChampagne.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = LuxeAccentSage,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(18.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = LuxeTextPrimary
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Text(
                    text = notification.message,
                    fontSize = 14.sp,
                    color = LuxeTextSecondary,
                    lineHeight = 22.sp
                )
            }
        }
    }
}
