package com.nisr.sauservices.ui.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnifiedCartScreen(
    navController: NavController,
    residentialViewModel: ResidentialViewModel,
    businessViewModel: BusinessViewModel,
    lifestyleViewModel: LifestyleViewModel,
    techViewModel: TechServicesViewModel,
    mensGroomingViewModel: MensGroomingViewModel,
    womensBeautyViewModel: WomensBeautyViewModel,
    healthcareViewModel: HealthcareViewModel,
    foodCartViewModel: FoodCartViewModel,
    homeCartViewModel: CartViewModel,
    educationViewModel: EducationCartViewModel
) {
    val resItems = residentialViewModel.cartItems
    val businessItems = businessViewModel.cartItems
    val lifestyleItems = lifestyleViewModel.cartItems
    val techItems = techViewModel.cartItems
    val mensItems = mensGroomingViewModel.cartItems
    val womensItems = womensBeautyViewModel.cartItems
    val healthItems = healthcareViewModel.cartItems
    val foodItems = foodCartViewModel.cartItems
    val eduItems = educationViewModel.cartItems
    val dbCartItems by homeCartViewModel.dbCartItems.collectAsState()

    val isEmpty = resItems.isEmpty() && businessItems.isEmpty() && 
                  lifestyleItems.isEmpty() && techItems.isEmpty() &&
                  mensItems.isEmpty() && womensItems.isEmpty() && healthItems.isEmpty() &&
                  foodItems.isEmpty() && dbCartItems.isEmpty() && eduItems.isEmpty()
    
    val subtotal = residentialViewModel.calculateTotal() + 
                   businessViewModel.getTotalPrice() +
                   lifestyleViewModel.getTotalPrice() +
                   techViewModel.getTotalPrice() +
                   mensGroomingViewModel.getTotalPrice() +
                   womensBeautyViewModel.calculateTotal() +
                   healthcareViewModel.calculateTotal() +
                   foodCartViewModel.getTotal().toDouble() +
                   educationViewModel.getTotal().toDouble() +
                   dbCartItems.sumOf { it.totalPrice }
    
    val deliveryFee = if (isEmpty) 0.0 else 30.0
    val grandTotal = subtotal + deliveryFee

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("My Cart", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                        if (!isEmpty) {
                            val totalItemsCount = resItems.size + businessItems.size + lifestyleItems.size + 
                                            techItems.size + mensItems.size + womensItems.size + 
                                            healthItems.size + foodItems.size + dbCartItems.size + eduItems.size
                            Text("$totalItemsCount Items in your basket", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!isEmpty) {
                        TextButton(onClick = {
                            residentialViewModel.clearCart()
                            businessViewModel.clearCart()
                            lifestyleViewModel.clearCart()
                            techViewModel.clearCart()
                            mensGroomingViewModel.clearCart()
                            womensBeautyViewModel.clearCart()
                            healthcareViewModel.clearCart()
                            foodCartViewModel.clearCart()
                            educationViewModel.clearCart()
                            homeCartViewModel.clearHomeCart()
                        }) {
                            Text("Clear", color = ErrorRed)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            if (!isEmpty) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 16.dp,
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp).navigationBarsPadding()) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("Total Amount", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                                Text("₹$grandTotal", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = PinkPrimary)
                            }
                            Button(
                                onClick = { 
                                    if (dbCartItems.any { it.unit != "Booking" && it.category != "Residential" }) {
                                        navController.navigate(Screen.HomeEssentialsCheckout.route)
                                    } else {
                                        navController.navigate(Screen.ResidentialBookingDetails.route)
                                    }
                                },
                                modifier = Modifier.height(56.dp).widthIn(min = 180.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                            ) {
                                Text("Checkout", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(Modifier.width(8.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFFF9FAFB)
    ) { padding ->
        if (isEmpty) {
            EmptyCartContent(padding, navController)
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Service Bookings Group
                val bookingItems = dbCartItems.filter { it.unit == "Booking" && it.category != "Residential" }
                if (bookingItems.isNotEmpty()) {
                    item { CartCategoryHeader("Service Bookings", Icons.Default.Event) }
                    items(bookingItems) { item ->
                        CartItemRow(
                            name = "${item.itemName} (${item.date} ${item.time})",
                            price = item.price.toInt(),
                            quantity = item.quantity,
                            category = "Service",
                            onIncrease = { homeCartViewModel.updateQuantity(item.itemId, item.quantity + 1) },
                            onDecrease = { homeCartViewModel.updateQuantity(item.itemId, item.quantity - 1) },
                            onDelete = { homeCartViewModel.updateQuantity(item.itemId, 0) }
                        )
                    }
                }

                // Residential Services Group
                val dbResItems = dbCartItems.filter { it.category == "Residential" }
                if (resItems.isNotEmpty() || dbResItems.isNotEmpty()) {
                    item { CartCategoryHeader("Residential Services", Icons.Default.HomeRepairService) }
                    items(resItems) { item ->
                        CartItemRow(
                            name = item.service.name,
                            price = item.service.price.toInt(),
                            quantity = item.quantity,
                            category = "Residential",
                            onIncrease = { residentialViewModel.updateQty(item.service.id, true) },
                            onDecrease = { residentialViewModel.updateQty(item.service.id, false) },
                            onDelete = { residentialViewModel.updateQty(item.service.id, false) }
                        )
                    }
                    items(dbResItems) { item ->
                        CartItemRow(
                            name = item.itemName,
                            price = item.price.toInt(),
                            quantity = item.quantity,
                            category = "Residential",
                            onIncrease = { homeCartViewModel.updateQuantity(item.itemId, item.quantity + 1) },
                            onDecrease = { homeCartViewModel.updateQuantity(item.itemId, item.quantity - 1) },
                            onDelete = { homeCartViewModel.updateQuantity(item.itemId, 0) }
                        )
                    }
                }

                // Essential Supplies Group
                val supplyItems = dbCartItems.filter { it.unit != "Booking" && it.category != "Residential" }
                if (supplyItems.isNotEmpty()) {
                    item { CartCategoryHeader("Essential Supplies", Icons.Default.Inventory2) }
                    items(supplyItems) { item ->
                        CartItemRow(
                            name = item.itemName,
                            price = item.price.toInt(),
                            quantity = item.quantity,
                            category = "Essential",
                            onIncrease = { homeCartViewModel.updateQuantity(item.itemId, item.quantity + 1) },
                            onDecrease = { homeCartViewModel.updateQuantity(item.itemId, item.quantity - 1) },
                            onDelete = { homeCartViewModel.updateQuantity(item.itemId, 0) }
                        )
                    }
                }

                // Food & Beverages Group
                if (foodItems.isNotEmpty()) {
                    item { CartCategoryHeader("Food & Beverages", Icons.Default.Fastfood) }
                    items(foodItems) { item ->
                        CartItemRow(
                            name = item.name,
                            price = item.price,
                            quantity = item.quantity,
                            category = "Food",
                            onIncrease = { foodCartViewModel.increaseQty(item.id) },
                            onDecrease = { foodCartViewModel.decreaseQty(item.id) },
                            onDelete = { foodCartViewModel.removeItem(item.id) }
                        )
                    }
                }

                // Education Services Group
                if (eduItems.isNotEmpty()) {
                    item { CartCategoryHeader("Education", Icons.Default.School) }
                    items(eduItems) { item ->
                        CartItemRow(
                            name = item.name,
                            price = item.price,
                            quantity = item.quantity,
                            category = "Education",
                            onIncrease = { educationViewModel.increaseQty(item.id) },
                            onDecrease = { educationViewModel.decreaseQty(item.id) },
                            onDelete = { educationViewModel.removeItem(item.id) }
                        )
                    }
                }

                // Professional Services Group (Business, Lifestyle, Tech, Grooming)
                if (businessItems.isNotEmpty() || lifestyleItems.isNotEmpty() || techItems.isNotEmpty() || mensItems.isNotEmpty() || womensItems.isNotEmpty() || healthItems.isNotEmpty()) {
                    item { CartCategoryHeader("Other Services", Icons.Default.BusinessCenter) }
                    
                    items(businessItems) { item ->
                        CartItemRow(item.name, item.price.toInt(), item.quantity, "Business", { businessViewModel.increaseQty(item.id) }, { businessViewModel.decreaseQty(item.id) }, { businessViewModel.removeFromCart(item.id) })
                    }
                    items(lifestyleItems) { item ->
                        CartItemRow(item.name, item.price.toInt(), item.quantity, "Lifestyle", { lifestyleViewModel.increaseQty(item.id) }, { lifestyleViewModel.decreaseQty(item.id) }, { lifestyleViewModel.removeFromCart(item.id) })
                    }
                    items(techItems) { item ->
                        CartItemRow(item.name, item.price.toInt(), item.quantity, "Tech", { techViewModel.increaseQty(item.id) }, { techViewModel.decreaseQty(item.id) }, { techViewModel.removeFromCart(item.id) })
                    }
                    items(mensItems) { item ->
                        CartItemRow(item.name, item.price.toInt(), item.quantity, "Grooming", { mensGroomingViewModel.increaseQty(item.id) }, { mensGroomingViewModel.decreaseQty(item.id) }, { mensGroomingViewModel.removeFromCart(item.id) })
                    }
                    items(womensItems) { item ->
                        CartItemRow(item.name, item.price.toInt(), item.quantity, "Beauty", { womensBeautyViewModel.updateQty(item.id, true) }, { womensBeautyViewModel.updateQty(item.id, false) }, { womensBeautyViewModel.removeFromCart(item.id) })
                    }
                    items(healthItems) { item ->
                        CartItemRow(item.name, item.price.toInt(), item.quantity, "Healthcare", { healthcareViewModel.updateQty(item.id, true) }, { healthcareViewModel.updateQty(item.id, false) }, { healthcareViewModel.removeFromCart(item.id) })
                    }
                }

                // Summary Section
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .padding(20.dp)
                    ) {
                        Text("Bill Summary", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color.Black)
                        Spacer(Modifier.height(16.dp))
                        
                        BillRow("Item Subtotal", "₹$subtotal")
                        BillRow("Delivery/Service Fee", "₹$deliveryFee")
                        
                        HorizontalDivider(Modifier.padding(vertical = 16.dp), color = Color(0xFFF3F4F6))
                        
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Grand Total", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                            Text("₹$grandTotal", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = PinkPrimary)
                        }
                    }
                }

                // Trust Badge
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Shield, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("100% Safe & Secure Payments", fontSize = 12.sp, color = Color.Gray)
                    }
                    Spacer(Modifier.height(80.dp)) // Padding for bottom bar
                }
            }
        }
    }
}

@Composable
fun CartCategoryHeader(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    ) {
        Icon(icon, null, tint = PinkPrimary, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(title, fontWeight = FontWeight.Bold, color = PinkPrimary, fontSize = 14.sp)
    }
}

@Composable
fun CartItemRow(
    name: String, 
    price: Int, 
    quantity: Int, 
    category: String,
    onIncrease: () -> Unit, 
    onDecrease: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp), 
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder for Image or Icon based on category
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PinkPrimary.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when(category) {
                        "Food" -> Icons.Default.Fastfood
                        "Residential" -> Icons.Default.Build
                        "Essential" -> Icons.Default.ShoppingBasket
                        "Service" -> Icons.Default.CalendarToday
                        "Education" -> Icons.Default.School
                        else -> Icons.Default.AutoFixHigh
                    },
                    contentDescription = null,
                    tint = PinkPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Bold, color = Color.Black, maxLines = 2, fontSize = 15.sp)
                Text("₹$price", color = PinkPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
            }

            Column(horizontalAlignment = Alignment.End) {
                Row(
                    verticalAlignment = Alignment.CenterVertically, 
                    modifier = Modifier
                        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                        .padding(horizontal = 4.dp)
                ) {
                    IconButton(onClick = { if (quantity > 1) onDecrease() else onDelete() }, modifier = Modifier.size(32.dp)) { 
                        Icon(if (quantity > 1) Icons.Default.Remove else Icons.Default.Delete, null, tint = if (quantity > 1) Color.Black else ErrorRed, modifier = Modifier.size(18.dp)) 
                    }
                    Text(
                        quantity.toString(), 
                        fontWeight = FontWeight.ExtraBold, 
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    IconButton(onClick = onIncrease, modifier = Modifier.size(32.dp)) { 
                        Icon(Icons.Default.Add, null, tint = Color.Black, modifier = Modifier.size(18.dp)) 
                    }
                }
            }
        }
    }
}

@Composable
fun BillRow(label: String, value: String, isDiscount: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), 
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = value, 
            color = if (isDiscount) SuccessGreen else Color.Black, 
            fontWeight = if (isDiscount) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun EmptyCartContent(padding: PaddingValues, navController: NavController) {
    Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Surface(
                modifier = Modifier.size(120.dp),
                shape = RoundedCornerShape(60.dp),
                color = PinkPrimary.copy(alpha = 0.05f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.ShoppingCart, null, modifier = Modifier.size(48.dp), tint = PinkPrimary)
                }
            }
            Spacer(Modifier.height(24.dp))
            Text("Your Cart is Empty", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(
                "Looks like you haven't added anything to your cart yet.",
                textAlign = TextAlign.Center,
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = { navController.navigate(Screen.Home.route) },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Start Shopping", fontWeight = FontWeight.Bold)
            }
        }
    }
}
