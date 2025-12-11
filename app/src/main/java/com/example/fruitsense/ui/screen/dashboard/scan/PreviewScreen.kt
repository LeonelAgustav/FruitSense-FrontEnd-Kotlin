package com.example.fruitsense.ui.screen.dashboard.scan

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.fruitsense.data.model.FruitItem

@Composable
fun PreviewScreen(
    imageUri: Uri,
    onRetake: () -> Unit,
    onAnalysisSuccess: (FruitItem) -> Unit,
    viewModel: ScanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is ScanUiState.Success) {
            onAnalysisSuccess((uiState as ScanUiState.Success).fruitItem)
            // [FIX] JANGAN reset state di sini!
            // Biarkan state tetap Success agar ResultScreen bisa membacanya.
            // viewModel.resetState() <--- HAPUS BARIS INI
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Image
        Image(
            painter = rememberAsyncImagePainter(imageUri),
            contentDescription = "Preview",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )

        // Loading Overlay
        if (uiState is ScanUiState.Loading) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(0.6f)), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            // Action Bar (Bottom)
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onRetake) {
                        Text("Ambil Ulang", color = MaterialTheme.colorScheme.error)
                    }

                    Button(
                        onClick = { viewModel.analyzeImage(imageUri) },
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.height(50.dp).padding(start = 16.dp)
                    ) {
                        Text("Analisa Sekarang", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Error Feedback
        if (uiState is ScanUiState.Error) {
            Snackbar(
                modifier = Modifier.align(Alignment.TopCenter).padding(16.dp).statusBarsPadding(),
                containerColor = MaterialTheme.colorScheme.error
            ) {
                Text((uiState as ScanUiState.Error).message, color = Color.White)
            }
        }
    }
}