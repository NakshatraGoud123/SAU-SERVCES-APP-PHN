package com.nisr.sauservices.ui.essentials

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import com.nisr.sauservices.R
import com.nisr.sauservices.data.model.*
import com.nisr.sauservices.data.repository.SupabaseRepository
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.components.*
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.viewmodel.CartViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nisr.sauservices.ui.viewmodel.VendorsUiState
import com.nisr.sauservices.ui.viewmodel.VendorsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeEssentialsMainScreen(
    navController: NavController, 
    cartViewModel: CartViewModel,
    viewModel: VendorsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nearby Shops", fontWeight = FontWeight.Black, color = LuxeTextPrimary, fontFamily = FontFamily.Serif) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LuxeTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxeBackground)
            )
        },
        containerColor = LuxeBackground
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Luxe Search Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .height(54.dp)
                    .clickable { navController.navigate(Screen.Search) }
                    .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = LuxeTextSecondary.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(16.dp),
                color = LuxeCard,
                border = BorderStroke(1.dp, LuxeBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Search, null, tint = LuxeAccentSage)
                    Spacer(Modifier.width(12.dp))
                    Text("Search for shops or services...", color = LuxeTextSecondary, fontSize = 14.sp)
                }
            }

            when (val state = uiState) {
                is VendorsUiState.Loading -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        items(5) { LuxeSkeletonWideCard() }
                    }
                }
                is VendorsUiState.Error -> {
                    Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.ErrorOutline, null, tint = ErrorRed, modifier = Modifier.size(64.dp))
                            Spacer(Modifier.height(16.dp))
                            Text("Oops! Connection failed", color = LuxeTextPrimary, fontWeight = FontWeight.Bold)
                            Text(state.message, color = LuxeTextSecondary, textAlign = TextAlign.Center)
                            TextButton(onClick = { viewModel.fetchAllVendors() }) {
                                Text("Retry", color = LuxeAccentSage)
                            }
                        }
                    }
                }
                is VendorsUiState.Success -> {
                    val vendors = state.vendors
                    if (vendors.isEmpty()) {
                        Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Storefront, null, tint = LuxeHighlightChampagne, modifier = Modifier.size(80.dp))
                                Spacer(Modifier.height(16.dp))
                                Text("No shops found near you.", color = LuxeTextSecondary, textAlign = TextAlign.Center)
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(20.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            item {
                                   Text(
                                    "EXPLORE ALL VENDORS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = LuxeTextSecondary,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 2.sp,
                                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                                )
                            }
                            
                            items(vendors, key = { it.id }) { vendor ->
                                LuxeWideVendorCard(vendor) {
                                    navController.navigate(Screen.MerchantShop(vendor.id))
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
fun LuxeSkeletonWideCard() {
    Surface(
        modifier = Modifier.fillMaxWidth().height(240.dp),
        shape = RoundedCornerShape(24.dp),
        color = LuxeHighlightChampagne.copy(alpha = 0.3f),
        border = BorderStroke(1.dp, LuxeBorder)
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = LuxeAccentSage, strokeWidth = 2.dp)
        }
    }
}

@Composable
fun LuxeWideVendorCard(vendor: Vendor, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = LuxeCard),
        border = BorderStroke(1.dp, LuxeBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                        modifier = Modifier.fillMaxWidth().height(160.dp).background(LuxeHighlightChampagne.copy(alpha = 0.3f)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(Modifier.fillMaxWidth().height(160.dp).background(LuxeHighlightChampagne.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Storefront, null, tint = LuxeAccentSage, modifier = Modifier.size(48.dp))
                    }
                }
                
                if (vendor.isAvailable == false) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color.Black.copy(alpha = 0.5f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("CLOSED", color = Color.White, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
            
            Column(Modifier.padding(20.dp)) {
                Text(
                    text = vendor.displayName, 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 18.sp, 
                    color = LuxeTextPrimary
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = LuxeGold, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${vendor.displayRating} • ${vendor.deliveryTime ?: "20 min"} • ${vendor.distance ?: "1.0 km"}",
                        color = LuxeTextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun GroceryShopCard(shop: GroceryShop, onClick: () -> Unit) {
    LuxuryCard(onClick = onClick) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(LuxuryCard),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Storefront,
                    null,
                    tint = LuxuryGold.copy(alpha = 0.2f),
                    modifier = Modifier.size(64.dp)
                )
                if (shop.offers.isNotEmpty()) {
                    Surface(
                        modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
                        color = LuxuryGold,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            shop.offers,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            color = LuxuryBackground,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            shop.name,
                            color = LuxuryTextPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                        if (shop.id == "our_shop") {
                            Text(
                                "Official SAU Store",
                                color = LuxuryGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                    Surface(
                        color = LuxuryGold.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, null, tint = LuxuryGold, modifier = Modifier.size(12.dp))
                            Text(shop.rating.toString(), color = LuxuryGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timer, null, tint = LuxuryGold, modifier = Modifier.size(14.dp))
                    Text(" ${shop.deliveryTime}", color = LuxuryTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Text(" • ", color = LuxuryBorder)
                    Icon(Icons.Default.LocationOn, null, tint = LuxuryTextSecondary, modifier = Modifier.size(14.dp))
                    Text(" ${shop.distance}", color = LuxuryTextSecondary, fontSize = 12.sp)
                }
                
                if (!shop.isOpen) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Currently Closed • Opens at 9:00 AM",
                        color = ErrorRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeEssentialsCategoryScreen(
    navController: NavController, 
    categoryId: String, 
    cartViewModel: CartViewModel,
    shopId: String? = null
) {
    val category = HomeEssentialsData.categories.find { it.id == categoryId }
    val subcategories = HomeEssentialsData.subcategories.filter { it.categoryId == categoryId }
    val products = HomeEssentialsData.products.filter { prod -> 
        subcategories.any { it.id == prod.subcategoryId } && (shopId == null || prod.shopId == shopId)
    }
    
    var selectedSubcategory by remember { mutableStateOf("All") }
    
    val filteredProducts = if (selectedSubcategory == "All") {
        products
    } else {
        products.filter { it.subcategoryId == subcategories.find { sub -> sub.name == selectedSubcategory }?.id }
    }

    val cartItems by cartViewModel.dbCartItems.collectAsState()
    val totalCount = cartItems.sumOf { it.quantity }

    LuxuryScaffold(
        title = category?.name ?: "Products",
        onBackClick = { navController.popBackStack() },
        actions = {
            IconButton(onClick = { navController.navigate(Screen.Cart) }) {
                BadgedBox(badge = {
                    if (totalCount > 0) {
                        Badge(containerColor = LuxuryGold, contentColor = LuxuryBackground) {
                            Text(totalCount.toString(), fontWeight = FontWeight.Bold)
                        }
                    }
                }) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = LuxuryTextPrimary)
                }
            }
        },
        bottomBar = {
            if (totalCount > 0) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 16.dp,
                    color = LuxuryCard,
                    border = BorderStroke(1.dp, LuxuryBorder),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp).navigationBarsPadding()) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                val totalPrice = cartItems.sumOf { it.totalPrice }
                                Text("$totalCount Items added", fontSize = 13.sp, color = LuxuryTextSecondary)
                                Text("₹$totalPrice", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = LuxuryGold)
                            }
                            Button(
                                onClick = { navController.navigate(Screen.Cart) },
                                modifier = Modifier.height(56.dp).widthIn(min = 160.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = LuxuryGold)
                            ) {
                                Text("View Cart", color = LuxuryBackground, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Subcategory Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    LuxuryChip(
                        selected = selectedSubcategory == "All",
                        label = "All",
                        onClick = { selectedSubcategory = "All" }
                    )
                }
                items(subcategories) { sub ->
                    LuxuryChip(
                        selected = selectedSubcategory == sub.name,
                        label = sub.name,
                        onClick = { selectedSubcategory = sub.name }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredProducts) { product ->
                    GroceryLuxuryProductCard(
                        product = product,
                        quantity = cartViewModel.getHomeItemQuantity(product.id),
                        onIncrease = { cartViewModel.addHomeProduct(product) },
                        onDecrease = { cartViewModel.removeHomeProduct(product.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun GroceryLuxuryProductCard(
    product: HomeProduct,
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    LuxuryCard {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(90.dp),
                shape = RoundedCornerShape(12.dp),
                color = LuxuryBackground
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.ShoppingBasket, null, tint = LuxuryGold.copy(alpha = 0.2f), modifier = Modifier.size(32.dp))
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LuxuryTextPrimary)
                Text(product.unit, color = LuxuryTextSecondary, fontSize = 13.sp)
                Spacer(Modifier.height(4.dp))
                Text("₹${product.price}", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = LuxuryGold)
            }
            
            if (quantity == 0) {
                Button(
                    onClick = onIncrease,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LuxuryBackground),
                    border = BorderStroke(1.dp, LuxuryGold),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("ADD", color = LuxuryGold, fontWeight = FontWeight.Bold)
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(LuxuryGold, RoundedCornerShape(8.dp))
                        .padding(horizontal = 4.dp)
                ) {
                    IconButton(onClick = onDecrease, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Remove, null, tint = LuxuryBackground, modifier = Modifier.size(16.dp))
                    }
                    Text(
                        quantity.toString(), 
                        color = LuxuryBackground, 
                        fontWeight = FontWeight.Bold, 
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    IconButton(onClick = onIncrease, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Add, null, tint = LuxuryBackground, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeEssentialsCheckoutScreen(navController: NavController, cartViewModel: CartViewModel) {
    val dbCartItems by cartViewModel.dbCartItems.collectAsState()
    val subtotal = dbCartItems.sumOf { it.totalPrice }
    val deliveryFee = 20
    val platformFee = 5
    val grandTotal = subtotal + deliveryFee + platformFee

    LuxuryScaffold(
        title = "Checkout",
        onBackClick = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Address Card
            LuxuryCheckoutSection("Delivery Address") {
                Column {
                    Text("Home", color = LuxuryTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("123, Main Road, Hyderabad, Telangana", fontSize = 14.sp, color = LuxuryTextSecondary)
                }
            }

            // Delivery Time Card
            LuxuryCheckoutSection("Delivery Time") {
                Text("Tomorrow, 10:00 AM - 12:00 PM", color = LuxuryTextPrimary, fontWeight = FontWeight.Medium)
            }

            // Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LuxuryCard),
                border = BorderStroke(1.dp, LuxuryBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Payment Summary", color = LuxuryTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(16.dp))
                    LuxuryBillRow(label = "Subtotal", value = "₹$subtotal")
                    LuxuryBillRow(label = "Delivery Fee", value = "₹$deliveryFee")
                    LuxuryBillRow(label = "Platform Fee", value = "₹$platformFee")
                    HorizontalDivider(Modifier.padding(vertical = 12.dp), color = LuxuryBorder)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Amount", color = LuxuryTextPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        Text("₹$grandTotal", color = LuxuryGold, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))

            LuxuryButton(
                text = "PAY ₹$grandTotal",
                onClick = { cartViewModel.placeOrder("Home Address", "UPI") }
            )
        }
    }
}

@Composable
fun LuxuryCheckoutSection(title: String, content: @Composable () -> Unit) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(title, color = LuxuryTextSecondary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("Change", color = LuxuryGold, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.clickable { })
        }
        Spacer(Modifier.height(12.dp))
        LuxuryCard {
            Box(Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun LuxuryBillRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = LuxuryTextSecondary, fontSize = 14.sp)
        Text(value, color = LuxuryTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun HomeEssentialsSuccessScreen(navController: NavController) {
    Surface(modifier = Modifier.fillMaxSize(), color = LuxuryBackground) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(100.dp).background(LuxuryGold.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, null, modifier = Modifier.size(60.dp), tint = LuxuryGold)
            }
            Spacer(Modifier.height(32.dp))
            Text("Order Confirmed!", color = LuxuryTextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Serif)
            Text("Your groceries will be delivered shortly.", color = LuxuryTextSecondary, textAlign = TextAlign.Center)
            Spacer(Modifier.height(60.dp))
            LuxuryButton(
                text = "CONTINUE SHOPPING",
                onClick = { navController.navigate(Screen.Home) { popUpTo(0) } }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeEssentialsSheetContent(navController: NavController, onDismiss: () -> Unit) {
    val categories = HomeEssentialsData.categories

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LuxuryBackground)
            .padding(bottom = 32.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Groceries",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = LuxuryGold,
                fontFamily = FontFamily.Serif
            )
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = LuxuryTextSecondary)
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { category ->
                LuxuryCard(
                    onClick = {
                        onDismiss()
                        navController.navigate(Screen.HomeEssentialsCategory(category.id))
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(category.icon, fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = category.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = LuxuryTextPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
