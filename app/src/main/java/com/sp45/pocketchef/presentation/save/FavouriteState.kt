package com.sp45.pocketchef.presentation.save

import com.sp45.pocketchef.data.local.recipe.RecipeEntity

data class FavoritesState(
    val recipes: List<RecipeEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)