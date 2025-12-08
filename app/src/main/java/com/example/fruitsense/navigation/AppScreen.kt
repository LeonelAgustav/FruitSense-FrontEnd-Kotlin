package com.example.fruitsense.navigation

enum class AppScreen {
    Splash,
    Login,
    Register,
    Dashboard,
    ForgotPassword,
    ResetPassword,
    EmailVerification,
    EmailVerificationSuccess,
    ResetPasswordSuccess,

    // Fitur Dashboard
    FruitDetail,
    FruitAnalysis,
    Recipes,
    RecipeDetail,

    // Fitur Scan
    ScanCamera,
    ScanPreview,
    ScanProcessing, // [BARU] State sementara saat analisa kamera
    ScanResult
}