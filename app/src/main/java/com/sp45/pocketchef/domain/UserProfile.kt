package com.sp45.pocketchef.domain

data class UserProfile(
    val userId: String = "",
    val email: String = "",
    val fullName: String = "",
    val profilePhotoUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),

    // for dietary preferences and allergies
    val dietaryPreferences: List<String> = emptyList(),
    val allergies: List<String> = emptyList(),

    // Notification preferences
    val dailyRecipeReminders: Boolean = true,
    val cookingTipAlerts: Boolean = true
)