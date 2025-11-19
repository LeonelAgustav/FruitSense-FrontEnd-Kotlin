package com.example.fruitsense.ui.screen.dashboard.scan

import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.fruitsense.ui.theme.FruitSenseColors

@Composable
fun ResultScreen(
    imageUri: Uri,
    fruitName: String = "Apel Fuji",
    freshness: Int = 92,
    grade: String = "A",
    onScanAgain: () -> Unit,
    onSaveToInventory: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            // Background utama adaptif (mencegah glitch putih saat transisi)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 1. Gambar Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        ) {
            AsyncImage(
                model = imageUri,
                contentDescription = "Hasil Analisa",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Overlay gradient hitam
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f))
            )

            IconButton(
                onClick = onScanAgain,
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.4f), CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.White)
            }
        }

        // 2. Detail Information Sheet
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                // PERUBAHAN PENTING: Background Sheet mengikuti tema
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Handle strip
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    // Warna handle adaptif (Abu-abu terang/gelap)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Header: Nama Buah & Grade Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hasil Deteksi",
                        style = MaterialTheme.typography.labelMedium,
                        // Warna teks label adaptif
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = fruitName,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        // Tetap Hijau (karena ini warna branding/aksen)
                        // Pastikan terlihat jelas di Dark Mode (GreenDark cukup terang)
                        color = FruitSenseColors.GreenDark
                    )
                }

                // Grade Badge
                Surface(
                    shape = CircleShape,
                    color = FruitSenseColors.GreenDark,
                    shadowElevation = 8.dp,
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = grade,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Freshness Indicator
            Text(
                text = "Tingkat Kesegaran",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                // Warna teks judul adaptif
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = freshness / 100f,
                    modifier = Modifier
                        .weight(1f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = FruitSenseColors.GreenDark,
                    // Track color adaptif (agar tidak terlalu terang di dark mode)
                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "$freshness%",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = FruitSenseColors.GreenDark
                )
            }

            // Deskripsi Singkat
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Buah ini dalam kondisi sangat baik. Kulit terlihat mulus tanpa bintik hitam yang signifikan. Aman untuk dikonsumsi atau disimpan.",
                style = MaterialTheme.typography.bodyMedium,
                // Warna teks isi adaptif
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            // Tombol Aksi
            Row(
                modifier = Modifier.padding(top = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tombol Scan Lagi
                OutlinedButton(
                    onClick = onScanAgain,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, FruitSenseColors.GreenDark),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = FruitSenseColors.GreenDark)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Scan Lagi")
                }

                // Tombol Simpan
                Button(
                    onClick = onSaveToInventory,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FruitSenseColors.GreenDark
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(Icons.Outlined.Inventory2, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simpan")
                }
            }
        }
    }
}