package com.fruitsense.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    // Input Fields (Small components) -> 12.dp
    small = RoundedCornerShape(12.dp),

    // Cards (Medium components) -> 16.dp
    medium = RoundedCornerShape(16.dp),

    // Bottom Sheets / Dialogs / Large Surface -> 24.dp
    large = RoundedCornerShape(24.dp),

    // Buttons (Full Pill) -> 50% or 100.dp (Extra Large for specific overrides)
    extraLarge = RoundedCornerShape(50.dp)
)