package com.example.fruitsense.ui.screen.dashboard.scan

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fruitsense.ui.theme.FruitSenseColors

@Composable
fun ScanHubScreen(
    onBukaKameraClick: () -> Unit,
    onImageSelected: (Uri) -> Unit
) {
    // Launcher untuk membuka Galeri HP
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        // [FIX] Logika ini dipanggil SETELAH user memilih gambar
        if (uri != null) {
            Log.d("ScanHubScreen", "Gambar dipilih: $uri")
            onImageSelected(uri) // Panggil callback navigasi di ScanScreen
        } else {
            Log.d("ScanHubScreen", "User membatalkan pemilihan gambar")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            // Background utama adaptif (Putih di Light, Hitam di Dark)
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Ilustrasi atau Icon Besar
        Surface(
            shape = RoundedCornerShape(32.dp),
            // Background icon: Hijau transparan agar menyatu di Dark & Light Mode
            color = FruitSenseColors.GreenDark.copy(alpha = 0.1f),
            modifier = Modifier.size(160.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = FruitSenseColors.GreenDark,
                    modifier = Modifier.size(64.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Judul Utama
        Text(
            text = "Deteksi Kesegaran Buah",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            // Warna judul adaptif (Hitam/Putih)
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Deskripsi Subjudul
        Text(
            text = "Scan buahmu sekarang untuk mengetahui kualitas dan kesegarannya secara instan.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            // Warna deskripsi adaptif (Abu gelap/terang)
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Tombol Buka Kamera (Primary Action)
        Button(
            onClick = onBukaKameraClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = FruitSenseColors.GreenDark,
                // Teks tombol tetap Putih karena backgroundnya Hijau Gelap
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Mulai Scan Kamera", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tombol Pilih dari Galeri (Secondary Action)
        OutlinedButton(
            onClick = {
                // [FIX] Launch gallery picker
                try {
                    galleryLauncher.launch("image/*")
                } catch (e: Exception) {
                    Log.e("ScanHubScreen", "Gagal membuka galeri", e)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            // Border tetap hijau branding
            border = BorderStroke(1.5.dp, FruitSenseColors.GreenDark),
            colors = ButtonDefaults.outlinedButtonColors(
                // Teks & Icon mengikuti warna branding
                contentColor = FruitSenseColors.GreenDark
            )
        ) {
            Icon(Icons.Default.Image, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Pilih dari Galeri", fontWeight = FontWeight.SemiBold)
        }
    }
}