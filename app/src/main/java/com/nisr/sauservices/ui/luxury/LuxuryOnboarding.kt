package com.nisr.sauservices.ui.luxury

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nisr.sauservices.ui.theme.*
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val highlightWord: String,
    val description: String,
    val imageUrl: String
)

private val onboardingPages = listOf(
    OnboardingPage(
        title = "Everything at Your ",
        highlightWord = "Doorstep",
        description = "Premium home repairs, lifestyle services, and verified professionals—all just a tap away.",
        imageUrl = "https://images.unsplash.com/photo-1581578731548-c64695cc6958" // High-end house cleaning/service
    ),
    OnboardingPage(
        title = "Freshly Picked & ",
        highlightWord = "Delivered",
        description = "The finest groceries and gourmet dining from your favorite local boutiques, delivered with care.",
        imageUrl = "https://images.unsplash.com/photo-1542838132-92c53300491e" // Fresh luxury produce
    ),
    OnboardingPage(
        title = "Wellness & Personal ",
        highlightWord = "Care",
        description = "Experience professional spa, beauty, and healthcare services in the comfort of your sanctuary.",
        imageUrl = "https://images.unsplash.com/photo-1544161515-4ab6ce6db874" // High-end spa/massage
    )
)

@Composable
fun LuxuryOnboardingScreen(onFinished: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LuxeBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp)
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "SAU SOLUTIONS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = LuxeAccentSage,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                )
                TextButton(onClick = onFinished) {
                    Text("Skip", color = LuxeTextSecondary, fontWeight = FontWeight.Bold)
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { pageIndex ->
                val page = onboardingPages[pageIndex]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Image Card
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .shadow(20.dp, RoundedCornerShape(32.dp)),
                        shape = RoundedCornerShape(32.dp),
                        color = LuxeCard
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(page.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    // Text Content
                    Text(
                        text = buildAnnotatedString {
                            append(page.title)
                            withStyle(SpanStyle(color = LuxeAccentSage)) {
                                append(page.highlightWord)
                            }
                        },
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                            fontWeight = FontWeight.Black,
                            fontSize = 32.sp,
                            lineHeight = 40.sp,
                            color = LuxeTextPrimary
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = page.description,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = LuxeTextSecondary,
                            lineHeight = 24.sp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Indicators
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(onboardingPages.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .size(if (isSelected) 32.dp else 8.dp, 8.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) LuxeAccentSage else LuxeBorder)
                        )
                    }
                }

                // Next Button
                IconButton(
                    onClick = {
                        if (pagerState.currentPage < onboardingPages.size - 1) {
                            val next = pagerState.currentPage + 1
                            scope.launch { 
                                pagerState.animateScrollToPage(next) 
                            }
                        } else {
                            onFinished()
                        }
                    },
                    modifier = Modifier
                        .size(64.dp)
                        .background(LuxeAccentSage, CircleShape)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}
