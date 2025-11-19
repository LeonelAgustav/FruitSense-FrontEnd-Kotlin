package com.example.fruitsense.ui.screen.dashboard.scan

import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage
import com.example.fruitsense.ui.theme.FruitSenseColors

@Composable
fun PreviewScreen(
    imageUri: Uri,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Background foto tetap hitam agar fokus
    ) {
        // 1. Area Gambar Utama
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            // Frame Foto
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .background(Color.DarkGray)
            ) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Preview Gambar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // 2. Bottom Action Card
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(24.dp),
            // PERUBAHAN PENTING: Warna panel mengikuti tema
            color = MaterialTheme.colorScheme.surface, // Putih (Light) / Abu Gelap (Dark)
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)),
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Apakah foto sudah jelas?",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    // Warna teks judul adaptif
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Pastikan buah terlihat utuh dan cahaya cukup.",
                    style = MaterialTheme.typography.bodyMedium,
                    // Warna teks deskripsi adaptif
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Row Tombol Aksi
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Tombol Ulang (Outlined)
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        // Border tombol mengikuti warna outline tema
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        colors = ButtonDefaults.outlinedButtonColors(
                            // Konten (Icon/Text) mengikuti warna onSurface (Hitam/Putih)
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ulang", fontWeight = FontWeight.SemiBold)
                    }

                    // Tombol Analisa (Filled)
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FruitSenseColors.GreenDark,
                            contentColor = Color.White // Tetap Putih karena tombolnya Hijau Gelap
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Analisa", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}