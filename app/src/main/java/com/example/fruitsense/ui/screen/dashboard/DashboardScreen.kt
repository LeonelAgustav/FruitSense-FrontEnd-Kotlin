package com.example.fruitsense.ui.screen.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fruitsense.ui.theme.FruitSenseColors // Tidak lagi dipakai untuk background utama
import com.example.fruitsense.ui.components.BottomNavigationBar
import com.example.fruitsense.ui.screen.dashboard.history.HistoryScreen
import com.example.fruitsense.ui.screen.dashboard.inventory.InventoryScreen
import com.example.fruitsense.ui.screen.dashboard.profile.ProfileScreen
import com.example.fruitsense.ui.screen.dashboard.scan.ScanScreen

@Composable
fun DashboardScreen(
    onNotificationClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    // State untuk mengontrol visibilitas Navbar
    var showBottomBar by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                // Padding ini yang membuat konten berhenti sebelum navbar
                // Area kosong di bawahnya akan mengikuti warna background Box di atas
                .padding(bottom = if (showBottomBar) 80.dp else 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (selectedTab) {
                0 -> ScanScreen(
                    onShowBottomBar = { isVisible ->
                        showBottomBar = isVisible
                    }
                )
                1 -> {
                    showBottomBar = true
                    InventoryScreen()
                }
                2 -> {
                    showBottomBar = true
                    HistoryScreen()
                }
                3 -> {
                    showBottomBar = true
                    ProfileScreen(onLogout = onLogoutClick)
                }
            }
        }

        // Bottom Navigation Bar
        AnimatedVisibility(
            visible = showBottomBar,
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = {
                    selectedTab = it
                    showBottomBar = true
                }
            )
        }
    }
}