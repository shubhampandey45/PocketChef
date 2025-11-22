package com.sp45.pocketchef.presentation.recipe_suggestion

import com.sp45.pocketchef.data.local.recipe.RecipeEntity

data class RecipeSuggestionState(
    val recipes: List<RecipeEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val ingredientsTitle: String = "",
    val favoriteRecipeIds: Set<String> = emptySet()
)