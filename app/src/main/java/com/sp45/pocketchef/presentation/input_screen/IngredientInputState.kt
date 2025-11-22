package com.sp45.pocketchef.presentation.input_screen

import com.sp45.pocketchef.data.local.ingredient.Ingredient

// State class to hold all necessary UI data for the Ingredient Input Screen
data class IngredientInputState(
    val activeIngredients: List<Ingredient> = emptyList(),
    val currentInput: String = "",
    val smartSuggestions: List<String> = emptyList(), // Gemini API suggestions (autocomplete)
    val recentIngredients: List<String> = emptyList(), // Cached ingredients from DataStore
    val isLoading: Boolean = false,
    val isSuggestionLoading: Boolean = false
)