package com.nisr.sauservices.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.delay
import com.nisr.sauservices.R
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.components.*
import com.nisr.sauservices.ui.viewmodel.HomeViewModel
import com.nisr.sauservices.ui.viewmodel.HomeUiState
import com.nisr.sauservices.ui.viewmodel.BookingsViewModel
import com.nisr.sauservices.ui.viewmodel.AuthViewModel
import com.nisr.sauservices.ui.viewmodel.LocationViewModel
import com.nisr.sauservices.data.model.Category
import com.nisr.sauservices.data.model.HomeEssentialsData
import com.nisr.sauservices.data.model.Vendor
import com.nisr.sauservices.data.local.SessionManager
import java.util.Calendar

// ============================================================
// PREMIUM COLORS
// ============================================================

private val LuxuryBlack = Color(0xFF0B0B0A)
private val LuxuryCard = Color(0xFF151514)
private val LuxuryCard2 = Color(0xFF1C1C1A)

private val Gold = Color(0xFFE8C66A)
private val Cream = Color(0xFFF3E3B5)

private val White = Color(0xFFFFFFFF)
private val Muted = Color(0xFFA8A8A5)
private val Divider = Color(0xFF292927)

private val Green = Color(0xFF35B879)
private val Orange = Color(0xFFFF8A3D)
private val Blue = Color(0xFF4D8DFF)


// ============================================================
// DEMO DATA
// NOTE: replace these with real state from your ViewModel.
// They only exist here so the screen has something to render.
// ============================================================

private data class SauActiveOrder(
    val bookingId: String,
    val title: String,
    val subtitle: String,
    val etaMinutes: Int
)

private data class SauHeroSlide(
    val eyebrow: String,
    val titleLine1: String,
    val titleLine2: String,
    val ctaText: String,
    val destinationCategory: String,
    val imageRes: Int? = null
)

private data class SauReorderItem(
    val title: String,
    val icon: ImageVector,
    val iconColor: Color,
    val category: String,
    val imageRes: Int? = null
)

private data class SauPopularService(
    val title: String,
    val price: String,
    val rating: String,
    val etaMinutes: String,
    val icon: ImageVector,
    val iconColor: Color,
    val imageRes: Int? = null
)


private val heroSlides = listOf(
    SauHeroSlide(
        eyebrow = "PREMIUM",
        titleLine1 = "Everything you need.",
        titleLine2 = "All in one place.",
        ctaText = "Explore Now",
        destinationCategory = "all",
        imageRes = R.drawable.homescreen_illustration // Standard: hero_lifestyle
    ),
    SauHeroSlide(
        eyebrow = "SPECIAL OFFER",
        titleLine1 = "20% off",
        titleLine2 = "your first order",
        ctaText = "Use code SAU20",
        destinationCategory = "essentials",
        imageRes = R.drawable.essential_supplies // Standard: hero_offer
    ),
    SauHeroSlide(
        eyebrow = "NEW",
        titleLine1 = "Verified pros,",
        titleLine2 = "same-day booking",
        ctaText = "Book a service",
        destinationCategory = "residential",
        imageRes = R.drawable.sau_worker // Standard: hero_professional
    )
)

private val reorderItems = listOf(
    SauReorderItem("Plumbing", Icons.Default.WaterDrop, Color(0xFF52B6FF), "residential", R.drawable.plumber),
    SauReorderItem("Biryani Hub", Icons.Default.Fastfood, Orange, "food", R.drawable.food_beverages),
    SauReorderItem("Weekly Kirana", Icons.Default.LocalGroceryStore, Green, "essentials", R.drawable.essential_supplies)
)

private val popularServices = listOf(
    SauPopularService("AC Repair", "From ₹499", "4.7", "30 min", Icons.Default.AcUnit, Blue, R.drawable.ac_repair), // Standard: srv_ac_repair
    SauPopularService("Plumbing", "From ₹299", "4.8", "20 min", Icons.Default.WaterDrop, Color(0xFF52B6FF), R.drawable.plumber), // Standard: srv_plumber
    SauPopularService("Cleaning", "From ₹399", "4.9", "45 min", Icons.Default.CleaningServices, Green, R.drawable.cleaning), // Standard: srv_cleaning
    SauPopularService("Electrician", "From ₹199", "4.6", "25 min", Icons.Default.Bolt, Orange, R.drawable.electrician), // Standard: srv_electrician
    SauPopularService("Salon", "From ₹249", "4.8", "40 min", Icons.Default.ContentCut, Blue, R.drawable.salon) // Standard: srv_salon
)

// Categories trimmed to remove overlap with the Quick Actions row
// (Groceries / Food / Services already live there).
private val exploreCategories = listOf(
    SauPremiumCategory("Lifestyle", Icons.Default.Spa, Color(0xFFE07BC5), "lifestyle"),
    SauPremiumCategory("Education", Icons.Default.School, Color(0xFF9B7BFF), "education"),
    SauPremiumCategory("Healthcare", Icons.Default.MedicalServices, Color(0xFF4AC6B7), "health"),
    SauPremiumCategory("Technology", Icons.Default.Computer, Color(0xFF55A9FF), "tech"),
    SauPremiumCategory("More", Icons.Default.Apps, Gold, "all")
)

private data class SauPremiumCategory(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val category: String
)


// ============================================================
// SKELETON SCREENS (LOADING STATES)
// ============================================================

@Composable
fun PremiumHeroSkeleton() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .clip(RoundedCornerShape(24.dp))
            .shimmerEffect()
    )
}

@Composable
fun PremiumCategoriesSkeleton() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        repeat(3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(66.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .shimmerEffect()
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(66.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .shimmerEffect()
                )
            }
        }
    }
}

// ============================================================
// GREETING UTILS
// ============================================================

private fun getSmartGreeting(): Pair<String, String> {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..11 -> "GOOD MORNING" to "Ready to start your day?"
        in 12..16 -> "GOOD AFTERNOON" to "Hope your day is going well!"
        in 17..20 -> "GOOD EVENING" to "How can we help you tonight?"
        else -> "HELLO" to "Late night needs? We're here."
    }
}

// ============================================================
// NAVIGATION UTILS
// ============================================================

private fun navigateToCategory(navController: NavController, category: String) {
    when (category) {
        "essentials" -> navController.navigate(Screen.HomeEssentialsMain)
        "food" -> navController.navigate(Screen.FoodCategories)
        "residential" -> navController.navigate(Screen.ResidentialCategories)
        "lifestyle" -> navController.navigate(Screen.LifestyleSubcategories("Lifestyle"))
        "education" -> navController.navigate(Screen.EducationSubcategories("Education"))
        "health" -> navController.navigate(Screen.HealthcareMain)
        "tech" -> navController.navigate(Screen.TechSubcategories("Technology"))
        "all" -> navController.navigate(Screen.Categories)
        else -> navController.navigate(Screen.Categories)
    }
}

// ============================================================
// HOME SCREEN
// ============================================================

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
    val myBookings by bookingsViewModel.myBookings.collectAsState()
    val locationUiState = locationViewModel.uiState
    
    val context = LocalContext.current
    
    val user = authViewModel.currentUser
    
    // Log for debugging
    LaunchedEffect(user) {
        user?.let {
            android.util.Log.d("AUTH_DEBUG", "Metadata keys: ${it.userMetadata?.keys}")
            android.util.Log.d("AUTH_DEBUG", "Full Name: ${it.userMetadata?.get("full_name")}")
            android.util.Log.d("AUTH_DEBUG", "Avatar: ${it.userMetadata?.get("avatar_url")}")
        }
    }

    val userName = (user?.userMetadata?.get("full_name") ?: user?.userMetadata?.get("name") ?: user?.userMetadata?.get("display_name"))?.toString()?.split(" ")?.firstOrNull() 
    val userAvatar = (user?.userMetadata?.get("avatar_url") ?: user?.userMetadata?.get("picture") ?: user?.userMetadata?.get("avatar"))?.toString()

    val smartGreeting = remember { getSmartGreeting() }
    
    // Fallback logic: Use real-time address if available, otherwise sessionManager's
    val currentAddress = if (locationUiState.address != "Fetching address..." && !locationUiState.isFetchingAddress) {
        locationUiState.address
    } else {
        remember { sessionManager.getAddress() }
    }

    // Trigger location fetch on start
    LaunchedEffect(Unit) {
        locationViewModel.getCurrentLocation(context)
    }
    
    // Auto-save new address to session if fetched successfully
    LaunchedEffect(locationUiState.address) {
        if (locationUiState.address != "Fetching address..." && 
            locationUiState.address != "Error fetching address" &&
            !locationUiState.isFetchingAddress) {
            sessionManager.saveLocation(
                locationUiState.centerLocation.latitude,
                locationUiState.centerLocation.longitude,
                locationUiState.address
            )
        }
    }

    val activeBooking = remember(myBookings) {
        myBookings.firstOrNull { 
            val s = it.status.lowercase()
            s == "pending" || s == "confirmed" || s == "assigned" || s == "in_progress" || s == "on_the_way"
        }
    }

    val activeOrder = activeBooking?.let {
        SauActiveOrder(
            bookingId = it.id,
            title = "${it.serviceName} is ${it.status}",
            subtitle = "${it.category} · #${it.id.takeLast(4)}",
            etaMinutes = 25
        )
    }

    Scaffold(
        containerColor = LuxuryBlack,
        topBar = {
            SauHomeHeader(
                navController = navController,
                cartItemCount = 3,
                currentAddress = currentAddress,
                userName = userName,
                userAvatar = userAvatar,
                greeting = smartGreeting.first,
                statusMessage = smartGreeting.second,
                onSignOut = {
                    authViewModel.signOut()
                    sessionManager.saveLoginState(false)
                    navController.navigate(Screen.Login("customer")) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        },
        bottomBar = {
            BottomNavBar(navController)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(LuxuryBlack),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)
        ) {
            // 1. SEARCH BAR (Now at the top for immediate access)
            item {
                PremiumSearchBar(navController = navController)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 2. TRENDING TAGS
            item {
                TrendingTagsRow(navController = navController)
                Spacer(modifier = Modifier.height(18.dp))
            }

            // 3. ACTIVE ORDER (only shows when something is in progress)
            item {
                ActiveOrderBanner(
                    order = activeOrder,
                    navController = navController
                )
                if (activeOrder != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }

            // 4. HERO CAROUSEL
            item {
                if (uiState is HomeUiState.Loading) {
                    PremiumHeroSkeleton()
                } else {
                    PremiumHeroCarousel(navController = navController)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // 5. QUICK ACTIONS
            item {
                PremiumQuickActions(navController = navController)
                Spacer(modifier = Modifier.height(26.dp))
            }

            // 5b. LOCAL VENDORS (New high-priority focus)
            item {
                PremiumSectionHeader(
                    eyebrow = "NEARBY",
                    title = "Local Vendors",
                    actionText = "See all",
                    onActionClick = { navController.navigate(Screen.HomeEssentialsMain) }
                )
                Spacer(modifier = Modifier.height(14.dp))
                
                when (val state = uiState) {
                    is HomeUiState.Success -> {
                        LocalVendorsRow(navController = navController, vendors = state.vendors)
                    }
                    else -> {
                        // Show placeholder or skeleton if needed
                    }
                }
                Spacer(modifier = Modifier.height(26.dp))
            }

            // 6. ORDER AGAIN
            item {
                PremiumSectionHeader(
                    eyebrow = "REORDER",
                    title = "Order again"
                )
                Spacer(modifier = Modifier.height(12.dp))
                OrderAgainRow(navController = navController)
                Spacer(modifier = Modifier.height(26.dp))
            }

            // 7. CATEGORIES
            item {
                PremiumSectionHeader(
                    eyebrow = "EXPLORE",
                    title = "More categories",
                    actionText = "See all",
                    onActionClick = { navController.navigate(Screen.Categories) }
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            item {
                when (val state = uiState) {
                    is HomeUiState.Loading -> PremiumCategoriesSkeleton()
                    is HomeUiState.Success -> PremiumCategoriesGrid(navController = navController, categories = state.categories)
                    is HomeUiState.Error -> Text(text = state.message, color = Color.Red, modifier = Modifier.padding(16.dp))
                }
                Spacer(modifier = Modifier.height(28.dp))
            }

            // 8. POPULAR SERVICES
            item {
                PremiumSectionHeader(
                    eyebrow = "POPULAR",
                    title = "Services near you",
                    actionText = "View all",
                    onActionClick = {
                        navController.navigate(Screen.ResidentialCategories)
                    }
                )
                Spacer(modifier = Modifier.height(14.dp))
                PopularServicesRow(navController = navController)
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // 9. TRUST BADGES (New addition for professionalism)
            item {
                TrustBadgesRow()
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}


// ============================================================
// HEADER (now with a cart badge)
// ============================================================

@Composable
private fun SauHomeHeader(
    navController: NavController,
    cartItemCount: Int,
    currentAddress: String,
    userName: String?,
    userAvatar: String?,
    greeting: String,
    statusMessage: String,
    onSignOut: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LuxuryBlack)
            .statusBarsPadding()
            .drawBehind {
                drawLine(
                    color = Divider.copy(alpha = 0.5f),
                    start = androidx.compose.ui.geometry.Offset(0f, size.height),
                    end = androidx.compose.ui.geometry.Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(start = 18.dp, end = 18.dp, top = 8.dp, bottom = 12.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box {
                Surface(
                    modifier = Modifier
                        .size(44.dp)
                        .clickable { showMenu = true },
                    shape = RoundedCornerShape(12.dp),
                    color = LuxuryCard,
                    border = BorderStroke(1.dp, Divider)
                ) {
                    Box(contentAlignment = Alignment.Center) {
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
                            Image(
                                painter = painterResource(id = R.drawable.sau_logo),
                                contentDescription = "Logo",
                                modifier = Modifier.size(28.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
                
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(LuxuryCard).border(1.dp, Divider, RoundedCornerShape(8.dp))
                ) {
                    DropdownMenuItem(
                        text = { Text("Sign Out", color = Color.Red, fontWeight = FontWeight.Bold) },
                        onClick = {
                            showMenu = false
                            onSignOut()
                        },
                        leadingIcon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.Red) }
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = if (userName != null) "$greeting, $userName" else "SAU SOLUTIONS",
                    color = Gold,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Black,
                        letterSpacing = if (userName != null) 0.5.sp else 3.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (userName != null) {
                    Text(
                        text = statusMessage,
                        color = Muted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { 
                        navController.navigate(Screen.MapPicker) 
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Gold,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = currentAddress,
                        color = Muted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Gold,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Cart icon with badge
            Box(
                modifier = Modifier
                    .clickable {
                        navController.navigate(Screen.Cart) // TODO: add this route if missing
                    }
                    .padding(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = "Cart",
                    tint = White
                )
                if (cartItemCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(15.dp)
                            .clip(CircleShape)
                            .background(Gold),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cartItemCount.toString(),
                            color = LuxuryBlack,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            IconButton(
                onClick = { navController.navigate(Screen.Notifications) }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = White
                )
            }
        }
    }
}


// ============================================================
// ACTIVE ORDER BANNER
// ============================================================

@Composable
private fun ActiveOrderBanner(
    order: SauActiveOrder?,
    navController: NavController
) {
    AnimatedVisibility(
        visible = order != null,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        if (order == null) return@AnimatedVisibility

        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "alpha"
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .clickable {
                    navController.navigate(Screen.OrderTracking(order.bookingId))
                },
            shape = RoundedCornerShape(16.dp),
            color = LuxuryCard,
            border = BorderStroke(1.dp, Gold.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Gold.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TrackChanges,
                        contentDescription = null,
                        tint = Gold,
                        modifier = Modifier
                            .size(20.dp)
                            .graphicsLayer(alpha = alpha)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Gold)
                                .graphicsLayer(alpha = alpha)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = order.title,
                            color = White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Text(
                        text = order.subtitle,
                        color = Muted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Surface(
                    color = Gold,
                    shape = RoundedCornerShape(8.dp),
                    onClick = { navController.navigate(Screen.OrderTracking(order.bookingId)) }
                ) {
                    Text(
                        text = "TRACK",
                        color = LuxuryBlack,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}


// ============================================================
// HERO CAROUSEL (replaces the static hero + separate offer banner)
// ============================================================

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PremiumHeroCarousel(
    navController: NavController
) {
    val pagerState = rememberPagerState(pageCount = { heroSlides.size })

    LaunchedEffect(pagerState.currentPage) {
        delay(5000)
        val nextPage = (pagerState.currentPage + 1) % heroSlides.size
        pagerState.animateScrollToPage(nextPage)
    }

    HorizontalPager(state = pagerState) { page ->
        val slide = heroSlides[page]

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF15140F))
        ) {
            if (slide.imageRes != null) {
                Image(
                    painter = painterResource(id = slide.imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.6f
                )
            }
            
            // Text Legibility Gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, LuxuryBlack.copy(alpha = 0.8f)),
                            startY = 0f
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .align(Alignment.BottomStart)
            ) {

                Text(
                    text = slide.eyebrow,
                    color = Gold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = slide.titleLine1,
                    color = White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 28.sp
                )

                Text(
                    text = slide.titleLine2,
                    color = Cream,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 28.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        navigateToCategory(navController, slide.destinationCategory)
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Gold,
                        contentColor = LuxuryBlack
                    ),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text(
                        text = slide.ctaText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(9.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        heroSlides.indices.forEach { index ->
            val isSelected = pagerState.currentPage == index
            Box(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .size(
                        width = if (isSelected) 14.dp else 4.dp,
                        height = 4.dp
                    )
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (isSelected) Gold else Divider)
            )
        }
    }
}


// ============================================================
// SEARCH
// ============================================================

@Composable
private fun TrendingTagsRow(navController: NavController) {
    val tags = listOf(
        "Local Shops" to Icons.Default.Storefront,
        "AC Service" to Icons.Default.AcUnit,
        "Cleaning" to Icons.Default.CleaningServices,
        "Plumbing" to Icons.Default.WaterDrop,
        "Electrician" to Icons.Default.Bolt
    )
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tags.forEach { (tag, icon) ->
            Surface(
                modifier = Modifier.clickable {
                    navController.navigate(Screen.SearchResults(tag))
                },
                shape = RoundedCornerShape(12.dp),
                color = LuxuryCard,
                border = BorderStroke(1.dp, Divider.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Gold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = tag,
                        color = Muted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun PremiumSearchBar(
    navController: NavController
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable {
                navController.navigate(Screen.Search)
            },
        shape = RoundedCornerShape(17.dp),
        color = LuxuryCard,
        border = BorderStroke(1.dp, Divider)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 17.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Search",
                tint = Gold,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Search groceries, food or services...",
                color = Muted,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Voice Search",
                tint = Muted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}


// ============================================================
// QUICK ACTIONS (the single source of truth for these 3 verticals —
// no longer duplicated inside the categories grid below)
// ============================================================

@Composable
private fun PremiumQuickActions(
    navController: NavController
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        PremiumQuickAction(
            modifier = Modifier.weight(1f),
            title = "Groceries",
            imageRes = R.drawable.essential_supplies,
            tintColor = Green,
            onClick = { navController.navigate(Screen.HomeEssentialsMain) }
        )

        PremiumQuickAction(
            modifier = Modifier.weight(1f),
            title = "Food",
            imageRes = R.drawable.food_beverages,
            tintColor = Orange,
            onClick = { navController.navigate(Screen.FoodCategories) }
        )

        PremiumQuickAction(
            modifier = Modifier.weight(1f),
            title = "Services",
            imageRes = R.drawable.sau_worker,
            tintColor = Blue,
            onClick = { navController.navigate(Screen.ResidentialCategories) }
        )
    }
}


@Composable
private fun PremiumQuickAction(
    modifier: Modifier = Modifier,
    title: String,
    imageRes: Int,
    tintColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(72.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = LuxuryCard.copy(alpha = 0.7f), // Glassy effect
        border = BorderStroke(1.dp, Gold.copy(alpha = 0.1f)) // Gold-tinted border
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(tintColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().padding(6.dp).clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                color = White,
                fontSize = 11.sp, // Slightly smaller to ensure one line
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


// ============================================================
// SECTION HEADER
// ============================================================

@Composable
private fun PremiumSectionHeader(
    eyebrow: String,
    title: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = eyebrow,
                color = Gold,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                color = White,
                fontSize = 21.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        if (actionText != null) {
            Text(
                text = actionText,
                color = Gold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onActionClick?.invoke() }
                    .padding(6.dp)
            )
        }
    }
}


// ============================================================
// ORDER AGAIN
// ============================================================

@Composable
private fun OrderAgainRow(
    navController: NavController
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        reorderItems.forEach { item ->
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        // TODO: hook up to real "reorder" logic (re-add last cart to basket)
                        navigateToCategory(navController, item.category)
                    },
                shape = RoundedCornerShape(16.dp),
                color = LuxuryCard
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (item.imageRes != null) {
                        Image(
                            painter = painterResource(id = item.imageRes),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = item.iconColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = item.title,
                        color = White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Reorder",
                        color = Gold,
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}


// ============================================================
// CATEGORIES (trimmed grid — 5 items instead of 8)
// ============================================================

@Composable
private fun PremiumCategoriesGrid(
    navController: NavController,
    categories: List<Category>
) {
    val items = categories.ifEmpty { 
        // Fallback to static if DB is empty for demo purposes
        exploreCategories.map { Category(id = it.category, name = it.title) } 
    }
    
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { category ->
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(66.dp)
                            .clickable {
                                navigateToCategory(navController, category.id)
                            },
                        shape = RoundedCornerShape(16.dp),
                        color = LuxuryCard
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(11.dp))
                                    .background(Gold.copy(alpha = 0.14f)), // Use generic Gold for dynamic
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Apps, // Generic icon for dynamic categories
                                    contentDescription = category.name,
                                    tint = Gold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(9.dp))
                            Text(
                                text = category.name,
                                color = White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}


// ============================================================
// POPULAR SERVICES (now with rating + ETA)
// ============================================================

@Composable
private fun PopularServicesRow(
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        popularServices.forEach { service ->
            Surface(
                modifier = Modifier
                    .width(150.dp)
                    .clickable {
                        navController.navigate(Screen.ResidentialCategories)
                    },
                shape = RoundedCornerShape(20.dp),
                color = LuxuryCard
            ) {
                Column(modifier = Modifier.padding(13.dp)) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .clip(RoundedCornerShape(13.dp))
                            .background(LuxuryCard2),
                        contentAlignment = Alignment.Center
                    ) {
                        if (service.imageRes != null) {
                            Image(
                                painter = painterResource(id = service.imageRes),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = service.icon,
                                contentDescription = null,
                                tint = service.iconColor,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = service.title,
                        color = White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Gold,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${service.rating} · ${service.etaMinutes}",
                            color = Muted,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = service.price,
                        color = Gold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun LocalVendorsRow(navController: NavController, vendors: List<Vendor>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        vendors.forEach { vendor ->
            Surface(
                modifier = Modifier
                    .width(220.dp)
                    .clickable {
                        navController.navigate(Screen.HomeEssentialsCategory("veg_fruits", vendor.id))
                    },
                shape = RoundedCornerShape(20.dp),
                color = LuxuryCard,
                border = BorderStroke(1.dp, if (vendor.isOpen) Gold.copy(alpha = 0.1f) else Divider)
            ) {
                Box {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Shop Image / Icon Placeholder
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(LuxuryCard2),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!vendor.imageUrl.isNullOrEmpty()) {
                                AsyncImage(
                                    model = vendor.imageUrl,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Store,
                                    contentDescription = null,
                                    tint = Gold,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            
                            if (!vendor.isOpen) {
                                Surface(
                                    modifier = Modifier.fillMaxSize(),
                                    color = Color.Black.copy(alpha = 0.6f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "CLOSED",
                                            color = Color.White,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 12.sp,
                                            letterSpacing = 1.sp
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = vendor.name,
                            color = White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, null, tint = Gold, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${vendor.rating} · ${vendor.distance}",
                                color = Muted,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Delivery Time & Offers
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Timer, null, tint = Blue, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(vendor.deliveryTime, color = Muted, fontSize = 11.sp)
                            }
                            
                            if (vendor.offers.isNotEmpty()) {
                                Surface(
                                    color = Green.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = vendor.offers,
                                        color = Green,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
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
private fun TrustBadgesRow() {
    val badges = listOf(
        Triple(Icons.Default.VerifiedUser, "Verified\nPros", Green),
        Triple(Icons.Default.Timer, "25-Min\nArrival", Blue),
        Triple(Icons.Default.Security, "Secure\nPayments", Gold),
        Triple(Icons.Default.Star, "Top\nRated", Orange)
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        items(badges) { badge ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(LuxuryCard)
                        .border(1.dp, Divider, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = badge.first,
                        contentDescription = null,
                        tint = badge.third,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = badge.second,
                    color = Muted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 12.sp
                )
            }
        }
    }
}
