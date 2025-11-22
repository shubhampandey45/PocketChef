package com.sp45.pocketchef.presentation.navigation


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Data class representing bottom navigation items
 */
data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

/**
 * List of bottom navigation items
 */
val bottomNavItems = listOf(
    BottomNavItem(
        label = "Home",
        icon = Icons.Default.Home,
        route = Screen.InputScreen.route
    ),
    BottomNavItem(
        label = "Saved",
        icon = Icons.Default.Favorite,
        route = Screen.Saved.route
    ),
    BottomNavItem(
        label = "Profile",
        icon = Icons.Default.Person,
        route = Screen.Profile.route
    )
)