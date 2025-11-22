package com.sp45.pocketchef.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import com.sp45.pocketchef.data.local.recipe.RecipeDao
import com.sp45.pocketchef.data.local.recipe.RecipeEntity
import com.sp45.pocketchef.domain.repository.RecipeRepository

@Singleton
class RecipeRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val recipeDao: RecipeDao
) : RecipeRepository {

    private val userId: String?
        get() = auth.currentUser?.uid.also {
            println("Current user ID: $it") // Debug log
        }

    // Path for user's favourites [cite: 101]
    private fun favoritesCollection() = userId?.let {
        firestore.collection("users").document(it).collection("favourites")
    }

    override suspend fun saveRecipeToFavorites(recipe: RecipeEntity) {
        val collection = favoritesCollection()
        if (collection == null) {
            println("User not logged in - cannot save favorite")
            throw Exception("User not logged in. Please sign in to save recipes.")
        } else {
            collection.document(recipe.id).set(recipe).await()
        }
    }

    override fun getFavoriteRecipes(): Flow<List<RecipeEntity>> {
        val collection = favoritesCollection()
        return collection?.snapshots()?.map { snapshot ->
            snapshot.toObjects(RecipeEntity::class.java)
        } ?: flowOf(emptyList())
    }

    override suspend fun deleteFavorite(recipe: RecipeEntity) {
        val collection = favoritesCollection()
        if (collection == null) {
            println("User not logged in - cannot delete favorite")
            throw Exception("User not logged in. Please sign in to manage favorites.")
        } else {
            collection.document(recipe.id).delete().await()
        }
    }

    override suspend fun cacheRecipes(recipes: List<RecipeEntity>) {
        // Clear old cache and insert new [cite: 86]
        recipeDao.clearCache()
        recipeDao.insertRecipes(recipes)
    }

    override fun getCachedRecipes(): Flow<List<RecipeEntity>> {
        return recipeDao.getCachedRecipes()
    }
}