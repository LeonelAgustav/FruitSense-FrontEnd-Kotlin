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
import com.example.fruitsense.data.UserPreferences
import com.example.fruitsense.navigation.AppNavigator
import com.example.fruitsense.ui.screen.dashboard.profile.ProfileViewModel
import com.example.fruitsense.ui.theme.FruitSenseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // 1. Inisialisasi DataStore & ViewModel di Root Aplikasi
            val context = LocalContext.current
            val viewModel: ProfileViewModel = viewModel(
                factory = ProfileViewModel.Factory(UserPreferences(context))
            )

            // 2. Ambil status tema terkini (LIGHT, DARK, atau SYSTEM)
            val appTheme by viewModel.theme.collectAsState()

            // 3. Terapkan tema ke seluruh aplikasi
            FruitSenseTheme(appTheme = appTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    // Background akan otomatis berubah (Putih/Hitam) mengikuti tema
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigator()
                }
            }
        }
    }
}