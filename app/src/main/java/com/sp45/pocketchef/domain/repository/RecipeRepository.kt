package com.sp45.pocketchef.domain.repository

import kotlinx.coroutines.flow.Flow
import com.sp45.pocketchef.data.local.recipe.RecipeEntity

interface RecipeRepository {
    // For Favourites
    suspend fun saveRecipeToFavorites(recipe: RecipeEntity)
    fun getFavoriteRecipes(): Flow<List<RecipeEntity>>
    suspend fun deleteFavorite(recipe: RecipeEntity)

    // For Caching
    suspend fun cacheRecipes(recipes: List<RecipeEntity>)
    fun getCachedRecipes(): Flow<List<RecipeEntity>>
}