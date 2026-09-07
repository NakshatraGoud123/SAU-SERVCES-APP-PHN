package com.nisr.sauservices.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.R
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.SAUBackground
import com.nisr.sauservices.ui.theme.SAUPrimary
import com.nisr.sauservices.ui.theme.SAUText
import com.nisr.sauservices.ui.theme.SAUTextSecondary

data class OnboardingPage(
    val title: String,
    val description: String,
    val image: Int
)

@Composable
fun OnboardingScreen(
    navController: NavController
) {

    var currentPage by remember {
        mutableIntStateOf(0)
    }

    val pages = listOf(

        OnboardingPage(
            title =
                "Everything You Need,\nRight at Your Doorstep",

            description =
                "Find trusted professionals for home,\nlifestyle, repair, delivery and more.",

            image =
                R.drawable.drawable_illustration1
        ),

        OnboardingPage(
            title =
                "Fast & Reliable",

            description =
                "Book services and track your\nrequests in real time.",

            image =
                R.drawable.drawable_illustration2
        ),

        OnboardingPage(
            title =
                "Trusted Professionals",

            description =
                "Every professional is verified so\nyou can book with confidence.",

            image =
                R.drawable.drawable_illustration3
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White,
                        SAUBackground
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 24.dp
                )
        ) {

            // =================================
            // TOP BAR
            // =================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 45.dp
                    ),

                verticalAlignment =
                    Alignment.CenterVertically,

                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                // BACK BUTTON

                if (currentPage > 0) {

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                SAUBackground
                            )
                            .clickable {
                                currentPage--
                            },

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.ArrowBack,

                            contentDescription =
                                "Back",

                            tint =
                                SAUPrimary,

                            modifier =
                                Modifier.size(21.dp)
                        )
                    }

                } else {

                    Spacer(
                        modifier =
                            Modifier.size(42.dp)
                    )
                }

                // LOGO

                Image(
                    painter =
                        painterResource(
                            id =
                                R.drawable.sau_logo
                        ),

                    contentDescription =
                        "SAU Solutions",

                    modifier =
                        Modifier.size(58.dp),

                    contentScale =
                        ContentScale.Fit
                )

                // SKIP

                Text(
                    text = "Skip",

                    color =
                        SAUPrimary,

                    fontSize = 14.sp,

                    fontWeight =
                        FontWeight.SemiBold,

                    modifier =
                        Modifier.clickable {
                            navController.navigate(Screen.Login("customer")) {
                                popUpTo<Screen.Onboarding> { inclusive = true }
                            }
                        }
                )
            }

            // =================================
            // CONTENT
            // =================================

            AnimatedContent(
                targetState = currentPage,

                transitionSpec = {

                    if (targetState > initialState) {

                        slideInHorizontally(
                            initialOffsetX = {
                                it
                            },

                            animationSpec =
                                tween(500)
                        ) +
                                fadeIn(
                                    animationSpec =
                                        tween(400)
                                ) togetherWith

                                slideOutHorizontally(
                                    targetOffsetX = {
                                        -it
                                    },

                                    animationSpec =
                                        tween(500)
                                ) +
                                fadeOut(
                                    animationSpec =
                                        tween(300)
                                )

                    } else {

                        slideInHorizontally(
                            initialOffsetX = {
                                -it
                            },

                            animationSpec =
                                tween(500)
                        ) +
                                fadeIn(
                                    animationSpec =
                                        tween(400)
                                ) togetherWith

                                slideOutHorizontally(
                                    targetOffsetX = {
                                        it
                                    },

                                    animationSpec =
                                        tween(500)
                                ) +
                                fadeOut(
                                    animationSpec =
                                        tween(300)
                                )
                    }
                },

                label = "Onboarding Animation",

                modifier =
                    Modifier.weight(1f)
            ) {

                    pageIndex ->

                val page =
                    pages[pageIndex]

                Column(
                    modifier =
                        Modifier.fillMaxSize(),

                    horizontalAlignment =
                        Alignment.CenterHorizontally,

                    verticalArrangement =
                        Arrangement.Center
                ) {

                    // =================================
                    // IMAGE
                    // =================================

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(330.dp)
                            .shadow(
                                elevation = 8.dp,

                                shape =
                                    RoundedCornerShape(
                                        30.dp
                                    ),

                                ambientColor =
                                    SAUPrimary.copy(
                                        alpha = 0.07f
                                    ),

                                spotColor =
                                    SAUPrimary.copy(
                                        alpha = 0.10f
                                    )
                            )
                            .clip(
                                RoundedCornerShape(
                                    30.dp
                                )
                            )
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFF2F7FD),
                                        Color(0xFFE8F1FB)
                                    )
                                )
                            ),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Image(
                            painter =
                                painterResource(
                                    id =
                                        page.image
                                ),

                            contentDescription =
                                page.title,

                            modifier =
                                Modifier.fillMaxSize(),

                            contentScale =
                                ContentScale.Crop
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.height(30.dp)
                    )

                    // =================================
                    // TITLE
                    // =================================

                    Text(
                        text =
                            page.title,

                        color =
                            SAUText,

                        fontSize = 25.sp,

                        fontWeight =
                            FontWeight.Bold,

                        textAlign =
                            TextAlign.Center,

                        lineHeight = 32.sp
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    // =================================
                    // DESCRIPTION
                    // =================================

                    Text(
                        text =
                            page.description,

                        color =
                            SAUTextSecondary,

                        fontSize = 14.sp,

                        lineHeight = 22.sp,

                        textAlign =
                            TextAlign.Center
                    )
                }
            }

            // =================================
            // BOTTOM NAVIGATION
            // =================================

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = 30.dp
                        ),

                verticalAlignment =
                    Alignment.CenterVertically,

                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                // =================================
                // PAGE INDICATORS
                // =================================

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    repeat(3) { index ->

                        Box(
                            modifier =
                                Modifier
                                    .height(8.dp)
                                    .width(
                                        if (
                                            index ==
                                            currentPage
                                        ) {
                                            26.dp
                                        } else {
                                            8.dp
                                        }
                                    )
                                    .clip(
                                        RoundedCornerShape(
                                            10.dp
                                        )
                                    )
                                    .background(
                                        if (
                                            index ==
                                            currentPage
                                        ) {
                                            SAUPrimary
                                        } else {
                                            SAUPrimary.copy(
                                                alpha =
                                                    0.20f
                                            )
                                        }
                                    )
                        )

                        if (index < 2) {

                            Spacer(
                                modifier =
                                    Modifier.width(6.dp)
                            )
                        }
                    }
                }

                // =================================
                // NEXT BUTTON
                // =================================

                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .shadow(
                            elevation = 8.dp,

                            shape =
                                CircleShape,

                            ambientColor =
                                SAUPrimary.copy(
                                    alpha = 0.15f
                                ),

                            spotColor =
                                SAUPrimary.copy(
                                    alpha = 0.20f
                                )
                        )
                        .clip(CircleShape)
                        .background(
                            SAUPrimary
                        )
                        .clickable {

                            if (currentPage < 2) {

                                currentPage++

                            } else {
                                navController.navigate(Screen.Login("customer")) {
                                    popUpTo<Screen.Onboarding> { inclusive = true }
                                }
                            }
                        },

                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.ArrowForward,

                        contentDescription =
                            "Next",

                        tint =
                            Color.White,

                        modifier =
                            Modifier.size(25.dp)
                    )
                }
            }
        }
    }
}
