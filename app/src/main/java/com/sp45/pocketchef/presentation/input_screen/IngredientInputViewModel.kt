package com.sp45.pocketchef.presentation.input_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.sp45.pocketchef.data.local.ingredient.Ingredient
import com.sp45.pocketchef.domain.repository.GeminiRepository
import com.sp45.pocketchef.domain.repository.IngredientRepository
import com.sp45.pocketchef.presentation.navigation.Screen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@HiltViewModel
class IngredientInputViewModel @Inject constructor(
    private val repository: IngredientRepository, // Using the main repository
    private val geminiRepository: GeminiRepository // Using the Gemini repository
) : ViewModel() {

    // --- State Initialization ---

    private val _currentInput = MutableStateFlow("")
    private val _smartSuggestions = MutableStateFlow<List<String>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _isSuggestionLoading = MutableStateFlow(false)
    private val gson = Gson() // For JSON serialization

    // Navigation event flow (as per project spec)
    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private var suggestionJob: Job? = null

    init {
        // Sync ingredients from Firestore when ViewModel is created
        viewModelScope.launch {
            repository.syncIngredientsFromFirestore()
        }
    }

    // Combine all relevant Flows into a single StateFlow for the UI
    // Combine all relevant Flows into a single StateFlow for the UI

    val state: StateFlow<IngredientInputState> = combine(

        combine(

            repository.getActiveIngredients(),

            repository.getRecentIngredients(),

            _currentInput

        ) { activeList, recentList, input ->

            Triple(activeList, recentList, input)

        },

        combine(

            _smartSuggestions,

            _isLoading,

            _isSuggestionLoading

        ) { smartList, loading, suggestionLoading ->

            Triple(smartList, loading, suggestionLoading)

        }

    ) { first, second ->

        IngredientInputState(

            activeIngredients = first.first,

            recentIngredients = first.second,

            currentInput = first.third,

            smartSuggestions = second.first,

            isLoading = second.second,

            isSuggestionLoading = second.third

        )

    }.stateIn(

        scope = viewModelScope,

        started = SharingStarted.WhileSubscribed(5000),

        initialValue = IngredientInputState()

    )

    // --- UI Interaction Handlers ---

    fun onInputChanged(newInput: String) {
        _currentInput.value = newInput
        // Trigger smart suggestions fetch
        fetchGeminiSuggestions(newInput)
    }

    private fun fetchGeminiSuggestions(query: String) {
        suggestionJob?.cancel()
        suggestionJob = viewModelScope.launch {
            if (query.length < 2) {
                _smartSuggestions.value = emptyList()
                _isSuggestionLoading.value = false
                return@launch
            }
            _isSuggestionLoading.value = true
            // Debounce for 300ms
            delay(300)
            try {
                // --- Real Gemini API call ---
                _smartSuggestions.value = geminiRepository.getIngredientSuggestions(query)
            } catch (e: Exception) {
                println("Gemini Suggestion Error: ${e.message}")
                _smartSuggestions.value = emptyList()
            } finally {
                _isSuggestionLoading.value = false
            }
        }
    }

    fun addIngredient(name: String) {
        if (name.isBlank()) return

        val trimmedName = name.trim().replaceFirstChar { it.uppercase() }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Save to local Room DB AND sync to Firestore via the repository
                repository.saveIngredient(Ingredient(name = trimmedName))
                _currentInput.value = ""
                _smartSuggestions.value = emptyList()
            } catch (e: Exception) {
                println("Error saving ingredient: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removeIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            // Delete from Room DB AND sync delete to Firestore
            repository.deleteIngredient(ingredient)
        }
    }

    fun onFindRecipesClicked() {
        viewModelScope.launch {
            val ingredientList = state.value.activeIngredients
            if (ingredientList.isEmpty()) return@launch

            // 1. Get only the names
            val ingredientNames = ingredientList.map { it.name }

            // 2. Serialize the list to JSON
            val ingredientListJson = gson.toJson(ingredientNames)

            // 3. URL-encode the JSON string [cite: 62]
            val encodedJson = URLEncoder.encode(ingredientListJson, StandardCharsets.UTF_8.name())

            // 4. Create the full navigation route
            val route = "${Screen.RecipeSuggestions.route}?ingredients=$encodedJson"

            // 5. Send the navigation event to the UI
            _navigationEvent.emit(route)
        }
    }
}