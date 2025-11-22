package com.sp45.pocketchef.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sp45.pocketchef.presentation.splash.SplashScreen
import com.sp45.pocketchef.presentation.auth.AuthScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // Splash Screen
        composable(route = Screen.Splash.route) {
            SplashScreen(navController = navController)
        }

        // Auth Screen (Login/Register)
        composable(route = Screen.Auth.route) {
            AuthScreen(navController = navController)
        }

        // Main Screen with Bottom Navigation
        composable(route = Screen.Main.route) {
            MainScreen(mainNavController = navController)
        }
    }
}