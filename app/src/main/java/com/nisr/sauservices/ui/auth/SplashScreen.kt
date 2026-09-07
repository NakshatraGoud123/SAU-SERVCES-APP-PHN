package com.nisr.sauservices.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nisr.sauservices.R
import com.nisr.sauservices.ui.theme.SAUBackground
import com.nisr.sauservices.ui.theme.SAUPrimary
import com.nisr.sauservices.ui.theme.SAUPrimaryLight
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {

    var showLogo by remember {
        mutableStateOf(false)
    }

    var showBrand by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {

        delay(300)

        showLogo = true

        delay(600)

        showBrand = true

        delay(1500)

        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White,
                        SAUBackground,
                        Color(0xFFEFF5FD)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        // ==================================
        // SOFT BLUE BACKGROUND GLOW
        // ==================================

        Box(
            modifier = Modifier
                .size(360.dp)
                .clip(CircleShape)
                .background(
                    SAUPrimaryLight.copy(
                        alpha = 0.06f
                    )
                )
        )

        // ==================================
        // MAIN CONTENT
        // ==================================

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center
        ) {

            // ==================================
            // LOGO ANIMATION
            // ==================================

            AnimatedVisibility(
                visible = showLogo,

                enter =
                    fadeIn(
                        animationSpec =
                            tween(700)
                    ) +
                            scaleIn(
                                initialScale = 0.65f,

                                animationSpec =
                                    tween(900)
                            )
            ) {

                Box(
                    modifier = Modifier
                        .size(175.dp)
                        .shadow(
                            elevation = 20.dp,

                            shape =
                                RoundedCornerShape(
                                    38.dp
                                ),

                            ambientColor =
                                SAUPrimary.copy(
                                    alpha = 0.12f
                                ),

                            spotColor =
                                SAUPrimary.copy(
                                    alpha = 0.18f
                                )
                        )
                        .clip(
                            RoundedCornerShape(
                                38.dp
                            )
                        )
                        .background(
                            Color.White
                        ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Image(
                        painter =
                            painterResource(
                                id =
                                    R.drawable.sau_logo
                            ),

                        contentDescription =
                            "SAU Solutions Logo",

                        modifier =
                            Modifier.size(145.dp),

                        contentScale =
                            ContentScale.Fit
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(30.dp)
            )

            // ==================================
            // BRAND ANIMATION
            // ==================================

            AnimatedVisibility(
                visible = showBrand,

                enter =
                    fadeIn(
                        animationSpec =
                            tween(600)
                    ) +
                            slideInVertically(
                                initialOffsetY = {
                                    it / 2
                                },

                                animationSpec =
                                    tween(600)
                            )
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "SAU SOLUTIONS",

                        color =
                            SAUPrimary,

                        fontSize = 26.sp,

                        fontWeight =
                            FontWeight.ExtraBold,

                        letterSpacing = 1.5.sp
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "All Services, One App",

                        color =
                            Color(0xFF64748B),

                        fontSize = 14.sp,

                        fontWeight =
                            FontWeight.Medium
                    )
                }
            }
        }

        // ==================================
        // BOTTOM TAGLINE
        // ==================================

        Text(
            text =
                "Trusted • Verified • Reliable",

            color =
                Color(0xFF94A3B8),

            fontSize = 11.sp,

            modifier =
                Modifier
                    .align(
                        Alignment.BottomCenter
                    )
                    .padding(
                        bottom = 35.dp
                    )
        )
    }
}
