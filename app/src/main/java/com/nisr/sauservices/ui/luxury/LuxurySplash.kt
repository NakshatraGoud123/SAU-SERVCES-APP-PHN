package com.nisr.sauservices.ui.luxury

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nisr.sauservices.R
import com.nisr.sauservices.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun LuxurySplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LuxuryBackground),
        contentAlignment = Alignment.Center
    ) {
        // Subtle lifestyle imagery in background
        Image(
            painter = painterResource(id = R.drawable.homescreen_illustration),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().padding(40.dp),
            contentScale = ContentScale.Fit,
            alpha = 0.1f
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "SAU",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Black,
                    fontSize = 72.sp,
                    color = LuxuryGold
                )
            )
            Text(
                text = "SERVICES",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 4.sp,
                    fontWeight = FontWeight.Black,
                    color = LuxuryGold,
                    fontSize = 14.sp
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "All Services, One App",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = LuxuryTextSecondary,
                    letterSpacing = 1.sp
                )
            )
        }

        LinearProgressIndicator(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
                .width(120.dp),
            color = LuxuryGold,
            trackColor = LuxuryGold.copy(alpha = 0.1f)
        )
    }
}
