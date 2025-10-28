package com.example.fruitsense.ui.screen.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fruitsense.ui.theme.FruitSenseColors

@Composable
fun InventoryScreen() {
    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Archive,
            contentDescription = "Inventory",
            modifier = Modifier.size(120.dp),
            tint = FruitSenseColors.GreenDark
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Inventory Buah",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = FruitSenseColors.GreenOlive,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Lihat daftar buah yang Anda miliki.",
            fontSize = 16.sp,
            color = FruitSenseColors.GrayDark,
            textAlign = TextAlign.Center
        )
    }
}