package com.nisr.sauservices.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.EditCalendar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.theme.PrimaryBlue

data class HowItWorksItem(val name: String, val icon: ImageVector)

@Composable
fun HowItWorks() {

    val list = listOf(
        HowItWorksItem("Choose\nService", Icons.Outlined.EditCalendar),
        HowItWorksItem("Select Date\n& Time", Icons.Outlined.CalendarMonth),
        HowItWorksItem("Get Pro\nat Home", Icons.Outlined.CheckCircle)
    )

    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            "How It Works",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = LuxuryTextPrimary
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            list.forEach { item ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            item.icon, 
                            null, 
                            tint = PrimaryBlue, 
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                    
                    Text(
                        item.name, 
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp, 
                            fontWeight = FontWeight.Medium,
                            color = GrayText
                        ),
                        textAlign = TextAlign.Center,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}
