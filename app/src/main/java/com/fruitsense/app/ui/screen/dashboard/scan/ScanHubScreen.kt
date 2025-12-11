package com.fruitsense.app.ui.screen.dashboard.scan

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ScanHubScreen(
    onBukaKameraClick: () -> Unit,
    onImageSelected: (Uri) -> Unit
) {
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) onImageSelected(uri)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Pilih Mode Scan",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Deteksi kesegaran buah secara instan",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Pilihan 1: Single Scan (Kamera)
        ScanModeCard(
            title = "Single Scan",
            subtitle = "Gunakan kamera untuk satu buah",
            icon = Icons.Default.CameraAlt,
            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
            iconColor = MaterialTheme.colorScheme.onPrimaryContainer,
            onClick = onBukaKameraClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Pilihan 2: Batch/Gallery Scan
        ScanModeCard(
            title = "Gallery Scan",
            subtitle = "Pilih foto dari galeri",
            icon = Icons.Default.PhotoLibrary,
            backgroundColor = MaterialTheme.colorScheme.surface,
            iconColor = MaterialTheme.colorScheme.primary,
            isOutlined = true,
            onClick = { galleryLauncher.launch("image/*") }
        )
    }
}

@Composable
fun ScanModeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    backgroundColor: Color,
    iconColor: Color,
    isOutlined: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.large, // 24.dp
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = if (isOutlined) BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(if (isOutlined) MaterialTheme.colorScheme.primary.copy(alpha=0.1f) else MaterialTheme.colorScheme.background.copy(alpha=0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(32.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isOutlined) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isOutlined) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha=0.8f)
                )
            }
        }
    }
}