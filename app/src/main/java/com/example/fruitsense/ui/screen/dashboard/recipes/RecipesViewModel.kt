package com.example.fruitsense.ui.screen.dashboard.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fruitsense.data.model.RecipeItem
import com.example.fruitsense.data.repository.FruitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RecipesUiState {
    object Loading : RecipesUiState()
    data class Success(val data: List<RecipeItem>) : RecipesUiState()
    data class Error(val message: String) : RecipesUiState()
    object Empty : RecipesUiState()
}

@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val repository: FruitRepository
) : ViewModel() {

    // --- State untuk Halaman List Resep ---
    private val _uiState = MutableStateFlow<RecipesUiState>(RecipesUiState.Loading)
    val uiState: StateFlow<RecipesUiState> = _uiState.asStateFlow()

    // --- Selection Mode States [BARU] ---
    // Menyimpan Set ID resep yang dipilih
    private val _selectedRecipeIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedRecipeIds: StateFlow<Set<String>> = _selectedRecipeIds.asStateFlow()

    // Mode seleksi aktif jika ada minimal 1 item terpilih
    // Kita bisa gunakan computed property di UI, tapi state juga oke.
    private val _deleteMessage = MutableStateFlow<String?>(null)
    val deleteMessage: StateFlow<String?> = _deleteMessage.asStateFlow()

    // --- State untuk Tombol Generate ---
    private val _generationState = MutableStateFlow<List<String>>(emptyList())
    val generationState: StateFlow<List<String>> = _generationState.asStateFlow()
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()
    private val _generateError = MutableStateFlow<String?>(null)
    val generateError: StateFlow<String?> = _generateError.asStateFlow()

    private val _hasGenerated = MutableStateFlow(false)
    val hasGenerated: StateFlow<Boolean> = _hasGenerated.asStateFlow()

    fun loadAllRecipes() {
        viewModelScope.launch {
            _uiState.value = RecipesUiState.Loading
            repository.getRecipesList().collect { result ->
                result.onSuccess { list ->
                    _uiState.value = if (list.isEmpty()) RecipesUiState.Empty else RecipesUiState.Success(list)
                }.onFailure { error ->
                    _uiState.value = RecipesUiState.Error(error.message ?: "Gagal memuat resep")
                }
            }
        }
    }

    // [BARU] Toggle Selection (Pilih/Hapus Pilih)
    fun toggleSelection(recipeId: String) {
        val currentSelection = _selectedRecipeIds.value.toMutableSet()
        if (currentSelection.contains(recipeId)) {
            currentSelection.remove(recipeId)
        } else {
            currentSelection.add(recipeId)
        }
        _selectedRecipeIds.value = currentSelection
    }

    // [BARU] Clear Selection (Batal Pilih)
    fun clearSelection() {
        _selectedRecipeIds.value = emptySet()
    }

    // Hapus Single Item
    fun deleteRecipe(id: String) {
        viewModelScope.launch {
            // 1. Optimistic Update (Hapus dari UI dulu)
            val currentState = _uiState.value
            if (currentState is RecipesUiState.Success) {
                val updatedList = currentState.data.filter { it.id != id }
                if (updatedList.isEmpty()) {
                    _uiState.value = RecipesUiState.Empty
                } else {
                    _uiState.value = RecipesUiState.Success(updatedList)
                }
            }

            // 2. Panggil API
            repository.deleteRecipe(id).collect { result ->
                result.onSuccess {
                    _deleteMessage.value = "Resep berhasil dihapus"
                    loadAllRecipes() // Sync
                }.onFailure {
                    _deleteMessage.value = "Gagal menghapus: ${it.message}"
                    loadAllRecipes() // Rollback jika gagal
                }
            }
        }
    }

    // [BARU] Delete Multiple Selected Recipes
    fun deleteSelectedRecipes() {
        val idsToDelete = _selectedRecipeIds.value.toList()
        if (idsToDelete.isEmpty()) return

        viewModelScope.launch {
            // Optimistic Update: Hapus dari UI dulu
            val currentState = _uiState.value
            if (currentState is RecipesUiState.Success) {
                val updatedList = currentState.data.filter { !idsToDelete.contains(it.id) }
                if (updatedList.isEmpty()) {
                    _uiState.value = RecipesUiState.Empty
                } else {
                    _uiState.value = RecipesUiState.Success(updatedList)
                }
            }

            // Reset mode seleksi
            _selectedRecipeIds.value = emptySet()
            var successCount = 0

            // Loop hapus satu per satu (karena API single delete)
            idsToDelete.forEach { id ->
                repository.deleteRecipe(id).collect { result ->
                    result.onSuccess { successCount++ }
                }
            }

            _deleteMessage.value = "Resep terpilih berhasil dihapus"
            // Reload untuk memastikan sinkronisasi
            loadAllRecipes()
        }
    }

    fun generateRecommendations(fruitId: String) {
        Log.d("RecipesVM", "Generating for ID: $fruitId")
        viewModelScope.launch {
            _isGenerating.value = true
            _generateError.value = null
            _hasGenerated.value = true // Menandai proses dimulai

            repository.generateRecipes(fruitId).collect { result ->
                result.onSuccess { list ->
                    Log.d("RecipesVM", "Generate Success. Items: ${list.size}")
                    _generationState.value = list
                }.onFailure { error ->
                    Log.e("RecipesVM", "Generate Failed", error)
                    _generateError.value = error.message ?: "Gagal membuat resep"
                }
                _isGenerating.value = false
            }
        }
    }

    fun clearGenerationState() {
        _generationState.value = emptyList()
        _generateError.value = null
        _isGenerating.value = false
        _hasGenerated.value = false
    }

    fun clearDeleteMessage() {
        _deleteMessage.value = null
    }
}