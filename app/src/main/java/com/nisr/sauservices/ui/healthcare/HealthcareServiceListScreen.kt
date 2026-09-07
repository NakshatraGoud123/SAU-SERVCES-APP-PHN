package com.nisr.sauservices.ui.healthcare

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.components.*
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.viewmodel.HealthcareService
import com.nisr.sauservices.ui.viewmodel.HealthcareViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthcareServiceListScreen(navController: NavController, subcategory: String, viewModel: HealthcareViewModel) {
    val services = getServicesForHealthcareSubcategory(subcategory)
    val cartItems = viewModel.cartItems

    LuxuryScaffold(
        title = subcategory,
        onBackClick = { navController.popBackStack() },
        actions = {
            if (cartItems.isNotEmpty()) {
                IconButton(onClick = { navController.navigate(Screen.Cart) }) {
                    BadgedBox(
                        badge = { 
                            Badge(containerColor = LuxuryGold, contentColor = LuxuryBackground) { 
                                Text(cartItems.sumOf { it.quantity }.toString(), fontWeight = FontWeight.Bold) 
                            }
                        }
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = LuxuryTextPrimary)
                    }
                }
            }
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 16.dp,
                    color = LuxuryCard,
                    border = BorderStroke(1.dp, LuxuryBorder),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp).navigationBarsPadding(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            val count = cartItems.sumOf { it.quantity }
                            Text("$count items added", fontSize = 13.sp, color = LuxuryTextSecondary)
                            Text("₹${viewModel.calculateTotal().toInt()}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = LuxuryGold)
                        }
                        Button(
                            onClick = { navController.navigate(Screen.Cart) },
                            colors = ButtonDefaults.buttonColors(containerColor = LuxuryGold),
                            modifier = Modifier.height(50.dp).widthIn(min = 140.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("VIEW CART", color = LuxuryBackground, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(services) { service ->
                val cartItem = cartItems.find { it.id == service.id }
                HealthcareServiceLuxuryCard(
                    service = service,
                    quantity = cartItem?.quantity ?: 0,
                    onAdd = { viewModel.addToCart(service) },
                    onIncrease = { viewModel.updateQty(service.id, true) },
                    onDecrease = { viewModel.updateQty(service.id, false) }
                )
            }
            
            if (services.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.MedicalServices, null, tint = LuxuryGold.copy(alpha = 0.2f), modifier = Modifier.size(64.dp))
                            Spacer(Modifier.height(16.dp))
                            Text("No services available in this category", color = LuxuryTextSecondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HealthcareServiceLuxuryCard(
    service: HealthcareService,
    quantity: Int,
    onAdd: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    LuxuryCard {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = service.name, 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 16.sp, 
                    color = LuxuryTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "₹${service.price.toInt()}", 
                    fontWeight = FontWeight.ExtraBold, 
                    color = LuxuryGold, 
                    fontSize = 15.sp
                )
                if (service.requiresPrescription) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                        Icon(Icons.Default.Info, null, tint = Color.Red, modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Prescription Required", fontSize = 10.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (quantity == 0) {
                Button(
                    onClick = onAdd,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LuxuryBackground),
                    border = BorderStroke(1.dp, LuxuryGold),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("ADD", color = LuxuryGold, fontWeight = FontWeight.Bold)
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(LuxuryGold, RoundedCornerShape(8.dp))
                        .padding(horizontal = 4.dp)
                ) {
                    IconButton(onClick = onDecrease, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Remove, null, tint = LuxuryBackground, modifier = Modifier.size(16.dp))
                    }
                    Text(
                        text = quantity.toString(), 
                        fontWeight = FontWeight.Black, 
                        color = LuxuryBackground, 
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    IconButton(onClick = onIncrease, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Add, null, tint = LuxuryBackground, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

fun getServicesForHealthcareSubcategory(sub: String): List<HealthcareService> {
    return when (sub) {
        "Blood Tests" -> listOf(
            HealthcareService("bt1", "Complete Blood Count (CBC)", 299.0, "Lab Tests", sub),
            HealthcareService("bt2", "Blood Sugar (Fasting)", 199.0, "Lab Tests", sub),
            HealthcareService("bt3", "HbA1c Test", 499.0, "Lab Tests", sub),
            HealthcareService("bt4", "ESR Test", 199.0, "Lab Tests", sub),
            HealthcareService("bt5", "Platelet Count", 299.0, "Lab Tests", sub)
        )
        "Health Profiles" -> listOf(
            HealthcareService("hp1", "Thyroid Profile", 699.0, "Lab Tests", sub),
            HealthcareService("hp2", "Lipid Profile", 799.0, "Lab Tests", sub),
            HealthcareService("hp3", "Liver Function Test", 899.0, "Lab Tests", sub),
            HealthcareService("hp4", "Kidney Function Test", 899.0, "Lab Tests", sub),
            HealthcareService("hp5", "Full Body Checkup", 1999.0, "Lab Tests", sub)
        )
        "Vitamin Tests" -> listOf(
            HealthcareService("vt1", "Vitamin D Test", 999.0, "Lab Tests", sub),
            HealthcareService("vt2", "Vitamin B12", 899.0, "Lab Tests", sub),
            HealthcareService("vt3", "Iron Profile", 699.0, "Lab Tests", sub),
            HealthcareService("vt4", "Calcium Test", 399.0, "Lab Tests", sub),
            HealthcareService("vt5", "Electrolyte Test", 499.0, "Lab Tests", sub)
        )
        "General Doctors" -> listOf(
            HealthcareService("gd1", "General Physician", 299.0, "Doctors", sub),
            HealthcareService("gd2", "Pediatrician", 399.0, "Doctors", sub),
            HealthcareService("gd3", "Dermatologist", 499.0, "Doctors", sub),
            HealthcareService("gd4", "Gynecologist", 499.0, "Doctors", sub),
            HealthcareService("gd5", "Orthopedic", 499.0, "Doctors", sub)
        )
        "Consultation Modes" -> listOf(
            HealthcareService("cm1", "Online Video Consultation", 199.0, "Doctors", sub),
            HealthcareService("cm2", "Audio Consultation", 149.0, "Doctors", sub),
            HealthcareService("cm3", "Chat Consultation", 99.0, "Doctors", sub),
            HealthcareService("cm4", "Home Visit Doctor", 999.0, "Doctors", sub)
        )
        "Medicine Orders" -> listOf(
            HealthcareService("mo1", "Upload Prescription Order", 0.0, "Pharmacy", sub, true),
            HealthcareService("mo2", "Order Regular Medicines", 500.0, "Pharmacy", sub),
            HealthcareService("mo3", "OTC Medicines", 200.0, "Pharmacy", sub),
            HealthcareService("mo4", "Emergency Medicines", 300.0, "Pharmacy", sub, true)
        )
        "Health Products" -> listOf(
            HealthcareService("hpr1", "Vitamins & Supplements", 450.0, "Pharmacy", sub),
            HealthcareService("hpr2", "Protein Powders", 1200.0, "Pharmacy", sub),
            HealthcareService("hpr3", "Ayurvedic Medicines", 350.0, "Pharmacy", sub),
            HealthcareService("hpr4", "Immunity Boosters", 250.0, "Pharmacy", sub)
        )
        "Personal Care" -> listOf(
            HealthcareService("pc1", "Skin Care Products", 299.0, "Pharmacy", sub),
            HealthcareService("pc2", "Hair Care Products", 399.0, "Pharmacy", sub),
            HealthcareService("pc3", "Baby Care Products", 499.0, "Pharmacy", sub),
            HealthcareService("pc4", "Hygiene Essentials", 150.0, "Pharmacy", sub)
        )
        "Medical Devices" -> listOf(
            HealthcareService("md1", "BP Monitor", 1800.0, "Pharmacy", sub),
            HealthcareService("md2", "Glucometer", 1200.0, "Pharmacy", sub),
            HealthcareService("md3", "Thermometer", 250.0, "Pharmacy", sub),
            HealthcareService("md4", "Pulse Oximeter", 999.0, "Pharmacy", sub)
        )
        "Care Services" -> listOf(
            HealthcareService("cs1", "Nurse at Home", 799.0, "Home Care", sub),
            HealthcareService("cs2", "Physiotherapy at Home", 599.0, "Home Care", sub),
            HealthcareService("cs3", "Elderly Care", 999.0, "Home Care", sub),
            HealthcareService("cs4", "Post Surgery Care", 1499.0, "Home Care", sub)
        )
        "Medical Support" -> listOf(
            HealthcareService("ms1", "Injection at Home", 199.0, "Home Care", sub),
            HealthcareService("ms2", "IV Drip at Home", 499.0, "Home Care", sub),
            HealthcareService("ms3", "Dressing / Wound Care", 299.0, "Home Care", sub)
        )
        "Equipment Rental" -> listOf(
            HealthcareService("er1", "Oxygen Cylinder", 499.0, "Home Care", sub),
            HealthcareService("er2", "Wheelchair", 199.0, "Home Care", sub),
            HealthcareService("er3", "Hospital Bed", 699.0, "Home Care", sub)
        )
        else -> emptyList()
    }
}
