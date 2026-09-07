package com.nisr.sauservices.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.LocalMall
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarUI(navController: NavController, sessionManager: SessionManager) {
    var showLogoutDialog by remember { mutableStateOf(value = false) }
    val userAddress = sessionManager.getAddress()
    val cartViewModel: CartViewModel = viewModel()
    val cartItems by cartViewModel.dbCartItems.collectAsState()
    val cartCount = cartItems.sumOf { it.quantity }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        sessionManager.logout()
                        navController.navigate(Screen.Login) {
                            popUpTo(0) { inclusive = true }
                        }
                        showLogoutDialog = false
                    },
                ) {
                    Text("Logout", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false },
                ) {
                    Text("Cancel")
                }
            },
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = LuxuryBackground,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Left: Branding
            Column(
                modifier = Modifier.clickable { navController.navigate(Screen.Home) }
            ) {
                Text(
                    "SAU",
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = LuxuryGold,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    "SERVICES",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = LuxuryTextSecondary,
                    letterSpacing = 2.sp
                )
            }

            Spacer(Modifier.width(16.dp))

            // Center: Location Selector
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { /* Navigate to Settings or Location screen */ },
                horizontalAlignment = Alignment.Start
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Location",
                        fontSize = 10.sp,
                        color = LuxuryTextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        null,
                        tint = LuxuryTextSecondary,
                        modifier = Modifier.size(12.dp)
                    )
                }
                Text(
                    text = userAddress.ifBlank { "Select Location" },
                    fontSize = 14.sp,
                    color = LuxuryTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Right: Action Icons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { /* Bag action */ }, modifier = Modifier.size(36.dp)) {
                    Icon(
                        Icons.Outlined.LocalMall,
                        contentDescription = "Shopping Bag",
                        tint = LuxuryTextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(
                    onClick = { /* Navigate to Cart or My Bookings */ 
                        navController.navigate(Screen.MyBookings)
                    },
                    modifier = Modifier.size(36.dp),
                ) {
                    BadgedBox(
                        badge = {
                            if (cartCount > 0) {
                                Badge(containerColor = LuxuryGold) {
                                    Text(text = cartCount.toString(), color = LuxuryBackground, fontSize = 9.sp)
                                }
                            }
                        },
                    ) {
                        Icon(
                            Icons.Outlined.ShoppingCart,
                            contentDescription = "Cart",
                            tint = LuxuryTextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                IconButton(onClick = { showLogoutDialog = true }, modifier = Modifier.size(36.dp)) {
                    Icon(
                        Icons.AutoMirrored.Outlined.Logout,
                        contentDescription = "Logout",
                        tint = LuxuryTextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
