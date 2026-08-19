package com.nisr.sauservices.ui.essentials

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.nisr.sauservices.ui.theme.PinkPrimary
import com.nisr.sauservices.ui.theme.LightPink
import com.nisr.sauservices.ui.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EssentialSuppliesScreen(navController: NavController, viewModel: CartViewModel) {
    val context = LocalContext.current
    val categories = NewModulesData.essentialSupplies
    var selectedCategory by remember { mutableStateOf<SupplyCategory?>(null) }
    val cartItems by viewModel.dbCartItems.collectAsState()
    
    val animateState = remember { MutableTransitionState(false) }.apply { targetState = true }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Essential Supplies", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Cart.route) }) {
                        BadgedBox(badge = {
                            if (cartItems.isNotEmpty()) {
                                Badge(containerColor = PinkPrimary) {
                                    val count = cartItems.sumOf { it.quantity }
                                    Text(count.toString(), color = Color.White)
                                }
                            }
                        }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF9FAFB))) {
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
                        CategoryCardSmall(category) {
                            selectedCategory = category
                        }
                    }
                }
            }

            if (cartItems.isNotEmpty()) {
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val scale by animateFloatAsState(if (isPressed) 0.96f else 1f)

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Button(
                        onClick = { navController.navigate(Screen.Cart.route) },
                        modifier = Modifier.fillMaxWidth().height(56.dp).scale(scale),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                        interactionSource = interactionSource
                    ) {
                        val totalCount = cartItems.sumOf { it.quantity }
                        Text("View Cart ($totalCount Items)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }

        if (selectedCategory != null) {
            SubcategoryPopupSmall(
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
fun CategoryCardSmall(category: SupplyCategory, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
        label = "cardScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = category.name,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.Black,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun SubcategoryPopupSmall(
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
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { onDismiss() },
                contentAlignment = Alignment.BottomCenter
            ) {
                Surface(
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clickable(enabled = false) {} // Prevent click-through
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
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.Black
                                )
                                Text("Choose from available options", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.background(Color(0xFFF3F4F6), CircleShape).size(36.dp)
                            ) {
                                Icon(Icons.Default.Close, null, modifier = Modifier.size(18.dp))
                            }
                        }
                        
                        Spacer(Modifier.height(24.dp))
                        
                        LazyColumn(modifier = Modifier.heightIn(max = 500.dp)) {
                            items(category.subcategories) { sub ->
                                val inCartCount = cartItems.find { it.productId == sub.id.toSafeUuid() }?.quantity ?: 0
                                SubcategoryItemProfessional(sub, inCartCount) {
                                    onAddToCart(sub)
                                }
                                if (category.subcategories.last() != sub) {
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF3F4F6))
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
fun SubcategoryItemProfessional(sub: SupplySubcategory, count: Int, onAdd: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
            Text(text = sub.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
            Text(text = sub.priceRange, color = PinkPrimary, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
        }
        
        if (count == 0) {
            Button(
                onClick = onAdd,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                modifier = Modifier.height(40.dp)
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Add", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = PinkPrimary.copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(1.dp, PinkPrimary),
                modifier = Modifier.height(40.dp).clickable { onAdd() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("$count In Cart", color = PinkPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Default.Add, null, tint = PinkPrimary, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}
