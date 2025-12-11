package com.fruitsense.app.ui.screen.dashboard.scan

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import java.io.File
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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

    // --- Setup CameraX (Sama seperti sebelumnya) ---
    val imageCapture = remember { ImageCapture.Builder().build() }
    val previewView = remember { PreviewView(context).apply { scaleType = PreviewView.ScaleType.FILL_CENTER } }
    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }

    LaunchedEffect(Unit) {
        val cameraProvider = context.getCameraProvider()
        val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
        val imageAnalysis = ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
        val labeler = ImageLabeling.getClient(ImageLabelerOptions.Builder().setConfidenceThreshold(0.7f).build())

        // Dummy analyzer hook
        imageAnalysis.setAnalyzer(analysisExecutor) { imageProxy -> imageProxy.close() }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture, imageAnalysis)
        } catch (e: Exception) { Log.e("Camera", "Bind failed", e) }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // 1. Preview
        AndroidView({ previewView }, modifier = Modifier.fillMaxSize())

        // 2. Corner Overlay (New UI Directive)
        CornerFocusOverlay()

        // 3. Controls
        Column(
            modifier = Modifier.fillMaxSize().padding(bottom = 32.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color.Black.copy(0.6f), Color.Transparent)))
                    .statusBarsPadding()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                }
                Icon(Icons.Default.FlashAuto, "Flash", tint = Color.White)
            }

            // Bottom Shutter
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                ShutterButton(onClick = { takePhoto(context, imageCapture, onPhotoTaken) })
            }
        }
    }
}

@Composable
fun CornerFocusOverlay() {
    // Menggambar 4 siku di pojok layar
    Canvas(modifier = Modifier.fillMaxSize().padding(48.dp)) {
        val strokeW = 4.dp.toPx()
        val lineL = 40.dp.toPx()
        val color = Color.White

        // Top Left
        drawLine(color, start = androidx.compose.ui.geometry.Offset(0f, 0f), end = androidx.compose.ui.geometry.Offset(lineL, 0f), strokeWidth = strokeW)
        drawLine(color, start = androidx.compose.ui.geometry.Offset(0f, 0f), end = androidx.compose.ui.geometry.Offset(0f, lineL), strokeWidth = strokeW)
        // Top Right
        drawLine(color, start = androidx.compose.ui.geometry.Offset(size.width, 0f), end = androidx.compose.ui.geometry.Offset(size.width - lineL, 0f), strokeWidth = strokeW)
        drawLine(color, start = androidx.compose.ui.geometry.Offset(size.width, 0f), end = androidx.compose.ui.geometry.Offset(size.width, lineL), strokeWidth = strokeW)
        // Bottom Left
        drawLine(color, start = androidx.compose.ui.geometry.Offset(0f, size.height), end = androidx.compose.ui.geometry.Offset(lineL, size.height), strokeWidth = strokeW)
        drawLine(color, start = androidx.compose.ui.geometry.Offset(0f, size.height), end = androidx.compose.ui.geometry.Offset(0f, size.height - lineL), strokeWidth = strokeW)
        // Bottom Right
        drawLine(color, start = androidx.compose.ui.geometry.Offset(size.width, size.height), end = androidx.compose.ui.geometry.Offset(size.width - lineL, size.height), strokeWidth = strokeW)
        drawLine(color, start = androidx.compose.ui.geometry.Offset(size.width, size.height), end = androidx.compose.ui.geometry.Offset(size.width, size.height - lineL), strokeWidth = strokeW)
    }
}

@Composable
fun ShutterButton(onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(80.dp).clickable(onClick = onClick)
    ) {
        // Outer Ring
        Box(Modifier.size(80.dp).border(4.dp, Color.White, CircleShape))
        // Inner Circle
        Box(Modifier.size(64.dp).background(Color.White, CircleShape))
    }
}

// ... (Simpan fungsi takePhoto dari kode sebelumnya) ...
private fun takePhoto(context: Context, imageCapture: ImageCapture, onPhotoTaken: (Uri) -> Unit) {
    val photoFile = File(context.cacheDir, "JPEG_${System.currentTimeMillis()}.jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
    imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(context), object : ImageCapture.OnImageSavedCallback {
        override fun onImageSaved(output: ImageCapture.OutputFileResults) { onPhotoTaken(Uri.fromFile(photoFile)) }
        override fun onError(exc: ImageCaptureException) { Log.e("Camera", "Error", exc) }
    })
}