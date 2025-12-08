package com.example.fruitsense.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// Extension untuk DataStore (Top Level)
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferences @Inject constructor(private val context: Context) {

    companion object {
        val THEME_KEY = stringPreferencesKey("app_theme")
        val TOKEN_KEY = stringPreferencesKey("user_token") // Token Login Utama
        val RESET_TOKEN_KEY = stringPreferencesKey("reset_token") // [BARU] Token Sementara untuk Reset Password
    }

    // --- TEMA ---
    val themeFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[THEME_KEY] ?: "SYSTEM"
        }

    suspend fun saveTheme(theme: String) {
        context.dataStore.edit { it[THEME_KEY] = theme }
    }

    // --- AUTH TOKEN (LOGIN) ---
    val tokenFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[TOKEN_KEY]
        }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { it[TOKEN_KEY] = token }
    }

    suspend fun clearToken() {
        context.dataStore.edit { it.remove(TOKEN_KEY) }
    }

    // --- RESET TOKEN (SEMENTARA) ---
    val resetTokenFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[RESET_TOKEN_KEY]
        }

    suspend fun saveResetToken(token: String) {
        context.dataStore.edit { it[RESET_TOKEN_KEY] = token }
    }

    suspend fun clearResetToken() {
        context.dataStore.edit { it.remove(RESET_TOKEN_KEY) }
    }
}