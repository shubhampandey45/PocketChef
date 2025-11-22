package com.sp45.pocketchef.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.sp45.pocketchef.presentation.input_screen.InputScreen
import com.sp45.pocketchef.presentation.profile.ProfileScreen
import com.sp45.pocketchef.presentation.recipe_suggestion.RecipeSuggestionsScreen
import com.sp45.pocketchef.presentation.save.SaveScreen

@Composable
fun MainScreen(mainNavController: NavHostController) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label) },
                        selected = currentRoute?.startsWith(item.route) == true, // Handle nested routes
                        onClick = {
                            bottomNavController.navigate(item.route) {
                                popUpTo(bottomNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        // This NavHost now controls all screens *within* the main app area
        NavHost(
            navController = bottomNavController,
            startDestination = Screen.InputScreen.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            // --- Bottom Nav Destinations ---

            composable(Screen.InputScreen.route) {
                // Updated call: Pass the NavController
                InputScreen(navController = bottomNavController)
            }

            composable(Screen.Saved.route) {
                SaveScreen(navController = bottomNavController)
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    navController = bottomNavController,
                    onLogout = {
                        FirebaseAuth.getInstance().signOut()
                        mainNavController.navigate(Screen.Auth.route) {
                            popUpTo(Screen.Main.route) { inclusive = true }
                        }
                    }
                )
            }

            val suggestionsRoute = "${Screen.RecipeSuggestions.route}?ingredients={ingredients}"

            composable(
                route = suggestionsRoute,
                arguments = listOf(navArgument("ingredients") {
                    type = NavType.StringType
                    nullable = true
                })
            ) { backStackEntry ->
                RecipeSuggestionsScreen(navController = bottomNavController)
            }
        }
    }
}