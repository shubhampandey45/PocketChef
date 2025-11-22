package com.sp45.pocketchef.presentation.save

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.sp45.pocketchef.data.local.recipe.RecipeEntity
import com.sp45.pocketchef.domain.repository.RecipeRepository
import javax.inject.Inject

@HiltViewModel
class SaveViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FavoritesState())
    val state = _state.asStateFlow()

    init {
        loadFavoriteRecipes()
    }

    private fun loadFavoriteRecipes() {
        viewModelScope.launch {
            recipeRepository.getFavoriteRecipes()
                .onStart { _state.update { it.copy(isLoading = true) } }
                .catch { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Failed to load favorites"
                        )
                    }
                }
                .collect { recipes ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            recipes = recipes,
                            error = null
                        )
                    }
                }
        }
    }

    fun deleteFavorite(recipe: RecipeEntity) {
        viewModelScope.launch {
            try {
                recipeRepository.deleteFavorite(recipe)
                // Reload the list after deletion
                loadFavoriteRecipes()
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Failed to delete: ${e.message}")
                }
            }
        }
    }
}