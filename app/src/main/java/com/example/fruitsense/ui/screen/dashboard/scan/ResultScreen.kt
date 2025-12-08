package com.example.fruitsense.ui.screen.dashboard.scan

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.fruitsense.data.model.FruitItem
import com.example.fruitsense.ui.theme.FruitSenseColors

@Composable
fun ResultScreen(
    resultData: FruitItem,
    onScanAgain: () -> Unit,
    onSaveToInventory: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        // Image Header
        Box(modifier = Modifier
            .fillMaxWidth().height(400.dp)) {
            AsyncImage(
                model = resultData.imageUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            IconButton(
                onClick = onScanAgain,
                modifier = Modifier.padding(16.dp).statusBarsPadding()
                    .background(Color.Black.copy(alpha = 0.4f), CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
        }

        // Details
        Column(
            modifier = Modifier.align(Alignment.BottomCenter)
                .fillMaxWidth().fillMaxHeight(fraction = 0.6f)
                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 32.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text("Hasil Deteksi", style = MaterialTheme.typography.labelMedium)
                    Text(resultData.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = FruitSenseColors.GreenDark)
                }
                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = FruitSenseColors.GreenDark,
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = resultData.grade ?: "?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(resultData.aiDescription, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(32.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(onClick = onScanAgain, modifier = Modifier.weight(1f)) {
                    Text("Scan Lagi")
                }
                Button(onClick = onSaveToInventory, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = FruitSenseColors.GreenDark)) {
                    Text("Simpan")
                }
            }
        }
    }
}