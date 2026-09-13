package com.nisr.sauservices.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.nisr.sauservices.R
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.components.*
import com.nisr.sauservices.ui.viewmodel.*
import com.nisr.sauservices.data.model.Vendor
import com.nisr.sauservices.data.local.SessionManager
import java.util.Calendar

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
private val LuxeGold = Color(0xFFE8C66A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SauHomeScreen(
    navController: NavController,
    viewModel: HomeViewModel,
    bookingsViewModel: BookingsViewModel,
    sessionManager: SessionManager,
    locationViewModel: LocationViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val locationUiState = locationViewModel.uiState
    val user = authViewModel.currentUser
    val context = LocalContext.current

    // Greeting logic
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greetingText = when (hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }
    val userName = (user?.userMetadata?.get("full_name") ?: user?.userMetadata?.get("name"))?.toString()?.split(" ")?.firstOrNull() ?: "User"
    val userAvatar = (user?.userMetadata?.get("avatar_url") ?: user?.userMetadata?.get("picture"))?.toString()

    // Location fetching
    LaunchedEffect(Unit) {
        locationViewModel.getCurrentLocation(context)
    }

    Scaffold(
        containerColor = LuxeBackground,
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // 1. SMART HEADER (Location & Profile)
            item {
                LuxeHomeHeader(
                    userName = userName,
                    userAvatar = userAvatar,
                    currentAddress = if (locationUiState.isFetchingAddress) "Locating..." else locationUiState.address,
                    greeting = greetingText,
                    onLocationClick = { navController.navigate(Screen.MapPicker) },
                    onProfileClick = { navController.navigate(Screen.LuxuryProfile) }
                )
            }

            // 2. UNIVERSAL SEARCH (Command Center)
            item {
                Box(Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    LuxeSearchBar(onSearchClick = { navController.navigate(Screen.Search) })
                }
            }

            // 3. CORE SHORTCUTS (High-Velocity Entry)
            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    LuxeCircleShortcut("Grocery", R.drawable.home_essentials, LuxeHighlightChampagne) { navController.navigate(Screen.CategoryVendors("Grocery")) }
                    LuxeCircleShortcut("Dining", R.drawable.home_lifestyle, LuxeHighlightChampagne) { navController.navigate(Screen.CategoryVendors("Dining")) }
                    LuxeCircleShortcut("Service", R.drawable.residential_services, LuxeHighlightChampagne) { navController.navigate(Screen.CategoryVendors("Service")) }
                    LuxeCircleShortcut("Health", R.drawable.healthcare_pharmacy, LuxeHighlightChampagne) { navController.navigate(Screen.CategoryVendors("Healthcare")) }
                }
            }

            // 4. CONCIERGE HERO (The "Help" card)
            item {
                Box(Modifier.padding(horizontal = 20.dp)) {
                    ConciergeHeroCard(onMakeRequest = { navController.navigate(Screen.Categories) })
                }
            }

            // 5. NEIGHBORHOOD PULSE (Lovely local find - removed hardcoded)
            /* 
            item {
                Box(Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {
                    DiscoveryMomentCard()
                }
            }
            */

            // 6. TRUSTED NEARBY (Live Shops)
            item {
                LuxeSectionHeader("Trusted Nearby", onActionClick = { navController.navigate(Screen.HomeEssentialsMain) })
                
                when (val state = uiState) {
                    is HomeUiState.Loading -> {
                        Row(Modifier.padding(horizontal = 20.dp).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            repeat(3) { LuxeSkeletonVendorCard() }
                        }
                    }
                    is HomeUiState.Success -> {
                        val vendors = state.vendors
                        if (vendors.isNotEmpty()) {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(vendors, key = { it.id }) { vendor ->
                                    // Luxe Animation: Fade & Scale in
                                    var visible by remember { mutableStateOf(false) }
                                    LaunchedEffect(Unit) { visible = true }
                                    
                                    AnimatedVisibility(
                                        visible = visible,
                                        enter = fadeIn(tween(1000)) + scaleIn(initialScale = 0.9f, animationSpec = tween(600))
                                    ) {
                                        LuxeVendorCard(vendor) { navController.navigate(Screen.MerchantShop(vendor.id)) }
                                    }
                                }
                            }
                        } else {
                            Box(Modifier.padding(horizontal = 20.dp)) {
                                Text("No vendors available in your area.", color = LuxeTextSecondary, fontSize = 14.sp)
                            }
                        }
                    }
                    is HomeUiState.Error -> {
                        Box(Modifier.padding(horizontal = 20.dp)) {
                            Column {
                                Text("Unable to load vendors", color = Color.Red, fontSize = 14.sp)
                                TextButton(onClick = { viewModel.refresh() }) {
                                    Text("Retry", color = LuxeAccentSage)
                                }
                            }
                        }
                    }
                }
            }

            // 7. YOUR USUALS (One-tap reorder - removed hardcoded)
            /*
            item {
                Spacer(Modifier.height(32.dp))
                LuxeSectionHeader("Your Usuals")
                Box(Modifier.padding(horizontal = 20.dp)) {
                    YourUsualsCard(onReorder = { })
                }
            }
            */
        }
    }
}

@Composable
private fun LuxeHomeHeader(
    userName: String,
    userAvatar: String?,
    currentAddress: String,
    greeting: String,
    onLocationClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            // Location
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(onClick = onLocationClick)
            ) {
                Icon(Icons.Default.LocationOn, null, tint = LuxeAccentSage, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    text = currentAddress,
                    color = LuxeTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(Icons.Default.KeyboardArrowDown, null, tint = LuxeTextSecondary, modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.height(8.dp))
            // Greeting
            Text(
                text = "$greeting, $userName",
                style = MaterialTheme.typography.headlineSmall,
                color = LuxeTextPrimary,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Serif
            )
            Text(
                text = "Your day, gently arranged.",
                color = LuxeTextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Profile Avatar
        Surface(
            modifier = Modifier
                .size(48.dp)
                .clickable(onClick = onProfileClick)
                .border(1.dp, LuxeGold.copy(alpha = 0.5f), CircleShape),
            shape = CircleShape,
            color = LuxeHighlightChampagne
        ) {
            if (userAvatar != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(userAvatar)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Profile",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(contentAlignment = Alignment.Center) {
                    Text(userName.take(1), fontWeight = FontWeight.Bold, color = LuxeTextPrimary)
                }
            }
        }
    }
}

@Composable
private fun LuxeSearchBar(onSearchClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clickable(onClick = onSearchClick)
            .shadow(4.dp, RoundedCornerShape(14.dp), spotColor = LuxeTextSecondary.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(14.dp),
        color = LuxeCard,
        border = BorderStroke(1.dp, LuxeBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Search, null, tint = LuxeAccentSage)
            Spacer(Modifier.width(12.dp))
            Text(
                "Search your neighbourhood...",
                color = LuxeTextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.Mic, null, tint = LuxeTextSecondary, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun LuxeCircleShortcut(title: String, imageRes: Int, color: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick)) {
        Surface(
            modifier = Modifier.size(68.dp),
            shape = CircleShape,
            color = color,
            border = BorderStroke(1.dp, LuxeBorder)
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier.padding(14.dp),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(title, color = LuxeTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ConciergeHeroCard(onMakeRequest: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = LuxeCard),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(24.dp)) {
            Text(
                "What may we take care of?",
                style = MaterialTheme.typography.titleLarge,
                color = LuxeTextPrimary,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onMakeRequest,
                colors = ButtonDefaults.buttonColors(containerColor = LuxeAccentSage),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(44.dp)
            ) {
                Text("MAKE A REQUEST", fontWeight = FontWeight.Black, fontSize = 11.sp, letterSpacing = 1.sp)
            }
        }
    }
}

@Composable
private fun LuxeSectionHeader(title: String, onActionClick: (() -> Unit)? = null) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = LuxeTextPrimary, fontWeight = FontWeight.Black, fontFamily = FontFamily.Serif)
        if (onActionClick != null) {
            TextButton(onClick = onActionClick) {
                Text("View all", color = LuxeAccentSage, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun LuxeVendorCard(vendor: Vendor, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(260.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = LuxeCard),
        border = BorderStroke(1.dp, LuxeBorder)
    ) {
        Column {
                 Box {
                if (vendor.imageUrl != null) {
                    android.util.Log.d("PHOTO_DEBUG", "Loading shop image: ${vendor.imageUrl}")
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(vendor.imageUrl)
                            .crossfade(true)
                            .placeholder(R.drawable.sau_logo) // Use an existing drawable as placeholder
                            .error(R.drawable.sau_logo) // Use same for error
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(140.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(Modifier.fillMaxWidth().height(140.dp).background(LuxeHighlightChampagne), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Store, null, tint = LuxeAccentSage)
                    }
                }
                
                if (vendor.isAvailable == false) {
                    Surface(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f))) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("CLOSED", color = Color.White, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
            Column(Modifier.padding(16.dp)) {
                Text(vendor.displayName, fontWeight = FontWeight.Bold, color = LuxeTextPrimary, maxLines = 1)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = LuxeGold, modifier = Modifier.size(14.dp))
                    Text(" ${vendor.displayRating} • ${vendor.deliveryTime ?: "20 min"}", color = LuxeTextSecondary, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun LuxeSkeletonVendorCard() {
    Surface(
        modifier = Modifier.width(260.dp).height(200.dp),
        shape = RoundedCornerShape(20.dp),
        color = LuxeHighlightChampagne.copy(alpha = 0.3f),
        border = BorderStroke(1.dp, LuxeBorder)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text("Loading...", color = LuxeTextSecondary, fontSize = 12.sp)
        }
    }
}

