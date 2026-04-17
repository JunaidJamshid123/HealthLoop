package com.junaidjamshid.healthloop.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryOrange,
    onPrimary = DeepBlack,
    primaryContainer = PrimaryOrangeDark,
    onPrimaryContainer = TextLight,
    secondary = SoftGreen,
    onSecondary = DeepBlack,
    secondaryContainer = SoftGreenDark,
    onSecondaryContainer = DeepBlack,
    tertiary = SkyBlue,
    onTertiary = DeepBlack,
    background = SurfaceDark,
    onBackground = TextLight,
    surface = CardSurfaceDark,
    onSurface = TextLight,
    surfaceVariant = SoftBlack,
    onSurfaceVariant = TextLight.copy(alpha = 0.7f),
    outline = BorderColor.copy(alpha = 0.3f)
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryOrange,
    onPrimary = DeepBlack,
    primaryContainer = PrimaryOrangeLight,
    onPrimaryContainer = DeepBlack,
    secondary = SoftGreen,
    onSecondary = DeepBlack,
    secondaryContainer = SoftGreenLight,
    onSecondaryContainer = DeepBlack,
    tertiary = SkyBlue,
    onTertiary = DeepBlack,
    background = SurfaceLight,
    onBackground = TextDark,
    surface = CardSurface,
    onSurface = TextDark,
    surfaceVariant = WarmBeige,
    onSurfaceVariant = TextSecondary,
    outline = BorderColor
)

@Composable
fun HealthLoopTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled to use our custom colors
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

    // Update status bar color
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}