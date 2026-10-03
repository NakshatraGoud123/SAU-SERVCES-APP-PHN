package com.nisr.sauservices.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import com.nisr.sauservices.ui.components.NotificationPermissionHandler
import kotlinx.coroutines.delay

@Composable
fun SuccessAnimationScreen(
    title: String = "Order Placed!",
    subtitle: String = "Your luxury experience is on its way.",
    onAnimationFinished: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        isVisible = true
        playSuccessSound(context)
        delay(2500) // Watch the animation for 2.5 seconds
        onAnimationFinished()
    }

    NotificationPermissionHandler()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDFBFA)), // LuxeBackground
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale)
                    .clip(CircleShape),
                color = Color(0xFF96A68F), // LuxeAccentSage
                shadowElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Success",
                        tint = Color.White,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(1000, 500)) + slideInVertically(initialOffsetY = { 50 })
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF423F3D),
                        fontFamily = FontFamily.Serif
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF8D7F77),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 40.dp)
                    )
                }
            }
        }
    }
}

private fun playSuccessSound(context: Context) {
    try {
        val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val r = RingtoneManager.getRingtone(context, notification)
        r.play()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
