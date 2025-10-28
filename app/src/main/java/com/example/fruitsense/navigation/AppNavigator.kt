package com.example.fruitsense.navigation

import androidx.compose.runtime.*
import com.example.fruitsense.ui.screen.auth.LoginScreen
import com.example.fruitsense.ui.screen.auth.RegisterScreen
import com.example.fruitsense.ui.screen.dashboard.DashboardScreen

@Composable
fun AppNavigator() {
    var currentScreen by remember { mutableStateOf(AppScreen.Login) }

    when (currentScreen) {
        AppScreen.Login -> {
            LoginScreen(
                onNavigateToRegister = { currentScreen = AppScreen.Register },
                onLoginSuccess = { currentScreen = AppScreen.Dashboard }
            )
        }
        AppScreen.Register -> {
            RegisterScreen(
                onNavigateToLogin = { currentScreen = AppScreen.Login },
                onRegisterSuccess = { currentScreen = AppScreen.Login }
            )
        }
        AppScreen.Dashboard -> {
            DashboardScreen(
                onLogoutClick = { currentScreen = AppScreen.Login },
                onNotificationClick = { /* Handle notification click */ },
                onSettingsClick = { /* Handle settings click */ }
            )
        }
    }
}