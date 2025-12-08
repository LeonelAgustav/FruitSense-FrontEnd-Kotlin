package com.example.fruitsense.ui.screen.dashboard.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fruitsense.data.UserPreferences
import com.example.fruitsense.data.model.UserProfile
import com.example.fruitsense.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject

// State untuk UI Profile
sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val user: UserProfile) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context // Butuh context untuk uriToFile
) : ViewModel() {

    // --- Theme State ---
    val theme: StateFlow<String> = userPreferences.themeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "SYSTEM"
        )

    // --- Profile State ---
    private val _profileState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val profileState: StateFlow<ProfileUiState> = _profileState.asStateFlow()

    // --- Update Status ---
    private val _updateStatus = MutableStateFlow<String?>(null) // null = idle, string = message
    val updateStatus: StateFlow<String?> = _updateStatus.asStateFlow()

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileUiState.Loading

            authRepository.getUserProfile().collect { result ->
                result.onSuccess { profile ->
                    _profileState.value = ProfileUiState.Success(profile)
                }.onFailure { error ->
                    _profileState.value = ProfileUiState.Error(error.message ?: "Gagal memuat profil")
                }
            }
        }
    }

    fun updateProfile(newName: String, avatarUri: Uri?) {
        viewModelScope.launch {
            _isUpdating.value = true
            _updateStatus.value = null

            val file = if (avatarUri != null) uriToFile(avatarUri) else null

            authRepository.updateProfile(newName, file).collect { result ->
                result.onSuccess {
                    _updateStatus.value = "Profil berhasil diperbarui!"
                    loadProfile() // Refresh data profil
                }.onFailure {
                    _updateStatus.value = "Gagal: ${it.message}"
                }
                _isUpdating.value = false
            }
        }
    }

    fun clearUpdateStatus() {
        _updateStatus.value = null
    }

    fun updateTheme(newTheme: String) {
        viewModelScope.launch {
            userPreferences.saveTheme(newTheme)
        }
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onLogoutSuccess()
        }
    }

    // Helper: Uri ke File
    private fun uriToFile(selectedImg: Uri): File {
        val contentResolver = context.contentResolver
        val myFile = File.createTempFile("avatar_temp", ".jpg", context.cacheDir)
        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()
        return myFile
    }
}