package com.nisr.sauservices.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = LuxuryGold,
    onPrimary = LuxuryBackground,
    primaryContainer = LuxuryGold.copy(alpha = 0.1f),
    onPrimaryContainer = LuxuryGold,
    secondary = LuxuryCream,
    onSecondary = LuxuryBackground,
    background = LuxuryBackground,
    onBackground = LuxuryTextPrimary,
    surface = LuxuryCard,
    onSurface = LuxuryTextPrimary,
    outline = LuxuryBorder,
    surfaceVariant = LuxuryCard,
    onSurfaceVariant = LuxuryTextSecondary
)

private val LightColorScheme = DarkColorScheme // Both use Luxury Dark Theme

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // With enableEdgeToEdge(), we usually want the system bars to be transparent.
            // But we still need to control the icon color (light vs dark).
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
