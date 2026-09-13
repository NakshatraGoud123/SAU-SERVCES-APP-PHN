package com.nisr.sauservices.ui.home

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniversalCheckoutScreen(
    navController: NavController,
    cartViewModel: CartViewModel,
    profileViewModel: ProfileViewModel,
    paymentViewModel: PaymentViewModel,
    walletViewModel: WalletViewModel = viewModel()
) {
    val cartItems by cartViewModel.dbCartItems.collectAsState()
    val userProfile by profileViewModel.userProfile.collectAsState()
    val addresses by profileViewModel.addresses.collectAsState()
    val orderStatus by cartViewModel.orderStatus.collectAsState()
    val walletBalance by walletViewModel.balance.collectAsState()
    
    val context = LocalContext.current
    val sessionManager = remember { com.nisr.sauservices.data.local.SessionManager(context) }
    val sessionAddress = sessionManager.getAddress()
    
    var selectedAddressId by remember { mutableStateOf("") }
    var useSessionAddress by remember { mutableStateOf(false) }
    var selectedPaymentMethod by remember { mutableStateOf("Razorpay") }
    val subtotal = cartItems.sumOf { it.totalPrice }
    val deliveryFee = if (cartItems.isEmpty()) 0.0 else 30.0
    val grandTotal = subtotal + deliveryFee

    // Initialize selection
    LaunchedEffect(addresses) {
        if (addresses.isNotEmpty()) {
            selectedAddressId = addresses.find { it.isDefault }?.id ?: addresses.first().id
            useSessionAddress = false
        } else if (sessionAddress != "Fetching location...") {
            useSessionAddress = true
        }
    }

    // Handle Order Result
    LaunchedEffect(orderStatus) {
        orderStatus?.onSuccess { orderId ->
            navController.navigate(Screen.OrderTracking(orderId)) {
                popUpTo(Screen.Home) { inclusive = false }
            }
            cartViewModel.resetOrderStatus()
        }?.onFailure {
            Toast.makeText(context, "Order failed: ${it.message}", Toast.LENGTH_LONG).show()
            cartViewModel.resetOrderStatus()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout", fontWeight = FontWeight.Black, color = LuxeTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = LuxeTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxeBackground)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = LuxeCard,
                border = BorderStroke(1.dp, LuxeBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp).navigationBarsPadding()) {
                    com.nisr.sauservices.ui.components.LuxuryButton(
                        text = "PAY ₹$grandTotal",
                        onClick = {
                            if (cartItems.isEmpty()) {
                                Toast.makeText(context, "Your cart is empty", Toast.LENGTH_SHORT).show()
                                return@LuxuryButton
                            }
                            
                            val finalAddress = if (useSessionAddress) {
                                sessionAddress
                            } else {
                                addresses.find { it.id == selectedAddressId }?.let { 
                                    "${it.houseNo}, ${it.street}, ${it.city}" 
                                } ?: ""
                            }

                            if (finalAddress.isEmpty() || finalAddress == "Fetching location...") {
                                Toast.makeText(context, "Please select or add an address", Toast.LENGTH_SHORT).show()
                                return@LuxuryButton
                            }

                            val activity = context.findActivity()
                            if (activity == null) {
                                Toast.makeText(context, "Unable to process payment context", Toast.LENGTH_SHORT).show()
                                return@LuxuryButton
                            }

                            if (selectedPaymentMethod == "Razorpay") {
                                paymentViewModel.startRazorpayPayment(
                                    activity = activity,
                                    bookingId = "ORDER_${System.currentTimeMillis()}",
                                    customerId = userProfile?.id ?: "",
                                    partnerId = "PENDING",
                                    amount = grandTotal,
                                    customerEmail = userProfile?.email ?: "",
                                    customerContact = userProfile?.phone ?: "",
                                    onSuccess = {
                                        cartViewModel.placeOrder(finalAddress, null, "Paid via Razorpay")
                                    }
                                )
                            } else if (selectedPaymentMethod == "Wallet") {
                                if (walletBalance >= grandTotal) {
                                    walletViewModel.processWalletPayment(
                                        amount = grandTotal,
                                        description = "Paid for Order"
                                    ) { result ->
                                        if (result.isSuccess) {
                                            cartViewModel.placeOrder(finalAddress, null, "Paid via Wallet")
                                        } else {
                                            Toast.makeText(context, "Wallet payment failed", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Insufficient Wallet Balance", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                cartViewModel.placeOrder(finalAddress, null, "Cash on Delivery")
                            }
                        },
                        isLoading = paymentViewModel.isLoading || walletViewModel.isLoading,
                        enabled = !paymentViewModel.isLoading && !walletViewModel.isLoading
                    )
                }
            }
        },
        containerColor = LuxeBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Delivery Address
            item {
                CheckoutSectionHeader("Delivery Address", Icons.Default.LocationOn)
                
                // Option 1: Saved Addresses
                if (addresses.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        addresses.forEach { addr ->
                            AddressSelectionCard(
                                addressName = addr.city,
                                addressDetail = "${addr.houseNo}, ${addr.street}",
                                isSelected = !useSessionAddress && selectedAddressId == addr.id,
                                onSelect = { 
                                    selectedAddressId = addr.id
                                    useSessionAddress = false 
                                }
                            )
                        }
                    }
                }
                
                Spacer(Modifier.height(12.dp))

                // Option 2: Current Map Location (Session)
                if (sessionAddress != "Fetching location...") {
                    AddressSelectionCard(
                        addressName = "Current Location",
                        addressDetail = sessionAddress,
                        isSelected = useSessionAddress,
                        onSelect = { 
                            useSessionAddress = true
                            selectedAddressId = ""
                        }
                    )
                }

                Spacer(Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { navController.navigate(Screen.ShippingAddress) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, LuxeAccentSage),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = LuxeAccentSage)
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("ADD NEW SAVED ADDRESS", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }

            // 2. Order Items Summary
            item {
                CheckoutSectionHeader("Order Summary", Icons.Default.ShoppingCart)
                Surface(
                    color = LuxeCard,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, LuxeBorder)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        cartItems.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${item.itemName} x ${item.quantity}", color = LuxeTextPrimary, fontSize = 14.sp, modifier = Modifier.weight(1f))
                                Text("₹${item.totalPrice}", color = LuxeTextPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = LuxeBorder)
                        LuxeBillRow("Subtotal", "₹$subtotal")
                        LuxeBillRow("Delivery Fee", "₹$deliveryFee")
                        Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Grand Total", fontWeight = FontWeight.Black, color = LuxeTextPrimary, fontSize = 18.sp)
                            Text("₹$grandTotal", fontWeight = FontWeight.Black, color = LuxeAccentSage, fontSize = 18.sp)
                        }
                    }
                }
            }

            // 3. Payment Method
            item {
                CheckoutSectionHeader("Payment Method", Icons.Default.Payment)
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    PaymentMethodCard(
                        title = "Razorpay (Cards, UPI, Netbanking)",
                        subtitle = "Fast & Secure",
                        icon = Icons.Default.CreditCard,
                        isSelected = selectedPaymentMethod == "Razorpay",
                        onSelect = { selectedPaymentMethod = "Razorpay" }
                    )
                    PaymentMethodCard(
                        title = "Wallet Balance (₹$walletBalance)",
                        subtitle = if (walletBalance >= grandTotal) "Available" else "Insufficient Balance",
                        icon = Icons.Default.AccountBalanceWallet,
                        isSelected = selectedPaymentMethod == "Wallet",
                        onSelect = { selectedPaymentMethod = "Wallet" }
                    )
                    PaymentMethodCard(
                        title = "Cash on Delivery",
                        subtitle = "Pay after service",
                        icon = Icons.Default.Payments,
                        isSelected = selectedPaymentMethod == "COD",
                        onSelect = { selectedPaymentMethod = "COD" }
                    )
                }
            }
            
            item { Spacer(Modifier.height(40.dp)) }
        }
    }
}

@Composable
private fun CheckoutSectionHeader(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
        Icon(icon, null, tint = LuxeAccentSage, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(title.uppercase(), fontWeight = FontWeight.Black, fontSize = 12.sp, color = LuxeTextSecondary, letterSpacing = 1.5.sp)
    }
}

@Composable
private fun AddressSelectionCard(addressName: String, addressDetail: String, isSelected: Boolean, onSelect: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onSelect),
        shape = RoundedCornerShape(20.dp),
        color = LuxeCard,
        border = BorderStroke(1.dp, if (isSelected) LuxeAccentSage else LuxeBorder)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = isSelected, onClick = onSelect, colors = RadioButtonDefaults.colors(selectedColor = LuxeAccentSage))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(addressName, fontWeight = FontWeight.Bold, color = LuxeTextPrimary)
                Text(addressDetail, fontSize = 13.sp, color = LuxeTextSecondary, maxLines = 2)
            }
        }
    }
}

@Composable
private fun PaymentMethodCard(title: String, subtitle: String, icon: ImageVector, isSelected: Boolean, onSelect: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onSelect),
        shape = RoundedCornerShape(20.dp),
        color = LuxeCard,
        border = BorderStroke(1.dp, if (isSelected) LuxeAccentSage else LuxeBorder)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(if (isSelected) LuxeHighlightChampagne else LuxeBorder),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = if (isSelected) LuxeTextPrimary else LuxeTextSecondary, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, color = LuxeTextPrimary)
                Text(subtitle, fontSize = 12.sp, color = LuxeTextSecondary)
            }
            RadioButton(selected = isSelected, onClick = onSelect, colors = RadioButtonDefaults.colors(selectedColor = LuxeAccentSage))
        }
    }
}

private fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

@Composable
private fun LuxeBillRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), 
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = LuxeTextSecondary, fontSize = 14.sp)
        Text(text = value, color = LuxeTextPrimary, fontWeight = FontWeight.Medium)
    }
}
