package com.sp45.pocketchef.presentation.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.sp45.pocketchef.domain.repository.GeminiRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val geminiRepository: GeminiRepository
) : ViewModel() {

    private val TAG = "SplashViewModel"

    private val _state = MutableStateFlow(SplashState())
    val state = _state.asStateFlow()

    private val _navigationEvent = MutableStateFlow<String?>(null)
    val navigationEvent = _navigationEvent.asStateFlow()

    init {
        checkAuthAndLoadContent()
    }

    private fun checkAuthAndLoadContent() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, statusMessage = "Checking connection...") }

                // Load cooking tip first (works offline/online)
                loadCookingTip()

                // Check authentication status
                val currentUser = firebaseAuth.currentUser

                _state.update { it.copy(statusMessage = "Almost ready...") }

                // Simulate minimum splash time (2-3 seconds)
                delay(2500)

                if (currentUser == null) {
                    Log.d(TAG, "No user logged in, navigating to Auth")
                    _navigationEvent.value = "auth_screen"
                } else {
                    Log.d(TAG, "User logged in: ${currentUser.uid}, navigating to Main")
                    _navigationEvent.value = "main"
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error during splash initialization", e)
                // Even if there's an error, navigate after delay
                delay(2500)
                _navigationEvent.value = "auth_screen"
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private suspend fun loadCookingTip() {
        _state.update { it.copy(statusMessage = "Loading cooking tips...") }

        try {
            // Try to get a fresh tip from Gemini
            val tip = geminiRepository.getCookingTip()
            _state.update {
                it.copy(
                    cookingTip = tip,
                    isOnline = true,
                    statusMessage = "Found a fresh cooking tip!"
                )
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to get online tip, using cached/offline tip", e)
            // Use offline fallback tip
            val offlineTip = geminiRepository.getCookingTip()
            _state.update {
                it.copy(
                    cookingTip = offlineTip,
                    isOnline = false,
                    statusMessage = "Using offline mode"
                )
            }
        }
    }

//    private fun getOfflineCookingTip(): String {
//        val offlineTips = listOf(
//            "Let meat rest for 5-10 minutes after cooking for juicier results! 🥩",
//            "Salt your pasta water generously - it should taste like the sea! 🌊",
//            "Use a sharp knife for safer, cleaner cuts in the kitchen! 🔪",
//            "Room temperature eggs incorporate better in baking recipes! 🥚",
//            "Don't overcrowd the pan - it steams food instead of browning! 🍳",
//            "Taste as you cook and adjust seasoning gradually! 👨‍🍳",
//            "Fresh herbs should be added at the end for maximum flavor! 🌿"
//        )
//        return offlineTips.random()
//    }

    fun resetNavigationEvent() {
        _navigationEvent.value = null
    }
}

data class SplashState(
    val isLoading: Boolean = true,
    val isOnline: Boolean = true,
    val cookingTip: String? = null,
    val statusMessage: String = "Getting things ready..."
)