package com.nisr.sauservices.ui.essentials

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.nisr.sauservices.data.model.NewModulesData
import com.nisr.sauservices.data.model.SupplyCategory
import com.nisr.sauservices.data.model.SupplySubcategory
import com.nisr.sauservices.data.model.toSafeUuid
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.components.*
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EssentialSuppliesScreen(navController: NavController, viewModel: CartViewModel) {
    val context = LocalContext.current
    val categories = NewModulesData.essentialSupplies
    var selectedCategory by remember { mutableStateOf<SupplyCategory?>(null) }
    val cartItems by viewModel.dbCartItems.collectAsState()
    
    val animateState = remember { MutableTransitionState(false) }.apply { targetState = true }

    LuxuryScaffold(
        title = "Essential Supplies",
        onBackClick = { navController.popBackStack() },
        actions = {
            IconButton(onClick = { navController.navigate(Screen.Cart) }) {
                BadgedBox(badge = {
                    if (cartItems.isNotEmpty()) {
                        Badge(containerColor = LuxuryGold, contentColor = LuxuryBackground) {
                            val count = cartItems.sumOf { it.quantity }
                            Text(count.toString(), fontWeight = FontWeight.Bold)
                        }
                    }
                }) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = LuxuryTextPrimary)
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(LuxuryBackground)) {
            AnimatedVisibility(
                visibleState = animateState,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 40 })
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(categories) { category ->
                        LuxuryCategoryCardSmall(category) {
                            selectedCategory = category
                        }
                    }
                }
            }

            if (cartItems.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(20.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = LuxuryCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryBorder),
                    shadowElevation = 16.dp
                ) {
                    val totalCount = cartItems.sumOf { it.quantity }
                    LuxuryButton(
                        text = "VIEW CART ($totalCount)",
                        onClick = { navController.navigate(Screen.Cart) },
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        if (selectedCategory != null) {
            SubcategoryLuxuryPopup(
                category = selectedCategory!!,
                cartViewModel = viewModel,
                onDismiss = { selectedCategory = null },
                onAddToCart = { sub ->
                    val priceStr = sub.priceRange.replace("₹", "").split("–").first().trim()
                    val price = priceStr.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 0.0
                    viewModel.addItemToCart(
                        name = sub.name,
                        price = price,
                        category = selectedCategory?.name ?: "",
                        subcategory = sub.name,
                        unit = sub.itemType,
                        productId = sub.id
                    ) { result ->
                        if (result.isSuccess) {
                            Toast.makeText(context, "${sub.name} added to cart", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Failed to add: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun LuxuryCategoryCardSmall(category: SupplyCategory, onClick: () -> Unit) {
    LuxuryCard(onClick = onClick) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(20.dp)) {
            Text(
                text = category.name,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = LuxuryTextPrimary,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun SubcategoryLuxuryPopup(
    category: SupplyCategory,
    cartViewModel: CartViewModel,
    onDismiss: () -> Unit,
    onAddToCart: (SupplySubcategory) -> Unit
) {
    val cartItems by cartViewModel.dbCartItems.collectAsState()
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val animateState = remember { MutableTransitionState(false) }.apply { targetState = true }
        
        AnimatedVisibility(
            visibleState = animateState,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .clickable { onDismiss() },
                contentAlignment = Alignment.BottomCenter
            ) {
                Surface(
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    color = LuxuryBackground,
                    border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clickable(enabled = false) {}
                ) {
                    Column(modifier = Modifier.padding(24.dp).navigationBarsPadding()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = LuxuryGold,
                                    fontFamily = FontFamily.Serif
                                )
                                Text("Premium Selection", style = MaterialTheme.typography.bodySmall, color = LuxuryTextSecondary)
                            }
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.background(LuxuryCard, CircleShape).size(36.dp)
                            ) {
                                Icon(Icons.Default.Close, null, modifier = Modifier.size(18.dp), tint = LuxuryTextSecondary)
                            }
                        }
                        
                        Spacer(Modifier.height(32.dp))
                        
                        LazyColumn(modifier = Modifier.heightIn(max = 500.dp)) {
                            items(category.subcategories) { sub ->
                                val inCartCount = cartItems.find { it.productId == sub.id.toSafeUuid() }?.quantity ?: 0
                                LuxurySubcategoryItem(sub, inCartCount) {
                                    onAddToCart(sub)
                                }
                                if (category.subcategories.last() != sub) {
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = LuxuryBorder)
                                }
                            }
                        }
                        
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun LuxurySubcategoryItem(sub: SupplySubcategory, count: Int, onAdd: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
            Text(text = sub.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LuxuryTextPrimary)
            Text(text = sub.priceRange, color = LuxuryGold, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
        }
        
        if (count == 0) {
            Button(
                onClick = onAdd,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LuxuryGold.copy(alpha = 0.1f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryGold),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                modifier = Modifier.height(40.dp)
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp), tint = LuxuryGold)
                Spacer(Modifier.width(8.dp))
                Text("ADD", color = LuxuryGold, fontSize = 13.sp, fontWeight = FontWeight.Black)
            }
        } else {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = LuxuryGold,
                modifier = Modifier.height(40.dp).clickable { onAdd() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("$count IN CART", color = LuxuryBackground, fontWeight = FontWeight.Black, fontSize = 12.sp)
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Default.Add, null, tint = LuxuryBackground, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}
