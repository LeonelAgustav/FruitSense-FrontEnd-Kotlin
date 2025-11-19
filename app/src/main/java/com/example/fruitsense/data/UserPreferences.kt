package com.example.fruitsense.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Membuat instance DataStore bernama "settings"
// Ini seperti database kecil khusus untuk pengaturan aplikasi
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferences(private val context: Context) {

    companion object {
        // Kunci untuk menyimpan tema. Nilainya nanti: "LIGHT", "DARK", atau "SYSTEM"
        val THEME_KEY = stringPreferencesKey("app_theme")
    }

    // Membaca preferensi tema (Default: SYSTEM jika belum ada settingan)
    val themeFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[THEME_KEY] ?: "SYSTEM"
        }

    // Fungsi untuk menyimpan pilihan tema user
    suspend fun saveTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
    }
}