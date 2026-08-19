package com.nisr.sauservices.ui.essentials

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.nisr.sauservices.data.model.BookingCategory
import com.nisr.sauservices.data.model.BookingItem
import com.nisr.sauservices.data.model.BookingSubcategory
import com.nisr.sauservices.data.model.NewModulesData
import com.nisr.sauservices.data.model.toSafeUuid
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.PinkPrimary
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Service Bookings", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold) },
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
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(categories) { category ->
                    BookingCategoryCardSmall(category) {
                        selectedCategory = category
                    }
                }
            }

            if (cartItems.isNotEmpty()) {
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
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
                    ) {
                        val totalCount = cartItems.sumOf { it.quantity }
                        Text("View Cart ($totalCount Items)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }

        if (selectedCategory != null) {
            BookingSubcategoryPopupProfessional(
                category = selectedCategory!!,
                onDismiss = { selectedCategory = null },
                onSubcategoryClick = { sub ->
                    selectedSubcategory = sub
                }
            )
        }

        if (selectedSubcategory != null) {
            BookingItemsPopupProfessional(
                subcategory = selectedSubcategory!!,
                cartViewModel = cartViewModel,
                onDismiss = { selectedSubcategory = null },
                onBookNow = { item ->
                    itemToBook = item
                }
            )
        }

        if (itemToBook != null) {
            SchedulingPopupProfessional(
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
                            navController.navigate(Screen.Cart.route)
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
fun BookingCategoryCardSmall(category: BookingCategory, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
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
fun BookingSubcategoryPopupProfessional(
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
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.Black
                                )
                                Text("Select a category to view options", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.background(Color(0xFFF3F4F6), CircleShape).size(36.dp)
                            ) {
                                Icon(Icons.Default.Close, null, modifier = Modifier.size(18.dp))
                            }
                        }
                        
                        Spacer(Modifier.height(24.dp))
                        
                        LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                            items(category.subcategories) { sub ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onSubcategoryClick(sub) },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                                    elevation = CardDefaults.cardElevation(0.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(20.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(sub.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                                        Icon(Icons.Default.ChevronRight, null, tint = PinkPrimary)
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
fun BookingItemsPopupProfessional(
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
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.Black
                                )
                                Text("Available service options", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
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
                            items(subcategory.items) { item ->
                                val inCartCount = cartItems.find { it.productId == item.id.toSafeUuid() }?.quantity ?: 0
                                BookingItemRowProfessional(item, inCartCount) {
                                    onBookNow(item)
                                }
                                if (subcategory.items.last() != item) {
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF3F4F6))
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
fun BookingItemRowProfessional(item: BookingItem, count: Int, onBook: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
            Text(text = item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
            Text(text = item.priceRange, color = PinkPrimary, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
        }
        
        if (count == 0) {
            Button(
                onClick = onBook,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                modifier = Modifier.height(40.dp)
            ) {
                Text("BOOK NOW", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = PinkPrimary.copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(1.dp, PinkPrimary),
                modifier = Modifier.height(40.dp).clickable { onBook() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("$count Scheduled", color = PinkPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Default.CheckCircle, null, tint = PinkPrimary, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

@Composable
fun SchedulingPopupProfessional(
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
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(48.dp).background(PinkPrimary.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Event, null, tint = PinkPrimary)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(text = "Schedule Now", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                        Text(text = item.name, color = Color.Gray, fontSize = 14.sp)
                    }
                }

                Spacer(Modifier.height(24.dp))

                OutlinedCard(
                    onClick = { datePickerDialog.show() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = Color(0xFFF9FAFB)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedDate.isEmpty()) Color(0xFFE5E7EB) else PinkPrimary)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarMonth, null, tint = PinkPrimary, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Text(if (selectedDate.isEmpty()) "Select Date" else selectedDate, fontSize = 15.sp, fontWeight = if(selectedDate.isEmpty()) FontWeight.Normal else FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(12.dp))

                OutlinedCard(
                    onClick = { timePickerDialog.show() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = Color(0xFFF9FAFB)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedTime.isEmpty()) Color(0xFFE5E7EB) else PinkPrimary)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, null, tint = PinkPrimary, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Text(if (selectedTime.isEmpty()) "Select Time" else selectedTime, fontSize = 15.sp, fontWeight = if(selectedTime.isEmpty()) FontWeight.Normal else FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Number of Persons/Units", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Row(
                        verticalAlignment = Alignment.CenterVertically, 
                        modifier = Modifier.background(Color(0xFFF3F4F6), RoundedCornerShape(12.dp)).padding(horizontal = 4.dp)
                    ) {
                        IconButton(onClick = { if (quantity > 1) quantity-- }, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.Remove, null, tint = PinkPrimary, modifier = Modifier.size(20.dp))
                        }
                        Text(quantity.toString(), modifier = Modifier.padding(horizontal = 12.dp), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        IconButton(onClick = { quantity++ }, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.Add, null, tint = PinkPrimary, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (selectedDate.isNotEmpty() && selectedTime.isNotEmpty()) {
                            onConfirm(selectedDate, selectedTime, quantity)
                        } else {
                            Toast.makeText(context, "Please select date and time", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text("PROCEED TO CHECKOUT", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}
