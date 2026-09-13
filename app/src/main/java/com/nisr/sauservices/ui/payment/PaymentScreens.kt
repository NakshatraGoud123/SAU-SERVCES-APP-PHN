package com.nisr.sauservices.ui.payment

import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.components.LuxuryButton
import com.nisr.sauservices.ui.viewmodel.AuthViewModel
import com.nisr.sauservices.ui.viewmodel.PaymentViewModel

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
private val SuccessGreen = Color(0xFF22C55E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodScreen(
    navController: NavController,
    bookingId: String,
    customerId: String,
    partnerId: String,
    amount: Double,
    viewModel: PaymentViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    var selectedMethod by remember { mutableStateOf("upi") }
    val context = LocalContext.current
    val activity = context as? Activity

    Scaffold(
        containerColor = LuxeBackground,
        topBar = {
            TopAppBar(
                title = { Text("Secure Payment", color = LuxeTextPrimary, fontWeight = FontWeight.Black, fontFamily = FontFamily.Serif) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LuxeTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxeBackground)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Text(
                "Choose Method",
                color = LuxeTextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Serif
            )

            Spacer(modifier = Modifier.height(24.dp))

            PaymentCard("UPI", "Google Pay, PhonePe, Paytm", Icons.Default.AccountBalanceWallet, "upi", selectedMethod) { selectedMethod = it }
            PaymentCard("Credit / Debit Card", "Visa, Mastercard, RuPay", Icons.Default.CreditCard, "card", selectedMethod) { selectedMethod = it }
            PaymentCard("Net Banking", "All major Indian banks", Icons.Default.AccountBalance, "netbanking", selectedMethod) { selectedMethod = it }
            PaymentCard("Cash Payment", "Pay after service complete", Icons.Default.Payments, "cash", selectedMethod) { selectedMethod = it }

            Spacer(modifier = Modifier.weight(1f))

            if (viewModel.paymentError != null) {
                Text(
                    text = viewModel.paymentError!!,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 12.dp),
                    fontSize = 14.sp
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = LuxeCard,
                border = BorderStroke(1.dp, LuxeBorder)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Payable Amount", color = LuxeTextSecondary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    Text("₹${amount.toInt()}", color = LuxeTextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Black)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            LuxuryButton(
                text = if (selectedMethod == "cash") "CONFIRM CASH BOOKING" else "PAY ₹${amount.toInt()}",
                onClick = {
                    if (selectedMethod == "cash") {
                        viewModel.createCashPayment(bookingId, customerId, partnerId, amount) { paymentId ->
                            navController.navigate(Screen.CashSuccess(paymentId, amount))
                        }
                    } else if (activity != null) {
                        val user = authViewModel.currentUser
                        viewModel.startRazorpayPayment(
                            activity = activity,
                            bookingId = bookingId,
                            customerId = customerId,
                            partnerId = partnerId,
                            amount = amount,
                            customerEmail = user?.email ?: "customer@example.com",
                            customerContact = "9999999999"
                        ) {
                            navController.navigate(Screen.PaidSuccess(amount))
                        }
                    }
                },
                isLoading = viewModel.isLoading,
                modifier = Modifier.height(56.dp)
            )
        }
    }
}

@Composable
fun PaymentCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    value: String,
    selectedValue: String,
    onSelect: (String) -> Unit
) {
    val selected = value == selectedValue
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .clickable { onSelect(value) },
        shape = RoundedCornerShape(20.dp),
        color = LuxeCard,
        border = BorderStroke(1.dp, if (selected) LuxeAccentSage else LuxeBorder)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (selected) LuxeHighlightChampagne else LuxeBorder),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = if (selected) LuxeTextPrimary else LuxeTextSecondary, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = LuxeTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(subtitle, color = LuxeTextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            RadioButton(
                selected = selected,
                onClick = { onSelect(value) },
                colors = RadioButtonDefaults.colors(selectedColor = LuxeAccentSage)
            )
        }
    }
}

@Composable
fun DigitalPaymentSuccessScreen(
    navController: NavController,
    amount: Double
) {
    Box(
        modifier = Modifier.fillMaxSize().background(LuxeBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = SuccessGreen.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, SuccessGreen)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(60.dp).padding(20.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Payment Successful", 
                color = LuxeTextPrimary, 
                fontSize = 26.sp, 
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Serif
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Your payment of ₹${amount.toInt()} was processed safely.",
                textAlign = TextAlign.Center,
                color = LuxeTextSecondary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(48.dp))

            LuxuryButton(
                text = "CONTINUE TO HOME",
                onClick = {
                    navController.navigate(Screen.Home) {
                        popUpTo<Screen.Home> { inclusive = true }
                    }
                },
                modifier = Modifier.height(56.dp)
            )
        }
    }
}

@Composable
fun CashBookingSuccessScreen(
    navController: NavController,
    paymentId: String,
    amount: Double
) {
    Box(
        modifier = Modifier.fillMaxSize().background(LuxeBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = LuxeAccentSage.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, LuxeAccentSage)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = LuxeAccentSage, modifier = Modifier.size(60.dp).padding(20.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Booking Confirmed", 
                color = LuxeTextPrimary, 
                fontSize = 26.sp, 
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Serif
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Please pay ₹${amount.toInt()} in cash\nafter your service is complete.",
                textAlign = TextAlign.Center,
                color = LuxeTextSecondary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(48.dp))

            LuxuryButton(
                text = "CONTINUE TO HOME",
                onClick = {
                    navController.navigate(Screen.Home) {
                        popUpTo<Screen.Home> { inclusive = true }
                    }
                },
                modifier = Modifier.height(56.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashCollectionScreen(
    navController: NavController,
    paymentId: String,
    bookingId: String,
    amount: Double,
    viewModel: PaymentViewModel = viewModel()
) {
    Scaffold(
        containerColor = LuxeBackground,
        topBar = {
            TopAppBar(
                title = { Text("Cash Collection", color = LuxeTextPrimary, fontWeight = FontWeight.Black, fontFamily = FontFamily.Serif) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LuxeTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxeBackground)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(LuxeHighlightChampagne.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Payments, contentDescription = null, tint = LuxeAccentSage, modifier = Modifier.size(50.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Amount to Collect", color = LuxeTextSecondary, fontSize = 16.sp, fontWeight = FontWeight.Medium)

            Spacer(modifier = Modifier.height(8.dp))

            Text("₹${amount.toInt()}", color = LuxeTextPrimary, fontSize = 42.sp, fontWeight = FontWeight.Black)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Collect the exact amount in cash\nfrom the customer.",
                textAlign = TextAlign.Center,
                color = LuxeTextSecondary,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            LuxuryButton(
                text = "CASH COLLECTED",
                onClick = {
                    viewModel.markCashCollected(paymentId) {
                        navController.navigate(Screen.CustomerOtp(paymentId, bookingId, amount))
                    }
                },
                isLoading = viewModel.isLoading,
                modifier = Modifier.height(56.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerOtpScreen(
    navController: NavController,
    paymentId: String,
    bookingId: String,
    amount: Double,
    viewModel: PaymentViewModel = viewModel()
) {
    var otp by remember { mutableStateOf("") }

    Scaffold(
        containerColor = LuxeBackground,
        topBar = {
            TopAppBar(
                title = { Text("Verify Payment", color = LuxeTextPrimary, fontWeight = FontWeight.Black, fontFamily = FontFamily.Serif) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LuxeTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxeBackground)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Enter OTP", color = LuxeTextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Serif)
            Text("Ask the customer for the OTP shown on their screen", color = LuxeTextSecondary, fontSize = 14.sp, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = otp,
                onValueChange = { if (it.length <= 6) otp = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = 24.sp, letterSpacing = 8.sp, fontWeight = FontWeight.Black, color = LuxeTextPrimary),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LuxeAccentSage,
                    unfocusedBorderColor = LuxeBorder,
                    focusedContainerColor = LuxeCard,
                    unfocusedContainerColor = LuxeCard
                )
            )

            if (viewModel.paymentError != null) {
                Text(viewModel.paymentError!!, color = Color.Red, modifier = Modifier.padding(top = 12.dp), fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            LuxuryButton(
                text = "VERIFY OTP",
                onClick = {
                    viewModel.verifyOtp(paymentId, otp, bookingId) {
                        navController.navigate(Screen.PaidSuccess(amount))
                    }
                },
                enabled = otp.length == 6 && !viewModel.isLoading,
                isLoading = viewModel.isLoading,
                modifier = Modifier.height(56.dp)
            )
        }
    }
}
