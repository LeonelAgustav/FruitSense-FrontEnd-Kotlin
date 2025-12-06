package com.example.fruitsense.ui.screen.dashboard.inventory

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.fruitsense.data.model.FruitItem
import com.example.fruitsense.ui.theme.FruitSenseColors

@Composable
fun InventoryScreen(
    onAnalyzeClick: (FruitItem) -> Unit,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val deleteMessage by viewModel.deleteMessage.collectAsState()
    val selectedIds by viewModel.selectedIds.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val isSelectionMode = selectedIds.isNotEmpty()

    val isAllSelected = if (uiState is InventoryUiState.Success) {
        selectedIds.containsAll((uiState as InventoryUiState.Success).data.map { it.id })
    } else false

    // Initial Load
    LaunchedEffect(Unit) {
        viewModel.loadInventory()
    }

    LaunchedEffect(deleteMessage) {
        deleteMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearDeleteMessage()
        }
    }

    // Dialogs
    var showDeleteDialog by remember { mutableStateOf(false) }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus ${selectedIds.size} Item?") },
            text = { Text("Item yang dipilih akan dihapus permanen.") },
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
            title = { Text("Hapus Item?") },
            text = { Text("Hapus '${itemToDeleteSingle?.name}'?") },
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

    // [PERBAIKAN UTAMA] Menggunakan Scaffold
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // Bungkus konten TopBar dalam Column agar background status bar konsisten
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding() // Padding status bar hanya di sini
            ) {
                // 1. Header Dinamis (Selection vs Normal)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isSelectionMode) {
                        // Header Selection Mode
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
                                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    } else {
                        // Header Normal
                        Column {
                            Text(
                                text = "Inventory Buah",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = FruitSenseColors.GreenDark
                            )
                            Text(
                                text = "Kelola stok buah Anda",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Divider pemisah antara header dan list
                if (!isSelectionMode) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Jika BUKAN mode seleksi, tampilkan Search & Filter di bawah header
                if (!isSelectionMode) {
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        // [BARU] Search Bar
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.onSearchQueryChanged(it) },
                            placeholder = { Text("Cari buah...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FruitSenseColors.GreenDark,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // [BARU] Filter Chips (Sort)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChipCustom(
                                selected = false,
                                onClick = { viewModel.onSortChanged("newest") },
                                label = "Terbaru",
                                icon = Icons.Default.Sort
                            )
                            FilterChipCustom(
                                selected = false,
                                onClick = { viewModel.onSortChanged("stock") },
                                label = "Stok Terbanyak",
                                icon = Icons.Default.Sort
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    ) { paddingValues ->
        // [PERBAIKAN 2] Menggunakan paddingValues dari Scaffold dengan benar
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Padding agar tidak tertutup TopBar
        ) {
            when (val state = uiState) {
                is InventoryUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = FruitSenseColors.GreenDark)
                    }
                }
                is InventoryUiState.Success -> {
                    // [PERBAIKAN 3] Padding content dipindah ke LazyColumn contentPadding
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(
                            top = 16.dp,
                            bottom = 100.dp, // Space untuk BottomBar
                            start = 24.dp,
                            end = 24.dp
                        )
                    ) {
                        items(state.data, key = { it.id }) { fruit ->
                            val isSelected = selectedIds.contains(fruit.id)

                            FruitItemCardSelectable(
                                fruit = fruit,
                                isSelected = isSelected,
                                isSelectionMode = isSelectionMode,
                                viewModel = viewModel,
                                onLongClick = { viewModel.toggleSelection(fruit.id) },
                                onClick = {
                                    if (isSelectionMode) {
                                        viewModel.toggleSelection(fruit.id)
                                    }
                                },
                                onDeleteClick = { itemToDeleteSingle = fruit },
                                onRecipeClick = { onAnalyzeClick(fruit) }
                            )
                        }
                    }
                }
                is InventoryUiState.Empty -> {
                    EmptyState()
                }
                is InventoryUiState.Error -> {
                    ErrorState(message = state.message, onRetry = { viewModel.loadInventory() })
                }
            }
        }
    }
}

// Helper Composable untuk Filter Chip (Sama seperti sebelumnya)
@Composable
fun FilterChipCustom(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    icon: ImageVector
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = FruitSenseColors.GreenDark.copy(alpha = 0.2f),
            selectedLabelColor = FruitSenseColors.GreenDark,
            selectedLeadingIconColor = FruitSenseColors.GreenDark
        )
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FruitItemCardSelectable(
    fruit: FruitItem,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    viewModel: InventoryViewModel,
    onLongClick: () -> Unit,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onRecipeClick: () -> Unit
) {
    val expiryInfo = remember(fruit.dateAdded, fruit.expiryDate) {
        viewModel.calculateExpiry(fruit.dateAdded, fruit.expiryDate)
    }

    val cardColor = if (isSelected) FruitSenseColors.GreenDark.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
    val borderStroke = if (isSelected) BorderStroke(2.dp, FruitSenseColors.GreenDark) else null
    val elevation = if (isSelected) 0.dp else 2.dp

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = borderStroke,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
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

                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(80.dp)
                ) {
                    if (!fruit.imageUri.isNullOrEmpty()) {
                        AsyncImage(
                            model = fruit.imageUri,
                            contentDescription = fruit.name,
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

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = fruit.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        if (!isSelectionMode) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = FruitSenseColors.GreenDark.copy(alpha = 0.1f),
                                modifier = Modifier.padding(end = 48.dp)
                            ) {
                                Text(
                                    text = "${fruit.grade ?: "?"}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = FruitSenseColors.GreenDark,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.CalendarToday, null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = expiryInfo.formattedDate,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Timer, null, tint = expiryInfo.statusColor, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = expiryInfo.statusText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = expiryInfo.statusColor
                        )
                    }
                }
            }

            if (!isSelectionMode) {
                Column(
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onRecipeClick, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.RestaurantMenu,
                            contentDescription = "Resep",
                            tint = FruitSenseColors.GreenDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

// ErrorState & EmptyState (Sama)
@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = message, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = FruitSenseColors.GreenDark)) {
                Text("Coba Lagi")
            }
        }
    }
}

@Composable
fun EmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Outlined.Inventory2, null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
            Text("Belum ada buah", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}