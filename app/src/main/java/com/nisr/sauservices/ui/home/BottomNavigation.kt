package com.nisr.sauservices.ui.home

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.text.font.FontWeight
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.*

@Composable
fun BottomNavBar(
    navController: NavController
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry.value?.destination
    
    NavigationBar(
        containerColor = LuxuryCard,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            NavigationItem("Home", Screen.Home, Icons.Outlined.Home),
            NavigationItem("Search", Screen.Search, Icons.Outlined.Search),
            NavigationItem("Categories", Screen.Categories, Icons.Outlined.GridView),
            NavigationItem("Bookings", Screen.MyBookings, Icons.AutoMirrored.Outlined.Assignment),
            NavigationItem("Profile", Screen.Profile, Icons.Outlined.Person)
        )

        items.forEach { item ->
            val isSelected = when (item.route) {
                is Screen.Home -> currentDestination?.hasRoute<Screen.Home>() == true
                is Screen.Search -> currentDestination?.hasRoute<Screen.Search>() == true
                is Screen.Categories -> currentDestination?.hasRoute<Screen.Categories>() == true
                is Screen.MyBookings -> currentDestination?.hasRoute<Screen.MyBookings>() == true
                is Screen.Profile -> currentDestination?.hasRoute<Screen.Profile>() == true
                else -> false
            }
            
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo<Screen.Home> { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(item.icon, null, modifier = Modifier.size(24.dp))
                },
                label = { 
                    Text(
                        item.title, 
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    ) 
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = LuxuryGold,
                    selectedTextColor = LuxuryGold,
                    unselectedIconColor = LuxuryTextSecondary,
                    unselectedTextColor = LuxuryTextSecondary,
                    indicatorColor = LuxuryGold.copy(alpha = 0.1f)
                )
            )
        }
    }
}

data class NavigationItem(
    val title: String,
    val route: Any,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val badgeCount: Int = 0
)
