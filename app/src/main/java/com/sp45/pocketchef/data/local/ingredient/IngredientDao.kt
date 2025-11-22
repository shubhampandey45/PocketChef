package com.sp45.pocketchef.data.local.ingredient

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDao {
    // Inserts or replaces an ingredient if it already exists (useful for updates)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredient: Ingredient)

    // Deletes a specific ingredient
    @Delete
    suspend fun deleteIngredient(ingredient: Ingredient)

    // Deletes an ingredient by its unique ID
    @Query("DELETE FROM ingredients WHERE id = :ingredientId")
    suspend fun deleteIngredientById(ingredientId: String)

    // Observe all ingredients, ordered by the time they were added
    @Query("SELECT * FROM ingredients ORDER BY addedTime DESC")
    fun getAllIngredients(): Flow<List<Ingredient>>

    // Simple query to check if an ingredient exists by name (for preventing duplicates)
    @Query("SELECT EXISTS(SELECT 1 FROM ingredients WHERE name = :name COLLATE NOCASE LIMIT 1)")
    suspend fun ingredientExistsByName(name: String): Boolean
}