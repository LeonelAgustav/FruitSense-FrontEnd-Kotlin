package com.example.fruitsense.ui.screen.dashboard.profile

import androidx.compose.foundation.layout.*
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onBackClick: () -> Unit
) {
    var pushNotificationsEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan Notifikasi") },
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
            SettingSwitchItem(
                title = "Notifikasi Push",
                subtitle = "Terima Pemberitahuan Tentang Buah",
                checked = pushNotificationsEnabled,
                onCheckedChange = { pushNotificationsEnabled = it }
            )
        }
    }
}

@Composable
private fun SettingSwitchItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .border( 1.dp, FruitSenseColors.Black),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, color = FruitSenseColors.Black)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = FruitSenseColors.Black)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}