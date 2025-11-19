package com.example.fruitsense.ui.screen.dashboard.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fruitsense.ui.theme.FruitSenseColors

@Composable
fun InventoryScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background), // Background adaptif
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon Container
            Surface(
                shape = CircleShape,
                color = FruitSenseColors.GreenDark.copy(alpha = 0.1f),
                modifier = Modifier.size(160.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Inventory2, // Menggunakan outlined agar lebih modern
                        contentDescription = "Inventory Empty",
                        modifier = Modifier.size(80.dp),
                        tint = FruitSenseColors.GreenDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Belum Ada Buah Tersimpan",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground, // Teks adaptif
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Hasil scan yang Anda simpan akan muncul di halaman ini. Yuk, mulai koleksi data buahmu!",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant, // Teks sekunder adaptif
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
        }
    }
}