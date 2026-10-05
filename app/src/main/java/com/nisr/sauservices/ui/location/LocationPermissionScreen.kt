package com.nisr.sauservices.ui.location

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.components.*
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.viewmodel.LocationViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

/**
 * Premium Location Permission screen for SAU Solutions.
 * Features a custom radar-pulse illustration and staggered content animations.
 */
@Composable
fun LocationPermissionScreen(
    navController: NavController,
    viewModel: LocationViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState
    val lifecycleOwner = LocalLifecycleOwner.current
    var visible by remember { mutableStateOf(value = false) }

    // Re-check GPS status when user returns to app
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkGpsStatus(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Auto-navigate to Home if both Permission and GPS are active
    LaunchedEffect(uiState.isGpsEnabled) {
        if (uiState.isGpsEnabled) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context, 
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            
            if (hasPermission) {
                navController.navigate(Screen.Home) {
                    popUpTo<Screen.LocationPermission> { inclusive = true }
                }
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        val isGranted = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, defaultValue = false) ||
                        permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, defaultValue = false)
        
        if (isGranted) {
            viewModel.checkGpsStatus(context)
            if (viewModel.uiState.isGpsEnabled) {
                navController.navigate(Screen.Home) {
                    popUpTo<Screen.LocationPermission> { inclusive = true }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(300.milliseconds)
        visible = true
        viewModel.checkGpsStatus(context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LuxuryBackground)
            .systemBarsPadding(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 1. Premium Illustration with Interactive Radar Pulse
            LocationIllustration(visible = visible)

            Spacer(modifier = Modifier.height(48.dp))

            // 2. Trust-Building Explanation (Slide + Fade)
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically { 40 } + fadeIn(animationSpec = tween(800)),
                label = "content_entrance"
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (!uiState.isGpsEnabled) "Enable Location Services" else "Location Permission",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = LuxuryTextPrimary,
                            textAlign = TextAlign.Center,
                            letterSpacing = (-0.5).sp
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = if (!uiState.isGpsEnabled) {
                            "Location Services are turned off. Please turn them on to discover expert service partners nearby."
                        } else {
                            "Allow SAU Solutions to access your location to discover expert service partners nearby and provide real-time tracking."
                        },
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = LuxuryTextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 26.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(64.dp))

            // 3. Primary CTA Button (Staggered Entrance)
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically { 80 } + fadeIn(animationSpec = tween(800, delayMillis = 200)),
                label = "button_entrance"
            ) {
                if (!uiState.isGpsEnabled) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Location Services are turned off",
                            color = ErrorRed,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        LuxuryButton(
                            text = "Turn On Location",
                            onClick = { 
                                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                            }
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        TextButton(onClick = { navController.navigate(Screen.ManualLocation) }) {
                            Text(
                                "Continue Manually",
                                color = LuxuryGold,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        LuxuryButton(
                            text = "Allow Location Access",
                            onClick = { 
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        TextButton(onClick = { navController.navigate(Screen.ManualLocation) }) {
                            Text(
                                "Continue Manually",
                                color = LuxuryGold,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationIllustration(visible: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar_transition")
    
    // Pulse expansion
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 3.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_scale"
    )
    
    // Pulse fading
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_alpha"
    )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(240.dp)) {
        // Radar Waves (Drawn on Canvas for smooth performance)
        Canvas(modifier = Modifier.size(80.dp)) {
            drawCircle(
                color = LuxuryGold,
                radius = (size.minDimension / 2) * scale,
                alpha = alpha,
                style = Stroke(width = 3.dp.toPx())
            )
        }
        
        // Inner Halo
        Box(
            modifier = Modifier
                .size(140.dp)
                .background(LuxuryGold.copy(alpha = 0.08f), CircleShape)
        )

        // Animated Pin with Bouncy Entrance and Idle Hover
        AnimatedVisibility(
            visible = visible,
            enter = scaleIn(animationSpec = spring(Spring.DampingRatioMediumBouncy)) + fadeIn()
        ) {
            val hoverOffset by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = -20f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = EaseInOutSine),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pin_hover"
            )

            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = LuxuryGold,
                modifier = Modifier
                    .size(96.dp)
                    .offset(y = hoverOffset.dp)
            )
        }
    }
}
