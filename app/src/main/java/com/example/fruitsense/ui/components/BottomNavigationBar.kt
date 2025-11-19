package com.example.fruitsense.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.fruitsense.ui.theme.FruitSenseColors

@Composable
fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Container utama dibuat "Floating" (Mengambang)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp) // Memberi jarak dari pinggir layar
    ) {
        Surface(
            shape = RoundedCornerShape(32.dp), // Sudut sangat membulat
            color = FruitSenseColors.GreenDark,
            shadowElevation = 10.dp, // Efek bayangan modern
            modifier = Modifier.height(80.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ModernBottomNavItem(
                    icon = Icons.Default.CameraAlt,
                    label = "Scan",
                    isSelected = selectedTab == 0,
                    onClick = { onTabSelected(0) }
                )

                ModernBottomNavItem(
                    icon = Icons.Default.Archive,
                    label = "Inventory",
                    isSelected = selectedTab == 1,
                    onClick = { onTabSelected(1) }
                )

                ModernBottomNavItem(
                    icon = Icons.Default.History,
                    label = "History",
                    isSelected = selectedTab == 2,
                    onClick = { onTabSelected(2) }
                )

                ModernBottomNavItem(
                    icon = Icons.Default.AccountCircle,
                    label = "Profile",
                    isSelected = selectedTab == 3,
                    onClick = { onTabSelected(3) }
                )
            }
        }
    }
}

@Composable
fun ModernBottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Animasi warna background saat dipilih
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) FruitSenseColors.White.copy(alpha = 0.2f) else Color.Transparent,
        label = "bgColorAnimation"
    )

    // Animasi warna ikon
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) FruitSenseColors.White else FruitSenseColors.GrayDark, // Asumsi GrayDark terlihat di atas GreenDark
        label = "iconColorAnimation"
    )

    // Animasi scale efek pantul (bounce) sedikit saat dipilih
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scaleAnimation"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp)) // Agar efek ripple rapi
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null // Menghilangkan ripple default agar lebih bersih
            ) { onClick() }
            .padding(8.dp) // Area sentuh
    ) {
        // Ikon dengan background pill/kapsul
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(color = backgroundColor, shape = RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 6.dp) // Padding di dalam kapsul
                .scale(scale)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Teks Label (Opsional: bisa dihilangkan jika ingin gaya ultra-minimalis)
        if (isSelected) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = FruitSenseColors.White
            )
        }
    }
}