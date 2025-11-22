package com.sp45.pocketchef.presentation.recipe_suggestion

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.sp45.pocketchef.data.local.recipe.RecipeEntity
import com.sp45.pocketchef.domain.repository.GeminiRepository
import com.sp45.pocketchef.domain.repository.RecipeRepository
import javax.inject.Inject

@HiltViewModel
class RecipeSuggestionViewModel @Inject constructor(
    private val geminiRepository: GeminiRepository,
    private val recipeRepository: RecipeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(RecipeSuggestionState())
    val state = _state.asStateFlow()

    private val gson = Gson()
    private var originalIngredients: List<String> = emptyList()

    init {
        // --- Listen for favorite changes in real-time ---
        viewModelScope.launch {
            recipeRepository.getFavoriteRecipes().collect { favoriteList ->
                _state.update {
                    it.copy(favoriteRecipeIds = favoriteList.map { fav -> fav.id }.toSet())
                }
            }
        }
        // ------------------------------------

        // Receive the ingredient list from navigation
        val ingredientsJson = savedStateHandle.get<String>("ingredients")
        if (ingredientsJson != null) {
            try {
                val listType = object : TypeToken<List<String>>() {}.type
                originalIngredients = gson.fromJson(ingredientsJson, listType)

                _state.update {
                    it.copy(ingredientsTitle = originalIngredients.joinToString(", "))
                }

                // Load from cache first for offline fallback
                viewModelScope.launch {
                    recipeRepository.getCachedRecipes().collect { cachedRecipes ->
                        if (cachedRecipes.isNotEmpty() && _state.value.recipes.isEmpty()) {
                            _state.update { it.copy(recipes = cachedRecipes) }
                        }
                    }
                }

                // Automatically generate recipes on load
                generateRecipes()

            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to parse ingredients: ${e.message}") }
            }
        } else {
            _state.update { it.copy(error = "No ingredients provided.") }
        }
    }

    fun generateRecipes(refinement: String? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val ingredientsToSearch = if (refinement != null) {
                    originalIngredients + refinement
                } else {
                    originalIngredients
                }

                println("Generating recipes for ingredients: $ingredientsToSearch")

                val recipes = geminiRepository.getRecipes(ingredientsToSearch)

                println("Received ${recipes.size} recipes from Gemini")

                if (recipes.isEmpty()) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "No recipes generated. Please try again."
                        )
                    }
                    return@launch
                }

                recipeRepository.cacheRecipes(recipes)

                _state.update {
                    it.copy(recipes = recipes, isLoading = false)
                }

            } catch (e: Exception) {
                println("Error generating recipes: ${e.message}")
                e.printStackTrace()
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to fetch recipes: ${e.message}. Showing cached results if available."
                    )
                }
            }
        }
    }

    // --- REPLACED saveRecipeToFavorites with toggleFavorite ---
    fun toggleFavorite(recipe: RecipeEntity, isCurrentlyFavorite: Boolean) {
        viewModelScope.launch {
            try {
                if (isCurrentlyFavorite) {
                    recipeRepository.deleteFavorite(recipe)
                } else {
                    recipeRepository.saveRecipeToFavorites(recipe)
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to update favorite: ${e.message}") }
            }
        }
    }
}