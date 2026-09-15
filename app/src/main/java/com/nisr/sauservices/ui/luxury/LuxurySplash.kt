package com.nisr.sauservices.ui.luxury

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    val opacity by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1500),
        label = "opacity"
    )

    LaunchedEffect(Unit) {
        delay(3000)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LuxeBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.alpha(opacity)
        ) {
            Surface(
                modifier = Modifier
                    .size(140.dp)
                    .scale(scale),
                shape = RoundedCornerShape(36.dp),
                color = LuxeCard,
                shadowElevation = 12.dp
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(28.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.sau_logo),
                        contentDescription = "Logo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "SAU",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Black,
                    fontSize = 68.sp,
                    color = LuxeTextPrimary,
                    letterSpacing = 4.sp
                )
            )
            Text(
                text = "SERVICES",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 8.sp,
                    fontWeight = FontWeight.Black,
                    color = LuxeAccentSage,
                    fontSize = 16.sp
                )
            )
            Spacer(modifier = Modifier.height(56.dp))
            Text(
                text = "Your day, gently arranged.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = LuxeTextSecondary,
                    fontFamily = FontFamily.Serif,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    fontSize = 18.sp
                )
            )
        }

        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
                .size(32.dp),
            color = LuxeAccentSage,
            strokeWidth = 2.dp
        )
    }
}
