package com.fruitsense.app.ui.screen.auth

import androidx.compose.foundation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.text.font.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.*
import com.fruitsense.app.R
import com.fruitsense.app.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

// --- View Model Khusus Splash ---
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _isLoggedIn = MutableStateFlow<Boolean?>(null) // null = checking
    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn

    fun checkSession() {
        viewModelScope.launch {
            _isLoggedIn.value = null // Reset ke loading state

            // Beri delay sedikit agar DataStore punya waktu sinkronisasi (penting setelah login)
            delay(1000)

            val token = authRepository.getSessionToken().first()
            val hasSession = !token.isNullOrEmpty()

            if (hasSession) {
                // [BARU] Update FCM Token secara background saat splash screen
                // Kita gunakan launch terpisah agar tidak memblokir UI splash screen
                launch {
                    authRepository.updateFcmToken().collect {
                        // Silent update
                    }
                }
            }

            _isLoggedIn.value = hasSession
        }
    }
}

// --- UI Screen ---
@Composable
fun SplashScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    // --- ANIMATION LOGIC ---
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoScale"
    )

    LaunchedEffect(Unit) {
        viewModel.checkSession()
    }

    LaunchedEffect(isLoggedIn) {
        when (isLoggedIn) {
            true -> onNavigateToDashboard()
            false -> onNavigateToLogin()
            null -> { /* Loading */ }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background), // Menggunakan warna background tema baru
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.fruitsense),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(180.dp)
                    .scale(scale) // Terapkan animasi
            )
        }
    }
}