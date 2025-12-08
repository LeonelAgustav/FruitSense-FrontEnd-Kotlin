package com.example.fruitsense.ui.screen.dashboard.recipes

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
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fruitsense.data.model.RecipeItem
import com.example.fruitsense.ui.theme.FruitSenseColors
import com.example.fruitsense.ui.screen.dashboard.detail.RecipesViewModel
import com.example.fruitsense.ui.screen.dashboard.detail.RecipesUiState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecipesScreen(
    onBackClick: () -> Unit,
    onRecipeClick: (RecipeItem) -> Unit,
    viewModel: RecipesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedIds by viewModel.selectedRecipeIds.collectAsState()
    val deleteMessage by viewModel.deleteMessage.collectAsState()
    val context = LocalContext.current
    val isSelectionMode = selectedIds.isNotEmpty()

    LaunchedEffect(Unit) {
        viewModel.loadAllRecipes()
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
            title = { Text("Hapus ${selectedIds.size} Resep?") },
            text = { Text("Item yang dipilih akan dihapus permanen.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteSelectedRecipes()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }

    var recipeToDeleteSingle by remember { mutableStateOf<RecipeItem?>(null) }
    if (recipeToDeleteSingle != null) {
        AlertDialog(
            onDismissRequest = { recipeToDeleteSingle = null },
            title = { Text("Hapus Resep?") },
            text = { Text("Hapus '${recipeToDeleteSingle?.title}' dari koleksi?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        recipeToDeleteSingle?.let { viewModel.deleteRecipe(it.id) }
                        recipeToDeleteSingle = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { recipeToDeleteSingle = null }) { Text("Batal") }
            }
        )
    }

    // --- UI Structure with Scaffold ---
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
            ) {
                // Header Content
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isSelectionMode) {
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
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Hapus Terpilih",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    } else {
                        Column {
                            Text(
                                text = "Koleksi Resep",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = FruitSenseColors.GreenDark
                            )
                            Text(
                                text = "Daftar resep yang Anda simpan",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
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
                is RecipesUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = FruitSenseColors.GreenDark)
                    }
                }
                is RecipesUiState.Success -> {
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
                        items(state.data, key = { it.id }) { recipe ->
                            val isSelected = selectedIds.contains(recipe.id)
                            RecipeItemCardSelectable(
                                recipe = recipe,
                                isSelected = isSelected,
                                isSelectionMode = isSelectionMode,
                                onLongClick = {
                                    viewModel.toggleSelection(recipe.id)
                                },
                                onClick = {
                                    if (isSelectionMode) {
                                        viewModel.toggleSelection(recipe.id)
                                    } else {
                                        onRecipeClick(recipe) // Navigasi bekerja
                                    }
                                },
                                onDeleteClick = { recipeToDeleteSingle = recipe }
                            )
                        }
                    }
                }
                is RecipesUiState.Empty -> {
                    EmptyState()
                }
                is RecipesUiState.Error -> {
                    ErrorState(message = state.message, onRetry = { viewModel.loadAllRecipes() })
                }
            }
        }
    }
}

// ... Component RecipeItemCardSelectable, ErrorState, EmptyState tetap sama
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecipeItemCardSelectable(
    recipe: RecipeItem,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onLongClick: () -> Unit,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val cardColor = if (isSelected) FruitSenseColors.GreenDark.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
    val borderStroke = BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = borderStroke,
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
                // Jika mode seleksi, tampilkan Checkbox
                if (isSelectionMode) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onClick() }, // Klik checkbox sama dengan klik item
                        colors = CheckboxDefaults.colors(
                            checkedColor = FruitSenseColors.GreenDark,
                            uncheckedColor = Color.Gray
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = recipe.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Timer,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = recipe.cookingTime,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = recipe.ingredients,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
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

// Komponen Error (Sama dengan Inventory)
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
            // Ikon Besar Abu-abu (RestaurantMenu untuk resep)
            Icon(
                imageVector = Icons.Default.RestaurantMenu,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Belum ada resep tersimpan",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}