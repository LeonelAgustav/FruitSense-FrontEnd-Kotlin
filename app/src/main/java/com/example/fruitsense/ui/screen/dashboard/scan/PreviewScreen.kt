package com.example.fruitsense.ui.screen.dashboard.scan

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.fruitsense.ui.theme.FruitSenseColors

@Composable
fun PreviewScreen(
    imageUri: Uri,
    onConfirm: () -> Unit, // Callback saat tombol "Scan" ditekan
    onCancel: () -> Unit   // Callback saat tombol "Pilih Ulang" ditekan
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FruitSenseColors.Black)
    ) {
        // 1. Tampilkan gambar yang dipilih/difoto
        AsyncImage(
            model = imageUri,
            contentDescription = "Preview Gambar",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )

        // 2. Tombol kontrol di bagian bawah
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                // Beri sedikit background transparan agar tombol terbaca
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background),
                        startY = 0f,
                        endY = 400f
                    )
                )
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Tombol Batal/Pilih Ulang
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary // Gunakan warna tema
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 2.dp,
                    brush = SolidColor(MaterialTheme.colorScheme.primary)
                )
            ) {
                Text("Pilih Ulang", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Tombol Konfirmasi/Scan
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary // Gunakan warna tema
                )
            ) {
                Text("Scan Gambar Ini", fontWeight = FontWeight.Bold)
            }
        }
    }
}