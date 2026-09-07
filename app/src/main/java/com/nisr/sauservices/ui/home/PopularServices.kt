package com.nisr.sauservices.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.R
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.theme.PrimaryBlue

data class PopularService(
    val id: String,
    val name: String,
    val imageRes: Int,
    val price: String,
    val rating: String,
    val categoryId: String = "ac_repair",
    val subcategoryId: String = "ac_service"
)

@Composable
fun PopularServicesSection(navController: NavController) {
    val list = listOf(
        PopularService("ac1", "AC Repair", R.drawable.ac_repair, "₹499", "4.6", "ac_repair", "ac_service"),
        PopularService("hc2", "Bathroom Cleaning", R.drawable.bathroom_cleaning, "₹399", "4.8", "home_cleaning", "clean_room")
    )

    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            "Popular Near You",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = LuxuryTextPrimary
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 0.dp)
        ) {
            items(list) { item ->
                Card(
                    modifier = Modifier
                        .width(280.dp)
                        .shadow(2.dp, RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        Image(
                            painter = painterResource(item.imageRes),
                            contentDescription = item.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)),
                            contentScale = ContentScale.Crop
                        )
                        
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    item.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Black
                                    )
                                )
                                Text(
                                    item.price,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        color = PrimaryBlue
                                    )
                                )
                            }
                            
                            Spacer(Modifier.height(10.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Star,
                                        null,
                                        tint = Color(0xFFFFB300),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        item.rating,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = GrayText
                                        )
                                    )
                                }
                                
                                Button(
                                    onClick = {
                                        navController.navigate(Screen.ResidentialServices(item.categoryId, item.subcategoryId))
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.height(34.dp).width(96.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Book Now", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
