package com.nisr.sauservices.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.R
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.components.LuxuryButton

@Composable
fun IntroScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LuxuryBackground)
    ) {
        // Luxury cinematic overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(LuxuryGold.copy(alpha = 0.05f), Color.Transparent, LuxuryBackground)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(140.dp),
                shape = RoundedCornerShape(40.dp),
                color = LuxuryCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryBorder)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.sau_logo),
                        contentDescription = "SAU Logo",
                        modifier = Modifier.size(90.dp).graphicsLayer(alpha = 0.9f),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Welcome to SAU",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = LuxuryTextPrimary,
                    fontFamily = FontFamily.Serif
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Discover a premium world of lifestyle and professional services curated just for you.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = LuxuryTextSecondary,
                    lineHeight = 26.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(80.dp))

            LuxuryButton(
                text = "EXPERIENCE NOW",
                onClick = { navController.navigate(Screen.Onboarding) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(
                onClick = { navController.navigate(Screen.Login("customer")) }
            ) {
                Text(
                    text = "Member? Sign In",
                    color = LuxuryGold,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
