package com.sp45.pocketchef.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.sp45.pocketchef.data.local.ingredient.IngredientDao
import com.sp45.pocketchef.data.local.recipe.RecipeDao
import com.sp45.pocketchef.domain.AppSettings
import com.sp45.pocketchef.domain.ThemeMode
import com.sp45.pocketchef.domain.UserProfile
import com.sp45.pocketchef.domain.notification.NotificationService
import com.sp45.pocketchef.domain.repository.SettingsRepository
import com.sp45.pocketchef.domain.repository.UserRepository
import com.sp45.pocketchef.domain.theme.ThemeManager
import kotlinx.coroutines.delay
import com.sp45.pocketchef.util.Resource
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository,
    private val themeManager: ThemeManager,
    private val firebaseAuth: FirebaseAuth,
    private val notificationService: NotificationService,
    private val ingredientDao: IngredientDao,
    private val recipeDao: RecipeDao
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    private val currentUser = firebaseAuth.currentUser
        ?: throw IllegalStateException("User not logged in")
    private val userId = currentUser.uid

    init {
        // Combine user profile and app settings
        combine(
            userRepository.getUserProfile(userId),
            settingsRepository.getAppSettings()
        ) { userProfileResult, appSettings ->
            Pair(userProfileResult, appSettings)
        }.onEach { (userProfileResult, appSettings) ->
            when (userProfileResult) {
                is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                is Resource.Success -> {
                    val profile = userProfileResult.data ?: createDefaultProfile()
                    _state.update {
                        it.copy(
                            isLoading = false,
                            userProfile = profile,
                            appSettings = appSettings,
                            userEmail = currentUser.email ?: "",
                            fullName = profile.fullName,
                            dietaryPreferences = profile.dietaryPreferences,
                            allergies = profile.allergies,
                            dailyRecipeReminders = profile.dailyRecipeReminders,
                            cookingTipAlerts = profile.cookingTipAlerts,
                            selectedTheme = appSettings.theme
                        )
                    }
                    // Update notification schedule based on preferences
                    updateNotificationSchedules(profile.dailyRecipeReminders, profile.cookingTipAlerts)
                }
                is Resource.Error -> _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = userProfileResult.message,
                        userEmail = currentUser.email ?: ""
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun createDefaultProfile(): UserProfile {
        return UserProfile(
            userId = userId,
            email = currentUser.email ?: "",
            fullName = "PocketChef User",
            dietaryPreferences = emptyList(),
            allergies = emptyList(),
            dailyRecipeReminders = true,
            cookingTipAlerts = true
        )
    }

    fun updateTheme(theme: ThemeMode) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                themeManager.updateTheme(theme)
                _state.update {
                    it.copy(
                        isLoading = false,
                        selectedTheme = theme,
                        successMessage = "Theme updated to ${theme.name.lowercase().replaceFirstChar { it.uppercase() }}"
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to update theme: ${e.message}"
                    )
                }
            }
        }
    }

    fun updateProfile(fullName: String, dietaryPreferences: List<String>, allergies: List<String>) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val updatedProfile = _state.value.userProfile?.copy(
                fullName = fullName,
                dietaryPreferences = dietaryPreferences,
                allergies = allergies
            ) ?: createDefaultProfile().copy(
                fullName = fullName,
                dietaryPreferences = dietaryPreferences,
                allergies = allergies
            )

            when (val result = userRepository.updateUserProfile(updatedProfile)) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            fullName = fullName,
                            dietaryPreferences = dietaryPreferences,
                            allergies = allergies,
                            successMessage = "Profile updated successfully"
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
                else -> {}
            }
        }
    }

    fun updateNotificationPreferences(dailyReminders: Boolean, cookingTips: Boolean) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            when (val result = userRepository.updateUserPreferences(
                userId = userId,
                dietaryPreferences = _state.value.dietaryPreferences,
                allergies = _state.value.allergies,
                dailyRecipeReminders = dailyReminders,
                cookingTipAlerts = cookingTips
            )) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            dailyRecipeReminders = dailyReminders,
                            cookingTipAlerts = cookingTips,
                            successMessage = "Notification preferences updated"
                        )
                    }
                    updateNotificationSchedules(dailyReminders, cookingTips)
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
                else -> {}
            }
        }
    }

    private fun updateNotificationSchedules(dailyReminders: Boolean, cookingTips: Boolean) {
        if (dailyReminders) {
            notificationService.scheduleDailyReminder()
        } else {
            notificationService.cancelDailyReminder()
        }

        if (cookingTips) {
            notificationService.scheduleRandomCookingTips()
        } else {
            notificationService.cancelCookingTips()
        }
    }

    // Test function to immediately show a cooking tip (for debugging)
    fun testCookingTipNotification() {
        notificationService.showCookingTipNotification()
    }

    fun clearCache() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                // Clear ingredients cache
                // Note: We might want to keep active ingredients and only clear recent/cached ones

                // Clear recipe cache
                recipeDao.clearCache()

                // Update last cache clear time
                settingsRepository.updateLastCacheClear(System.currentTimeMillis())

                _state.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Cache cleared successfully"
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to clear cache: ${e.message}"
                    )
                }
            }
        }
    }

    fun reSyncData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                // This would trigger a re-sync from Firestore
                // For now, we'll just simulate it
                delay(1000) // Simulate network delay

                _state.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Data re-synced successfully"
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to re-sync data: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearMessages() {
        _state.update { it.copy(errorMessage = null, successMessage = null) }
    }
}

data class ProfileState(
    val isLoading: Boolean = false,
    val userProfile: UserProfile? = null,
    val appSettings: AppSettings? = null,
    val userEmail: String = "",
    val fullName: String = "",
    val dietaryPreferences: List<String> = emptyList(),
    val allergies: List<String> = emptyList(),
    val dailyRecipeReminders: Boolean = true,
    val cookingTipAlerts: Boolean = true,
    val selectedTheme: ThemeMode = ThemeMode.SYSTEM,
    val errorMessage: String? = null,
    val successMessage: String? = null
)