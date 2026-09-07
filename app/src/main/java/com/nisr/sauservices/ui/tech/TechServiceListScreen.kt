package com.nisr.sauservices.ui.tech

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.PinkPrimary
import com.nisr.sauservices.ui.theme.LightPink
import com.nisr.sauservices.ui.viewmodel.TechServicesViewModel
import java.net.URLDecoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechServiceListScreen(navController: NavController, subcategory: String, viewModel: TechServicesViewModel) {
    val decodedSub = URLDecoder.decode(subcategory, "UTF-8")
    val services = getTechServicesForSubcategory(decodedSub)
    val cartItems = viewModel.cartItems

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(decodedSub, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    BadgedBox(
                        badge = {
                            if (cartItems.isNotEmpty()) {
                                Badge(containerColor = PinkPrimary) {
                                    Text(cartItems.sumOf { it.quantity }.toString(), color = Color.White)
                                }
                            }
                        },
                        modifier = Modifier.padding(end = 16.dp).clickable { navController.navigate(Screen.Cart) }
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp,
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).navigationBarsPadding(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            val count = cartItems.sumOf { it.quantity }
                            Text("$count items added", fontSize = 14.sp, color = Color.Gray)
                            Text("₹${viewModel.getTotalPrice().toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PinkPrimary)
                        }
                        Button(
                            onClick = { navController.navigate(Screen.Cart) },
                            colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("View Cart", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFFF7F7F7)
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(services) { service ->
                TechServiceCard(service, viewModel)
            }
        }
    }
}

@Composable
fun TechServiceCard(service: TechServiceItem, viewModel: TechServicesViewModel) {
    val cartItem = viewModel.cartItems.find { it.id == service.id }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = service.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = "₹${service.price.toInt()}", fontWeight = FontWeight.Bold, color = PinkPrimary, fontSize = 14.sp)
            }

            if (cartItem == null) {
                Button(
                    onClick = { viewModel.addToCart(service) },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("ADD", fontWeight = FontWeight.Bold)
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(LightPink)
                ) {
                    IconButton(onClick = { viewModel.decreaseQty(service.id) }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Remove, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(18.dp))
                    }
                    Text(
                        text = cartItem.quantity.toString(),
                        fontWeight = FontWeight.Bold,
                        color = PinkPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    IconButton(onClick = { viewModel.increaseQty(service.id) }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

fun getTechServicesForSubcategory(sub: String): List<TechServiceItem> {
    return when (sub) {
        "Screen Issues" -> listOf(
            TechServiceItem("m1", "Screen Replacement", 2500.0, "Mobile Repair", "Screen Issues"),
            TechServiceItem("m2", "Touch Not Working", 1500.0, "Mobile Repair", "Screen Issues"),
            TechServiceItem("m3", "Display Flickering", 1800.0, "Mobile Repair", "Screen Issues")
        )
        "Battery Issues" -> listOf(
            TechServiceItem("m4", "Battery Replacement", 1200.0, "Mobile Repair", "Battery Issues"),
            TechServiceItem("m5", "Battery Draining Fast", 800.0, "Mobile Repair", "Battery Issues")
        )
        "Charging Issues" -> listOf(
            TechServiceItem("m6", "Charging Port Repair", 900.0, "Mobile Repair", "Charging Issues"),
            TechServiceItem("m7", "Charging IC Repair", 1500.0, "Mobile Repair", "Charging Issues")
        )
        "Software Issues" -> listOf(
            TechServiceItem("m8", "Software Update", 500.0, "Mobile Repair", "Software Issues"),
            TechServiceItem("m9", "Virus Removal", 700.0, "Mobile Repair", "Software Issues"),
            TechServiceItem("m10", "Data Recovery", 1500.0, "Mobile Repair", "Software Issues"),
            TechServiceItem("l4", "OS Installation", 800.0, "Laptop & Computer Repair", "Software Issues"),
            TechServiceItem("l5", "Driver Installation", 500.0, "Laptop & Computer Repair", "Software Issues"),
            TechServiceItem("l6", "Virus Removal", 700.0, "Laptop & Computer Repair", "Software Issues")
        )
        "Hardware Issues" -> listOf(
            TechServiceItem("l1", "Motherboard Repair", 4000.0, "Laptop & Computer Repair", "Hardware Issues"),
            TechServiceItem("l2", "RAM Upgrade", 2000.0, "Laptop & Computer Repair", "Hardware Issues"),
            TechServiceItem("l3", "Hard Disk Replacement", 3000.0, "Laptop & Computer Repair", "Hardware Issues")
        )
        "Performance" -> listOf(
            TechServiceItem("l7", "Laptop Slow Fix", 1000.0, "Laptop & Computer Repair", "Performance"),
            TechServiceItem("l8", "System Optimization", 1200.0, "Laptop & Computer Repair", "Performance")
        )
        "Accessories" -> listOf(
            TechServiceItem("l9", "Keyboard Replacement", 1500.0, "Laptop & Computer Repair", "Accessories"),
            TechServiceItem("l10", "Screen Replacement", 5000.0, "Laptop & Computer Repair", "Accessories")
        )
        "TV Repair" -> listOf(
            TechServiceItem("h1", "LED TV Repair", 2000.0, "TV & Home Appliance Repair", "TV Repair"),
            TechServiceItem("h2", "Smart TV Software Fix", 1500.0, "TV & Home Appliance Repair", "TV Repair")
        )
        "Refrigerator" -> listOf(
            TechServiceItem("h3", "Cooling Issue", 2500.0, "TV & Home Appliance Repair", "Refrigerator"),
            TechServiceItem("h4", "Gas Refill", 2000.0, "TV & Home Appliance Repair", "Refrigerator")
        )
        "Washing Machine" -> listOf(
            TechServiceItem("h5", "Motor Repair", 1800.0, "TV & Home Appliance Repair", "Washing Machine"),
            TechServiceItem("h6", "Drainage Issue", 1200.0, "TV & Home Appliance Repair", "Washing Machine")
        )
        "AC Repair" -> listOf(
            TechServiceItem("h7", "AC Gas Filling", 3000.0, "TV & Home Appliance Repair", "AC Repair"),
            TechServiceItem("h8", "AC General Service", 800.0, "TV & Home Appliance Repair", "AC Repair")
        )
        "New Setup" -> listOf(
            TechServiceItem("i1", "New WiFi Installation", 1500.0, "Internet & WiFi Setup", "New Setup"),
            TechServiceItem("i2", "Router Setup", 800.0, "Internet & WiFi Setup", "New Setup")
        )
        "Repair" -> listOf(
            TechServiceItem("i3", "Slow Internet Fix", 600.0, "Internet & WiFi Setup", "Repair"),
            TechServiceItem("i4", "Router Troubleshooting", 500.0, "Internet & WiFi Setup", "Repair")
        )
        "Upgrades" -> listOf(
            TechServiceItem("i5", "Network Upgrade", 2000.0, "Internet & WiFi Setup", "Upgrades"),
            TechServiceItem("i6", "Range Extender Setup", 1000.0, "Internet & WiFi Setup", "Upgrades"),
            TechServiceItem("c5", "Night Vision Upgrade", 3000.0, "CCTV & Security Systems", "Upgrades"),
            TechServiceItem("c6", "Cloud Storage Setup", 2000.0, "CCTV & Security Systems", "Upgrades")
        )
        "CCTV Installation" -> listOf(
            TechServiceItem("c1", "2 Camera Setup", 5000.0, "CCTV & Security Systems", "CCTV Installation"),
            TechServiceItem("c2", "4 Camera Setup", 9000.0, "CCTV & Security Systems", "CCTV Installation"),
            TechServiceItem("c3", "8 Camera Setup", 15000.0, "CCTV & Security Systems", "CCTV Installation")
        )
        "Maintenance" -> listOf(
            TechServiceItem("c4", "Camera Repair", 1500.0, "CCTV & Security Systems", "Maintenance"),
            TechServiceItem("c5", "DVR Repair", 2500.0, "CCTV & Security Systems", "Maintenance")
        )
        "Smart Devices" -> listOf(
            TechServiceItem("s1", "Smart Lights Setup", 1500.0, "Smart Home Installation", "Smart Devices"),
            TechServiceItem("s2", "Smart Switch Installation", 2000.0, "Smart Home Installation", "Smart Devices")
        )
        "Security" -> listOf(
            TechServiceItem("s3", "Smart Door Lock", 4000.0, "Smart Home Installation", "Security"),
            TechServiceItem("s4", "Video Doorbell", 3500.0, "Smart Home Installation", "Security")
        )
        "Automation" -> listOf(
            TechServiceItem("s5", "Full Home Automation", 20000.0, "Smart Home Installation", "Automation"),
            TechServiceItem("s6", "Voice Assistant Setup", 2500.0, "Smart Home Installation", "Automation")
        )
        else -> emptyList()
    }
}
