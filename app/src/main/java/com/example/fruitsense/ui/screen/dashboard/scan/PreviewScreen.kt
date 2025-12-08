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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.fruitsense.data.model.FruitItem
import com.example.fruitsense.ui.theme.FruitSenseColors

@Composable
fun PreviewScreen(
    imageUri: Uri,
    onRetake: () -> Unit,
    onAnalysisSuccess: (FruitItem) -> Unit,
    viewModel: ScanViewModel = hiltViewModel() // Inject ViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle State Change
    LaunchedEffect(uiState) {
        if (uiState is ScanUiState.Success) {
            onAnalysisSuccess((uiState as ScanUiState.Success).fruitItem)
            viewModel.resetState() // Reset agar tidak trigger lagi saat back
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Gambar Full Screen
        Image(
            painter = rememberAsyncImagePainter(imageUri),
            contentDescription = "Preview",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )

        // Loading Indicator Overlay
        if (uiState is ScanUiState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = FruitSenseColors.GreenDark)
            }
        } else {
            // Bottom Actions
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onRetake,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Ulang")
                    }
                    Button(
                        onClick = { viewModel.analyzeImage(imageUri) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = FruitSenseColors.GreenDark)
                    ) {
                        Text("Analisa", color = Color.White)
                    }
                }
            }
        }

        // Error Message
        if (uiState is ScanUiState.Error) {
            // Tampilkan Snackbar atau Text Error sederhana
            val errorMsg = (uiState as ScanUiState.Error).message
            Text(
                text = errorMsg,
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center).background(Color.White)
            )
        }
    }
}