package com.nisr.sauservices.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.EventNote
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontFamily
import androidx.navigation.NavController
import com.nisr.sauservices.R
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Premium Home Dashboard for SAU Solutions.
 * Inspired by Urban Company, Swiggy, and Blinkit.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeDashboard(navController: NavController) {
    val scrollState = rememberScrollState()
    
    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = LuxuryBackground 
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
        ) {
            // 1. Contextual Header
            DashboardHeader(navController)
            
            // 2. Omni Search Bar
            DashboardSearchBar(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            
            // 3. Hero Carousel
            DashboardHeroCarousel()
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 4. Categories Grid
            DashboardCategoriesGrid(navController)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 5. Promotional Banners
            DashboardPromotionalBanners()
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 6. Popular Services (Horizontal)
            DashboardPopularServices(navController)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 7. Recommended Section (Vertical)
            DashboardRecommendedSection(navController)
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun DashboardHeader(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Welcome to SAU,",
                style = MaterialTheme.typography.labelMedium,
                color = LuxuryGold,
                fontWeight = FontWeight.Bold
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { navController.navigate(Screen.MapPicker) }
            ) {
                Icon(Icons.Rounded.LocationOn, null, tint = LuxuryGold, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Downtown Dubai, UAE",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = LuxuryTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(Icons.Default.KeyboardArrowDown, null, tint = LuxuryTextSecondary)
            }
        }
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { /* Notifications */ },
                modifier = Modifier
                    .size(44.dp)
                    .background(LuxuryCard, CircleShape)
                    .border(1.dp, LuxuryBorder, CircleShape)
            ) {
                Icon(Icons.Outlined.Notifications, null, tint = LuxuryTextPrimary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .border(2.dp, LuxuryGold, CircleShape)
                    .clickable { navController.navigate(Screen.Profile) }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Profile",
                    modifier = Modifier.fillMaxSize().background(LuxuryGold.copy(alpha = 0.1f))
                )
            }
        }
    }
}

@Composable
private fun DashboardSearchBar(modifier: Modifier = Modifier) {
    val hints = listOf("Search 'AC Repair'", "Search 'Deep Cleaning'", "Search 'Salon at Home'")
    var index by remember { mutableIntStateOf(0) }
    
    LaunchedEffect(Unit) {
        while(true) {
            delay(3000)
            index = (index + 1) % hints.size
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        color = LuxuryCard,
        border = BorderStroke(1.dp, LuxuryBorder)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, null, tint = LuxuryGold)
            Spacer(modifier = Modifier.width(12.dp))
            
            AnimatedContent(
                targetState = hints[index],
                transitionSpec = {
                    (slideInVertically { it } + fadeIn()) togetherWith (slideOutVertically { -it } + fadeOut())
                },
                label = "search_hint"
            ) { hint ->
                Text(text = hint, style = MaterialTheme.typography.bodyLarge, color = LuxuryTextSecondary.copy(alpha = 0.6f))
            }
            
            Spacer(modifier = Modifier.weight(1f))
            VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 8.dp), thickness = 1.dp, color = LuxuryBorder)
            Icon(Icons.Default.Mic, null, tint = LuxuryGold)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DashboardHeroCarousel() {
    val pagerState = rememberPagerState { 3 }
    
    Column {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(top = 16.dp).height(190.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing = 12.dp
        ) { page ->
            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(24.dp)
            ) {
                Box {
                    Image(
                        painter = painterResource(id = R.drawable.homescreen_illustration),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .background(Brush.horizontalGradient(listOf(LuxuryGold.copy(alpha = 0.9f), Color.Transparent)))
                    )
                    Column(modifier = Modifier.align(Alignment.CenterStart).padding(24.dp)) {
                        Text("PREMIUM QUALITY", color = LuxuryBackground, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        Text("Home Repairs &\nCleaning - 40% Off", color = LuxuryBackground, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(containerColor = LuxuryBackground),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Book Now", color = LuxuryGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
        
        Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.Center) {
            repeat(3) { i ->
                val active = pagerState.currentPage == i
                Box(modifier = Modifier.padding(horizontal = 3.dp).size(if (active) 16.dp else 6.dp, 6.dp).clip(CircleShape).background(if (active) LuxuryGold else LuxuryBorder))
            }
        }
    }
}

@Composable
private fun DashboardCategoriesGrid(navController: NavController) {
    val items = listOf(
        HomeCatItem("Cleaning", Icons.Rounded.CleaningServices),
        HomeCatItem("Repairs", Icons.Rounded.Build),
        HomeCatItem("Salon", Icons.Rounded.Face),
        HomeCatItem("Electric", Icons.Rounded.Bolt),
        HomeCatItem("Plumbing", Icons.Rounded.WaterDrop),
        HomeCatItem("Painting", Icons.Rounded.FormatPaint),
        HomeCatItem("Security", Icons.Rounded.Shield),
        HomeCatItem("More", Icons.Rounded.Apps)
    )

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Our Services", 
            style = MaterialTheme.typography.titleLarge, 
            fontWeight = FontWeight.ExtraBold, 
            color = LuxuryTextPrimary,
            fontFamily = FontFamily.Serif
        )
        Spacer(modifier = Modifier.height(20.dp))
        
        repeat(2) { row ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                repeat(4) { col ->
                    val item = items[row * 4 + col]
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(75.dp)) {
                        Box(modifier = Modifier.size(60.dp).clip(RoundedCornerShape(18.dp)).background(LuxuryCard).border(1.dp, LuxuryBorder, RoundedCornerShape(18.dp)), contentAlignment = Alignment.Center) {
                            Icon(item.icon, null, tint = LuxuryGold, modifier = Modifier.size(26.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(item.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LuxuryTextPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardPromotionalBanners() {
    Column {
        Text(
            text = "Special Savings", 
            style = MaterialTheme.typography.titleLarge, 
            fontWeight = FontWeight.ExtraBold, 
            color = LuxuryTextPrimary, 
            modifier = Modifier.padding(horizontal = 16.dp),
            fontFamily = FontFamily.Serif
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(3) {
                Card(modifier = Modifier.size(300.dp, 120.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = LuxuryCard), border = BorderStroke(1.dp, LuxuryBorder)) {
                    Row(modifier = Modifier.fillMaxSize().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("SAVE AED 50", color = LuxuryGold, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                            Text("Premium AC Service", color = LuxuryTextPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                            Text("Use Code: SAU50", fontSize = 11.sp, color = LuxuryTextSecondary)
                        }
                        Image(painter = painterResource(id = R.drawable.ac_repair), contentDescription = null, modifier = Modifier.size(80.dp).clip(RoundedCornerShape(14.dp)), contentScale = ContentScale.Crop)
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardPopularServices(navController: NavController) {
    Column {
        Text(
            text = "Most Booked", 
            style = MaterialTheme.typography.titleLarge, 
            fontWeight = FontWeight.ExtraBold, 
            color = LuxuryTextPrimary, 
            modifier = Modifier.padding(horizontal = 16.dp),
            fontFamily = FontFamily.Serif
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(4) {
                Card(modifier = Modifier.width(160.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = LuxuryCard), border = BorderStroke(1.dp, LuxuryBorder)) {
                    Column {
                        Image(painter = painterResource(id = R.drawable.bathroom_cleaning), contentDescription = null, modifier = Modifier.height(110.dp).fillMaxWidth(), contentScale = ContentScale.Crop)
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Sofa Cleaning", color = LuxuryTextPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, null, tint = LuxuryGold, modifier = Modifier.size(12.dp))
                                Text(" 4.8", color = LuxuryTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("AED 149", fontWeight = FontWeight.Bold, color = LuxuryGold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardRecommendedSection(navController: NavController) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Personalized for you", 
            style = MaterialTheme.typography.titleLarge, 
            fontWeight = FontWeight.ExtraBold, 
            color = LuxuryTextPrimary,
            fontFamily = FontFamily.Serif
        )
        Spacer(modifier = Modifier.height(16.dp))
        repeat(3) {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).background(LuxuryCard, RoundedCornerShape(20.dp)).border(1.dp, LuxuryBorder, RoundedCornerShape(20.dp)).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painterResource(id = R.drawable.homescreen_illustration), contentDescription = null, modifier = Modifier.size(70.dp).clip(RoundedCornerShape(14.dp)), contentScale = ContentScale.Crop)
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Kitchen Cleaning", color = LuxuryTextPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                    Text("Starting at AED 199", color = LuxuryGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = {}, modifier = Modifier.background(LuxuryGold.copy(alpha = 0.1f), CircleShape)) {
                    Icon(Icons.Default.Add, null, tint = LuxuryGold)
                }
            }
        }
    }
}

private data class HomeCatItem(val name: String, val icon: ImageVector)
