package com.sp45.pocketchef.presentation.ui.theme

import androidx.compose.ui.graphics.Color

// --- PocketChef Brand Colors ---
// New Primary Teal: #00ceb7 (Bright, vibrant)
val PocketChefTeal = Color(0xFF00CEB7)

// Black: #000000 (Replaces Charcoal for deep contrast)
val PocketChefBlack = Color(0xFF000000)

// White: #FFFFFF
val PocketChefWhite = Color(0xFFFFFFFF)

// Light Grey: #F4F4F2 (Retained for Light Mode backgrounds to avoid harsh white)
val PocketChefLightGrey = Color(0xFFF4F4F2)

// --- Light Theme Palette ---
val primaryLight = PocketChefTeal
val onPrimaryLight = PocketChefBlack // Black text is required for readability on bright teal
val primaryContainerLight = Color(0xFFA0EFE7) // Very light teal
val onPrimaryContainerLight = Color(0xFF00201C) // Dark teal text

// Secondary: Using Black as the secondary bold element
val secondaryLight = PocketChefBlack
val onSecondaryLight = PocketChefWhite
val secondaryContainerLight = Color(0xFF333333)
val onSecondaryContainerLight = PocketChefWhite

// Backgrounds and Surfaces
val backgroundLight = PocketChefWhite // Clean White background
val onBackgroundLight = PocketChefBlack // Black text
val surfaceLight = PocketChefLightGrey // Slight separation for cards
val onSurfaceLight = PocketChefBlack
val surfaceVariantLight = Color(0xFFDBDBDB)
val onSurfaceVariantLight = PocketChefBlack

// Custom Tertiary (Spiced Orange - Complementary to Teal)
val tertiaryLight = Color(0xFFFF6F00)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFFFDBC9)
val onTertiaryContainerLight = Color(0xFF351200)

// Default Material 3 colors
val errorLight = Color(0xFFBA1A1A)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD6)
val onErrorContainerLight = Color(0xFF410002)

val outlineLight = Color(0xFF707977)
val outlineVariantLight = Color(0xFFBFC9C6)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF2E3130)
val inverseOnSurfaceLight = Color(0xFFF0F1F0)
val inversePrimaryLight = Color(0xFF4CDBC9) // Lighter teal for inverse
val surfaceDimLight = Color(0xFFD9DBD9)
val surfaceBrightLight = PocketChefWhite
val surfaceContainerLowestLight = PocketChefWhite
val surfaceContainerLowLight = PocketChefLightGrey
val surfaceContainerLight = Color(0xFFEAECEC)
val surfaceContainerHighLight = Color(0xFFE4E6E5)
val surfaceContainerHighestLight = Color(0xFFDEE0DF)

// --- Dark Theme Palette (True Black) ---
val primaryDark = PocketChefTeal // Teal pops incredibly well on black
val onPrimaryDark = PocketChefBlack // Keep text black for contrast
val primaryContainerDark = Color(0xFF004E45) // Deep Teal
val onPrimaryContainerDark = Color(0xFFA0EFE7) // Light Teal text

val secondaryDark = PocketChefWhite // White accents on Black background
val onSecondaryDark = PocketChefBlack
val secondaryContainerDark = Color(0xFF333333)
val onSecondaryContainerDark = PocketChefWhite

// Backgrounds and Surfaces
val backgroundDark = PocketChefBlack // True Black background
val onBackgroundDark = PocketChefWhite
val surfaceDark = Color(0xFF121212) // Slightly lighter than black for cards (Material standard)
val onSurfaceDark = PocketChefWhite
val surfaceVariantDark = Color(0xFF404040)
val onSurfaceVariantDark = Color(0xFFCCCCCC)

// Tertiary (Softer Orange for Dark Mode)
val tertiaryDark = Color(0xFFFFB68E)
val onTertiaryDark = Color(0xFF552100)
val tertiaryContainerDark = Color(0xFF783200)
val onTertiaryContainerDark = Color(0xFFFFDBC9)

val errorDark = Color(0xFFFFB4AB)
val onErrorDark = Color(0xFF690005)
val errorContainerDark = Color(0xFF93000A)
val onErrorContainerDark = Color(0xFFFFDAD6)
val outlineDark = Color(0xFF899390)
val outlineVariantDark = Color(0xFF404040)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFE1E3E2)
val inverseOnSurfaceDark = PocketChefBlack
val inversePrimaryDark = Color(0xFF006A60)
val surfaceDimDark = PocketChefBlack
val surfaceBrightDark = Color(0xFF353A39)
val surfaceContainerLowestDark = Color(0xFF000000)
val surfaceContainerLowDark = Color(0xFF1A1D1C)
val surfaceContainerDark = Color(0xFF121212)
val surfaceContainerHighDark = Color(0xFF262A29)
val surfaceContainerHighestDark = Color(0xFF313534)