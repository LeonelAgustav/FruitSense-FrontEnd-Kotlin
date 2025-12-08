package com.example.fruitsense.ui.screen.dashboard.profile

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object ProfileNavigation {
    const val MENU = "profile_menu"
    const val SETTINGS = "profile_settings"
    const val NOTIFICATIONS = "profile_notifications"
}

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    // NavController untuk navigasi internal di dalam tab Profile
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = ProfileNavigation.MENU
    ) {
        // Rute 1: Menu Utama (Kode lama Anda pindah ke sini)
        composable(ProfileNavigation.MENU) {
            ProfileMenuScreen(
                onNotificationClick = {
                    navController.navigate(ProfileNavigation.NOTIFICATIONS)
                },
                onSettingsClick = {
                    navController.navigate(ProfileNavigation.SETTINGS)
                },
                onLogoutClick = {
                    viewModel.logout {
                        onLogout()
                    }
                }
            )
        }

        // Rute 2: Layar Pengaturan
        composable(ProfileNavigation.SETTINGS) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // Rute 3: Layar Notifikasi
        composable(ProfileNavigation.NOTIFICATIONS) {
            NotificationScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}