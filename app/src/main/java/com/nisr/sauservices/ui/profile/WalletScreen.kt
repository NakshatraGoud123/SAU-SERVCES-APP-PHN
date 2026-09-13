package com.nisr.sauservices.ui.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.data.model.Transaction
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.components.LuxuryButton
import com.nisr.sauservices.ui.viewmodel.WalletViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    navController: NavController,
    viewModel: WalletViewModel = viewModel()
) {
    val balance by viewModel.balance.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val isLoading = viewModel.isLoading
    
    var showTopUpDialog by remember { mutableStateOf(false) }
    var topUpAmount by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Wallet", fontWeight = FontWeight.Black, color = LuxeTextPrimary, fontFamily = androidx.compose.ui.text.font.FontFamily.Serif) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = LuxeTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxeBackground)
            )
        },
        containerColor = LuxeBackground
    ) { padding ->
        if (isLoading && balance == 0.0) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LuxeAccentSage)
            }
        } else {
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                // Balance Card
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    shape = RoundedCornerShape(28.dp),
                    color = LuxeCard,
                    border = BorderStroke(1.dp, LuxeBorder),
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Available Balance", color = LuxeTextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "₹$balance",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Black,
                            color = LuxeTextPrimary,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Serif
                        )
                        Spacer(Modifier.height(32.dp))
                        LuxuryButton(
                            text = "TOP UP WALLET",
                            onClick = { showTopUpDialog = true },
                            modifier = Modifier.width(220.dp).height(50.dp)
                        )
                    }
                }

                Text(
                    "RECENT TRANSACTIONS",
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    color = LuxeTextSecondary,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    letterSpacing = 1.5.sp
                )

                if (transactions.isEmpty()) {
                    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No transactions yet.", color = LuxeTextSecondary, fontSize = 14.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(transactions) { tx ->
                            TransactionRow(tx)
                        }
                    }
                }
            }
        }
    }

    if (showTopUpDialog) {
        AlertDialog(
            onDismissRequest = { showTopUpDialog = false },
            containerColor = LuxeCard,
            title = { Text("Top Up Wallet", fontWeight = FontWeight.Black, color = LuxeTextPrimary, fontFamily = androidx.compose.ui.text.font.FontFamily.Serif) },
            text = {
                Column {
                    Text("Enter the amount you wish to add to your luxury balance.", color = LuxeTextSecondary, fontSize = 14.sp)
                    Spacer(Modifier.height(20.dp))
                    OutlinedTextField(
                        value = topUpAmount,
                        onValueChange = { if (it.all { char -> char.isDigit() }) topUpAmount = it },
                        label = { Text("Amount (₹)", color = LuxeAccentSage) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LuxeAccentSage,
                            unfocusedBorderColor = LuxeBorder
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val amount = topUpAmount.toDoubleOrNull()
                        if (amount != null && amount > 0) {
                            viewModel.topUpWallet(amount) {
                                showTopUpDialog = false
                                topUpAmount = ""
                            }
                        }
                    }
                ) {
                    Text("ADD MONEY", color = LuxeAccentSage, fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTopUpDialog = false }) {
                    Text("CANCEL", color = LuxeTextSecondary)
                }
            }
        )
    }
}

@Composable
fun TransactionRow(tx: Transaction) {
    val isCredit = tx.type == "credit"
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = LuxeCard,
        border = BorderStroke(1.dp, LuxeBorder)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (isCredit) LuxeAccentSage.copy(alpha = 0.1f) else ErrorRed.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCredit) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                    contentDescription = null,
                    tint = if (isCredit) LuxeAccentSage else ErrorRed,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(tx.description, fontWeight = FontWeight.Bold, color = LuxeTextPrimary, fontSize = 15.sp)
                Text(tx.createdAt ?: "", fontSize = 12.sp, color = LuxeTextSecondary)
            }
            Text(
                text = "${if (isCredit) "+" else "-"}₹${tx.amount}",
                fontWeight = FontWeight.Black,
                color = if (isCredit) LuxeAccentSage else LuxeTextPrimary,
                fontSize = 17.sp
            )
        }
    }
}
