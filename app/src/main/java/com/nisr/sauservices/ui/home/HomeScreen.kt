package com.nisr.sauservices.ui.home

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
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
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.ui.education.EducationBottomSheet
import com.nisr.sauservices.ui.mechanic.MechanicBottomSheet
import com.nisr.sauservices.ui.mobility.MobilityBottomSheet
import com.nisr.sauservices.ui.tech.TechBottomSheet
import kotlinx.coroutines.delay
import java.util.Calendar

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
        
        locationViewModel.loadSavedAddress(context)
        locationViewModel.getCurrentLocation(context, autoConfirmIfNew = isFirstLaunchLocation)
        
        if (userProfile == null) {
            profileViewModel.fetchUserProfile()
        }
    }

    val activeOrder by viewModel.latestActiveOrder.collectAsState()
    val quickReorderItems by viewModel.quickReorderItems.collectAsState()

    var selectedFilter by remember { mutableStateOf("All") }
    var showEduSheet by remember { mutableStateOf(false) }
    var showTechSheet by remember { mutableStateOf(false) }
    var showMechanicSheet by remember { mutableStateOf(false) }
    var showMobilitySheet by remember { mutableStateOf(false) }

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

    fun startVoiceSearch() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "What can SAU find for you?")
        }
        try {
            voiceLauncher.launch(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "Voice search is not supported on this device", Toast.LENGTH_SHORT).show()
        }
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startVoiceSearch()
        } else {
            Toast.makeText(context, "Microphone access is required for voice search. Please enable it in Settings.", Toast.LENGTH_LONG).show()
        }
    }

    val scrollState = rememberLazyListState()
    
    // Parallax calculations
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
        containerColor = MaterialTheme.colorScheme.background,
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
                            onProfileClick = { navController.navigate(Screen.LuxuryProfile) },
                            onWalletClick = { navController.navigate(Screen.Wallet) }
                        )
                    }
                }

                // 2. UNIVERSAL SEARCH (Command Center)
                item {
                    Box(Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                        LuxeSearchBar(
                            onSearchClick = { navController.navigate(Screen.Search) },
                            onMicClick = {
                                val hasMicPermission = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.RECORD_AUDIO
                                ) == PackageManager.PERMISSION_GRANTED

                                if (hasMicPermission) {
                                    startVoiceSearch()
                                } else {
                                    micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            }
                        )
                    }
                }

                // 2.5. CATEGORY FILTER CHIPS
                item {
                    Spacer(Modifier.height(4.dp))
                    LuxeCategoryFilterChips(selectedCategory = selectedFilter) { filter ->
                        selectedFilter = filter
                        when (filter) {
                            "Essentials" -> navController.navigate(Screen.HomeEssentialsMain)
                            "Services" -> navController.navigate(Screen.ResidentialCategories)
                            else -> {}
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }

                // 3. MODERN BENTO GRID SERVICES (Image-Backed Design)
                item {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "EXPLORE SAU",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )

                        // Row 1: Groceries & Dining
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            LuxeImageBentoCard(
                                title = "Groceries",
                                subtitle = "Kirana & Fresh",
                                imageRes = R.drawable.home_essentials,
                                modifier = Modifier.weight(1f)
                            ) {
                                navController.navigate(Screen.HomeEssentialsMain)
                            }

                            LuxeImageBentoCard(
                                title = "Dining",
                                subtitle = "Restaurants & Cafes",
                                imageRes = R.drawable.home_lifestyle,
                                modifier = Modifier.weight(1f)
                            ) {
                                navController.navigate(Screen.CategoryVendors("Dining"))
                            }
                        }

                        // Row 2: Home Services Bento Card
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(115.dp)
                                .clickable { navController.navigate(Screen.ResidentialCategories) },
                            shape = RoundedCornerShape(20.dp),
                            color = LuxeCard,
                            border = BorderStroke(1.dp, LuxeBorder),
                            shadowElevation = 2.dp
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Image(
                                    painter = painterResource(id = R.drawable.residential_services),
                                    contentDescription = "Home Services",
                                    modifier = Modifier.fillMaxSize().alpha(0.25f),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.horizontalGradient(
                                                colors = listOf(Color.White, Color.White.copy(alpha = 0.85f), Color.Transparent),
                                                startX = 0f,
                                                endX = Float.POSITIVE_INFINITY
                                            )
                                        )
                                )

                                Row(
                                    modifier = Modifier.fillMaxSize().padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "HOME SERVICES",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = LuxeAccentSage,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 10.sp,
                                            letterSpacing = 1.sp
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = "Plumbing • Electrical • Cleaning • AC",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 14.sp,
                                            color = LuxeTextPrimary
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            text = "Verified professionals at your doorstep",
                                            fontSize = 11.sp,
                                            color = LuxeTextSecondary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. SERVICES FOR EVERY NEED (Carousel)
                item {
                    Column(Modifier.padding(vertical = 12.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Services for every need",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = LuxeTextPrimary,
                                fontFamily = FontFamily.Serif
                            )
                            TextButton(onClick = { navController.navigate(Screen.ResidentialCategories) }) {
                                Text("See all", color = LuxeAccentSage, fontWeight = FontWeight.Bold)
                            }
                        }
                        
                        Spacer(Modifier.height(8.dp))

                        val serviceCards = listOf(
                            Triple("Mechanic", R.drawable.mechanic_services) { showMechanicSheet = true },
                            Triple("Mobility", R.drawable.mobility_services) { showMobilitySheet = true },
                            Triple("Tech Repair", R.drawable.tech_services) { showTechSheet = true },
                            Triple("Education", R.drawable.education_services) { showEduSheet = true },
                            Triple("Cleaning", R.drawable.cleaning) { navController.navigate(Screen.ResidentialCategories) },
                            Triple("Electrical", R.drawable.electrician) { navController.navigate(Screen.ResidentialCategories) },
                            Triple("Plumbing", R.drawable.plumber) { navController.navigate(Screen.ResidentialCategories) },
                            Triple("AC Repair", R.drawable.ac_repair) { navController.navigate(Screen.ResidentialCategories) }
                        )

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(serviceCards) { (title, imageRes, onClick) ->
                                Surface(
                                    modifier = Modifier
                                        .width(90.dp)
                                        .height(100.dp)
                                        .clickable(onClick = onClick),
                                    shape = RoundedCornerShape(18.dp),
                                    color = LuxeCard,
                                    border = BorderStroke(1.dp, LuxeBorder),
                                    shadowElevation = 1.dp
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize().padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Surface(
                                            modifier = Modifier.size(44.dp),
                                            shape = CircleShape,
                                            color = LuxeHighlightChampagne
                                        ) {
                                            Image(
                                                painter = painterResource(id = imageRes),
                                                contentDescription = title,
                                                modifier = Modifier.fillMaxSize().clip(CircleShape).alpha(0.85f),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            text = title,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = LuxeTextPrimary,
                                            maxLines = 1,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 5. LIVE ORDER TRACKER (ONLY IF ACTIVE ORDER EXISTS)
                activeOrder?.let { order ->
                    item {
                        Box(Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                            LuxeActiveOrderCard(
                                order = order,
                                onClick = { navController.navigate(Screen.OrderTracking(order.id)) }
                            )
                        }
                    }
                }

                // 6. FEATURED OFFERS
                item {
                    Box(Modifier.padding(horizontal = 20.dp, vertical = 8.dp).graphicsLayer {
                        val carouselParallax = if (scrollState.firstVisibleItemIndex == 0) -scrollState.firstVisibleItemScrollOffset.toFloat() * 0.1f else 0f
                        translationY = carouselParallax
                    }) {
                        LuxeFeaturedCarousel()
                    }
                }

                // 7. TRUSTED NEARBY (Live Shops)
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
                                    Text("No vendors available in your area.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                                }
                            }
                        }
                        is HomeUiState.Error -> {
                            Box(Modifier.padding(horizontal = 20.dp)) {
                                Column {
                                    Text("Unable to load vendors", color = Color.Red, fontSize = 14.sp)
                                    TextButton(onClick = { viewModel.refresh() }) {
                                        Text("Retry", color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                }

                // 8. QUICK REORDER SECTION
                if (quickReorderItems.isNotEmpty()) {
                    item {
                        LuxeQuickReorderSection(
                            items = quickReorderItems,
                            onItemClick = { item ->
                                when (item.type) {
                                    ReorderType.VENDOR -> navController.navigate(Screen.MerchantShop(item.id))
                                    ReorderType.SERVICE -> navController.navigate(Screen.PartnerList(item.id))
                                }
                            }
                        )
                    }
                }

                // 9. THE SAU PROMISE (TRUST & SAFETY)
                item {
                    LuxeTrustSafetySection(navController)
                }
            }
        }

        if (showEduSheet) {
            EducationBottomSheet(navController = navController, onDismiss = { showEduSheet = false })
        }
        if (showTechSheet) {
            TechBottomSheet(navController = navController, onDismiss = { showTechSheet = false })
        }
        if (showMechanicSheet) {
            MechanicBottomSheet(navController = navController, onDismiss = { showMechanicSheet = false })
        }
        if (showMobilitySheet) {
            MobilityBottomSheet(navController = navController, onDismiss = { showMobilitySheet = false })
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
    onProfileClick: () -> Unit,
    onWalletClick: () -> Unit
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
                Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    text = currentAddress,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(Icons.Default.KeyboardArrowDown, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "$greeting, $userName",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Serif
            )
            Text(
                text = "Your day, gently arranged.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .clickable(onClick = onWalletClick),
                shape = RoundedCornerShape(16.dp),
                color = LuxeHighlightChampagne.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, LuxeBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AccountBalanceWallet, null, tint = LuxeAccentSage, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("₹450", fontWeight = FontWeight.Black, fontSize = 12.sp, color = LuxeTextPrimary)
                }
            }

            Surface(
                modifier = Modifier
                    .size(44.dp)
                    .clickable(onClick = onProfileClick)
                    .border(1.dp, LuxeBorder, CircleShape),
                shape = CircleShape,
                color = LuxeCard
            ) {
                if (!userAvatar.isNullOrEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(userAvatar)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Profile",
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
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
}

@Composable
private fun LuxeImageBentoCard(
    title: String,
    subtitle: String,
    imageRes: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(600)) + slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(600)),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(115.dp)
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(20.dp),
            color = LuxeCard,
            border = BorderStroke(1.dp, LuxeBorder),
            shadowElevation = 2.dp
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize().alpha(0.25f),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color.White, Color.White.copy(alpha = 0.85f), Color.Transparent),
                                startX = 0f,
                                endX = Float.POSITIVE_INFINITY
                            )
                        )
                )

                Row(
                    modifier = Modifier.fillMaxSize().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = LuxeAccentSage,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = title,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = LuxeTextPrimary
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = subtitle,
                            fontSize = 11.sp,
                            color = LuxeTextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Surface(
                        modifier = Modifier.size(52.dp),
                        shape = CircleShape,
                        border = BorderStroke(1.5.dp, LuxeBorder),
                        color = LuxeHighlightChampagne
                    ) {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LuxeCategoryFilterChips(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf("All", "Essentials", "Services", "Mechanic", "Mobility", "Tech", "Education")
    
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            Surface(
                modifier = Modifier
                    .height(36.dp)
                    .clickable { onCategorySelected(category) },
                shape = RoundedCornerShape(18.dp),
                color = if (isSelected) LuxeAccentSage else LuxeCard,
                border = BorderStroke(1.dp, if (isSelected) LuxeAccentSage else LuxeBorder),
                shadowElevation = if (isSelected) 2.dp else 0.dp
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else LuxeTextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun LuxeSearchBar(onSearchClick: () -> Unit, onMicClick: () -> Unit) {
    val hints = listOf(
        "Search 'Kirana stores'",
        "Search 'Meat shops'",
        "Search 'Organic Milk'",
        "Search 'Fresh Vegetables'",
        "Search 'Plumbing service'",
        "Search 'AC Repair'"
    )
    var index by remember { mutableIntStateOf(0) }
    
    LaunchedEffect(Unit) {
        while(true) {
            delay(3500)
            index = (index + 1) % hints.size
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = onSearchClick)
            .shadow(4.dp, RoundedCornerShape(14.dp), spotColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Search, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            
            AnimatedContent(
                targetState = hints[index],
                transitionSpec = {
                    (slideInVertically { it } + fadeIn(tween(400))) togetherWith 
                    (slideOutVertically { -it } + fadeOut(tween(400)))
                },
                label = "search_hint_anim",
                modifier = Modifier.weight(1f)
            ) { hint ->
                Text(
                    text = hint,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            IconButton(
                onClick = onMicClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(Icons.Default.Mic, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
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
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier.padding(14.dp),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(title, color = MaterialTheme.colorScheme.onBackground, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
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
        Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Black, fontFamily = FontFamily.Serif)
        if (onActionClick != null) {
            TextButton(onClick = onActionClick) {
                Text("View all", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun LuxeVendorCard(vendor: Vendor, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(260.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
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
                        modifier = Modifier.fillMaxWidth().height(140.dp).background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(Modifier.fillMaxWidth().height(140.dp).background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Storefront, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
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
                Text(vendor.displayName, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, maxLines = 1)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(14.dp))
                    Text(" ${vendor.displayRating} · 20 min", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
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
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text("Loading...", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        }
    }
}

@Composable
private fun LuxeActiveOrderCard(
    order: OrderModel,
    onClick: () -> Unit,
    trackingViewModel: TrackingViewModel = viewModel()
) {
    val uiState by trackingViewModel.uiState.collectAsState()

    LaunchedEffect(order.id) {
        trackingViewModel.startTracking(order.id)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ACTIVE ORDER",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = (order.serviceName ?: "Your Order").ifEmpty { "Your Order" },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                if (!uiState.isCompleted && uiState.etaMinutes > 0) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "~${uiState.etaMinutes} min",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = uiState.statusTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            
            Text(
                text = uiState.statusSubtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { uiState.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            )

            Spacer(Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Track Order",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun LuxeQuickReorderSection(
    items: List<QuickReorderItem>,
    onItemClick: (QuickReorderItem) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        LuxeSectionHeader("Frequently Requested", null)
        
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(items) { item ->
                QuickReorderCard(item = item, onClick = { onItemClick(item) })
            }
        }
    }
}

@Composable
private fun QuickReorderCard(
    item: QuickReorderItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
            ) {
                if (!item.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(item.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = item.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (item.type == ReorderType.VENDOR) Icons.Default.Storefront else Icons.Default.Build,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
            
            Column(Modifier.padding(12.dp)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = if (item.type == ReorderType.VENDOR) "Order Again" else "Book Again",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LuxeTrustSafetySection(navController: NavController) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        LuxeSectionHeader("The SAU Promise", null)
        
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                LuxeTrustSafetyCard(
                    title = "Live Tracking",
                    description = "Follow your professional in real-time.",
                    icon = Icons.Default.MyLocation,
                    onClick = {} // Informational
                )
            }
            item {
                LuxeTrustSafetyCard(
                    title = "Safe Payments",
                    description = "Secure Razorpay and OTP-verified cash.",
                    icon = Icons.Default.Security,
                    onClick = {} // Informational
                )
            }
            item {
                LuxeTrustSafetyCard(
                    title = "Community Rated",
                    description = "Services backed by member reviews.",
                    icon = Icons.Default.Star,
                    onClick = { navController.navigate(Screen.MyBookings) }
                )
            }
            item {
                LuxeTrustSafetyCard(
                    title = "Direct Support",
                    description = "Concierge assistance for every booking.",
                    icon = Icons.Default.SupportAgent,
                    onClick = { navController.navigate(Screen.ContactUs) }
                )
            }
        }
    }
}

@Composable
private fun LuxeTrustSafetyCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
