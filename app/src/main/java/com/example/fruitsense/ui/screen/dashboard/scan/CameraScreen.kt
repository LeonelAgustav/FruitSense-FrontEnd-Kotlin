package com.example.fruitsense.ui.screen.dashboard.scan

import android.content.Context
import android.net.Uri
import android.util.Log
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
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import java.io.File
import java.util.concurrent.Executors
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

    // State untuk ImageCapture
    val imageCapture = remember { ImageCapture.Builder().build() }

    // Preview View
    val previewView = remember {
        PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    // Executor untuk analisis ML
    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }

    LaunchedEffect(Unit) {
        val cameraProvider = context.getCameraProvider()

        // 1. Setup Preview
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        // 2. Setup Image Analysis
        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        // 3. Setup Image Labeler
        // Menggunakan opsi default. Anda bisa mengatur threshold confidence jika perlu.
        val options = ImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.7f) // Hanya ambil label dengan akurasi > 70%
            .build()
        val labeler = ImageLabeling.getClient(options)

        imageAnalysis.setAnalyzer(analysisExecutor) { imageProxy ->
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            val mediaImage = imageProxy.image

            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, rotationDegrees)

                // Proses Pelabelan Gambar
                labeler.process(image)
                    .addOnSuccessListener { labels ->
                        // --- HASIL LABEL KELUAR DISINI ---
                        for (label in labels) {
                            val text = label.text
                            val confidence = label.confidence
                            // Log hasil ke Logcat untuk dicek
                            Log.d("MLKit-Label", "Terdeteksi: $text (Akurasi: $confidence)")

                            // TODO: Nanti bisa update UI State untuk menampilkan nama buah di layar
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("MLKit-Label", "Gagal mendeteksi", e)
                    }
                    .addOnCompleteListener {
                        // PENTING: Tutup imageProxy agar frame berikutnya bisa diproses
                        imageProxy.close()
                    }
            } else {
                imageProxy.close()
            }
        }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture,
                imageAnalysis
            )
        } catch (e: Exception) {
            Log.e("CameraScreen", "Gagal binding camera use cases", e)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // 1. Camera Preview Layer
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        // 2. Grid Overlay Layer
        ScannerGridOverlay()

        // 3. Top Bar Controls
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
                .statusBarsPadding()
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
                .background(Color.Black.copy(alpha = 0.7f))
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

            // Shutter Button
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(4.dp, Color.White, CircleShape)
        )
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(Color.White, CircleShape)
                .border(2.dp, Color.Gray, CircleShape)
        )
    }
}

@Composable
fun ScannerGridOverlay() {
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
                Log.e("CameraX", "Photo capture failed: ${exc.message}", exc)
            }
        }
    )
}