package com.fruitsense.app.ui.theme

import androidx.compose.ui.graphics.Color

object FruitSenseColors {
    // --- Common Palette (JSON Requirement) ---
    val FreshGreen = Color(0xFF4CAF50)
    val RottenRed = Color(0xFFFF5252)
    val WarningOrange = Color(0xFFFFA726)
    val NeutralGrey = Color(0xFF9E9E9E)
    val White = Color.White // Tambahan untuk keamanan

    // --- Light Mode Colors ---
    val LightPrimary = Color(0xFF228B22)      // Forest Green
    val LightOnPrimary = Color(0xFFFFFFFF)
    val LightSecondary = Color(0xFF556B2F)    // Olive Green
    val LightBackground = Color(0xFFFFFFFF)
    val LightSurface = Color(0xFFF5F5F5)      // Very Light Grey
    val LightOnSurface = Color(0xFF1E1E1E)
    val LightOutline = Color(0xFFE0E0E0)

    // --- Dark Mode Colors ---
    val DarkPrimary = Color(0xFF81C784)       // Light Green (Pastel)
    val DarkOnPrimary = Color(0xFF00390A)     // Dark Green Text
    val DarkSecondary = Color(0xFFBCCFA7)     // Pale Olive
    val DarkBackground = Color(0xFF121212)    // Dark Grey/Black
    val DarkSurface = Color(0xFF1E1E1E)       // Dark Grey Surface
    val DarkOnSurface = Color(0xFFE2E2E2)     // Light Grey Text
    val DarkOutline = Color(0xFF444444)

    // --- BACKWARD COMPATIBILITY (FIX ERROR) ---
    // Alias nama lama ke warna baru agar file lama tidak error
    val GreenDark = LightPrimary
    val GreenOlive = LightSecondary
    val GrayDark = NeutralGrey
}