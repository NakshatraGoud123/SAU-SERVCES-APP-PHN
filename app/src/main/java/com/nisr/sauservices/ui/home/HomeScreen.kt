package com.nisr.sauservices.ui.home

import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
    authViewModel: AuthViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val locationUiState = locationViewModel.uiState
    val userProfile by profileViewModel.userProfile.collectAsState()
    val context = LocalContext.current

    // Greeting logic
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greetingText = when (hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }
    val userName = userProfile?.name?.split(" ")?.firstOrNull() ?: "User"
    val userAvatar = userProfile?.profilePicUrl

    // Location fetching
    LaunchedEffect(Unit) {
        val currentSavedAddress = sessionManager.getAddress()
        val isFirstLaunchLocation = currentSavedAddress == "Fetching location..." || currentSavedAddress.isEmpty()
        
        locationViewModel.getCurrentLocation(context, autoConfirmIfNew = isFirstLaunchLocation)
        profileViewModel.fetchUserProfile() // Ensure profile is fresh
    }

    val voiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val query = data?.get(0) ?: ""
                if (query.isNotEmpty()) {
                    navController.navigate(Screen.SearchResults(query))
                }
            }
        }
    )

    val scrollState = rememberLazyListState()
    
    // Parallax & Alpha calculations
    val headerAlpha by remember {
        derivedStateOf {
            if (scrollState.firstVisibleItemIndex > 0) 0f
            else (1f - (scrollState.firstVisibleItemScrollOffset.toFloat() / 300f)).coerceIn(0f, 1f)
        }
    }
    
    val headerTranslation by remember {
        derivedStateOf {
            if (scrollState.firstVisibleItemIndex > 0) 0f
            else -scrollState.firstVisibleItemScrollOffset.toFloat() * 0.4f
        }
    }

    Scaffold(
        containerColor = LuxeBackground,
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // 1. SMART HEADER (Location & Profile)
                item {
                    Box(Modifier.graphicsLayer { 
                        alpha = headerAlpha
                        translationY = headerTranslation 
                    }) {
                        LuxeHomeHeader(
                            userName = userName,
                            userAvatar = userAvatar,
                            currentAddress = if (locationUiState.isFetchingAddress) "Locating..." else locationUiState.address,
                            greeting = greetingText,
                            onLocationClick = { navController.navigate(Screen.MapPicker) },
                            onProfileClick = { navController.navigate(Screen.LuxuryProfile) }
                        )
                    }
                }

                // 2. UNIVERSAL SEARCH (Command Center)
                item {
                    Box(Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                        LuxeSearchBar(
                            onSearchClick = { navController.navigate(Screen.Search) },
                            onMicClick = {
                                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                    putExtra(RecognizerIntent.EXTRA_PROMPT, "What can SAU find for you?")
                                }
                                voiceLauncher.launch(intent)
                            }
                        )
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

                // 4. FEATURED OFFERS
                item {
                    Box(Modifier.padding(horizontal = 20.dp, vertical = 8.dp).graphicsLayer {
                        val carouselParallax = if (scrollState.firstVisibleItemIndex == 0) -scrollState.firstVisibleItemScrollOffset.toFloat() * 0.1f else 0f
                        translationY = carouselParallax
                    }) {
                        LuxeFeaturedCarousel()
                    }
                }

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
            }
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

        Surface(
            modifier = Modifier
                .size(48.dp)
                .clickable(onClick = onProfileClick)
                .border(1.dp, LuxeGold.copy(alpha = 0.5f), CircleShape),
            shape = CircleShape,
            color = LuxeHighlightChampagne
        ) {
            if (!userAvatar.isNullOrEmpty()) {
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
                    Text(userName.take(1).uppercase(), fontWeight = FontWeight.Black, color = LuxeTextPrimary)
                }
            }
        }
    }
}

@Composable
private fun LuxeSearchBar(onSearchClick: () -> Unit, onMicClick: () -> Unit) {
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
            IconButton(
                onClick = onMicClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(Icons.Default.Mic, null, tint = LuxeAccentSage, modifier = Modifier.size(20.dp))
            }
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
private fun LuxeFeaturedCarousel() {
    val pagerState = rememberPagerState(pageCount = { 3 })

    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(end = 32.dp),
        pageSpacing = 16.dp
    ) { page ->
        val banner = when(page) {
            0 -> "https://images.unsplash.com/photo-1542838132-92c53300491e" to "Premium Grocery"
            1 -> "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4" to "Exquisite Dining"
            else -> "https://images.unsplash.com/photo-1581578731548-c64695cc6958" to "Expert Care"
        }
        
        Card(
            modifier = Modifier.fillMaxWidth().height(160.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = LuxeHighlightChampagne.copy(alpha = 0.5f)),
            border = BorderStroke(1.dp, LuxeBorder)
        ) {
            Box {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(banner.first)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.8f
                )
                Column(Modifier.align(Alignment.BottomStart).padding(20.dp)) {
                    Text(banner.second.uppercase(), fontWeight = FontWeight.Black, color = Color.White, fontSize = 12.sp, letterSpacing = 2.sp)
                    Text("Summer Selection", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                }
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
                if (!vendor.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(vendor.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(140.dp).background(LuxeHighlightChampagne.copy(alpha = 0.3f)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(Modifier.fillMaxWidth().height(140.dp).background(LuxeHighlightChampagne.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Storefront, null, tint = LuxeAccentSage, modifier = Modifier.size(32.dp))
                    }
                }
                
                if (vendor.isAvailable == false) {
                    Surface(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f))) {
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
