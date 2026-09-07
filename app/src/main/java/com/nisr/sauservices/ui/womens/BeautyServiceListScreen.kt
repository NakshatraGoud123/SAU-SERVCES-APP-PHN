package com.nisr.sauservices.ui.womens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.nisr.sauservices.ui.theme.PinkPrimary
import com.nisr.sauservices.ui.viewmodel.BeautyService
import com.nisr.sauservices.ui.viewmodel.WomensBeautyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeautyServiceListScreen(navController: NavController, subcategory: String, viewModel: WomensBeautyViewModel) {
    val services = getServicesForSubcategory(subcategory)
    val cartItems = viewModel.cartItems

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(subcategory, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    BadgedBox(
                        badge = { 
                            if (cartItems.isNotEmpty()) {
                                Badge(containerColor = PinkPrimary) { Text(cartItems.size.toString(), color = Color.White) }
                            }
                        },
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable { navController.navigate(Screen.Cart) }
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
                            Text("$count items", fontSize = 14.sp, color = Color.Gray)
                            Text("₹${viewModel.calculateTotal().toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PinkPrimary)
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
        containerColor = Color(0xFFFFFBFD)
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(services) { service ->
                val cartItem = cartItems.find { it.id == service.id }
                BeautyServiceCard(
                    service = service,
                    quantity = cartItem?.quantity ?: 0,
                    onAdd = { viewModel.addToCart(service) },
                    onIncrease = { viewModel.updateQty(service.id, true) },
                    onDecrease = { viewModel.updateQty(service.id, false) }
                )
            }
        }
    }
}

@Composable
fun BeautyServiceCard(
    service: BeautyService,
    quantity: Int,
    onAdd: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
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
                Text(service.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text("₹${service.price.toInt()}", fontWeight = FontWeight.ExtraBold, color = PinkPrimary, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("${service.durationMinutes} mins", fontSize = 12.sp, color = Color.Gray)
            }

            if (quantity == 0) {
                OutlinedButton(
                    onClick = onAdd,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PinkPrimary),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PinkPrimary)
                ) {
                    Text("ADD", fontWeight = FontWeight.Bold)
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.background(PinkPrimary.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                ) {
                    IconButton(onClick = onDecrease) {
                        Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PinkPrimary)
                    }
                    Text(quantity.toString(), fontWeight = FontWeight.Bold, color = PinkPrimary, modifier = Modifier.padding(horizontal = 4.dp))
                    IconButton(onClick = onIncrease) {
                        Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PinkPrimary)
                    }
                }
            }
        }
    }
}

fun getServicesForSubcategory(sub: String): List<BeautyService> {
    return when (sub) {
        "Hair Cut & Styling" -> listOf(
            BeautyService("hcs1", "Basic Haircut", 199.0, "Hair Services", sub),
            BeautyService("hcs2", "Advanced Haircut", 299.0, "Hair Services", sub),
            BeautyService("hcs3", "Layer Cut", 399.0, "Hair Services", sub),
            BeautyService("hcs4", "Straight Cut", 249.0, "Hair Services", sub),
            BeautyService("hcs5", "Hair Styling", 349.0, "Hair Services", sub),
            BeautyService("hcs6", "Blow Dry", 299.0, "Hair Services", sub)
        )
        "Hair Treatments" -> listOf(
            BeautyService("ht1", "Hair Spa", 799.0, "Hair Services", sub),
            BeautyService("ht2", "Hair Smoothening", 2499.0, "Hair Services", sub),
            BeautyService("ht3", "Hair Straightening", 2999.0, "Hair Services", sub),
            BeautyService("ht4", "Hair Botox", 1999.0, "Hair Services", sub),
            BeautyService("ht5", "Keratin Treatment", 2999.0, "Hair Services", sub),
            BeautyService("ht6", "Dandruff Treatment", 599.0, "Hair Services", sub)
        )
        "Hair Coloring" -> listOf(
            BeautyService("hc1", "Root Touch-up", 699.0, "Hair Services", sub),
            BeautyService("hc2", "Global Hair Color", 1999.0, "Hair Services", sub),
            BeautyService("hc3", "Highlights", 2499.0, "Hair Services", sub),
            BeautyService("hc4", "Balayage", 3999.0, "Hair Services", sub),
            BeautyService("hc5", "Fashion Color", 2999.0, "Hair Services", sub)
        )
        "Facials" -> listOf(
            BeautyService("f1", "Fruit Facial", 699.0, "Facial & Cleanup", sub),
            BeautyService("f2", "Gold Facial", 999.0, "Facial & Cleanup", sub),
            BeautyService("f3", "Diamond Facial", 1299.0, "Facial & Cleanup", sub),
            BeautyService("f4", "Oxy Facial", 1499.0, "Facial & Cleanup", sub),
            BeautyService("f5", "Anti-Aging Facial", 1999.0, "Facial & Cleanup", sub),
            BeautyService("f6", "Brightening Facial", 1199.0, "Facial & Cleanup", sub)
        )
        "Cleanup" -> listOf(
            BeautyService("cl1", "Basic Cleanup", 399.0, "Facial & Cleanup", sub),
            BeautyService("cl2", "Detan Cleanup", 499.0, "Facial & Cleanup", sub),
            BeautyService("cl3", "Acne Cleanup", 599.0, "Facial & Cleanup", sub),
            BeautyService("cl4", "Hydrating Cleanup", 699.0, "Facial & Cleanup", sub),
            BeautyService("cl5", "Whitening Cleanup", 799.0, "Facial & Cleanup", sub)
        )
        "Body Wax" -> listOf(
            BeautyService("bw1", "Full Arms", 299.0, "Waxing", sub),
            BeautyService("bw2", "Full Legs", 499.0, "Waxing", sub),
            BeautyService("bw3", "Full Body", 1299.0, "Waxing", sub),
            BeautyService("bw4", "Half Arms", 199.0, "Waxing", sub),
            BeautyService("bw5", "Half Legs", 299.0, "Waxing", sub)
        )
        "Special Wax" -> listOf(
            BeautyService("sw1", "Chocolate Wax", 699.0, "Waxing", sub),
            BeautyService("sw2", "Rica Wax", 899.0, "Waxing", sub),
            BeautyService("sw3", "Honey Wax", 499.0, "Waxing", sub),
            BeautyService("sw4", "Hard Wax", 799.0, "Waxing", sub),
            BeautyService("sw5", "Bikini Wax", 999.0, "Waxing", sub)
        )
        "Threading" -> listOf(
            BeautyService("th1", "Eyebrow", 49.0, "Threading & Face", sub),
            BeautyService("th2", "Upper Lip", 29.0, "Threading & Face", sub),
            BeautyService("th3", "Forehead", 39.0, "Threading & Face", sub),
            BeautyService("th4", "Chin", 39.0, "Threading & Face", sub),
            BeautyService("th5", "Full Face Threading", 199.0, "Threading & Face", sub)
        )
        "Face Services" -> listOf(
            BeautyService("fs1", "Bleach", 199.0, "Threading & Face", sub),
            BeautyService("fs2", "Detan", 249.0, "Threading & Face", sub),
            BeautyService("fs3", "Face Polish", 399.0, "Threading & Face", sub),
            BeautyService("fs4", "Face Massage", 299.0, "Threading & Face", sub),
            BeautyService("fs5", "Face Mask", 199.0, "Threading & Face", sub)
        )
        "Manicure" -> listOf(
            BeautyService("m1", "Basic Manicure", 299.0, "Manicure & Pedicure", sub),
            BeautyService("m2", "Spa Manicure", 499.0, "Manicure & Pedicure", sub),
            BeautyService("m3", "Gel Manicure", 699.0, "Manicure & Pedicure", sub),
            BeautyService("m4", "French Manicure", 599.0, "Manicure & Pedicure", sub),
            BeautyService("m5", "Nail Art", 399.0, "Manicure & Pedicure", sub)
        )
        "Pedicure" -> listOf(
            BeautyService("p1", "Basic Pedicure", 399.0, "Manicure & Pedicure", sub),
            BeautyService("p2", "Spa Pedicure", 699.0, "Manicure & Pedicure", sub),
            BeautyService("p3", "Gel Pedicure", 899.0, "Manicure & Pedicure", sub),
            BeautyService("p4", "Foot Massage", 299.0, "Manicure & Pedicure", sub),
            BeautyService("p5", "Heel Repair", 399.0, "Manicure & Pedicure", sub)
        )
        "Makeup" -> listOf(
            BeautyService("mk1", "Party Makeup", 1999.0, "Makeup & Styling", sub),
            BeautyService("mk2", "HD Makeup", 2999.0, "Makeup & Styling", sub),
            BeautyService("mk3", "Airbrush Makeup", 4999.0, "Makeup & Styling", sub),
            BeautyService("mk4", "Engagement Makeup", 5999.0, "Makeup & Styling", sub),
            BeautyService("mk5", "Reception Makeup", 6999.0, "Makeup & Styling", sub)
        )
        "Hair Styling" -> listOf(
            BeautyService("hs1", "Curl Styling", 499.0, "Makeup & Styling", sub),
            BeautyService("hs2", "Straight Styling", 399.0, "Makeup & Styling", sub),
            BeautyService("hs3", "Bridal Hair", 1499.0, "Makeup & Styling", sub),
            BeautyService("hs4", "Bun Styling", 699.0, "Makeup & Styling", sub),
            BeautyService("hs5", "Ironing", 299.0, "Makeup & Styling", sub)
        )
        "Massage" -> listOf(
            BeautyService("ms1", "Full Body Massage", 1499.0, "Spa & Massage", sub),
            BeautyService("ms2", "Back Massage", 799.0, "Spa & Massage", sub),
            BeautyService("ms3", "Head Massage", 399.0, "Spa & Massage", sub),
            BeautyService("ms4", "Foot Massage", 499.0, "Spa & Massage", sub),
            BeautyService("ms5", "Aroma Massage", 1999.0, "Spa & Massage", sub)
        )
        "Spa" -> listOf(
            BeautyService("sp1", "Body Spa", 2499.0, "Spa & Massage", sub),
            BeautyService("sp2", "Detox Spa", 1999.0, "Spa & Massage", sub),
            BeautyService("sp3", "Relax Spa", 1799.0, "Spa & Massage", sub),
            BeautyService("sp4", "Oil Therapy", 1499.0, "Spa & Massage", sub),
            BeautyService("sp5", "Steam Therapy", 799.0, "Spa & Massage", sub)
        )
        "Bridal" -> listOf(
            BeautyService("br1", "Bridal Makeup", 9999.0, "Bridal & Premium", sub),
            BeautyService("br2", "Pre-Bridal Package", 6999.0, "Bridal & Premium", sub),
            BeautyService("br3", "Bridal Hair Styling", 2999.0, "Bridal & Premium", sub),
            BeautyService("br4", "Saree Draping", 999.0, "Bridal & Premium", sub),
            BeautyService("br5", "Bridal Facial", 2499.0, "Bridal & Premium", sub)
        )
        "Premium Packages" -> listOf(
            BeautyService("pp1", "Glow Package", 1999.0, "Bridal & Premium", sub),
            BeautyService("pp2", "Skin Care Package", 2999.0, "Bridal & Premium", sub),
            BeautyService("pp3", "Hair Care Package", 2499.0, "Bridal & Premium", sub),
            BeautyService("pp4", "Luxury Spa Package", 3999.0, "Bridal & Premium", sub),
            BeautyService("pp5", "Full Beauty Package", 4999.0, "Bridal & Premium", sub)
        )
        else -> emptyList()
    }
}
