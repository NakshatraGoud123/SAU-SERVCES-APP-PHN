package com.nisr.sauservices.ui.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import kotlinx.coroutines.delay

private val SAUPink = Color(0xFFFF1E5E)
private val PremiumDark = Color(0xFF0A0A0B)

@Composable
fun LoginSuccessScreen(navController: NavController, userName: String = "Abdul Shahad") {
    val checkScale = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        checkScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PremiumDark),
        contentAlignment = Alignment.Center
    ) {
        // Decorative background particles (simplified confetti)
        Box(modifier = Modifier.fillMaxSize()) {
            repeat(15) {
                Box(
                    modifier = Modifier
                        .offset(x = (10..350).random().dp, y = (10..700).random().dp)
                        .size((4..8).random().dp)
                        .background(listOf(SAUPink, Color.Blue, Color.Yellow).random(), CircleShape)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            // Big Checkmark Circle
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .scale(checkScale.value)
                    .background(SAUPink.copy(alpha = 0.1f), CircleShape)
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(SAUPink, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Login Successful! 🎉",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Welcome back",
                fontSize = 16.sp,
                color = Color.Gray
            )
            
            Text(
                text = userName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = SAUPink,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(100.dp))

            Button(
                onClick = { 
                    navController.navigate(Screen.Home) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SAUPink)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Go to Home", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Rounded.ArrowForward, null, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
