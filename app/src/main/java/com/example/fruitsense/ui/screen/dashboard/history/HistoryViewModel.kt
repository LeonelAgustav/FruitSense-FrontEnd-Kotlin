package com.example.fruitsense.ui.screen.dashboard.history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fruitsense.data.model.FruitItem
import com.example.fruitsense.data.repository.FruitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HistoryUiState {
    object Loading : HistoryUiState()
    data class Success(val data: List<FruitItem>) : HistoryUiState()
    data class Error(val message: String) : HistoryUiState()
    object Empty : HistoryUiState()
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: FruitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private val _deleteMessage = MutableStateFlow<String?>(null)
    val deleteMessage: StateFlow<String?> = _deleteMessage.asStateFlow()

    // [BARU] Multi-Select State
    private val _selectedIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedIds: StateFlow<Set<String>> = _selectedIds.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            Log.d("HistoryVM", "Loading history...")
            //_uiState.value = HistoryUiState.Loading

            repository.getHistory().collect { result ->
                result.onSuccess { list ->
                    Log.d("HistoryVM", "Load success: ${list.size} items")
                    if (list.isEmpty()) {
                        _uiState.value = HistoryUiState.Empty
                    } else {
                        _uiState.value = HistoryUiState.Success(list)
                    }
                }.onFailure { error ->
                    Log.e("HistoryVM", "Load failed", error)
                    _uiState.value = HistoryUiState.Error(error.message ?: "Gagal memuat riwayat")
                }
            }
        }
    }

    // Toggle Selection
    fun toggleSelection(id: String) {
        val current = _selectedIds.value.toMutableSet()
        if (current.contains(id)) {
            current.remove(id)
        } else {
            current.add(id)
        }
        _selectedIds.value = current
    }

    // Select All
    fun toggleSelectAll() {
        val currentState = _uiState.value
        if (currentState is HistoryUiState.Success) {
            val allIds = currentState.data.map { it.id }.toSet()
            val currentSelection = _selectedIds.value

            if (currentSelection.containsAll(allIds)) {
                clearSelection()
            } else {
                _selectedIds.value = allIds
            }
        }
    }

    // Clear Selection
    fun clearSelection() {
        _selectedIds.value = emptySet()
    }

    // Delete Multiple Selected Items
    fun deleteSelectedItems() {
        val ids = _selectedIds.value.toList()
        if (ids.isEmpty()) return

        viewModelScope.launch {
            // 1. Optimistic Update
            val currentState = _uiState.value
            if (currentState is HistoryUiState.Success) {
                val updatedList = currentState.data.filter { !ids.contains(it.id) }
                if (updatedList.isEmpty()) {
                    _uiState.value = HistoryUiState.Empty
                } else {
                    _uiState.value = HistoryUiState.Success(updatedList)
                }
            }

            // 2. Call API (Looping)
            ids.forEach { id ->
                repository.deleteHistory(id).collect { }
            }

            _deleteMessage.value = "${ids.size} riwayat dihapus"
            clearSelection()
            loadHistory()
        }
    }

    // Delete Single
    fun deleteItem(id: String) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is HistoryUiState.Success) {
                val updatedList = currentState.data.filter { it.id != id }
                if (updatedList.isEmpty()) {
                    _uiState.value = HistoryUiState.Empty
                } else {
                    _uiState.value = HistoryUiState.Success(updatedList)
                }
            }

            Log.d("HistoryVM", "Deleting history: $id")
            repository.deleteHistory(id).collect { result ->
                result.onSuccess {
                    Log.d("HistoryVM", "Delete success API.")
                    _deleteMessage.value = "Riwayat berhasil dihapus"
                    loadHistory()
                }.onFailure {
                    Log.e("HistoryVM", "Delete failed", it)
                    _deleteMessage.value = "Gagal menghapus: ${it.message}"
                    loadHistory()
                }
            }
        }
    }

    fun clearDeleteMessage() {
        _deleteMessage.value = null
    }
}