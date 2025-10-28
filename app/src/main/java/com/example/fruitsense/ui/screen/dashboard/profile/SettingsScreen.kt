package com.example.fruitsense.ui.screen.dashboard.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fruitsense.ui.theme.FruitSenseColors
import androidx.compose.foundation.background
import androidx.compose.foundation.border

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {
    var selectedTheme by remember { mutableStateOf("SYSTEM") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan Aplikasi") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FruitSenseColors.GreenDark,
                    titleContentColor = FruitSenseColors.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FruitSenseColors.White)
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Setelan Tema",
                style = MaterialTheme.typography.titleMedium,
                color = FruitSenseColors.Black
            )
            Spacer(modifier = Modifier.height(8.dp))

            ThemeOptionRow(
                text = "Mode Terang",
                selected = selectedTheme == "LIGHT",
                onClick = { selectedTheme = "LIGHT" }
            )
            ThemeOptionRow(
                text = "Mode Gelap",
                selected = selectedTheme == "DARK",
                onClick = { selectedTheme = "DARK" }
            )
            ThemeOptionRow(
                text = "Ikuti Sistem",
                selected = selectedTheme == "SYSTEM",
                onClick = { selectedTheme = "SYSTEM" }
            )
        }
    }
}

@Composable
private fun ThemeOptionRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick)
            .padding(vertical = 8.dp)
            .border( 1.dp, FruitSenseColors.Black),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, style = MaterialTheme.typography.bodyLarge, color = FruitSenseColors.Black)
    }
}