package com.example.fruitsense.ui.screen.dashboard.inventory

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fruitsense.data.model.FruitItem
import com.example.fruitsense.data.model.RecipeItem
import com.example.fruitsense.data.repository.FruitRepository
import com.example.fruitsense.ui.theme.FruitSenseColors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject

sealed class InventoryUiState {
    object Loading : InventoryUiState()
    data class Success(val data: List<FruitItem>) : InventoryUiState()
    data class Error(val message: String) : InventoryUiState()
    object Empty : InventoryUiState()
}

data class ExpiryInfo(
    val statusText: String,
    val statusColor: Color,
    val formattedDate: String
)

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val repository: FruitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<InventoryUiState>(InventoryUiState.Loading)
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    private val _deleteMessage = MutableStateFlow<String?>(null)
    val deleteMessage: StateFlow<String?> = _deleteMessage.asStateFlow()

    private val _updateMessage = MutableStateFlow<String?>(null)
    val updateMessage: StateFlow<String?> = _updateMessage.asStateFlow()

    private val _selectedIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedIds: StateFlow<Set<String>> = _selectedIds.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private var fullInventoryList: List<FruitItem> = emptyList()

    init {
        loadInventory()
    }

    fun loadInventory(query: String? = null, sortBy: String? = null) {
        viewModelScope.launch {
            if (query == null && sortBy == null) _uiState.value = InventoryUiState.Loading

            repository.getInventory(null, sortBy).collect { result ->
                result.onSuccess { list ->
                    fullInventoryList = list
                    val filteredList = if (!query.isNullOrEmpty()) {
                        list.filter { it.name.contains(query, ignoreCase = true) }
                    } else list

                    _uiState.value = if (filteredList.isEmpty()) InventoryUiState.Empty else InventoryUiState.Success(filteredList)
                }.onFailure { error ->
                    _uiState.value = InventoryUiState.Error(error.message ?: "Gagal memuat data")
                }
            }
        }
    }

    fun updateFruitStock(id: String, newQuantity: Int, currentName: String) {
        viewModelScope.launch {
            Log.d("InventoryVM", "Memulai update: ID=$id, Qty=$newQuantity, Name=$currentName")

            // Panggil Repository
            repository.updateInventory(id, newQuantity, currentName).collect { result ->
                result.onSuccess {
                    Log.d("InventoryVM", "Update Sukses!")
                    _updateMessage.value = "Stok berhasil diperbarui"
                    loadInventory(_searchQuery.value) // Refresh data agar UI update
                }.onFailure { e ->
                    Log.e("InventoryVM", "Update Gagal: ${e.message}", e)
                    _updateMessage.value = "Gagal update stok: ${e.message}"
                }
            }
        }
    }

    fun clearUpdateMessage() {
        _updateMessage.value = null
    }

    fun generateRecipeForFruit(fruit: FruitItem, onSuccess: (RecipeItem) -> Unit) {
        viewModelScope.launch {
            _isGenerating.value = true
            Log.d("InventoryVM", "Generating recipe for: ${fruit.name}")

            repository.generateRecipes(fruit.id).collect { result ->
                result.onSuccess { list ->
                    if (list.isNotEmpty()) {
                        val recipeName = list.first().toString()

                        val newRecipe = RecipeItem(
                            id = "gen_${System.currentTimeMillis()}",
                            title = recipeName,
                            cookingTime = "15 Menit",
                            ingredients = "Lihat detail untuk bahan lengkap",
                            instructions = "Lihat detail untuk cara memasak"
                        )

                        withContext(Dispatchers.Main) { onSuccess(newRecipe) }
                    }
                }.onFailure { e ->
                    val errorMessage = e.message ?: ""
                    Log.e("InventoryVM", "Error generate: $errorMessage")

                    if (errorMessage.contains("parameter specified as non-null", ignoreCase = true) ||
                        errorMessage.contains("JsonDataException", ignoreCase = true)) {

                        Log.w("InventoryVM", "Parsing error detected. Fetching latest recipe manually...")
                        fetchLatestRecipeFallback(onSuccess)

                    } else {
                        // Error beneran (Koneksi putus, Server error 500, dll)
                        _deleteMessage.value = "Gagal: ${e.message}"
                    }
                }
                _isGenerating.value = false
            }
        }
    }

    // Fungsi Fallback: Ambil semua resep, pilih yang paling baru
    private fun fetchLatestRecipeFallback(onSuccess: (RecipeItem) -> Unit) {
        viewModelScope.launch {
            // Kita beri sedikit delay (1 detik) agar server selesai menyimpan data ke DB
            delay(1000)

            repository.getRecipesList().collect { result ->
                result.onSuccess { recipes ->
                    if (recipes.isNotEmpty()) {
                        // Asumsi: Backend mengurutkan resep terbaru di awal list (Index 0)
                        val latestRecipe = recipes.first()

                        withContext(Dispatchers.Main) {
                            onSuccess(latestRecipe)
                        }
                    } else {
                        _deleteMessage.value = "Resep berhasil dibuat, tapi gagal dimuat ulang."
                    }
                }
            }
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
        val filteredList = if (newQuery.isBlank()) fullInventoryList else fullInventoryList.filter { it.name.contains(newQuery, ignoreCase = true) }
        _uiState.value = if (filteredList.isEmpty()) InventoryUiState.Empty else InventoryUiState.Success(filteredList)
    }

    fun onSortChanged(sortBy: String) { loadInventory(query = null, sortBy = sortBy) }

    fun toggleSelection(id: String) {
        val current = _selectedIds.value.toMutableSet()
        if (current.contains(id)) current.remove(id) else current.add(id)
        _selectedIds.value = current
    }

    fun toggleSelectAll() {
        val currentState = _uiState.value
        if (currentState is InventoryUiState.Success) {
            val allIds = currentState.data.map { it.id }.toSet()
            val currentSelection = _selectedIds.value
            if (currentSelection.containsAll(allIds)) clearSelection() else _selectedIds.value = allIds
        }
    }

    fun clearSelection() { _selectedIds.value = emptySet() }

    fun deleteSelectedItems() {
        val ids = _selectedIds.value.toList()
        if (ids.isEmpty()) return
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is InventoryUiState.Success) {
                val updatedList = currentState.data.filter { !ids.contains(it.id) }
                _uiState.value = if (updatedList.isEmpty()) InventoryUiState.Empty else InventoryUiState.Success(updatedList)
            }
            ids.forEach { id -> repository.deleteInventory(id).collect { } }
            _deleteMessage.value = "${ids.size} item dihapus"
            clearSelection()
            loadInventory(_searchQuery.value)
        }
    }

    fun deleteItem(id: String) {
        viewModelScope.launch {
            repository.deleteInventory(id).collect { result ->
                result.onSuccess {
                    _deleteMessage.value = "Item berhasil dihapus"
                    loadInventory(_searchQuery.value)
                }
            }
        }
    }

    fun clearDeleteMessage() { _deleteMessage.value = null }

    fun calculateExpiry(createdDateString: String?, expiryDateString: String?): ExpiryInfo {
        val defaultResult = ExpiryInfo("-", Color.Gray, "-")
        if (createdDateString.isNullOrEmpty()) return defaultResult
        return try {
            val zoneId = ZoneId.systemDefault()
            val today = LocalDate.now(zoneId)
            val createdInstant = Instant.parse(createdDateString)
            val createdDate = createdInstant.atZone(zoneId).toLocalDate()
            val displayFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
            val formattedDate = createdDate.format(displayFormatter)
            val expiryDate: LocalDate = if (!expiryDateString.isNullOrEmpty()) {
                try { LocalDate.parse(expiryDateString) } catch (e: Exception) { createdDate.plusDays(7) }
            } else { createdDate.plusDays(7) }
            val daysLeft = ChronoUnit.DAYS.between(today, expiryDate).toInt()
            val (text, color) = when {
                daysLeft < 0 -> "Expired" to Color.Red
                daysLeft == 0 -> "Habis Hari Ini" to Color(0xFFFF9800)
                daysLeft <= 2 -> "$daysLeft Hari Lagi" to Color(0xFFFF9800)
                else -> "$daysLeft Hari Lagi" to FruitSenseColors.FreshGreen
            }
            ExpiryInfo(text, color, formattedDate)
        } catch (e: Exception) {
            ExpiryInfo("-", Color.Gray, "-")
        }
    }
}