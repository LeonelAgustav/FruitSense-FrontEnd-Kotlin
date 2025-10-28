package com.example.fruitsense.ui.screen.dashboard.scan

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fruitsense.ui.theme.FruitSenseColors

@Composable
fun ScanHubScreen(
    onBukaKameraClick: () -> Unit,
    onImageSelected: (Uri) -> Unit // Callback untuk mengirim Uri hasil pilihan
) {
    // --- Launcher untuk Galeri ---
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                // Kirim Uri kembali ke ScanScreen (host)
                onImageSelected(uri)
            }
        }
    )

    // --- UI Hub ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Scan Buah",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = FruitSenseColors.GreenOlive,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Ambil foto atau pilih dari galeri untuk mendeteksi kematangan buah.",
            fontSize = 16.sp,
            color = FruitSenseColors.GrayDark,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Tombol Buka Kamera
        Button(
            onClick = onBukaKameraClick, // Panggil callback
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = FruitSenseColors.GreenDark)
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = "Kamera", tint = Color.White)
            Spacer(modifier = Modifier.width(12.dp))
            Text("Buka Kamera", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tombol Buka Galeri
        OutlinedButton(
            onClick = {
                // Langsung luncurkan galeri dari sini
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = FruitSenseColors.GreenDark),
            border = ButtonDefaults.outlinedButtonBorder().copy(brush = SolidColor(FruitSenseColors.GreenDark))
        ) {
            Icon(Icons.Default.PhotoLibrary, contentDescription = "Galeri", tint = FruitSenseColors.GreenDark)
            Spacer(modifier = Modifier.width(12.dp))
            Text("Pilih dari Galeri", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}