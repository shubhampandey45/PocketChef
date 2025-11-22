package com.sp45.pocketchef.domain.repository

import kotlinx.coroutines.flow.Flow
import com.sp45.pocketchef.data.local.ingredient.Ingredient

/**
 * Single source of truth for ingredient data.
 * Manages both local Room and remote Firestore data.
 */
interface IngredientRepository {

    // Gets the active list of ingredients from the local Room DB
    fun getActiveIngredients(): Flow<List<Ingredient>>

    // Gets the recent ingredients from the local cache (DataStore)
    fun getRecentIngredients(): Flow<List<String>>

    /**
     * Saves an ingredient to both local Room DB and remote Firestore.
     * Also updates the recent ingredients cache.
     */
    suspend fun saveIngredient(ingredient: Ingredient)

    /**
     * Deletes an ingredient from both local Room DB and remote Firestore.
     */
    suspend fun deleteIngredient(ingredient: Ingredient)

    /**
     * Fetches the user's ingredient list from Firestore and updates
     * the local Room DB.
     */
    suspend fun syncIngredientsFromFirestore()
}