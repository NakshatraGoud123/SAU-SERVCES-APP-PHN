package com.nisr.sauservices.ui.home

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
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
        containerColor = LuxeCard,
        tonalElevation = 2.dp,
        modifier = Modifier
            .border(1.dp, LuxeBorder, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        val items = listOf(
            NavigationItem("Home", Screen.Home, Icons.Outlined.Home),
            NavigationItem("Search", Screen.Search, Icons.Outlined.Search),
            NavigationItem("Categories", Screen.Categories, Icons.Outlined.Category),
            NavigationItem("Orders", Screen.MyBookings, Icons.AutoMirrored.Outlined.Assignment),
            NavigationItem("Profile", Screen.LuxuryProfile, Icons.Outlined.Person)
        )

        items.forEach { item ->
            val isSelected = when (item.route) {
                is Screen.Home -> currentDestination?.hasRoute<Screen.Home>() == true
                is Screen.Search -> currentDestination?.hasRoute<Screen.Search>() == true
                is Screen.Categories -> currentDestination?.hasRoute<Screen.Categories>() == true
                is Screen.MyBookings -> currentDestination?.hasRoute<Screen.MyBookings>() == true
                is Screen.LuxuryProfile -> currentDestination?.hasRoute<Screen.LuxuryProfile>() == true
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
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1
                    ) 
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = LuxeTextPrimary,
                    selectedTextColor = LuxeTextPrimary,
                    unselectedIconColor = LuxeTextSecondary,
                    unselectedTextColor = LuxeTextSecondary,
                    indicatorColor = LuxeAccentSage.copy(alpha = 0.15f)
                )
            )
        }
    }
}

data class NavigationItem(
    val title: String,
    val route: Any,
    val icon: ImageVector,
    val badgeCount: Int = 0
)
