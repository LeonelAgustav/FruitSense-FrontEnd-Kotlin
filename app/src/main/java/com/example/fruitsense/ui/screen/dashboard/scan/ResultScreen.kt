package com.example.fruitsense.ui.screen.dashboard.scan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.fruitsense.data.model.FruitItem
import com.example.fruitsense.ui.theme.FruitSenseColors
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun ResultScreen(
    resultData: FruitItem,
    onScanAgain: () -> Unit,
    onSaveToInventory: () -> Unit
) {
    // --- Logika Expired ---
    val expiryInfo = remember(resultData) {

        // Prioritas 1: Gunakan 'expiryDays' langsung dari API
        val apiDaysLeft = resultData.expiryDays

        if (apiDaysLeft != null) {
            when {
                apiDaysLeft < 0 -> Triple("Expired", "Sudah Kadaluwarsa", FruitSenseColors.RottenRed)
                apiDaysLeft == 0 -> Triple("0", "Habis Hari Ini", Color(0xFFFF9800))
                apiDaysLeft <= 2 -> Triple(apiDaysLeft.toString(), "Hari Lagi (Segera Habis)", Color(0xFFFF9800))
                else -> Triple(apiDaysLeft.toString(), "Hari Lagi (Aman)", FruitSenseColors.FreshGreen)
            }
        }
        // Prioritas 2: Fallback hitung manual dari 'expiryDate'
        else {
            try {
                val today = LocalDate.now()
                val expiryDate = if (!resultData.expiryDate.isNullOrEmpty()) {
                    try { LocalDate.parse(resultData.expiryDate) } catch (e: Exception) { today.plusDays(7) }
                } else {
                    today.plusDays(7)
                }

                val daysLeft = ChronoUnit.DAYS.between(today, expiryDate).toInt()

                when {
                    daysLeft < 0 -> Triple("Expired", "Sudah Kadaluwarsa", FruitSenseColors.RottenRed)
                    daysLeft == 0 -> Triple("0", "Habis Hari Ini", Color(0xFFFF9800))
                    daysLeft <= 2 -> Triple(daysLeft.toString(), "Hari Lagi (Segera Habis)", Color(0xFFFF9800))
                    else -> Triple(daysLeft.toString(), "Hari Lagi (Aman)", FruitSenseColors.FreshGreen)
                }
            } catch (e: Exception) {
                Triple("?", "Status Tidak Diketahui", Color.Gray)
            }
        }
    }

    // Destructuring hasil: (Angka Besar, Label Kecil, Warna)
    val (daysValue, daysLabel, statusColor) = expiryInfo

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // 1. Full Background Image
        AsyncImage(
            model = resultData.imageUri,
            contentDescription = null,
            // Memberikan padding bawah besar agar gambar utama (tengah) tidak tertutup sheet
            modifier = Modifier.fillMaxSize().padding(bottom = 200.dp),
            contentScale = ContentScale.Crop
        )

        // Tombol Back di pojok kiri atas
        IconButton(
            onClick = onScanAgain,
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
                .background(Color.Black.copy(0.3f), CircleShape)
        ) {
            Icon(Icons.Default.ArrowBack, null, tint = Color.White)
        }

        // 2. Bottom Sheet Content
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.45f),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Drag Handle
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // --- INFO EXPIRED ---
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = daysValue,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 64.sp,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = statusColor,
                        textAlign = TextAlign.Center,
                        lineHeight = 64.sp
                    )
                    Text(
                        text = daysLabel,
                        style = MaterialTheme.typography.titleMedium,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Info Nama Buah & Grade
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Terdeteksi",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = resultData.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    // Badge Grade
                    Surface(
                        color = statusColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "${resultData.grade ?: "-"}",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = statusColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Deskripsi AI
                Text(
                    text = resultData.aiDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Tombol Aksi (Ulang / Simpan)
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedButton(
                        onClick = onScanAgain,
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        Icon(Icons.Default.Refresh, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Ulang")
                    }
                    Button(
                        onClick = onSaveToInventory,
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Save, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Simpan")
                    }
                }

                // Tambahan padding bawah agar aman di layar panjang
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}