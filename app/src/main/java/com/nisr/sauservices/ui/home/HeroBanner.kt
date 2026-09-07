package com.nisr.sauservices.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun HeroBanner(navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = LuxuryCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1.5f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    color = LuxuryGold,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "UP TO 50% OFF",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = LuxuryBackground
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Experience Premium\nLifestyle Services",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = LuxuryTextPrimary,
                    lineHeight = 24.sp
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Use code: SAULUXURY",
                    fontSize = 13.sp,
                    color = LuxuryGold,
                    fontWeight = FontWeight.Bold
                )
            }

            // Illustration on the right
            Image(
                painter = painterResource(id = R.drawable.essential_supplies),
                contentDescription = null,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentScale = ContentScale.Fit
            )
        }
    }
}
