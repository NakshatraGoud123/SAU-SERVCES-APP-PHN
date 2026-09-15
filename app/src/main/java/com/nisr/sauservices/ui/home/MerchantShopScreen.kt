package com.nisr.sauservices.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.nisr.sauservices.data.model.Product
import com.nisr.sauservices.data.model.Vendor
import com.nisr.sauservices.data.repository.SupabaseRepository
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.viewmodel.CartViewModel
import com.nisr.sauservices.ui.components.LuxuryButton
import kotlinx.coroutines.launch

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
fun MerchantShopScreen(
    navController: NavController,
    vendorId: String,
    cartViewModel: CartViewModel
) {
    val repository = remember { SupabaseRepository() }
    val scope = rememberCoroutineScope()
    
    var vendor by remember { mutableStateOf<Vendor?>(null) }
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val cartItems by cartViewModel.dbCartItems.collectAsState()
    val totalAmount = cartItems.sumOf { it.totalPrice }

    LaunchedEffect(vendorId) {
        isLoading = true
        try {
            val vendorResult = repository.getVendorDetails(vendorId)
            val productsResult = repository.getProductsByVendor(vendorId)
            
            vendor = vendorResult.getOrNull()
            products = productsResult.getOrDefault(emptyList())
        } catch (e: Exception) {
            android.util.Log.e("MERCHANT_SHOP", "Error loading shop: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        containerColor = LuxeBackground,
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                LuxeCartBar(
                    itemCount = cartItems.sumOf { it.quantity },
                    totalAmount = totalAmount,
                    onViewCart = { navController.navigate(Screen.Cart) }
                )
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LuxeAccentSage)
            }
        } else if (vendor == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Vendor not found", color = LuxeTextPrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(bottom = if (cartItems.isNotEmpty()) 100.dp else 0.dp)
            ) {
                // cinematic Header
                item {
                    MerchantHeader(vendor!!, onBack = { navController.popBackStack() })
                }

                item {
                    Column(Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {
                        Text(
                            text = "AVAILABLE PRODUCTS",
                            style = MaterialTheme.typography.labelSmall,
                            color = LuxeTextSecondary,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                    }
                }

                // Product List
                if (products.isNotEmpty()) {
                    items(products) { product ->
                        // Luxe Animation: Slide & Fade
                        var visible by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) { visible = true }
                        
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(androidx.compose.animation.core.tween(800)) + slideInVertically(animationSpec = androidx.compose.animation.core.tween(600), initialOffsetY = { 50 })
                        ) {
                            ProductItemRow(
                                product = product,
                                cartQuantity = cartViewModel.getHomeItemQuantity(product.id),
                                onAdd = { cartViewModel.addHomeProduct(com.nisr.sauservices.data.model.HomeProduct(
                                    id = product.id,
                                    subcategoryId = "",
                                    name = product.name,
                                    price = product.price.toInt(),
                                    unit = product.unit,
                                    category = product.categoryId ?: ""
                                )) },
                                onRemove = { cartViewModel.removeHomeProduct(product.id) }
                            )
                        }
                        HorizontalDivider(color = LuxeBorder, thickness = 1.dp, modifier = Modifier.padding(horizontal = 20.dp))
                    }
                } else {
                    item {
                        Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No products available yet.", color = LuxeTextSecondary, fontSize = 14.sp)
                        }
                    }
                }
                
                item {
                    Spacer(Modifier.height(48.dp))
                }
            }
        }
    }
}

@Composable
private fun MerchantHeader(vendor: Vendor, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        android.util.Log.d("PHOTO_DEBUG", "Loading merchant header: ${vendor.imageUrl}")
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(vendor.imageUrl)
                .crossfade(true)
                .placeholder(com.nisr.sauservices.R.drawable.sau_logo)
                .error(com.nisr.sauservices.R.drawable.sau_logo)
                .build(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Soft Daylight Gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.2f), Color.Transparent, LuxeBackground),
                        startY = 0f
                    )
                )
        )

        // Back Button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(16.dp)
                .statusBarsPadding()
                .background(Color.White.copy(alpha = 0.8f), CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = LuxeTextPrimary, modifier = Modifier.size(20.dp))
        }

        // Vendor Info
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
        ) {
            Surface(
                color = LuxeAccentSage,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    vendor.displayCategory.uppercase(),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = vendor.displayName,
                style = MaterialTheme.typography.headlineMedium,
                color = LuxeTextPrimary,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Serif
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, null, tint = LuxeGold, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "${vendor.displayRating} • ${vendor.deliveryTime ?: "20 min"} • ${vendor.distance ?: "1.0 km"}",
                    color = LuxeTextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ProductItemRow(
    product: Product,
    cartQuantity: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = product.name,
                color = LuxeTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = product.unit,
                color = LuxeTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "₹${product.price}",
                color = LuxeTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
        }

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(LuxeHighlightChampagne.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(product.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            // Add/Qty Controller
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 14.dp)
                    .shadow(4.dp, RoundedCornerShape(10.dp)),
                shape = RoundedCornerShape(10.dp),
                color = LuxeCard,
                border = BorderStroke(1.dp, LuxeBorder)
            ) {
                if (cartQuantity == 0) {
                    Text(
                        "ADD",
                        color = LuxeAccentSage,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .clickable(onClick = onAdd)
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp)
                    ) {
                        IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Remove, null, tint = LuxeAccentSage, modifier = Modifier.size(18.dp))
                        }
                        Text(
                            text = cartQuantity.toString(),
                            color = LuxeTextPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                        IconButton(onClick = onAdd, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Add, null, tint = LuxeAccentSage, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LuxeCartBar(
    itemCount: Int,
    totalAmount: Double,
    onViewCart: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        color = LuxeAccentSage,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "$itemCount ITEMS",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "₹$totalAmount",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }
            
            TextButton(
                onClick = onViewCart,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("VIEW CART", fontWeight = FontWeight.Black, fontSize = 13.sp)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

private val BoxShadow = 8.dp
