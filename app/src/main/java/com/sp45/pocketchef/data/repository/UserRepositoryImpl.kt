package com.sp45.pocketchef.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import com.sp45.pocketchef.domain.UserProfile
import com.sp45.pocketchef.domain.repository.UserRepository
import com.sp45.pocketchef.util.Constants
import com.sp45.pocketchef.util.Resource
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {

    override suspend fun saveUserProfile(userProfile: UserProfile): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            firestore.collection(Constants.USERS_COLLECTION)
                .document(userProfile.userId)
                .set(userProfile)
                .await()
            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to save profile"))
        }
    }

    override fun getUserProfile(userId: String): Flow<Resource<UserProfile>> = flow {
        emit(Resource.Loading())
        try {
            val document = firestore.collection(Constants.USERS_COLLECTION)
                .document(userId)
                .get()
                .await()

            if (document.exists()) {
                val userProfile = document.toObject(UserProfile::class.java)
                emit(Resource.Success(userProfile!!))
            } else {
                // Create default profile if doesn't exist
                val defaultProfile = UserProfile(
                    userId = userId,
                    email = "", // Will be filled by ViewModel
                    fullName = "PocketChef User",
                    dietaryPreferences = emptyList(),
                    allergies = emptyList(),
                    dailyRecipeReminders = true,
                    cookingTipAlerts = true
                )
                saveUserProfile(defaultProfile)
                emit(Resource.Success(defaultProfile))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to get profile"))
        }
    }

    override suspend fun updateUserProfile(userProfile: UserProfile): Resource<Boolean> {
        return try {
            firestore.collection(Constants.USERS_COLLECTION).document(userProfile.userId)
                .set(userProfile)
                .await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to update user profile")
        }
    }

    override suspend fun updateUserPreferences(
        userId: String,
        dietaryPreferences: List<String>,
        allergies: List<String>,
        dailyRecipeReminders: Boolean,
        cookingTipAlerts: Boolean
    ): Resource<Boolean> {
        return try {
            val updates = mapOf(
                "dietaryPreferences" to dietaryPreferences,
                "allergies" to allergies,
                "dailyRecipeReminders" to dailyRecipeReminders,
                "cookingTipAlerts" to cookingTipAlerts
            )

            firestore.collection(Constants.USERS_COLLECTION).document(userId)
                .update(updates)
                .await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to update preferences")
        }
    }
}