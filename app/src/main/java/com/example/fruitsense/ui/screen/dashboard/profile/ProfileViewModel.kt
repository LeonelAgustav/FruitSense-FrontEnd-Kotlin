package com.example.fruitsense.ui.screen.dashboard.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fruitsense.data.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(private val userPreferences: UserPreferences) : ViewModel() {

    // StateFlow ini akan diamati oleh UI.
    // Jika data di UserPreferences berubah, variabel ini otomatis update.
    val theme: StateFlow<String> = userPreferences.themeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "SYSTEM"
        )

    // Fungsi yang dipanggil saat user mengklik tombol tema
    fun updateTheme(newTheme: String) {
        viewModelScope.launch {
            userPreferences.saveTheme(newTheme)
        }
    }

    // Factory: Diperlukan agar kita bisa mengirim "UserPreferences" ke dalam ViewModel
    companion object {
        fun Factory(userPreferences: UserPreferences): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ProfileViewModel(userPreferences)
            }
        }
    }
}