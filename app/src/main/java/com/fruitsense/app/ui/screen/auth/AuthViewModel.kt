package com.fruitsense.app.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fruitsense.app.data.repository.AuthRepository
import com.fruitsense.app.data.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// --- STATE DATA CLASSES ---

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val loginError: String? = null,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false
)

data class RegisterUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val fullNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val registerError: String? = null,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false
)

data class ForgotPasswordUiState(
    val email: String = "",
    val emailError: String? = null,
    val isLoading: Boolean = false,
    val message: String? = null
)

data class ResetPasswordUiState(
    val password: String = "",
    val confirmPassword: String = "",
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val message: String? = null
)

// --- VIEWMODEL ---

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    // Login State
    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    // Register State
    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState: StateFlow<RegisterUiState> = _registerState.asStateFlow()

    // Forgot & Reset States
    private val _forgotPasswordState = MutableStateFlow(ForgotPasswordUiState())
    val forgotPasswordState: StateFlow<ForgotPasswordUiState> = _forgotPasswordState.asStateFlow()

    private val _resetPasswordState = MutableStateFlow(ResetPasswordUiState())
    val resetPasswordState: StateFlow<ResetPasswordUiState> = _resetPasswordState.asStateFlow()

    // --- LOGIC LOGIN ---

    fun onLoginEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> _loginState.update { it.copy(email = event.value, emailError = null, loginError = null) }
            is LoginEvent.PasswordChanged -> _loginState.update { it.copy(password = event.value, passwordError = null, loginError = null) }
            is LoginEvent.TogglePasswordVisibility -> _loginState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
        }
    }

    fun login(onSuccess: () -> Unit) {
        val currentState = _loginState.value

        if (currentState.email.isBlank() || currentState.password.isBlank()) {
            _loginState.update { it.copy(loginError = "Email dan Password wajib diisi") }
            return
        }

        viewModelScope.launch {
            _loginState.update { it.copy(isLoading = true, loginError = null) }

            repository.login(currentState.email, currentState.password)
                .collect { result ->
                    result.onSuccess {
                        // Token sudah tersimpan di Repository sebelum emit success
                        _loginState.update { it.copy(isLoading = false) }

                        launch {
                            repository.updateFcmToken().collect {
                                // Optional: Handle hasil update token (biasanya silent)
                            }
                        }

                        kotlinx.coroutines.delay(500)

                        onSuccess() // Navigasi ke Splash -> Dashboard
                    }.onFailure { error ->
                        _loginState.update { it.copy(isLoading = false, loginError = error.message ?: "Login gagal") }
                    }
                }
        }
    }

    fun clearLoginErrors() { _loginState.update { LoginUiState() } }

    // --- LOGIC REGISTER ---

    fun onRegisterEvent(event: RegisterEvent) {
        when(event) {
            is RegisterEvent.FullNameChanged -> _registerState.update { it.copy(fullName = event.value, fullNameError = null) }
            is RegisterEvent.EmailChanged -> _registerState.update { it.copy(email = event.value, emailError = null) }
            is RegisterEvent.PasswordChanged -> _registerState.update { it.copy(password = event.value, passwordError = null) }
            is RegisterEvent.ConfirmPasswordChanged -> _registerState.update { it.copy(confirmPassword = event.value, confirmPasswordError = null) }
            is RegisterEvent.TogglePasswordVisibility -> _registerState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            is RegisterEvent.ToggleConfirmPasswordVisibility -> _registerState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
        }
    }

    fun register(onSuccess: () -> Unit) {
        val state = _registerState.value

        if (state.email.isBlank() || state.password.length < 6 || state.password != state.confirmPassword) {
            _registerState.update { it.copy(registerError = "Cek kembali inputan Anda") }
            return
        }

        viewModelScope.launch {
            _registerState.update { it.copy(isLoading = true, registerError = null) }

            repository.register(state.fullName, state.email, state.password)
                .collect { result ->
                    result.onSuccess {
                        _registerState.update { it.copy(isLoading = false) }
                        onSuccess()
                    }.onFailure { error ->
                        _registerState.update { it.copy(isLoading = false, registerError = error.message ?: "Register gagal") }
                    }
                }
        }
    }

    fun clearRegisterErrors() { _registerState.update { RegisterUiState() } }

    // --- LOGIC FORGOT PASSWORD ---

    fun onForgotEvent(event: ForgotPasswordEvent) {
        when(event) {
            is ForgotPasswordEvent.EmailChanged -> _forgotPasswordState.update { it.copy(email = event.value, emailError = null) }
        }
    }

    fun sendForgotPasswordCode(onSuccess: () -> Unit) {
        val state = _forgotPasswordState.value
        if (state.email.isBlank()) {
            _forgotPasswordState.update { it.copy(emailError = "Email tidak boleh kosong") }
            return
        }

        viewModelScope.launch {
            _forgotPasswordState.update { it.copy(isLoading = true) }
            repository.forgotPassword(state.email).collect { result ->
                result.onSuccess {
                    _forgotPasswordState.update { it.copy(isLoading = false) }
                    onSuccess()
                }.onFailure {
                    _forgotPasswordState.update { it.copy(isLoading = false, emailError = it.message) }
                }
            }
        }
    }

    fun clearForgotErrors() { _forgotPasswordState.update { ForgotPasswordUiState() } }

    // --- LOGIC RESET PASSWORD ---

    fun onResetEvent(event: ResetPasswordEvent) {
        when(event) {
            is ResetPasswordEvent.PasswordChanged -> _resetPasswordState.update { it.copy(password = event.value) }
            is ResetPasswordEvent.ConfirmPasswordChanged -> _resetPasswordState.update { it.copy(confirmPassword = event.value) }
            is ResetPasswordEvent.TogglePasswordVisibility -> _resetPasswordState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            is ResetPasswordEvent.ToggleConfirmPasswordVisibility -> _resetPasswordState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
        }
    }

    fun submitResetPassword(onSuccess: () -> Unit) {
        val state = _resetPasswordState.value

        // Validasi
        if (state.password.length < 6 || state.password != state.confirmPassword) {
            _resetPasswordState.update { it.copy(passwordError = "Password tidak valid") }
            return
        }

        viewModelScope.launch {
            _resetPasswordState.update { it.copy(isLoading = true) }

            repository.resetPassword(state.password).collect { result ->
                result.onSuccess {
                    // [PERBAIKAN] Reset state ke awal agar bersih
                    _resetPasswordState.update { ResetPasswordUiState() }

                    // Panggil callback navigasi
                    onSuccess()
                }.onFailure { e ->
                    _resetPasswordState.update {
                        it.copy(
                            isLoading = false,
                            passwordError = e.message ?: "Gagal mereset password"
                        )
                    }
                }
            }
        }
    }

    fun clearResetErrors() { _resetPasswordState.update { ResetPasswordUiState() } }

    // --- LOGIC EMAIL VERIFICATION ---

    fun verifyEmail(email: String, code: String, isRecovery: Boolean, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val flow = if (isRecovery) {
                repository.verifyRecovery(email, code) // Panggil endpoint verify-recovery
            } else {
                repository.verifyEmail(email, code)    // Panggil endpoint verify-otp (register)
            }
            flow.collect { result ->
                result.onSuccess { onSuccess() }
                result.onFailure { onError(it.message ?: "Verifikasi gagal") }
            }
        }
    }
}

// --- EVENTS ---

sealed class LoginEvent {
    data class EmailChanged(val value: String) : LoginEvent()
    data class PasswordChanged(val value: String) : LoginEvent()
    object TogglePasswordVisibility : LoginEvent()
}

sealed class RegisterEvent {
    data class FullNameChanged(val value: String) : RegisterEvent()
    data class EmailChanged(val value: String) : RegisterEvent()
    data class PasswordChanged(val value: String) : RegisterEvent()
    data class ConfirmPasswordChanged(val value: String) : RegisterEvent()
    object TogglePasswordVisibility : RegisterEvent()
    object ToggleConfirmPasswordVisibility : RegisterEvent()
}

sealed class ForgotPasswordEvent {
    data class EmailChanged(val value: String) : ForgotPasswordEvent()
}

sealed class ResetPasswordEvent {
    data class PasswordChanged(val value: String) : ResetPasswordEvent()
    data class ConfirmPasswordChanged(val value: String) : ResetPasswordEvent()
    object TogglePasswordVisibility : ResetPasswordEvent()
    object ToggleConfirmPasswordVisibility : ResetPasswordEvent()
}