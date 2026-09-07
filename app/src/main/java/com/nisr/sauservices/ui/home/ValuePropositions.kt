package com.nisr.sauservices.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.OfflineBolt
import androidx.compose.material.icons.rounded.SupportAgent
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nisr.sauservices.ui.theme.*

@Composable
fun ValuePropositionsRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LuxuryCard, RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ValueItem(Icons.Rounded.Verified, "Verified\nPros")
        ValueItem(Icons.Rounded.OfflineBolt, "Quick\nBooking")
        ValueItem(Icons.Rounded.Lock, "Secure\nPay")
        ValueItem(Icons.Rounded.SupportAgent, "24/7\nHelp")
    }
}

@Composable
fun ValueItem(icon: ImageVector, text: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = LuxuryGold,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = LuxuryTextPrimary,
                lineHeight = 11.sp
            ),
            textAlign = TextAlign.Center,
            maxLines = 2,
            softWrap = true
        )
    }
}
