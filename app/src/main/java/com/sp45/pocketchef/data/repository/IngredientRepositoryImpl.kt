package com.sp45.pocketchef.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.sp45.pocketchef.data.local.ingredient.Ingredient
import com.sp45.pocketchef.data.local.ingredient.IngredientDao
import com.sp45.pocketchef.domain.repository.IngredientLocalRepository
import com.sp45.pocketchef.domain.repository.IngredientRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IngredientRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val ingredientDao: IngredientDao,
    private val localRepository: IngredientLocalRepository
) : IngredientRepository {

    // Coroutine scope for repository operations
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val userId: String?
        get() = auth.currentUser?.uid

    // Path for the user's ingredients collection in Firestore
    private fun ingredientsCollection() = userId?.let {
        firestore.collection("users").document(it).collection("ingredients")
    }

    override fun getActiveIngredients(): Flow<List<Ingredient>> {
        // Always return the local DB's data as the source of truth for the UI
        return ingredientDao.getAllIngredients()
    }

    override fun getRecentIngredients(): Flow<List<String>> {
        return localRepository.getRecentIngredients()
    }

    override suspend fun saveIngredient(ingredient: Ingredient) {
        // 1. Save locally first (fastest response to UI)
        localRepository.saveIngredient(ingredient) // This handles Room + DataStore

        // 2. Save to Firestore (in background)
        scope.launch {
            try {
                ingredientsCollection()?.document(ingredient.id)?.set(ingredient)?.await()
            } catch (e: Exception) {
                println("Firestore save error: ${e.message}")
            }
        }
    }

    override suspend fun deleteIngredient(ingredient: Ingredient) {
        // 1. Delete locally first
        ingredientDao.deleteIngredient(ingredient)

        // 2. Delete from Firestore (in background)
        scope.launch {
            try {
                ingredientsCollection()?.document(ingredient.id)?.delete()?.await()
            } catch (e: Exception) {
                println("Firestore delete error: ${e.message}")
            }
        }
    }

    override suspend fun syncIngredientsFromFirestore() {
        val uid = userId ?: return // Exit if no user is logged in
        try {
            val snapshot = firestore.collection("users").document(uid)
                .collection("ingredients").get().await()

            val ingredients = snapshot.toObjects(Ingredient::class.java)

            // Insert all fetched ingredients into the local Room DB
            // The OnConflictStrategy.REPLACE will update existing entries
            ingredients.forEach { ingredient ->
                ingredientDao.insertIngredient(ingredient)
            }
        } catch (e: Exception) {
            println("Error syncing ingredients from Firestore: ${e.message}")
        }
    }
}