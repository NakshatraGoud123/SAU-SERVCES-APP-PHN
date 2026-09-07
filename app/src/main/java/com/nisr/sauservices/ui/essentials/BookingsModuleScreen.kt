package com.nisr.sauservices.ui.essentials

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.compose.ui.text.font.FontFamily
import com.nisr.sauservices.data.model.BookingCategory
import com.nisr.sauservices.data.model.BookingItem
import com.nisr.sauservices.data.model.BookingSubcategory
import com.nisr.sauservices.data.model.NewModulesData
import com.nisr.sauservices.data.model.toSafeUuid
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.components.*
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.viewmodel.CartViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingsModuleScreen(navController: NavController, cartViewModel: CartViewModel) {
    val categories = NewModulesData.bookings
    var selectedCategory by remember { mutableStateOf<BookingCategory?>(null) }
    var selectedSubcategory by remember { mutableStateOf<BookingSubcategory?>(null) }
    var itemToBook by remember { mutableStateOf<BookingItem?>(null) }
    
    val context = LocalContext.current
    val cartItems by cartViewModel.dbCartItems.collectAsState()

    LuxuryScaffold(
        title = "Service Bookings",
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
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(categories) { category ->
                    LuxuryBookingCategoryCard(category) {
                        selectedCategory = category
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
            LuxuryBookingSubcategoryPopup(
                category = selectedCategory!!,
                onDismiss = { selectedCategory = null },
                onSubcategoryClick = { sub ->
                    selectedSubcategory = sub
                }
            )
        }

        if (selectedSubcategory != null) {
            LuxuryBookingItemsPopup(
                subcategory = selectedSubcategory!!,
                cartViewModel = cartViewModel,
                onDismiss = { selectedSubcategory = null },
                onBookNow = { item ->
                    itemToBook = item
                }
            )
        }

        if (itemToBook != null) {
            LuxurySchedulingPopup(
                item = itemToBook!!,
                onDismiss = { itemToBook = null },
                onConfirm = { date, time, qty ->
                    val priceStr = itemToBook!!.priceRange.replace("₹", "").split("–").first().trim()
                    val price = priceStr.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 0.0
                    
                    cartViewModel.addItemToCart(
                        name = itemToBook!!.name,
                        price = price,
                        category = selectedCategory?.name ?: "Booking",
                        subcategory = selectedSubcategory?.name ?: "",
                        unit = "Booking",
                        productId = itemToBook!!.id,
                        date = date,
                        time = time,
                        quantity = qty
                    ) { result ->
                        if (result.isSuccess) {
                            Toast.makeText(context, "${itemToBook!!.name} added to cart", Toast.LENGTH_SHORT).show()
                            navController.navigate(Screen.Cart)
                        } else {
                            Toast.makeText(context, "Failed to add to cart", Toast.LENGTH_SHORT).show()
                        }
                    }

                    itemToBook = null
                    selectedSubcategory = null
                    selectedCategory = null
                }
            )
        }
    }
}

@Composable
fun LuxuryBookingCategoryCard(category: BookingCategory, onClick: () -> Unit) {
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
fun LuxuryBookingSubcategoryPopup(
    category: BookingCategory,
    onDismiss: () -> Unit,
    onSubcategoryClick: (BookingSubcategory) -> Unit
) {
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
                                Text("Premium Services", style = MaterialTheme.typography.bodySmall, color = LuxuryTextSecondary)
                            }
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.background(LuxuryCard, CircleShape).size(36.dp)
                            ) {
                                Icon(Icons.Default.Close, null, modifier = Modifier.size(18.dp), tint = LuxuryTextSecondary)
                            }
                        }
                        
                        Spacer(Modifier.height(32.dp))
                        
                        LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                            items(category.subcategories) { sub ->
                                LuxuryCard(
                                    onClick = { onSubcategoryClick(sub) }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(20.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(sub.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LuxuryTextPrimary)
                                        Icon(Icons.Default.ChevronRight, null, tint = LuxuryGold)
                                    }
                                }
                                Spacer(Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LuxuryBookingItemsPopup(
    subcategory: BookingSubcategory,
    cartViewModel: CartViewModel,
    onDismiss: () -> Unit,
    onBookNow: (BookingItem) -> Unit
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
                                    text = subcategory.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = LuxuryGold,
                                    fontFamily = FontFamily.Serif
                                )
                                Text("Available Packages", style = MaterialTheme.typography.bodySmall, color = LuxuryTextSecondary)
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
                            items(subcategory.items) { item ->
                                val inCartCount = cartItems.find { it.productId == item.id.toSafeUuid() }?.quantity ?: 0
                                LuxuryBookingItemRow(item, inCartCount) {
                                    onBookNow(item)
                                }
                                if (subcategory.items.last() != item) {
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = LuxuryBorder)
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
fun LuxuryBookingItemRow(item: BookingItem, count: Int, onBook: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
            Text(text = item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LuxuryTextPrimary)
            Text(text = item.priceRange, color = LuxuryGold, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
        }
        
        if (count == 0) {
            Button(
                onClick = onBook,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LuxuryGold.copy(alpha = 0.1f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryGold),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                modifier = Modifier.height(40.dp)
            ) {
                Text("BOOK", color = LuxuryGold, fontSize = 12.sp, fontWeight = FontWeight.Black)
            }
        } else {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = LuxuryGold,
                modifier = Modifier.height(40.dp).clickable { onBook() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("$count SCHEDULED", color = LuxuryBackground, fontWeight = FontWeight.Black, fontSize = 11.sp)
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Default.CheckCircle, null, tint = LuxuryBackground, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

@Composable
fun LuxurySchedulingPopup(
    item: BookingItem,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int) -> Unit
) {
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var quantity by remember { mutableIntStateOf(1) }
    
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            selectedDate = "$dayOfMonth/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = LuxuryBackground,
            border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(48.dp).background(LuxuryGold.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Event, null, tint = LuxuryGold)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(text = "Schedule Now", color = LuxuryTextPrimary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, fontFamily = FontFamily.Serif)
                        Text(text = item.name, color = LuxuryTextSecondary, fontSize = 14.sp)
                    }
                }

                Spacer(Modifier.height(32.dp))

                LuxurySelectionCard(
                    onClick = { datePickerDialog.show() },
                    icon = Icons.Default.CalendarMonth,
                    label = if (selectedDate.isEmpty()) "Select Date" else selectedDate,
                    isSelected = selectedDate.isNotEmpty()
                )

                Spacer(Modifier.height(16.dp))

                LuxurySelectionCard(
                    onClick = { timePickerDialog.show() },
                    icon = Icons.Default.Schedule,
                    label = if (selectedTime.isEmpty()) "Select Time" else selectedTime,
                    isSelected = selectedTime.isNotEmpty()
                )

                Spacer(Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Number of Units", color = LuxuryTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Row(
                        verticalAlignment = Alignment.CenterVertically, 
                        modifier = Modifier.background(LuxuryCard, RoundedCornerShape(12.dp)).border(1.dp, LuxuryBorder, RoundedCornerShape(12.dp)).padding(horizontal = 4.dp)
                    ) {
                        IconButton(onClick = { if (quantity > 1) quantity-- }, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.Remove, null, tint = LuxuryGold, modifier = Modifier.size(20.dp))
                        }
                        Text(quantity.toString(), color = LuxuryTextPrimary, modifier = Modifier.padding(horizontal = 12.dp), fontWeight = FontWeight.Black, fontSize = 16.sp)
                        IconButton(onClick = { quantity++ }, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.Add, null, tint = LuxuryGold, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                Spacer(Modifier.height(40.dp))

                LuxuryButton(
                    text = "CONFIRM & PROCEED",
                    onClick = {
                        if (selectedDate.isNotEmpty() && selectedTime.isNotEmpty()) {
                            onConfirm(selectedDate, selectedTime, quantity)
                        } else {
                            Toast.makeText(context, "Please select date and time", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun LuxurySelectionCard(onClick: () -> Unit, icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, isSelected: Boolean) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = LuxuryCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) LuxuryGold else LuxuryBorder)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = if (isSelected) LuxuryGold else LuxuryTextSecondary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Text(label, color = if (isSelected) LuxuryTextPrimary else LuxuryTextSecondary, fontSize = 15.sp, fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Medium)
        }
    }
}
