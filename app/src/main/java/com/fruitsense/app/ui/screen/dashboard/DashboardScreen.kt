package com.fruitsense.app.ui.screen.dashboard

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fruitsense.app.data.model.*
import com.fruitsense.app.ui.components.BottomNavigationBar
import com.fruitsense.app.ui.screen.dashboard.history.HistoryScreen
import com.fruitsense.app.ui.screen.dashboard.inventory.InventoryScreen
import com.fruitsense.app.ui.screen.dashboard.profile.ProfileScreen
import com.fruitsense.app.ui.screen.dashboard.recipes.RecipesScreen
import com.fruitsense.app.ui.screen.dashboard.scan.ScanHubScreen

@Composable
fun DashboardScreen(
    currentTab: Int,
    onTabChange: (Int) -> Unit,
    onLogoutClick: () -> Unit,
    onAnalyzeClick: (FruitItem) -> Unit,
    onRecipeClick: (RecipeItem) -> Unit, // Callback navigasi resep
    onOpenCamera: () -> Unit,
    onGallerySelected: (Uri) -> Unit
) {
    val showFab = currentTab != 0 && currentTab != 4

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomNavigationBar(
                selectedTab = currentTab,
                onTabSelected = onTabChange
            )
        },
        floatingActionButton = {
            if (showFab) {
                FloatingActionButton(
                    onClick = onOpenCamera,
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(Icons.Default.Camera, "Quick Scan", modifier = Modifier.size(32.dp))
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (currentTab) {
                0 -> ScanHubScreen(onBukaKameraClick = onOpenCamera, onImageSelected = onGallerySelected)
                1 -> InventoryScreen(
                    onAnalyzeClick = onAnalyzeClick,
                    onNavigateToRecipe = onRecipeClick
                )
                2 -> RecipesScreen(onBackClick = { onTabChange(0) }, onRecipeClick = onRecipeClick)
                3 -> HistoryScreen()
                4 -> ProfileScreen(onLogout = onLogoutClick)
            }
        }
    }
}