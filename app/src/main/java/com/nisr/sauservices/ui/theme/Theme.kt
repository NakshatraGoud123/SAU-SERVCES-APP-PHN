package com.nisr.sauservices.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LuxeColorScheme = lightColorScheme(
    primary = LuxeAccentSage,
    onPrimary = Color.White,
    primaryContainer = LuxeHighlightChampagne,
    onPrimaryContainer = LuxeTextPrimary,
    secondary = LuxeGold,
    onSecondary = Color.White,
    background = LuxeBackground,
    onBackground = LuxeTextPrimary,
    surface = LuxeCard,
    onSurface = LuxeTextPrimary,
    outline = LuxeBorder,
    surfaceVariant = LuxeHighlightChampagne.copy(alpha = 0.3f),
    onSurfaceVariant = LuxeTextSecondary,
    error = ErrorRed,
    onError = Color.White
)

private val LuxeDarkColorScheme = darkColorScheme(
    primary = LuxeAccentSage,
    onPrimary = Color.White,
    primaryContainer = LuxuryBorder,
    onPrimaryContainer = LuxuryTextPrimary,
    secondary = LuxuryGold,
    onSecondary = Color.Black,
    background = LuxuryBackground,
    onBackground = LuxuryTextPrimary,
    surface = LuxuryCard,
    onSurface = LuxuryTextPrimary,
    outline = LuxuryBorder,
    surfaceVariant = LuxuryBorder.copy(alpha = 0.5f),
    onSurfaceVariant = LuxuryTextSecondary,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // For SAU, we prioritize the Luxe Light theme as the signature brand look
    val colorScheme = if (darkTheme) LuxeDarkColorScheme else LuxeColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Adjust system bars based on theme
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
            
            // Ensure status bar is transparent
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
