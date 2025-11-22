package com.sp45.pocketchef.domain.repository

import kotlinx.coroutines.flow.Flow
import com.sp45.pocketchef.domain.UserProfile
import com.sp45.pocketchef.util.Resource

interface UserRepository {
    suspend fun saveUserProfile(userProfile: UserProfile): Flow<Resource<Boolean>>
    fun getUserProfile(userId: String): Flow<Resource<UserProfile>>
    suspend fun updateUserProfile(userProfile: UserProfile): Resource<Boolean>

    // method for updating specific preferences
    suspend fun updateUserPreferences(
        userId: String,
        dietaryPreferences: List<String>,
        allergies: List<String>,
        dailyRecipeReminders: Boolean,
        cookingTipAlerts: Boolean
    ): Resource<Boolean>

}