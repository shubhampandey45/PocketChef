package com.sp45.pocketchef.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object Auth : Screen("auth_screen")
    object Main : Screen("main")

    // Bottom nav destinations
    object InputScreen : Screen("input_screen")
    object Saved : Screen("saved_screen")
    object Profile : Screen("profile_screen")

    // Other Screens
    object RecipeSuggestions : Screen("recipe_suggestions_screen")
}