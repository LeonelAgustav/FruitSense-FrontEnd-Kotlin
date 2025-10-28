package com.example.fruitsense.ui.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fruitsense.ui.theme.FruitSenseColors
import com.example.fruitsense.ui.components.BottomNavigationBar
import com.example.fruitsense.ui.screen.dashboard.profile.ProfileScreen
import com.example.fruitsense.ui.screen.dashboard.scan.ScanScreen

@Composable
fun DashboardScreen(
    onNotificationClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FruitSenseColors.White)
    ) {
        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (selectedTab) {
                0 -> ScanScreen()
                1 -> InventoryScreen()
                2 -> HistoryScreen()
                3 -> ProfileScreen(
                    onLogout = onLogoutClick
                )
            }
        }

        // Bottom Navigation Bar
        BottomNavigationBar(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}