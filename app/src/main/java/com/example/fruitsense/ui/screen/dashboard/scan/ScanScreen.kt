package com.example.fruitsense.ui.screen.dashboard.scan

import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

object ScanScreenNavigation {
    const val HUB = "scan_hub"
    const val CAMERA = "scan_camera"
    const val PREVIEW = "scan_preview"
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanScreen() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    // --- State baru untuk menyimpan URI yang dipilih ---
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // --- Logika untuk MENANGANI HASIL GAMBAR (dari Kamera/Galeri) ---
    // (Sekarang tugasnya hanya menyimpan URI dan pindah ke Preview)
    val handleImageResult: (Uri) -> Unit = { uri ->
        // 1. Simpan Uri yang didapat
        selectedImageUri = uri
        // 2. Pindah ke layar Preview
        navController.navigate(ScanScreenNavigation.PREVIEW)
    }

    // --- Logika untuk MEMPROSES GAMBAR (setelah dikonfirmasi) ---
    // (Ini adalah logika lama Anda, sekarang dipisah)
    val processImage = { uri: Uri ->
        // TODO: Ganti Toast ini dengan logika upload ke NodeJS
        Toast.makeText(context, "Gambar diproses: $uri", Toast.LENGTH_SHORT).show()

        // Setelah gambar diproses, kembali ke hub
        navController.popBackStack(ScanScreenNavigation.HUB, inclusive = false)
    }

    // --- NavHost Internal ---
    NavHost(
        navController = navController,
        startDestination = ScanScreenNavigation.HUB
    ) {

        composable(ScanScreenNavigation.HUB) {
            ScanHubScreen(
                onBukaKameraClick = {
                    if (cameraPermissionState.status.isGranted) {
                        navController.navigate(ScanScreenNavigation.CAMERA)
                    } else {
                        cameraPermissionState.launchPermissionRequest()
                    }
                },
                onImageSelected = handleImageResult // Panggil handler
            )
        }

        composable(ScanScreenNavigation.CAMERA) {
            if (cameraPermissionState.status.isGranted) {
                CameraScreen(
                    onPhotoTaken = handleImageResult, // Panggil handler
                    onBack = { navController.popBackStack() }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }

        // ⬇️ --- RUTE BARU UNTUK PREVIEW --- ⬇️
        composable(ScanScreenNavigation.PREVIEW) {
            // Panggil Composable baru 'PreviewScreen'
            // Pastikan URI tidak null sebelum menampilkannya
            selectedImageUri?.let { uri ->
                PreviewScreen(
                    imageUri = uri,
                    onConfirm = {
                        // Panggil logika proses yang sebenarnya
                        processImage(uri)
                    },
                    onCancel = {
                        // Kembali ke layar hub
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}