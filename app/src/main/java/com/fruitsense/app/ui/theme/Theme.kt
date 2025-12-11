package com.fruitsense.app.ui.theme

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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Mapping warna dari JSON ke Material3 Dark Scheme
private val DarkColorScheme = darkColorScheme(
    primary = FruitSenseColors.DarkPrimary,
    onPrimary = FruitSenseColors.DarkOnPrimary,
    secondary = FruitSenseColors.DarkSecondary,
    onSecondary = FruitSenseColors.DarkOnPrimary, // Kontras aman
    background = FruitSenseColors.DarkBackground,
    surface = FruitSenseColors.DarkSurface,
    onSurface = FruitSenseColors.DarkOnSurface,
    outline = FruitSenseColors.DarkOutline,
    error = FruitSenseColors.RottenRed
)

// Mapping warna dari JSON ke Material3 Light Scheme
private val LightColorScheme = lightColorScheme(
    primary = FruitSenseColors.LightPrimary,
    onPrimary = FruitSenseColors.LightOnPrimary,
    secondary = FruitSenseColors.LightSecondary,
    onSecondary = FruitSenseColors.LightOnPrimary,
    background = FruitSenseColors.LightBackground,
    surface = FruitSenseColors.LightSurface,
    onSurface = FruitSenseColors.LightOnSurface,
    outline = FruitSenseColors.LightOutline,
    error = FruitSenseColors.RottenRed
)

@Composable
fun FruitSenseTheme(
    appTheme: String = "SYSTEM", // Parameter dinamis dari ProfileViewModel
    dynamicColor: Boolean = false, // FALSE secara default agar warna brand kita tidak tertimpa warna wallpaper HP user
    content: @Composable () -> Unit
) {
    val darkTheme = when (appTheme) {
        "LIGHT" -> false
        "DARK" -> true
        else -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Mengatur warna Status Bar agar sesuai tema
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb() // Status bar mengikuti background
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography, // Menggunakan Type.kt baru
        shapes = Shapes,            // Menggunakan Shapes.kt baru
        content = content
    )
}