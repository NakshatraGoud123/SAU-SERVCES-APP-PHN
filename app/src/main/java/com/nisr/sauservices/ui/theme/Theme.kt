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
    onSurfaceVariant = LuxeTextSecondary
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // For SAU, we prioritize the Luxe Light theme as the signature brand look
    val colorScheme = LuxeColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Use dark icons for the light background (Luxe Theme)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
            
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
