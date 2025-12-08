package com.example.fruitsense.ui.screen.dashboard

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fruitsense.data.model.*
import com.example.fruitsense.ui.components.BottomNavigationBar
import com.example.fruitsense.ui.screen.dashboard.recipes.RecipesScreen
import com.example.fruitsense.ui.screen.dashboard.history.HistoryScreen
import com.example.fruitsense.ui.screen.dashboard.inventory.InventoryScreen
import com.example.fruitsense.ui.screen.dashboard.profile.ProfileScreen
import com.example.fruitsense.ui.screen.dashboard.scan.ScanHubScreen

@Composable
fun DashboardScreen(
    currentTab: Int,
    onTabChange: (Int) -> Unit,
    onLogoutClick: () -> Unit,
    onAnalyzeClick: (FruitItem) -> Unit,
    onRecipeClick: (RecipeItem) -> Unit,
    onOpenCamera: () -> Unit,
    onGallerySelected: (Uri) -> Unit
) {
    val showBottomBar by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (showBottomBar) 100.dp else 0.dp)
                .navigationBarsPadding()
        ) {
            when (currentTab) {
                0 -> { // Scan
                    ScanHubScreen(
                        onBukaKameraClick = onOpenCamera,
                        onImageSelected = onGallerySelected
                    )
                }
                1 -> { // Inventory
                    InventoryScreen(
                        onAnalyzeClick = onAnalyzeClick
                    )
                }
                2 -> { // Recipes
                    RecipesScreen(
                        onBackClick = { onTabChange(0) },
                        onRecipeClick = onRecipeClick
                    )
                }
                3 -> { // History
                    HistoryScreen()
                }
                4 -> { // Profile
                    ProfileScreen(onLogout = onLogoutClick)
                }
            }
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavigationBar(
                selectedTab = currentTab,
                onTabSelected = onTabChange
            )
        }
    }
}