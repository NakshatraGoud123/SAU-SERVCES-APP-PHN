package com.nisr.sauservices.ui.essentials

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.text.font.FontFamily
import com.nisr.sauservices.data.api.SupabaseClient
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.components.*
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.viewmodel.CartViewModel
import io.github.jan.supabase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuppliesCheckoutScreen(navController: NavController, viewModel: CartViewModel) {
    val cartItems by viewModel.dbCartItems.collectAsState()
    var address by remember { mutableStateOf("") }
    var selectedPayment by remember { mutableStateOf("UPI") }

    val itemTotal = cartItems.sumOf { it.totalPrice }
    val deliveryCharge = 30.0
    val tax = itemTotal * 0.05
    val grandTotal = itemTotal + deliveryCharge + tax
    
    val orderStatus by viewModel.orderStatus.collectAsState()
    var orderId by remember { mutableStateOf("") }

    LaunchedEffect(orderStatus) {
        orderStatus?.let {
            if (it.isSuccess) {
                orderId = it.getOrNull() ?: "ORD${System.currentTimeMillis() % 1000000}"
                val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: ""
                
                // Navigate to Payment Method Screen
                navController.navigate(Screen.PaymentMethod(
                    bookingId = orderId,
                    customerId = userId,
                    partnerId = "partner_pending",
                    amount = grandTotal
                )) {
                    popUpTo<Screen.HomeEssentialsCheckout> { inclusive = true }
                }
                viewModel.resetOrderStatus()
            }
        }
    }

    LuxuryScaffold(
        title = "Checkout Supplies",
        onBackClick = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            Text("Order Summary", color = LuxuryTextPrimary, fontWeight = FontWeight.Black, fontSize = 18.sp, fontFamily = FontFamily.Serif)
            
            LuxuryCard(modifier = Modifier.padding(vertical = 12.dp)) {
                Column(Modifier.padding(20.dp)) {
                    cartItems.forEach { item ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${item.itemName} x ${item.quantity}", color = LuxuryTextPrimary)
                            Text("₹${item.totalPrice}", color = LuxuryTextPrimary, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                    HorizontalDivider(Modifier.padding(vertical = 12.dp), color = LuxuryBorder)
                    PriceRowLuxury("Subtotal", itemTotal)
                    PriceRowLuxury("Delivery Charge", deliveryCharge)
                    PriceRowLuxury("Tax (5%)", tax)
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Grand Total", color = LuxuryTextPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        Text("₹${grandTotal.toInt()}", color = LuxuryGold, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text("Delivery Address", color = LuxuryTextPrimary, fontWeight = FontWeight.Black, fontSize = 18.sp, fontFamily = FontFamily.Serif)
            
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                placeholder = { Text("Enter full delivery address", color = LuxuryTextSecondary) },
                leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = LuxuryGold) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LuxuryGold,
                    unfocusedBorderColor = LuxuryBorder,
                    focusedContainerColor = LuxuryCard,
                    unfocusedContainerColor = LuxuryCard,
                    focusedTextColor = LuxuryTextPrimary,
                    unfocusedTextColor = LuxuryTextPrimary
                )
            )

            Spacer(Modifier.height(24.dp))
            Text("Payment Method", color = LuxuryTextPrimary, fontWeight = FontWeight.Black, fontSize = 18.sp, fontFamily = FontFamily.Serif)
            
            PaymentOptionRowLuxury("UPI Transfer", Icons.Default.QrCode, selectedPayment == "UPI") { selectedPayment = "UPI" }
            PaymentOptionRowLuxury("Cash on Delivery", Icons.Default.Payments, selectedPayment == "COD") { selectedPayment = "COD" }

            Spacer(Modifier.height(40.dp))

            LuxuryButton(
                text = "CONFIRM ORDER",
                onClick = {
                    if (address.isNotBlank()) {
                        viewModel.placeOrder(address, selectedPayment)
                    }
                },
                enabled = address.isNotBlank()
            )
            
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun PriceRowLuxury(label: String, amount: Double) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = LuxuryTextSecondary, fontSize = 14.sp)
        Text("₹${amount.toInt()}", color = LuxuryTextPrimary, fontSize = 14.sp)
    }
}

@Composable
fun PaymentOptionRowLuxury(name: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isSelected: Boolean, onSelect: () -> Unit) {
    OutlinedCard(
        onClick = onSelect,
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (isSelected) LuxuryGold.copy(alpha = 0.05f) else LuxuryCard
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) LuxuryGold else LuxuryBorder)
    ) {
        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = if (isSelected) LuxuryGold else LuxuryTextSecondary)
            Spacer(Modifier.width(16.dp))
            Text(name, color = LuxuryTextPrimary, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
            Spacer(Modifier.weight(1f))
            RadioButton(
                selected = isSelected, 
                onClick = null, 
                colors = RadioButtonDefaults.colors(selectedColor = LuxuryGold, unselectedColor = LuxuryTextSecondary)
            )
        }
    }
}
