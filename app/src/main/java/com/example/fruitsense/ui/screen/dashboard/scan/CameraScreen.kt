package com.example.fruitsense.ui.screen.dashboard.scan

import android.content.Context
import android.net.Uri
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.fruitsense.ui.theme.FruitSenseColors
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

// Fungsi helper untuk mengambil CameraProvider
private suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { future ->
        future.addListener({
            continuation.resume(future.get())
        }, ContextCompat.getMainExecutor(this))
    }
}

@Composable
fun CameraScreen(
    onPhotoTaken: (Uri) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val imageCapture = remember { ImageCapture.Builder().build() }
    val previewView = remember {
        PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    LaunchedEffect(Unit) {
        val cameraProvider = context.getCameraProvider()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            // Handle error
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // 1. Camera Preview Layer
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        // 2. Grid Overlay Layer (Garis bantu scanner)
        ScannerGridOverlay()

        // 3. Top Bar Controls (Gradient Background for visibility)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)
                    )
                )
                .padding(16.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Kembali",
                    tint = Color.White
                )
            }

            // Contoh icon flash (kosmetik visual)
            Icon(
                imageVector = Icons.Default.FlashAuto,
                contentDescription = "Flash",
                tint = Color.White,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }

        // 4. Bottom Control Panel
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.7f)) // Panel semi-transparan
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Posisikan buah di dalam bingkai",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Shutter Button Modern
            ShutterButton(
                onClick = {
                    takePhoto(context, imageCapture, onPhotoTaken)
                }
            )
        }
    }
}

@Composable
fun ShutterButton(onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(84.dp)
            .clickable(onClick = onClick)
    ) {
        // Outer Ring
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(4.dp, Color.White, CircleShape)
        )
        // Inner Circle (Tombol pencet)
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(Color.White, CircleShape)
                .border(2.dp, Color.Gray, CircleShape) // Sedikit border agar dimensi terlihat
        )
    }
}

@Composable
fun ScannerGridOverlay() {
    // Membuat garis grid tipis 3x3
    Column(Modifier.fillMaxSize()) {
        WeightSpacer()
        Divider(color = Color.White.copy(alpha = 0.3f), thickness = 1.dp)
        WeightSpacer()
        Divider(color = Color.White.copy(alpha = 0.3f), thickness = 1.dp)
        WeightSpacer()
    }
    Row(Modifier.fillMaxSize()) {
        WeightSpacer()
        VerticalDivider(color = Color.White.copy(alpha = 0.3f), thickness = 1.dp)
        WeightSpacer()
        VerticalDivider(color = Color.White.copy(alpha = 0.3f), thickness = 1.dp)
        WeightSpacer()
    }
}

@Composable
fun RowScope.WeightSpacer() {
    Spacer(modifier = Modifier.weight(1f))
}

@Composable
fun ColumnScope.WeightSpacer() {
    Spacer(modifier = Modifier.weight(1f))
}

// Fungsi helper takePhoto (Logika tetap sama)
private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    onPhotoTaken: (Uri) -> Unit
) {
    val photoFile = File(
        context.cacheDir,
        "JPEG_${System.currentTimeMillis()}.jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(photoFile)
                onPhotoTaken(savedUri)
            }
            override fun onError(exc: ImageCaptureException) {
                // Log error
            }
        }
    )
}