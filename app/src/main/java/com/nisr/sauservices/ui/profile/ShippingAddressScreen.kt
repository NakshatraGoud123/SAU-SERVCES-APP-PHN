package com.nisr.sauservices.ui.profile

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nisr.sauservices.data.model.Address
import com.nisr.sauservices.ui.viewmodel.ProfileViewModel
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShippingAddressScreen(
    navController: NavController, 
    viewModel: ProfileViewModel = viewModel(),
    locationViewModel: com.nisr.sauservices.ui.viewmodel.LocationViewModel = viewModel()
) {
    val addresses by viewModel.addresses.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Addresses", fontWeight = FontWeight.Black, color = LuxuryTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LuxuryTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxuryBackground)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = LuxuryGold,
                contentColor = LuxuryBackground,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Address")
            }
        },
        containerColor = LuxuryBackground
    ) { padding ->
        if (addresses.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(64.dp), tint = LuxuryBorder)
                    Spacer(Modifier.height(16.dp))
                    Text("No addresses saved yet", color = LuxuryTextSecondary)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(addresses) { address ->
                    AddressItem(
                        address = address,
                        onDelete = { viewModel.deleteAddress(address.id) },
                        onSetDefault = { viewModel.setDefaultAddress(address.id) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddAddressDialog(
            userProfile = userProfile,
            locationViewModel = locationViewModel,
            onDismiss = { showAddDialog = false },
            onSave = { 
                viewModel.addAddress(it)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddressItem(address: Address, onDelete: () -> Unit, onSetDefault: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = LuxuryCard,
        border = BorderStroke(1.dp, if (address.isDefault) LuxuryGold else LuxuryBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (address.isDefault) LuxuryGold.copy(alpha = 0.1f) else LuxuryBorder),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.LocationOn, null, tint = if (address.isDefault) LuxuryGold else LuxuryTextSecondary, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(16.dp))
                Text(
                    text = address.city.ifEmpty { "Saved Address" }, 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 17.sp, 
                    color = LuxuryTextPrimary,
                    modifier = Modifier.weight(1f)
                )
                if (address.isDefault) {
                    LuxuryLabel(text = "DEFAULT", color = LuxuryGold)
                }
            }
            
            Spacer(Modifier.height(12.dp))
            Text("${address.houseNo}, ${address.street}", fontSize = 14.sp, color = LuxuryTextSecondary)
            Text("${address.city}, ${address.pincode}", fontSize = 14.sp, color = LuxuryTextSecondary)
            
            Spacer(Modifier.height(20.dp))
            HorizontalDivider(color = LuxuryBorder)
            Spacer(Modifier.height(12.dp))
            
            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                if (!address.isDefault) {
                    TextButton(onClick = onSetDefault) {
                        Text("Set as Default", color = LuxuryGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Spacer(Modifier.width(10.dp))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAddressDialog(
    userProfile: com.nisr.sauservices.data.model.UserProfile?,
    locationViewModel: com.nisr.sauservices.ui.viewmodel.LocationViewModel,
    onDismiss: () -> Unit, 
    onSave: (Address) -> Unit
) {
    var fullName by remember { mutableStateOf(userProfile?.name ?: "") }
    var phone by remember { mutableStateOf(userProfile?.phone ?: "") }
    var houseNo by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var pincode by remember { mutableStateOf("") }
    var isDefault by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val locationState = locationViewModel.uiState

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = LuxuryCard,
        title = { Text("New Delivery Address", color = LuxuryTextPrimary, fontWeight = FontWeight.Black) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.verticalScroll(rememberScrollState())) {
                
                // Auto-fill button
                OutlinedButton(
                    onClick = {
                        street = locationState.street
                        city = locationState.city
                        pincode = locationState.pincode
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, LuxeAccentSage),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = LuxeAccentSage)
                ) {
                    Icon(Icons.Default.MyLocation, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("USE PINNED LOCATION", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                LuxuryTextField(
                    value = fullName, 
                    onValueChange = { fullName = it }, 
                    label = "FULL NAME",
                    leadingIcon = Icons.Default.Person
                )
                LuxuryTextField(
                    value = phone, 
                    onValueChange = { phone = it }, 
                    label = "PHONE NUMBER", 
                    leadingIcon = Icons.Default.Phone,
                    keyboardType = KeyboardType.Phone
                )
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.weight(1f)) {
                        LuxuryTextField(value = houseNo, onValueChange = { houseNo = it }, label = "HOUSE NO", leadingIcon = Icons.Default.Home)
                    }
                    Box(Modifier.weight(1f)) {
                        LuxuryTextField(value = pincode, onValueChange = { pincode = it }, label = "PINCODE", leadingIcon = Icons.Default.Numbers, keyboardType = KeyboardType.Number)
                    }
                }

                LuxuryTextField(value = street, onValueChange = { street = it }, label = "STREET / AREA", leadingIcon = Icons.Default.LocationOn)
                LuxuryTextField(value = city, onValueChange = { city = it }, label = "CITY", leadingIcon = Icons.Default.Storefront)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isDefault, 
                        onCheckedChange = { isDefault = it },
                        colors = CheckboxDefaults.colors(checkedColor = LuxuryGold, uncheckedColor = LuxuryBorder)
                    )
                    Text("Set as default delivery address", color = LuxuryTextSecondary, fontSize = 14.sp)
                }
            }
        },
        confirmButton = {
            LuxuryButton(
                text = "SAVE ADDRESS",
                onClick = {
                    if (fullName.isNotEmpty() && phone.isNotEmpty() && city.isNotEmpty()) {
                        onSave(Address("", fullName, phone, houseNo, street, city, "", pincode, "", isDefault))
                    } else {
                        Toast.makeText(context, "Please fill required fields", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.height(48.dp)
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCEL", color = LuxuryTextSecondary) }
        }
    )
}
