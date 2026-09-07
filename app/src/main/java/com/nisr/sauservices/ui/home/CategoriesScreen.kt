package com.nisr.sauservices.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontFamily
import androidx.navigation.NavController
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(navController: NavController) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    
    Scaffold(
        topBar = { PremiumHomeHeader(navController) },
        bottomBar = { BottomNavBar(navController) },
        containerColor = LuxuryBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = "All Categories",
                fontWeight = FontWeight.Black,
                fontSize = 28.sp,
                color = LuxuryTextPrimary,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(bottom = 24.dp, start = 4.dp)
            )
            
            CategoriesGrid(
                navController = navController, 
                showAll = true
            )
            
            Spacer(Modifier.height(24.dp))
        }
    }
}
