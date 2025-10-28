package com.example.fruitsense.ui.screen.dashboard.scan

import android.content.Context
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

// Fungsi helper untuk mengambil CameraProvider (wajib untuk CameraX)
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

    // State untuk ImageCapture (objek utama untuk mengambil foto)
    val imageCapture = remember { ImageCapture.Builder().build() }

    // State untuk Preview (objek utama untuk menampilkan live feed)
    val previewView = remember { PreviewView(context) }

    // Mengikat CameraX ke Lifecycle
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
            // Tangani error
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        // Tombol Kembali
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.White)
        }

        // Tombol Shutter (Ambil Foto)
        IconButton(
            onClick = {
                // Panggil fungsi untuk mengambil foto
                takePhoto(
                    context = context,
                    imageCapture = imageCapture,
                    onPhotoTaken = onPhotoTaken
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp)
                .size(70.dp)
                .background(Color.White, CircleShape)
                .border(2.dp, Color.Black, CircleShape)
        ) {}
    }
}

// Fungsi helper untuk mengambil foto dan menyimpannya
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
                // Jika sukses, panggil callback dengan Uri dari file yang disimpan
                val savedUri = Uri.fromFile(photoFile)
                onPhotoTaken(savedUri)
            }
            override fun onError(exc: ImageCaptureException) {
                // Tangani error
            }
        }
    )
}