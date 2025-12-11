package com.fruitsense.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.fruitsense.app.navigation.AppNavigator
import com.fruitsense.app.ui.screen.dashboard.profile.ProfileViewModel
import com.fruitsense.app.ui.theme.FruitSenseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Ambil Theme Setting dari ProfileViewModel
            val profileViewModel: ProfileViewModel = hiltViewModel()
            val currentTheme by profileViewModel.theme.collectAsState()

            // [BARU] Panggil fungsi permission handler
            NotificationPermissionHandler()

            FruitSenseTheme(appTheme = currentTheme) {
                // Panggil Navigator Anda
                AppNavigator()
            }
        }
    }
}

// [BARU] Composable Khusus untuk Handle Izin Notifikasi
@Composable
fun NotificationPermissionHandler() {
    val context = LocalContext.current

    // Launcher untuk request permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // Izin diberikan
            } else {
                // Izin ditolak, bisa tampilkan pesan edukasi jika perlu
                Toast.makeText(context, "Notifikasi dimatikan. Anda mungkin melewatkan info buah busuk.", Toast.LENGTH_LONG).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        // Cek apakah Android 13 (Tiramisu) atau lebih baru
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionStatus = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )

            // Jika belum diizinkan, minta izin
            if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}