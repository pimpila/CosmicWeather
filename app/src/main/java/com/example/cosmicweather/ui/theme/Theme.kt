package com.example.cosmicweather.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Cosmic dark theme (for Android <12 or when dynamic colors disabled)
private val DarkColorScheme = darkColorScheme(
    primary = CosmicPurple80,
    secondary = DeepSpace80,
    tertiary = StardustPink80,
    background = MidnightBlue,
    surface = Color(0xFF1E1E2E),
    surfaceVariant = Color(0xFF2A2A3E),
    onPrimary = Color(0xFF1A1A2E),
    onSecondary = Color(0xFF1A1A2E),
    onTertiary = Color(0xFF1A1A2E),
    onBackground = Color(0xFFE8E3F0),
    onSurface = Color(0xFFE8E3F0)
)

// Cosmic light theme (for Android <12 or when dynamic colors disabled)
private val LightColorScheme = lightColorScheme(
    primary = CosmicPurple40,
    secondary = DeepSpace40,
    tertiary = StardustPink40,
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFAF8FF),
    surfaceVariant = Color(0xFFF3EFFF),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    primaryContainer = Color(0xFFE8DEFF),
    onPrimaryContainer = Color(0xFF21005D)
)

/**
 * Cosmic Weather theme with Material You dynamic theming support.
 *
 * On Android 12+ (API 31+): Uses dynamic colors from user's wallpaper
 * On Android <12: Uses cosmic-themed purple/pink/indigo color scheme
 *
 * @param darkTheme Whether to use dark theme (defaults to system setting)
 * @param dynamicColor Whether to use Material You dynamic colors (Android 12+)
 */
@Composable
fun CosmicWeatherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+ (Material You)
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // Select color scheme based on Android version and theme
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Update system bars for edge-to-edge experience
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}