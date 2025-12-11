package com.fruitsense.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// [PENTING] Anotasi ini WAJIB ada agar Hilt jalan
@HiltAndroidApp
class FruitSenseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Konfigurasi lain (jika ada) bisa ditaruh di sini
    }
}