package com.nisr.sauservices.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontFamily
import androidx.navigation.NavController
import com.nisr.sauservices.R
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.components.SauColors
import com.nisr.sauservices.ui.essentials.HomeEssentialsSheetContent
import com.nisr.sauservices.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController){

    Scaffold(
        topBar = { PremiumHomeHeader(navController) },
        bottomBar = { BottomNavBar(navController) },
        containerColor = LuxuryBackground
        ){ pad ->
        Column(
            Modifier
                .padding(pad)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ){
            Column(Modifier.padding(horizontal = 16.dp)) {
                Spacer(Modifier.height(16.dp))
                HeroBanner(navController)
                
                Spacer(Modifier.height(24.dp))
                CategoriesGrid(navController)

                Spacer(Modifier.height(32.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Top Stores Near You", 
                        color = LuxuryTextPrimary, 
                        fontSize = 18.sp, 
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Serif
                    )
                    Text("View all", color = LuxuryGold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(16.dp))
                TopStoresRow()

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun TopStoresRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StoreCard(
            name = "More Supermarket",
            rating = "4.6",
            imageRes = R.drawable.home_essentials,
            modifier = Modifier.weight(1f)
        )
        StoreCard(
            name = "FreshMart",
            rating = "4.4",
            imageRes = R.drawable.home_lifestyle,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StoreCard(name: String, rating: String, @androidx.annotation.DrawableRes imageRes: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LuxuryCard),
        border = BorderStroke(1.dp, LuxuryBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = name,
                modifier = Modifier.size(50.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            Spacer(Modifier.height(8.dp))
            Text(name, color = LuxuryTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, null, tint = LuxuryGold, modifier = Modifier.size(12.dp))
                Text(rating, fontSize = 11.sp, color = LuxuryTextSecondary, modifier = Modifier.padding(start = 2.dp))
            }
        }
    }
}

@Composable
fun PremiumHomeHeader(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LuxuryBackground)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.Default.LocationOn, 
                null, 
                tint = LuxuryGold, 
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Hyderabad, Telangana", 
                        fontSize = 15.sp, 
                        color = LuxuryTextPrimary, 
                        fontWeight = FontWeight.Bold
                    )
                    Icon(Icons.Default.KeyboardArrowDown, null, tint = LuxuryTextPrimary, modifier = Modifier.size(20.dp))
                }
            }
            IconButton(onClick = { navController.navigate(Screen.Notifications) }) {
                Icon(Icons.Outlined.Notifications, null, tint = LuxuryTextPrimary)
            }
        }
        
        Spacer(Modifier.height(8.dp))
        
        OutlinedTextField(
            value = "",
            onValueChange = { /* Navigate to search or handle search */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clickable { navController.navigate(Screen.Search) },
            placeholder = { Text("Search for products, food or services...", color = LuxuryTextSecondary, fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = LuxuryGold) },
            trailingIcon = { Icon(Icons.Default.Mic, null, tint = LuxuryGold) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LuxuryBorder,
                unfocusedBorderColor = LuxuryBorder,
                focusedContainerColor = LuxuryCard,
                unfocusedContainerColor = LuxuryCard
            ),
            enabled = false // Make it clickable but not focusable for home screen
        )
    }
}
