package com.nisr.sauservices.ui.luxury

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nisr.sauservices.R
import com.nisr.sauservices.ui.components.CinematicHeading
import com.nisr.sauservices.ui.theme.*

@Composable
fun LuxuryOnboardingScreen1(onNext: () -> Unit, onSkip: () -> Unit) {
    OnboardingTemplate(
        imageRes = R.drawable.homescreen_illustration,
        heading = buildAnnotatedString {
            append("Everything You Need,\nRight at Your ")
            withStyle(SpanStyle(color = LuxuryGold)) { append("Doorstep") }
        },
        description = "Find trusted professionals for home,\nlifestyle, repair, delivery and more.",
        pageIndex = 0,
        onNext = onNext,
        onSkip = onSkip
    )
}

@Composable
fun LuxuryOnboardingScreen2(onNext: () -> Unit, onSkip: () -> Unit) {
    OnboardingTemplate(
        imageRes = R.drawable.homescreen_illustration,
        heading = buildAnnotatedString { append("Fast & Reliable") },
        description = "Book services and track your\nrequests in real time.",
        pageIndex = 1,
        onNext = onNext,
        onSkip = onSkip
    )
}

@Composable
fun LuxuryOnboardingScreen3(onNext: () -> Unit, onSkip: () -> Unit) {
    OnboardingTemplate(
        imageRes = R.drawable.homescreen_illustration,
        heading = buildAnnotatedString { append("Trusted Professionals") },
        description = "We verify every professional\nso you get the best service.",
        pageIndex = 2,
        onNext = onNext,
        onSkip = onSkip
    )
}

@Composable
private fun OnboardingTemplate(
    imageRes: Int,
    heading: CharSequence,
    description: String,
    pageIndex: Int,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LuxuryBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "SAU SOLUTIONS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = LuxuryGold,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                )
                TextButton(onClick = onSkip) {
                    Text("Skip", color = LuxuryGold, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier.size(280.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.weight(1f))

            if (heading is String) {
                CinematicHeading(text = heading)
            } else {
                Text(
                    text = heading as androidx.compose.ui.text.AnnotatedString,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                        fontWeight = FontWeight.Black,
                        fontSize = 32.sp,
                        lineHeight = 40.sp
                    ),
                    color = LuxuryTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                color = LuxuryTextSecondary
            )

            Spacer(modifier = Modifier.height(48.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .size(if (index == pageIndex) 24.dp else 8.dp, 8.dp)
                                .background(
                                    color = if (index == pageIndex) LuxuryGold else LuxuryGold.copy(alpha = 0.2f),
                                    shape = CircleShape
                                )
                        )
                    }
                }

                IconButton(
                    onClick = onNext,
                    modifier = Modifier
                        .size(56.dp)
                        .background(LuxuryGold, CircleShape)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next",
                        tint = LuxuryBackground
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
