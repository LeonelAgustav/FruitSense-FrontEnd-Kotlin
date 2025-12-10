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

    // --- State List Resep ---
    private val _uiState = MutableStateFlow<RecipesUiState>(RecipesUiState.Loading)
    val uiState: StateFlow<RecipesUiState> = _uiState.asStateFlow()

    private val _selectedRecipeIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedRecipeIds: StateFlow<Set<String>> = _selectedRecipeIds.asStateFlow()

    private val _deleteMessage = MutableStateFlow<String?>(null)
    val deleteMessage: StateFlow<String?> = _deleteMessage.asStateFlow()

    // --- State Generate ---
    private val _generationState = MutableStateFlow<List<String>>(emptyList())
    val generationState: StateFlow<List<String>> = _generationState.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    // [BARU] State untuk Trigger Navigasi ke Detail
    private val _navigateToDetail = MutableStateFlow<RecipeItem?>(null)
    val navigateToDetail: StateFlow<RecipeItem?> = _navigateToDetail.asStateFlow()

    init {
        // loadAllRecipes() // Optional: Load on init
    }

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

    // [BARU] Fungsi Generate Langsung ke Detail
    fun generateAndNavigate(fruitId: String, fruitName: String) {
        viewModelScope.launch {
            _isGenerating.value = true

            repository.generateRecipes(fruitId).collect { result ->
                result.onSuccess { list ->
                    if (list.isNotEmpty()) {
                        val recipeName = list.first() // Ambil rekomendasi pertama

                        // Buat Dummy Object RecipeItem Lengkap (Mocking AI Result)
                        val newRecipe = RecipeItem(
                            id = "gen_${System.currentTimeMillis()}",
                            title = recipeName,
                            cookingTime = "15 Menit",
                            ingredients = "2 buah $fruitName\n1 sdm Madu\nEs Batu secukupnya\nDaun Mint (opsional)",
                            instructions = "1. Cuci bersih buah $fruitName.\n2. Potong kecil-kecil sesuai selera.\n3. Campurkan dengan bahan lain.\n4. Sajikan dingin.",
                        )
                        _generationState.value = list
                        // Trigger navigasi
                        _navigateToDetail.value = newRecipe
                    }
                }.onFailure {
                    // Handle error (bisa tambahkan state error message terpisah)
                }
                _isGenerating.value = false
            }
        }
    }

    // Reset navigasi setelah pindah layar
    fun onDetailNavigated() {
        _navigateToDetail.value = null
    }

    // --- Fungsi Lama (Tetap Dipertahankan) ---
    fun generateRecommendations(fruitId: String) {
        viewModelScope.launch {
            _isGenerating.value = true
            repository.generateRecipes(fruitId).collect { result ->
                result.onSuccess { list -> _generationState.value = list }
                _isGenerating.value = false
            }
        }
    }

    fun toggleSelection(recipeId: String) {
        val currentSelection = _selectedRecipeIds.value.toMutableSet()
        if (currentSelection.contains(recipeId)) currentSelection.remove(recipeId) else currentSelection.add(recipeId)
        _selectedRecipeIds.value = currentSelection
    }

    fun clearSelection() { _selectedRecipeIds.value = emptySet() }

    fun deleteSelectedRecipes() {
        val idsToDelete = _selectedRecipeIds.value.toList()
        if (idsToDelete.isEmpty()) return
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is RecipesUiState.Success) {
                val updatedList = currentState.data.filter { !idsToDelete.contains(it.id) }
                _uiState.value = if (updatedList.isEmpty()) RecipesUiState.Empty else RecipesUiState.Success(updatedList)
            }
            _selectedRecipeIds.value = emptySet()
            idsToDelete.forEach { id -> repository.deleteRecipe(id).collect {} }
            _deleteMessage.value = "Resep terpilih berhasil dihapus"
            loadAllRecipes()
        }
    }

    fun clearDeleteMessage() { _deleteMessage.value = null }
}