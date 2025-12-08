package com.example.fruitsense.ui.screen.dashboard.history

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.fruitsense.data.model.FruitItem
import com.example.fruitsense.ui.theme.FruitSenseColors

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val deleteMessage by viewModel.deleteMessage.collectAsState()
    val selectedIds by viewModel.selectedIds.collectAsState()
    val context = LocalContext.current

    val isSelectionMode = selectedIds.isNotEmpty()

    val isAllSelected = if (uiState is HistoryUiState.Success) {
        selectedIds.containsAll((uiState as HistoryUiState.Success).data.map { it.id })
    } else false

    LaunchedEffect(Unit) {
        viewModel.loadHistory()
    }

    LaunchedEffect(deleteMessage) {
        deleteMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearDeleteMessage()
        }
    }

    // --- Dialogs ---
    var showDeleteDialog by remember { mutableStateOf(false) }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus ${selectedIds.size} Riwayat?") },
            text = { Text("Riwayat yang dipilih akan dihapus permanen.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteSelectedItems()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Hapus") }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") } }
        )
    }

    var itemToDeleteSingle by remember { mutableStateOf<FruitItem?>(null) }
    if (itemToDeleteSingle != null) {
        AlertDialog(
            onDismissRequest = { itemToDeleteSingle = null },
            title = { Text("Hapus Riwayat?") },
            text = { Text("Hapus riwayat scan '${itemToDeleteSingle?.name}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        itemToDeleteSingle?.let { viewModel.deleteItem(it.id) }
                        itemToDeleteSingle = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Hapus") }
            },
            dismissButton = { TextButton(onClick = { itemToDeleteSingle = null }) { Text("Batal") } }
        )
    }

    // --- UI Structure with Scaffold ---
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // Header ditempatkan di topBar agar layout stabil
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isSelectionMode) {
                        // Header Selection
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { viewModel.clearSelection() }) {
                                Icon(Icons.Default.Close, contentDescription = "Batal")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${selectedIds.size} Dipilih",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextButton(onClick = { viewModel.toggleSelectAll() }) {
                                Text(if (isAllSelected) "Batal Semua" else "Pilih Semua")
                            }
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(Icons.Default.Delete, contentDescription = "Hapus Terpilih", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    } else {
                        // Header Normal
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Riwayat Scan", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = FruitSenseColors.GreenDark)
                                Text("Aktivitas deteksi sebelumnya", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    ) { paddingValues ->
        // Content Area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is HistoryUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = FruitSenseColors.GreenDark)
                    }
                }
                is HistoryUiState.Success -> {
                    // Padding horizontal dipindah ke sini
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(
                            top = 8.dp,
                            bottom = 100.dp,
                            start = 24.dp,
                            end = 24.dp
                        )
                    ) {
                        items(state.data, key = { it.id }) { item ->
                            val isSelected = selectedIds.contains(item.id)

                            HistoryItemCardSelectable(
                                item = item,
                                isSelected = isSelected,
                                isSelectionMode = isSelectionMode,
                                onLongClick = { viewModel.toggleSelection(item.id) },
                                onClick = {
                                    if (isSelectionMode) viewModel.toggleSelection(item.id)
                                    // Tambahkan else navigation di sini jika nanti ada detail history
                                },
                                onDeleteClick = { itemToDeleteSingle = item }
                            )
                        }
                    }
                }
                is HistoryUiState.Empty -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Outlined.Inventory2, null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Belum ada riwayat", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                is HistoryUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadHistory() }, colors = ButtonDefaults.buttonColors(containerColor = FruitSenseColors.GreenDark)) {
                                Text("Coba Lagi")
                            }
                        }
                    }
                }
            }
        }
    }
}

// ... Component HistoryItemCardSelectable tetap sama, pastikan importnya benar
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryItemCardSelectable(
    item: FruitItem,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onLongClick: () -> Unit,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    // Data sisa hari
    val daysLeft = item.expiryDays ?: 0

    val (statusText, statusColor) = when {
        daysLeft < 0 -> "Expired" to MaterialTheme.colorScheme.error
        daysLeft == 0 -> "Hari Ini" to Color(0xFFFF9800)
        daysLeft <= 2 -> "$daysLeft Hari" to Color(0xFFFF9800)
        else -> "$daysLeft Hari" to FruitSenseColors.GreenDark
    }

    val cardColor = if (isSelected) FruitSenseColors.GreenDark.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
    val borderStroke = BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = borderStroke, // Border saat dipilih
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 0.dp else 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        Box {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Checkbox (Selection Mode)
                if (isSelectionMode) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onClick() },
                        colors = CheckboxDefaults.colors(
                            checkedColor = FruitSenseColors.GreenDark,
                            uncheckedColor = Color.Gray
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                // Gambar Kecil
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(80.dp)
                ) {
                    if (!item.imageUri.isNullOrEmpty()) {
                        AsyncImage(
                            model = item.imageUri,
                            contentDescription = item.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize().background(Color.Gray.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Inventory2, null, tint = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Informasi
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(end = 32.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Grade Badge (Jika bukan selection mode)
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = FruitSenseColors.GreenDark.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = " ${item.grade ?: "?"}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = FruitSenseColors.GreenDark,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Baris Status Expired (Menggunakan data dari backend)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Timer, null, modifier = Modifier.size(12.dp), tint = statusColor)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                }
            }
            Column(
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // delete
                if (!isSelectionMode) {
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            Icons.Outlined.Delete,
                            null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}