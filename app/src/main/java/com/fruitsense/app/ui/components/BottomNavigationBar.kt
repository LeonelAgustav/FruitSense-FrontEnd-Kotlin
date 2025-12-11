package com.fruitsense.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import com.fruitsense.app.ui.theme.FruitSenseColors

@Composable
fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Definisi Item Navigasi
    val items = listOf(
        NavigationItem("Scan", Icons.Filled.CameraAlt, Icons.Outlined.CameraAlt),
        NavigationItem("Inventory", Icons.Filled.Inventory2, Icons.Outlined.Inventory2),
        NavigationItem("Resep", Icons.Filled.RestaurantMenu, Icons.Outlined.RestaurantMenu), // Mengganti ReceiptLong agar lebih relevan
        NavigationItem("Riwayat", Icons.Filled.History, Icons.Outlined.History),
        NavigationItem("Profil", Icons.Filled.Person, Icons.Outlined.Person)
    )

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = selectedTab == index

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary, // Forest Green
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), // Highlight hijau muda transparan
                    unselectedIconColor = FruitSenseColors.NeutralGrey,
                    unselectedTextColor = FruitSenseColors.NeutralGrey
                )
            )
        }
    }
}

// Data Class Helper untuk Item Navigasi
private data class NavigationItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)