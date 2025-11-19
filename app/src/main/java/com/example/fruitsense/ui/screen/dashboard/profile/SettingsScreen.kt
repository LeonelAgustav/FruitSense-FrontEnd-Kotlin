package com.example.fruitsense.ui.screen.dashboard.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fruitsense.data.UserPreferences
import com.example.fruitsense.ui.theme.FruitSenseColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModel.Factory(UserPreferences(context))
    )

    val selectedTheme by viewModel.theme.collectAsState()

    Scaffold(
        topBar = {
            // Bungkus TopAppBar & Divider dalam Column
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Pengaturan",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
                // --- PEMBATAS / DIVIDER ---
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Tampilan Aplikasi",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = FruitSenseColors.GreenDark,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            ThemeOptionCard(
                title = "Mode Terang",
                subtitle = "Tampilan cerah klasik",
                icon = Icons.Outlined.LightMode,
                isSelected = selectedTheme == "LIGHT",
                onClick = { viewModel.updateTheme("LIGHT") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ThemeOptionCard(
                title = "Mode Gelap",
                subtitle = "Nyaman untuk mata",
                icon = Icons.Outlined.DarkMode,
                isSelected = selectedTheme == "DARK",
                onClick = { viewModel.updateTheme("DARK") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ThemeOptionCard(
                title = "Ikuti Sistem",
                subtitle = "Menyesuaikan pengaturan HP",
                icon = Icons.Outlined.SettingsSystemDaydream,
                isSelected = selectedTheme == "SYSTEM",
                onClick = { viewModel.updateTheme("SYSTEM") }
            )
        }
    }
}

@Composable
private fun ThemeOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (isSelected) {
        FruitSenseColors.GreenDark.copy(alpha = 0.1f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    val borderColor = if (isSelected) {
        FruitSenseColors.GreenDark
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }

    val textColor = MaterialTheme.colorScheme.onSurface

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = borderColor
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) FruitSenseColors.GreenDark else Color.Gray,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) FruitSenseColors.GreenDark else textColor
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            RadioButton(
                selected = isSelected,
                onClick = null,
                colors = RadioButtonDefaults.colors(
                    selectedColor = FruitSenseColors.GreenDark,
                    unselectedColor = Color.LightGray
                )
            )
        }
    }
}