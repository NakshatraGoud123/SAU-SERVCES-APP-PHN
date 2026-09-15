package com.nisr.sauservices.ui.home

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.graphics.graphicsLayer
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
import com.nisr.sauservices.R
import com.nisr.sauservices.data.model.HomeProduct
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
    
    var vendor by remember { mutableStateOf<Vendor?>(null) }
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val cartItems by cartViewModel.dbCartItems.collectAsState()
    val totalAmount = cartItems.sumOf { it.totalPrice }
    
    val scrollState = rememberLazyListState()
    val headerHeight = 280.dp
    
    // Parallax Logic
    val headerAlpha by remember {
        derivedStateOf {
            if (scrollState.firstVisibleItemIndex > 0) 0f
            else (1f - (scrollState.firstVisibleItemScrollOffset.toFloat() / 500f)).coerceIn(0f, 1f)
        }
    }
    
    val translationY by remember {
        derivedStateOf {
            if (scrollState.firstVisibleItemIndex > 0) 0f
            else -scrollState.firstVisibleItemScrollOffset.toFloat() * 0.5f
        }
    }

    LaunchedEffect(vendorId) {
        isLoading = true
        try {
            val vendorResult = repository.getVendorDetails(vendorId)
            val productsResult = repository.getProductsByVendor(vendorId)
            
            vendor = vendorResult.getOrNull()
            products = productsResult.getOrDefault(emptyList())
        } catch (e: Exception) {
            Log.e("MERCHANT_SHOP", "Error: ${e.message}")
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
        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = LuxeAccentSage)
                }
            } else if (vendor == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Vendor not found", color = LuxeTextPrimary)
                }
            } else {
                // Parallax Layer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(headerHeight)
                        .graphicsLayer {
                            alpha = headerAlpha
                            this.translationY = translationY
                        }
                ) {
                    MerchantHeaderContent(vendor!!)
                }

                LazyColumn(
                    state = scrollState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Spacer(Modifier.height(headerHeight))
                    }

                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = LuxeBackground,
                            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                        ) {
                            Column(Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
                                Text(
                                    text = "AVAILABLE SELECTIONS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = LuxeAccentSage,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 2.sp
                                )
                            }
                        }
                    }

                    if (products.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                                Text("No items available at the moment.", color = LuxeTextSecondary)
                            }
                        }
                    } else {
                        items(products) { product ->
                            ProductItemRow(
                                product = product,
                                cartQuantity = cartViewModel.getHomeItemQuantity(product.id),
                                onAdd = { cartViewModel.addHomeProduct(HomeProduct(
                                    id = product.id,
                                    subcategoryId = "",
                                    name = product.name,
                                    price = product.price.toInt(),
                                    unit = product.unit,
                                    category = product.categoryId ?: "",
                                    imageUrl = product.imageUrl
                                )) },
                                onRemove = { cartViewModel.removeHomeProduct(product.id) }
                            )
                            HorizontalDivider(color = LuxeBorder, modifier = Modifier.padding(horizontal = 24.dp))
                        }
                    }
                    
                    item { Spacer(Modifier.height(120.dp)) }
                }
            }

            // Floating Header (Fixed Back Button)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.8f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = LuxeTextPrimary, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
private fun MerchantHeaderContent(vendor: Vendor) {
    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(vendor.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().background(LuxeHighlightChampagne.copy(alpha = 0.3f)),
            contentScale = ContentScale.Crop
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                        startY = 0f
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
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
            Spacer(Modifier.height(12.dp))
            Text(
                text = vendor.displayName,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Serif
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, null, tint = LuxeGold, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "${vendor.displayRating} • ${vendor.deliveryTime ?: "25 min"}",
                    color = Color.White.copy(alpha = 0.9f),
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
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(product.name, color = LuxeTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(product.unit, color = LuxeTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(12.dp))
            Text("₹${product.price}", color = LuxeTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Black)
        }

        Box(
            modifier = Modifier.size(110.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.size(100.dp),
                shape = RoundedCornerShape(16.dp),
                color = LuxeHighlightChampagne.copy(alpha = 0.3f)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(product.imageUrl).crossfade(true).build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            
            // Add/Qty Controller
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 12.dp)
                    .shadow(6.dp, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
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
                        Text(cartQuantity.toString(), color = LuxeTextPrimary, fontWeight = FontWeight.Black, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 10.dp))
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
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        shape = RoundedCornerShape(24.dp),
        color = LuxeAccentSage,
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("$itemCount ITEMS", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                Text("₹$totalAmount", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
            }
            TextButton(onClick = onViewCart) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("VIEW CART", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
