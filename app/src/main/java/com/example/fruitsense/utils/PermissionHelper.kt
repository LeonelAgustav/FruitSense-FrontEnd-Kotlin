package com.example.fruitsense.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestPermissionsEffect(
    onPermissionsGranted: () -> Unit = {}
) {
    // Daftar izin yang dibutuhkan
    val permissions = remember {
        mutableListOf(Manifest.permission.CAMERA).apply {
            // Tambahkan izin notifikasi hanya untuk Android 13+ (Tiramisu)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val permissionState = rememberMultiplePermissionsState(permissions = permissions)

    // State untuk dialog "Izin Ditolak Permanen"
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Effect: Cek izin saat pertama kali composable di-load
    LaunchedEffect(Unit) {
        if (!permissionState.allPermissionsGranted) {
            permissionState.launchMultiplePermissionRequest()
        } else {
            onPermissionsGranted()
        }
    }

    // Pantau perubahan status izin
    LaunchedEffect(permissionState.allPermissionsGranted) {
        if (permissionState.allPermissionsGranted) {
            onPermissionsGranted()
        }
    }

    // Jika izin ditolak permanen (user klik "Don't ask again"), tampilkan dialog arahkan ke Settings
    if (permissionState.shouldShowRationale || (!permissionState.allPermissionsGranted && !permissionState.shouldShowRationale && permissionState.revokedPermissions.isNotEmpty())) {
        // Logika ini bisa disesuaikan: apakah mau maksa user atau tidak.
        // Di sini kita buat simple: Jika user menolak, kita biarkan saja (tidak memaksa loop).
        // Tapi jika Anda ingin "Memaksa", bisa gunakan showSettingsDialog = true di kondisi tertentu.
    }
}

@Composable
fun PermissionSettingsDialog(
    onDismiss: () -> Unit,
    context: Context
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Izin Diperlukan") },
        text = { Text("Aplikasi ini membutuhkan izin Kamera dan Notifikasi agar dapat berfungsi dengan baik. Mohon aktifkan di Pengaturan.") },
        confirmButton = {
            TextButton(onClick = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
                onDismiss()
            }) {
                Text("Buka Pengaturan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}