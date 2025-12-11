package com.fruitsense.app.ui.screen.dashboard.inventory

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
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
import com.fruitsense.app.data.model.FruitItem
import com.fruitsense.app.data.model.RecipeItem

@Composable
fun InventoryScreen(
    onAnalyzeClick: (FruitItem) -> Unit,
    onNavigateToRecipe: (RecipeItem) -> Unit,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val deleteMessage by viewModel.deleteMessage.collectAsState()
    val updateMessage by viewModel.updateMessage.collectAsState()
    val selectedIds by viewModel.selectedIds.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val isSelectionMode = selectedIds.isNotEmpty()

    val isAllSelected = if (uiState is InventoryUiState.Success) {
        selectedIds.containsAll((uiState as InventoryUiState.Success).data.map { it.id })
    } else false

    var showEditStockDialog by remember { mutableStateOf(false) }
    var fruitToEdit by remember { mutableStateOf<FruitItem?>(null) }

    LaunchedEffect(Unit) { viewModel.loadInventory() }

    LaunchedEffect(deleteMessage, updateMessage) {
        deleteMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearDeleteMessage()
        }
        updateMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearUpdateMessage()
        }
    }

    if (showEditStockDialog && fruitToEdit != null) {
        UpdateStockDialog(
            fruit = fruitToEdit!!,
            onDismiss = { showEditStockDialog = false },
            onConfirm = { newQuantity, newName -> // Menerima quantity DAN name
                viewModel.updateFruitStock(fruitToEdit!!.id, newQuantity, newName)
                showEditStockDialog = false
            }
        )
    }

    // Delete Dialog
    var showDeleteDialog by remember { mutableStateOf(false) }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus ${selectedIds.size} Item?") },
            text = { Text("Item yang dipilih akan dihapus permanen.") },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteSelectedItems(); showDeleteDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("Hapus") }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") } }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isSelectionMode) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { viewModel.clearSelection() }) { Icon(Icons.Default.Close, "Batal") }
                            Text("${selectedIds.size} Dipilih", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        Row {
                            TextButton(onClick = { viewModel.toggleSelectAll() }) { Text(if (isAllSelected) "Batal Semua" else "Pilih Semua") }
                            IconButton(onClick = { showDeleteDialog = true }) { Icon(Icons.Default.Delete, "Hapus", tint = MaterialTheme.colorScheme.error) }
                        }
                    } else {
                        Column {
                            Text("Inventory", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                            Text("Stok buah Anda", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                if (!isSelectionMode) {
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.onSearchQueryChanged(it) },
                            placeholder = { Text("Cari buah...") },
                            leadingIcon = { Icon(Icons.Default.Search, null) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.extraLarge,
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChipCustom(false, { viewModel.onSortChanged("newest") }, "Terbaru", Icons.Default.AccessTime)
                            FilterChipCustom(false, { viewModel.onSortChanged("stock") }, "Stok", Icons.Default.Sort)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(vertical = 24.dp)
        ) {
            when (val state = uiState) {
                is InventoryUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                is InventoryUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message) }
                is InventoryUiState.Empty -> EmptyState()
                is InventoryUiState.Success -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.data, key = { it.id }) { fruit ->
                            FruitGridItem(
                                fruit = fruit,
                                isSelected = selectedIds.contains(fruit.id),
                                isSelectionMode = isSelectionMode,
                                viewModel = viewModel,
                                onClick = {
                                    if (isSelectionMode) viewModel.toggleSelection(fruit.id)
                                },
                                onLongClick = { viewModel.toggleSelection(fruit.id) },
                                onRecipeClick = {
                                    viewModel.generateRecipeForFruit(
                                        fruit = fruit,
                                        onSuccess = { recipe ->
                                            Toast.makeText(context, "Resep Berhasil Dibuat!", Toast.LENGTH_SHORT).show()
                                            onNavigateToRecipe(recipe)
                                        }
                                    )
                                },
                                onEditStockClick = {
                                    fruitToEdit = fruit
                                    showEditStockDialog = true
                                }
                            )
                        }
                    }
                }
            }

            // [LOADING OVERLAY] Muncul saat proses generate
            if (isGenerating) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Sedang Membuat Resep...",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UpdateStockDialog(
    fruit: FruitItem,
    onDismiss: () -> Unit,
    onConfirm: (Int, String) -> Unit
) {
    var quantityText by remember { mutableStateOf(fruit.stock?.toString() ?: "0") }
    var nameText by remember { mutableStateOf(fruit.name) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Stok") },
        text = {
            Column {
                Text("Edit detail item:", style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(16.dp))

                // Input Nama
                OutlinedTextField(
                    value = nameText,
                    onValueChange = { nameText = it },
                    label = { Text("Nama Buah") },
                    singleLine = true,
                    shape = MaterialTheme.shapes.small
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Input Stok
                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { if (it.all { char -> char.isDigit() }) quantityText = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text("Jumlah Stok") },
                    singleLine = true,
                    shape = MaterialTheme.shapes.small
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        quantityText.toIntOrNull() ?: 0,
                        nameText
                    ) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FruitGridItem(
    fruit: FruitItem,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    viewModel: InventoryViewModel,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onRecipeClick: () -> Unit,
    onEditStockClick: () -> Unit
) {
    val expiryInfo = remember(fruit) { viewModel.calculateExpiry(fruit.dateAdded, fruit.expiryDate) }

    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface

    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (isSelected) BorderStroke(2.dp, borderColor) else null,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
            .clip(MaterialTheme.shapes.medium)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                if (!fruit.imageUri.isNullOrEmpty()) {
                    AsyncImage(
                        model = fruit.imageUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.Inventory2, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                if (isSelectionMode) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = null,
                        modifier = Modifier.align(Alignment.TopEnd).padding(4.dp),
                        colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(bottomEnd = 12.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = fruit.grade ?: "?",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                // Baris Nama dan Edit Stok
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = fruit.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // [BARU] Tombol Edit Stok
                    if (!isSelectionMode) {
                        IconButton(
                            onClick = onEditStockClick,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Stok", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // STOK (Kiri)
                    Text(
                        text = "Stok: ${fruit.stock ?: 0}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )

                    // EXPIRY (Kanan)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Timer,
                            contentDescription = "Expiry",
                            tint = expiryInfo.statusColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = expiryInfo.statusText,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (!isSelectionMode) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onRecipeClick,
                        modifier = Modifier.fillMaxWidth().height(32.dp),
                        contentPadding = PaddingValues(0.dp),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text("Resep", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChipCustom(selected: Boolean, onClick: () -> Unit, label: String, icon: ImageVector) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null, modifier = Modifier.size(16.dp)) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Outlined.ShoppingBasket, null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.outline)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Inventory Kosong", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("Mulai scan buah untuk mengisi stok", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
    }
}