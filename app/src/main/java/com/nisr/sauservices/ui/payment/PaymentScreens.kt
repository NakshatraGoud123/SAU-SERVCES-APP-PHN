package com.nisr.sauservices.ui.payment

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.viewmodel.AuthViewModel
import com.nisr.sauservices.ui.viewmodel.PaymentViewModel

private val DarkBackground = Color(0xFF0D1117)
private val CardBackground = Color(0xFF161B22)
private val AccentColor = Color(0xFF00E5FF)
private val SuccessGreen = Color(0xFF00C853)

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
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Payment", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
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
                "Choose Payment Method",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(25.dp))

            PaymentCard("UPI", "Google Pay • PhonePe • Paytm", Icons.Default.AccountBalanceWallet, "upi", selectedMethod) { selectedMethod = it }
            PaymentCard("Credit / Debit Card", "Visa • Mastercard • RuPay", Icons.Default.CreditCard, "card", selectedMethod) { selectedMethod = it }
            PaymentCard("Net Banking", "All major banks", Icons.Default.AccountBalance, "netbanking", selectedMethod) { selectedMethod = it }
            PaymentCard("Cash Payment", "Pay the partner after service", Icons.Default.Payments, "cash", selectedMethod) { selectedMethod = it }

            Spacer(modifier = Modifier.weight(1f))

            if (viewModel.paymentError != null) {
                Text(
                    text = viewModel.paymentError!!,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = CardBackground
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total", color = Color.White.copy(alpha = 0.7f), fontSize = 16.sp)
                    Text("₹${amount.toInt()}", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Button(
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
                            customerContact = "9999999999" // TODO: Get from user data if available
                        ) {
                            navController.navigate(Screen.PaidSuccess(amount))
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentColor),
                shape = RoundedCornerShape(18.dp),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        if (selectedMethod == "cash") "Confirm Cash Booking" else "Pay ₹${amount.toInt()}",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
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
            .padding(bottom = 12.dp)
            .clickable { onSelect(value) },
        shape = RoundedCornerShape(18.dp),
        color = if (selected) Color(0xFF17252B) else CardBackground,
        border = BorderStroke(if (selected) 1.5.dp else 1.dp, if (selected) AccentColor else Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = if (selected) AccentColor else Color.White.copy(alpha = 0.7f), modifier = Modifier.size(30.dp))
            Spacer(modifier = Modifier.width(15.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(subtitle, color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
            }
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .border(2.dp, if (selected) AccentColor else Color.White.copy(alpha = 0.38f), CircleShape)
                    .padding(4.dp)
            ) {
                if (selected) {
                    Box(modifier = Modifier.fillMaxSize().background(AccentColor, CircleShape))
                }
            }
        }
    }
}

@Composable
fun DigitalPaymentSuccessScreen(
    navController: NavController,
    amount: Double
) {
    Box(
        modifier = Modifier.fillMaxSize().background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = SuccessGreen
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(60.dp).padding(20.dp))
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text("Payment Successful!", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Amount ₹${amount.toInt()} received successfully.",
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(35.dp))

            Button(
                onClick = {
                    navController.navigate(Screen.Home) {
                        popUpTo<Screen.Home> { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentColor)
            ) {
                Text("Go to Home", color = Color.Black, fontWeight = FontWeight.Bold)
            }
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
        modifier = Modifier.fillMaxSize().background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = SuccessGreen
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(60.dp).padding(20.dp))
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text("Booking Confirmed!", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Pay ₹${amount.toInt()} in cash after the service.",
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(35.dp))

            Button(
                onClick = {
                    navController.navigate(Screen.Home) {
                        popUpTo<Screen.Home> { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentColor)
            ) {
                Text("Go to Home", color = Color.Black, fontWeight = FontWeight.Bold)
            }
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
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Cash Collection", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
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

            Icon(Icons.Default.Payments, contentDescription = null, tint = AccentColor, modifier = Modifier.size(90.dp))

            Spacer(modifier = Modifier.height(30.dp))

            Text("Amount to Collect", color = Color.White.copy(alpha = 0.7f), fontSize = 16.sp)

            Spacer(modifier = Modifier.height(10.dp))

            Text("₹${amount.toInt()}", color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Ask the customer to pay the exact amount in cash.",
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.markCashCollected(paymentId) {
                        navController.navigate(Screen.CustomerOtp(paymentId, bookingId, amount))
                    }
                },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                shape = RoundedCornerShape(18.dp),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("CASH COLLECTED", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
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
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Verify Payment", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
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
            Text("Enter OTP", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Ask the customer for the OTP shown on their screen", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp)

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = otp,
                onValueChange = { if (it.length <= 6) otp = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = 24.sp, letterSpacing = 8.sp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentColor,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            if (viewModel.paymentError != null) {
                Text(viewModel.paymentError!!, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.verifyOtp(paymentId, otp, bookingId) {
                        navController.navigate(Screen.PaidSuccess(amount))
                    }
                },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentColor),
                shape = RoundedCornerShape(18.dp),
                enabled = otp.length == 6 && !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                } else {
                    Text("VERIFY OTP", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
