package com.nisr.sauservices.ui.education

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen

private val EduPrimary = Color(0xFF1565C0)
private val EduAccent = Color(0xFFE3F2FD)

@Composable
fun EducationSuccessScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(EduAccent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = EduPrimary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "🎉 Tutor Booked Successfully",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Details:",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = EduAccent)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("• Tutor will contact you shortly", fontSize = 14.sp)
                Text("• Course start date: Within 48 hours", fontSize = 14.sp)
                Text("• Order ID: EDU${System.currentTimeMillis().toString().takeLast(6)}", fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                navController.navigate(Screen.Home) {
                    popUpTo<Screen.Home> { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EduPrimary)
        ) {
            Text("Back to Home", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}
