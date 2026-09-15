package com.nisr.sauservices.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontFamily
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Discover", fontWeight = FontWeight.Black, color = LuxeTextPrimary, fontFamily = FontFamily.Serif) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = LuxeTextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.MerchantMap) }) {
                        Icon(Icons.Default.Map, "Map View", tint = LuxeAccentSage)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxeBackground)
            )
        },
        bottomBar = { BottomNavBar(navController) },
        containerColor = LuxeBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Luxe Search Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(52.dp)
                    .clickable { navController.navigate(Screen.Search) },
                shape = RoundedCornerShape(16.dp),
                color = LuxeCard,
                border = BorderStroke(1.dp, LuxeBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, null, tint = LuxeAccentSage)
                    Spacer(Modifier.width(12.dp))
                    Text("Search categories...", color = LuxeTextSecondary, fontSize = 14.sp)
                }
            }

            Text(
                text = "FEATURED CATEGORIES",
                style = MaterialTheme.typography.labelSmall,
                color = LuxeAccentSage,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
            )
            
            CategoriesGrid(
                navController = navController, 
                showAll = true
            )
            
            Spacer(Modifier.height(48.dp))
        }
    }
}
