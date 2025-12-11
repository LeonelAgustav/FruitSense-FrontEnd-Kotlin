package com.fruitsense.app.navigation

import android.net.Uri
import androidx.activity.compose.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.fruitsense.app.ui.theme.*
import com.fruitsense.app.data.model.*
import com.fruitsense.app.ui.screen.auth.*
import com.fruitsense.app.ui.screen.auth.forgot_password.*
import com.fruitsense.app.ui.screen.auth.verification.EmailVerificationScreen
import com.fruitsense.app.ui.screen.dashboard.DashboardScreen
import com.fruitsense.app.ui.screen.dashboard.detail.*
import com.fruitsense.app.ui.screen.dashboard.recipes.RecipesScreen
import com.fruitsense.app.ui.screen.dashboard.scan.*

@Composable
fun AppNavigator() {
    var currentScreen by remember { mutableStateOf(AppScreen.Splash) }

    var dashboardTab by remember { mutableIntStateOf(0) }
    var selectedFruitItem by remember { mutableStateOf<FruitItem?>(null) }
    var isFromForgotPassword by remember { mutableStateOf(false) }
    var autoGenerateRecipes by remember { mutableStateOf(false) }
    var selectedRecipeItem by remember { mutableStateOf<RecipeItem?>(null) }

    var scanUri by remember { mutableStateOf<Uri?>(null) }

    val scanViewModel: ScanViewModel = hiltViewModel()
    val scanUiState by scanViewModel.uiState.collectAsState()

    // Logic pindah layar otomatis jika scan sukses
    LaunchedEffect(scanUiState) {
        if (currentScreen == AppScreen.ScanProcessing) {
            if (scanUiState is ScanUiState.Success) {
                currentScreen = AppScreen.ScanResult
            }
        }
    }

    val isDashboard = currentScreen == AppScreen.Dashboard
    val isLogin = currentScreen == AppScreen.Login
    val isSplash = currentScreen == AppScreen.Splash

    // Manual Back Button Handling
    BackHandler(enabled = !isLogin && !isSplash) {
        if (isDashboard) {
            if (dashboardTab != 0) dashboardTab = 0
        } else {
            currentScreen = when (currentScreen) {
                AppScreen.ScanCamera -> AppScreen.Dashboard
                AppScreen.ScanPreview -> AppScreen.ScanCamera
                AppScreen.ScanResult -> AppScreen.Dashboard
                AppScreen.Recipes -> AppScreen.Dashboard
                AppScreen.RecipeDetail -> AppScreen.Dashboard

                AppScreen.Register -> AppScreen.Login
                AppScreen.ForgotPassword -> AppScreen.Login
                AppScreen.EmailVerification -> if (isFromForgotPassword) AppScreen.ForgotPassword else AppScreen.Register
                AppScreen.ResetPassword -> AppScreen.ForgotPassword
                else -> AppScreen.Login
            }
        }
    }

    // Routing Layar
    when (currentScreen) {
        AppScreen.Splash -> SplashScreen(
            onNavigateToDashboard = { currentScreen = AppScreen.Dashboard },
            onNavigateToLogin = { currentScreen = AppScreen.Login }
        )

        AppScreen.Login -> LoginScreen(
            onNavigateToRegister = {
                isFromForgotPassword = false
                currentScreen = AppScreen.Register
            },
            onLoginSuccess = { currentScreen = AppScreen.Dashboard },
            onNavigateToForgotPassword = {
                isFromForgotPassword = true
                currentScreen = AppScreen.ForgotPassword
            }
        )

        AppScreen.Register -> RegisterScreen(
            onNavigateToLogin = { currentScreen = AppScreen.Login },
            onRegisterSuccess = {
                isFromForgotPassword = false
                currentScreen = AppScreen.EmailVerification
            }
        )

        AppScreen.EmailVerification -> EmailVerificationScreen(
            isFromForgotPassword = isFromForgotPassword,
            onVerificationSuccess = {
                if (isFromForgotPassword) {
                    currentScreen = AppScreen.ResetPassword
                } else {
                    currentScreen = AppScreen.EmailVerificationSuccess
                }
            },
            onCancel = {
                currentScreen = if (isFromForgotPassword) AppScreen.ForgotPassword else AppScreen.Register
            }
        )

        AppScreen.ForgotPassword -> ForgotPasswordScreen(
            onNavigateToLogin = { currentScreen = AppScreen.Login },
            onCodeSent = {
                isFromForgotPassword = true
                currentScreen = AppScreen.EmailVerification
            }
        )

        AppScreen.ResetPassword -> ResetPasswordScreen(
            onPasswordResetSuccess = { currentScreen = AppScreen.ResetPasswordSuccess }
        )

        AppScreen.EmailVerificationSuccess -> AuthSuccessScreen(
            title = "Verifikasi Berhasil!",
            subtitle = "Email Anda telah terverifikasi. Silakan masuk untuk mulai.",
            buttonText = "Masuk Sekarang",
            onButtonClick = { currentScreen = AppScreen.Login }
        )

        AppScreen.ResetPasswordSuccess -> AuthSuccessScreen(
            title = "Password Diubah!",
            subtitle = "Kata sandi Anda berhasil diperbarui. Silakan login ulang.",
            buttonText = "Masuk Kembali",
            onButtonClick = { currentScreen = AppScreen.Login }
        )

        AppScreen.Dashboard -> {
            DashboardScreen(
                currentTab = dashboardTab,
                onTabChange = { dashboardTab = it },
                onLogoutClick = { currentScreen = AppScreen.Login },
                onAnalyzeClick = { fruit ->
                    selectedFruitItem = fruit
                    autoGenerateRecipes = true
                },
                onOpenCamera = { currentScreen = AppScreen.ScanCamera },
                onGallerySelected = { uri ->
                    scanUri = uri
                    currentScreen = AppScreen.ScanPreview
                },
                onRecipeClick = { recipe ->
                    selectedRecipeItem = recipe
                    currentScreen = AppScreen.RecipeDetail
                }
            )
        }

        AppScreen.ScanCamera -> {
            CameraScreen(
                onPhotoTaken = { uri ->
                    scanUri = uri
                    scanViewModel.analyzeImage(uri)
                    currentScreen = AppScreen.ScanProcessing
                },
                onBack = { currentScreen = AppScreen.Dashboard }
            )
        }

        AppScreen.ScanPreview -> {
            scanUri?.let { uri ->
                PreviewScreen(
                    imageUri = uri,
                    onRetake = { currentScreen = AppScreen.ScanCamera },
                    onAnalysisSuccess = { result ->
                        currentScreen = AppScreen.ScanResult
                    },
                    viewModel = scanViewModel
                )
            }
        }

        AppScreen.ScanProcessing -> {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        AppScreen.ScanResult -> {
            val state = scanUiState
            if (state is ScanUiState.Success) {
                ResultScreen(
                    resultData = state.fruitItem,
                    onScanAgain = {
                        dashboardTab = 0
                        currentScreen = AppScreen.Dashboard
                        scanViewModel.resetState()
                    },
                    onSaveToInventory = {
                        dashboardTab = 1
                        currentScreen = AppScreen.Dashboard
                        scanViewModel.resetState()
                    }
                )
            } else {
                LaunchedEffect(Unit) { currentScreen = AppScreen.Dashboard }
            }
        }

        AppScreen.Recipes -> {
            RecipesScreen(
                onBackClick = { currentScreen = AppScreen.Dashboard },
                onRecipeClick = { recipe ->
                    selectedRecipeItem = recipe
                    currentScreen = AppScreen.RecipeDetail
                }
            )
        }

        AppScreen.RecipeDetail -> {
            selectedRecipeItem?.let { recipe ->
                RecipesDetailScreen(
                    recipe = recipe,
                    onBackClick = { currentScreen = AppScreen.Dashboard }
                )
            }
        }

        // Tetap tambahkan else untuk keamanan jika enum bertambah di masa depan
        else -> {
            LaunchedEffect(Unit) { currentScreen = AppScreen.Dashboard }
        }
    }
}