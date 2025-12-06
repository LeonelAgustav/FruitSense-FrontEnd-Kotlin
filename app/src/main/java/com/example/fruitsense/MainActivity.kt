package com.example.fruitsense

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.fruitsense.data.UserPreferences
import com.example.fruitsense.navigation.AppNavigator
import com.example.fruitsense.ui.screen.dashboard.profile.ProfileViewModel
import com.example.fruitsense.ui.theme.FruitSenseTheme
import com.example.fruitsense.utils.NotificationHelper
import com.example.fruitsense.utils.RequestPermissionsEffect
import com.example.fruitsense.worker.FruitExpiryWorker
import java.util.concurrent.TimeUnit
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Setup Channel Notifikasi
        val notificationHelper = NotificationHelper(this)
        notificationHelper.createNotificationChannel()

        // 2. Jadwalkan Background Worker
        setupPeriodicWork()

        enableEdgeToEdge()
        setContent {
            // Popup Izin
            RequestPermissionsEffect()

            // [PERBAIKAN] Gunakan hiltViewModel(), hapus Factory manual
            val viewModel: ProfileViewModel = hiltViewModel()

            val appTheme by viewModel.theme.collectAsState()

            FruitSenseTheme(appTheme = appTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigator()
                }
            }
        }
    }

    private fun setupPeriodicWork() {
        val fruitWorkRequest = PeriodicWorkRequestBuilder<FruitExpiryWorker>(12, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "FruitExpiryCheck",
            ExistingPeriodicWorkPolicy.KEEP,
            fruitWorkRequest
        )
    }
}