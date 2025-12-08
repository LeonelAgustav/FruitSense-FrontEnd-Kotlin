package com.example.fruitsense.ui.screen.dashboard.inventory

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fruitsense.data.model.FruitItem
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

    // Multi-Select State
    private val _selectedIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedIds: StateFlow<Set<String>> = _selectedIds.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var fullInventoryList: List<FruitItem> = emptyList()

    init {
        loadInventory()
    }

    // Load data dari server
    fun loadInventory(query: String? = null, sortBy: String? = null) {
        viewModelScope.launch {
            // Jika hanya search lokal, tidak perlu loading penuh
            if (query == null && sortBy == null) {
                _uiState.value = InventoryUiState.Loading
            }

            // Panggil API (Tanpa query search, kita filter sendiri nanti)
            // Atau kirim query jika backend support
            // Disini kita coba ambil SEMUA data dulu, baru filter lokal agar responsif
            repository.getInventory(null, sortBy).collect { result ->
                result.onSuccess { list ->
                    fullInventoryList = list // Simpan master data

                    // Terapkan filter jika ada query
                    val filteredList = if (!query.isNullOrEmpty()) {
                        list.filter { it.name.contains(query, ignoreCase = true) }
                    } else {
                        list
                    }

                    if (filteredList.isEmpty()) {
                        _uiState.value = InventoryUiState.Empty
                    } else {
                        _uiState.value = InventoryUiState.Success(filteredList)
                    }
                }.onFailure { error ->
                    _uiState.value = InventoryUiState.Error(error.message ?: "Gagal memuat data")
                }
            }
        }
    }

    // Fungsi Search Lokal Instan
    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery

        // Filter data yang sudah ada di memori (Client Side Search)
        val filteredList = if (newQuery.isBlank()) {
            fullInventoryList
        } else {
            fullInventoryList.filter {
                it.name.contains(newQuery, ignoreCase = true) // "b" cocok dengan "Banana"
            }
        }

        if (filteredList.isEmpty()) {
            _uiState.value = InventoryUiState.Empty
        } else {
            _uiState.value = InventoryUiState.Success(filteredList)
        }
    }

    fun onSortChanged(sortBy: String) {
        // Untuk sort, kita panggil API ulang karena sort biasanya logic backend
        loadInventory(query = null, sortBy = sortBy)
    }

    // [BARU] Toggle Selection
    fun toggleSelection(id: String) {
        val current = _selectedIds.value.toMutableSet()
        if (current.contains(id)) {
            current.remove(id)
        } else {
            current.add(id)
        }
        _selectedIds.value = current
    }

    // [BARU] Select All Logic
    fun toggleSelectAll() {
        val currentState = _uiState.value
        if (currentState is InventoryUiState.Success) {
            val allIds = currentState.data.map { it.id }.toSet()
            val currentSelection = _selectedIds.value

            if (currentSelection.containsAll(allIds)) {
                clearSelection()
            } else {
                _selectedIds.value = allIds
            }
        }
    }

    // [BARU] Clear Selection
    fun clearSelection() {
        _selectedIds.value = emptySet()
    }

    // [BARU] Delete Multiple
    fun deleteSelectedItems() {
        val ids = _selectedIds.value.toList()
        if (ids.isEmpty()) return

        viewModelScope.launch {
            // 1. Optimistic Update
            val currentState = _uiState.value
            if (currentState is InventoryUiState.Success) {
                val updatedList = currentState.data.filter { !ids.contains(it.id) }
                if (updatedList.isEmpty()) {
                    _uiState.value = InventoryUiState.Empty
                } else {
                    _uiState.value = InventoryUiState.Success(updatedList)
                }
            }

            // 2. Call API (Looping karena API single delete)
            ids.forEach { id ->
                repository.deleteInventory(id).collect { }
            }

            _deleteMessage.value = "${ids.size} item dihapus"
            clearSelection()

            // 3. Reload to sync
            loadInventory(_searchQuery.value)
        }
    }

    // Delete Single (Legacy support or direct delete)
    fun deleteItem(id: String) {
        viewModelScope.launch {
            // Optimistic
            val currentState = _uiState.value
            if (currentState is InventoryUiState.Success) {
                val updatedList = currentState.data.filter { it.id != id }
                if (updatedList.isEmpty()) {
                    _uiState.value = InventoryUiState.Empty
                } else {
                    _uiState.value = InventoryUiState.Success(updatedList)
                }
            }

            repository.deleteInventory(id).collect { result ->
                result.onSuccess {
                    _deleteMessage.value = "Item berhasil dihapus"
                    loadInventory(_searchQuery.value)
                }.onFailure {
                    _deleteMessage.value = "Gagal menghapus: ${it.message}"
                    loadInventory(_searchQuery.value) // Rollback
                }
            }
        }
    }

    fun clearDeleteMessage() {
        _deleteMessage.value = null
    }

    fun calculateExpiry(createdDateString: String?, expiryDateString: String?): ExpiryInfo {
        val defaultResult = ExpiryInfo("-", Color.Gray, "-")
        if (createdDateString.isNullOrEmpty()) return defaultResult

        return try {
            val zoneId = ZoneId.systemDefault()
            val today = LocalDate.now(zoneId)

            // Parse Tanggal Dibuat (untuk tampilan teks "06 Des 2025")
            val createdInstant = Instant.parse(createdDateString) // ISO Format
            val createdDate = createdInstant.atZone(zoneId).toLocalDate()

            val displayFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
            val formattedDate = createdDate.format(displayFormatter)

            // Tentukan Tanggal Kadaluarsa
            val expiryDate: LocalDate = if (!expiryDateString.isNullOrEmpty()) {
                // Jika server mengirim data expiry (Format: yyyy-MM-dd)
                try {
                    LocalDate.parse(expiryDateString)
                } catch (e: Exception) {
                    createdDate.plusDays(7)
                }
            } else {
                createdDate.plusDays(7)
            }

            val daysLeft = ChronoUnit.DAYS.between(today, expiryDate).toInt()

            // Tentukan Warna & Teks
            val (text, color) = when {
                daysLeft < 0 -> "Expired (${kotlin.math.abs(daysLeft)} hari lalu)" to Color.Red
                daysLeft == 0 -> "Habis Hari Ini" to Color(0xFFFF9800)
                daysLeft <= 2 -> "$daysLeft Hari Lagi" to Color(0xFFFF9800)
                else -> "$daysLeft Hari Lagi" to FruitSenseColors.GreenDark
            }

            ExpiryInfo(text, color, formattedDate)

        } catch (e: Exception) {
            Log.e("InventoryVM", "Error calculating expiry: ${e.message}")
            val rawDate = if (createdDateString.length > 10) createdDateString.substring(0, 10) else createdDateString
            ExpiryInfo("-", Color.Gray, rawDate)
        }
    }
}