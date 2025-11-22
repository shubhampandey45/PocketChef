package com.sp45.pocketchef.domain.repository


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.sp45.pocketchef.data.local.ingredient.Ingredient
import com.sp45.pocketchef.data.local.ingredient.IngredientDao

// DataStore keys for caching recent ingredients
private val RECENT_INGREDIENTS_KEY = stringSetPreferencesKey("recent_ingredients")
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Handles all local data operations for ingredients, using Room for the active list
 * and DataStore/SharedPreferences for the quick local cache (recent ingredients).
 */
class IngredientLocalRepository(
    private val context: Context,
    private val ingredientDao: IngredientDao
) {

    // --- Active Ingredient List (Room DB) ---

    // Flow to observe the active ingredient list
    fun getActiveIngredients(): Flow<List<Ingredient>> = ingredientDao.getAllIngredients()

    // Add or Update an ingredient
    suspend fun saveIngredient(ingredient: Ingredient) {
        // Prevent adding ingredients with the same name (case-insensitive)
        if (!ingredientDao.ingredientExistsByName(ingredient.name)) {
            ingredientDao.insertIngredient(ingredient)
            // Also cache it as a recent ingredient
            addRecentIngredient(ingredient.name)
        }
    }

    // Delete an ingredient
    suspend fun deleteIngredient(ingredient: Ingredient) {
        ingredientDao.deleteIngredient(ingredient)
    }

    // --- Recent Ingredients Cache (DataStore) ---

    private val gson = Gson()

    // Retrieve recent ingredients from cache
    fun getRecentIngredients(): Flow<List<String>> {
        return context.dataStore.data
            .map { preferences ->
                preferences[RECENT_INGREDIENTS_KEY]?.toList() ?: emptyList()
            }
    }

    // Add a new ingredient to the recent cache
    suspend fun addRecentIngredient(name: String) {
        context.dataStore.edit { preferences ->
            val currentSet = preferences[RECENT_INGREDIENTS_KEY] ?: emptySet()
            // Keep the set size limited (e.g., last 10)
            val newSet = (setOf(name) + currentSet).take(10).toSet()
            preferences[RECENT_INGREDIENTS_KEY] = newSet
        }
    }

    // Clear the recent ingredients cache
    suspend fun clearRecentIngredientsCache() {
        context.dataStore.edit { preferences ->
            preferences.remove(RECENT_INGREDIENTS_KEY)
        }
    }
}