package com.example.fruitsense.ui.screen.dashboard.scan

import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.google.accompanist.permissions.*
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object ScanScreenNavigation {
    const val HUB = "scan_hub"
    const val CAMERA = "scan_camera"
    const val PREVIEW = "scan_preview"
    const val RESULT = "scan_result" // Rute baru
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanScreen(
    onShowBottomBar: (Boolean) -> Unit // Callback dari DashboardScreen
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    // State untuk menyimpan URI gambar sementara di memory
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Mengontrol Visibilitas BottomBar berdasarkan Rute Navigasi
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(currentRoute) {
        // Tampilkan BottomBar HANYA jika berada di HUB
        val isHub = currentRoute == ScanScreenNavigation.HUB
        onShowBottomBar(isHub)
    }

    // Handler saat gambar didapat (dari Kamera/Galeri)
    val handleImageResult: (Uri) -> Unit = { uri ->
        selectedImageUri = uri
        navController.navigate(ScanScreenNavigation.PREVIEW)
    }

    // Handler proses gambar akhir (Simulasi Analisa AI)
    val processImage = { uri: Uri ->
        // Disini biasanya Anda melakukan upload ke server
        // Setelah sukses, kita navigasi ke ResultScreen

        // Encode URI agar aman dikirim lewat navigasi string URL
        val encodedUri = URLEncoder.encode(uri.toString(), StandardCharsets.UTF_8.toString())
        navController.navigate("${ScanScreenNavigation.RESULT}/$encodedUri")
    }

    NavHost(
        navController = navController,
        startDestination = ScanScreenNavigation.HUB
    ) {
        // 1. HUB SCREEN
        composable(ScanScreenNavigation.HUB) {
            ScanHubScreen(
                onBukaKameraClick = {
                    if (cameraPermissionState.status.isGranted) {
                        navController.navigate(ScanScreenNavigation.CAMERA)
                    } else {
                        cameraPermissionState.launchPermissionRequest()
                    }
                },
                onImageSelected = handleImageResult
            )
        }

        // 2. CAMERA SCREEN
        composable(ScanScreenNavigation.CAMERA) {
            if (cameraPermissionState.status.isGranted) {
                CameraScreen(
                    onPhotoTaken = handleImageResult,
                    onBack = { navController.popBackStack() }
                )
            } else {
                LaunchedEffect(Unit) { navController.popBackStack() }
            }
        }

        // 3. PREVIEW SCREEN
        composable(ScanScreenNavigation.PREVIEW) {
            if (selectedImageUri != null) {
                PreviewScreen(
                    imageUri = selectedImageUri!!,
                    onConfirm = {
                        // Panggil fungsi proses gambar -> Lanjut ke Result
                        processImage(selectedImageUri!!)
                    },
                    onCancel = { navController.popBackStack() }
                )
            }
        }

        // 4. RESULT SCREEN (Halaman Baru)
        // Menerima argumen URI gambar
        composable(
            route = "${ScanScreenNavigation.RESULT}/{imageUri}",
            arguments = listOf(navArgument("imageUri") { type = NavType.StringType })
        ) { backStackEntry ->
            val uriString = backStackEntry.arguments?.getString("imageUri")
            val imageUri = Uri.parse(uriString)

            ResultScreen(
                imageUri = imageUri,
                // Data di bawah ini nantinya didapat dari API response
                fruitName = "Jeruk Mandarin",
                freshness = 88,
                grade = "B",
                onScanAgain = {
                    // Kembali ke Hub dan bersihkan stack
                    navController.popBackStack(ScanScreenNavigation.HUB, inclusive = false)
                },
                onSaveToInventory = {
                    Toast.makeText(context, "Disimpan ke Inventory", Toast.LENGTH_SHORT).show()
                    navController.popBackStack(ScanScreenNavigation.HUB, inclusive = false)
                }
            )
        }
    }
}