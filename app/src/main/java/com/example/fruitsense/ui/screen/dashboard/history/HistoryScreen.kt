package com.example.fruitsense.ui.screen.dashboard.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
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
fun HistoryScreen() {
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
                        imageVector = Icons.Default.History,
                        contentDescription = "History Empty",
                        modifier = Modifier.size(80.dp),
                        tint = FruitSenseColors.GreenDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Riwayat Scan Kosong",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground, // Teks adaptif
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Anda belum melakukan scan buah apapun. Mulai scan sekarang untuk melihat riwayat di sini.",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant, // Teks sekunder adaptif
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
        }
    }
}