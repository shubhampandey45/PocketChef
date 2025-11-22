package com.sp45.pocketchef.data.local.recipe


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    // For Caching [cite: 86]
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<RecipeEntity>)

    @Query("SELECT * FROM recipes ORDER BY cachedTime DESC LIMIT 5")
    fun getCachedRecipes(): Flow<List<RecipeEntity>>

    @Query("DELETE FROM recipes")
    suspend fun clearCache()

    // For Favourites (This assumes favourites are also cached,
    // but the spec points to Firestore as primary [cite: 90])
}