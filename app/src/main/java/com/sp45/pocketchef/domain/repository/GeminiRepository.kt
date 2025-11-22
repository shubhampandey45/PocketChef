package com.sp45.pocketchef.domain.repository

import com.sp45.pocketchef.data.local.recipe.RecipeEntity

interface GeminiRepository {
    // For splash screen
    suspend fun getCookingTip(): String

    // Fetches smart ingredient suggestions from Gemini
    // For input ingredient screen
    suspend fun getIngredientSuggestions(query: String): List<String>

    //For recipe screen
    suspend fun getRecipes(ingredients: List<String>): List<RecipeEntity>

}