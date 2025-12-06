package com.example.fruitsense.ui.screen.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fruitsense.R
import com.example.fruitsense.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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
            _isLoggedIn.value = !token.isNullOrEmpty()
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

    LaunchedEffect(Unit) {
        viewModel.checkSession()
    }

    LaunchedEffect(isLoggedIn) {
        when (isLoggedIn) {
            true -> onNavigateToDashboard()
            false -> onNavigateToLogin()
            null -> { /* Masih loading/checking, do nothing */ }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.fruitsense),
            contentDescription = "Logo",
            modifier = Modifier.size(180.dp)
        )
    }
}