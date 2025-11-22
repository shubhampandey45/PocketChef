package com.sp45.pocketchef.presentation.ui.theme

import androidx.compose.ui.graphics.Color

// --- PocketChef Brand Colors ---
// Primary Teal: #009688 (New Main brand color, CTAs)
val PocketChefTeal = Color(0xFF009688)

// Dark Charcoal / Black: #2D2D2D (Headings, high-contrast text, dark surfaces)
// Keeping this as it provides excellent contrast for text
val PocketChefDarkCharcoal = Color(0xFF2D2D2D)

// White: #FFFFFF (Clean base, cards)
val PocketChefWhite = Color(0xFFFFFFFF)

// Light Neutral Grey: #F4F4F2 (Soft background)
val PocketChefLightGrey = Color(0xFFF4F4F2)

// --- Light Theme Palette (Based on PocketChef Teal) ---
val primaryLight = PocketChefTeal // Primary Teal for CTAs, buttons
val onPrimaryLight = PocketChefWhite // White text on Teal background
val primaryContainerLight = Color(0xFFB2DFDB) // Lighter tone of teal for containers
val onPrimaryContainerLight = Color(0xFF00201C) // Dark teal text on light container

val secondaryLight = PocketChefDarkCharcoal // Dark Charcoal for secondary elements/icons
val onSecondaryLight = PocketChefWhite // White text on dark charcoal
val secondaryContainerLight = PocketChefLightGrey // Light Grey for soft contrast elements
val onSecondaryContainerLight = PocketChefDarkCharcoal // Dark text on light grey

// Backgrounds and Surfaces
val backgroundLight = PocketChefLightGrey // Soft background
val onBackgroundLight = PocketChefDarkCharcoal // Dark text on light background
val surfaceLight = PocketChefWhite // White for cards/content areas
val onSurfaceLight = PocketChefDarkCharcoal // Dark text on white surface
val surfaceVariantLight = PocketChefLightGrey // Light Grey for separators/variants
val onSurfaceVariantLight = PocketChefDarkCharcoal // Dark text on light grey variant

// Custom Tertiary (Spiced Orange to complement Teal)
val tertiaryLight = Color(0xFF9C4300)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFFFDBC9)
val onTertiaryContainerLight = Color(0xFF351200)

// Default Material 3 colors (Error, Outline)
val errorLight = Color(0xFFBA1A1A)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD6)
val onErrorContainerLight = Color(0xFF93000A)
val outlineLight = Color(0xFF707977)
val outlineVariantLight = Color(0xFFBFC9C6)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF2E3130)
val inverseOnSurfaceLight = Color(0xFFF0F1F0)
val inversePrimaryLight = Color(0xFF80CBC4)
val surfaceDimLight = Color(0xFFD9DBD9)
val surfaceBrightLight = PocketChefWhite
val surfaceContainerLowestLight = PocketChefWhite
val surfaceContainerLowLight = PocketChefLightGrey
val surfaceContainerLight = Color(0xFFEAECEC)
val surfaceContainerHighLight = Color(0xFFE4E6E5)
val surfaceContainerHighestLight = Color(0xFFDEE0DF)

// --- Dark Theme Palette (Inverted PocketChef Colors) ---
val primaryDark = Color(0xFF80CBC4) // Lighter Teal for Dark Mode visibility
val onPrimaryDark = Color(0xFF003732) // Dark text on Light Teal
val primaryContainerDark = Color(0xFF005048) // Deep Teal container
val onPrimaryContainerDark = Color(0xFFB2DFDB) // Light text on Deep container

val secondaryDark = PocketChefLightGrey // Light Grey for secondary elements/icons on dark surfaces
val onSecondaryDark = PocketChefDarkCharcoal // Dark text on light grey
val secondaryContainerDark = PocketChefDarkCharcoal // Dark Charcoal for soft contrast elements
val onSecondaryContainerDark = PocketChefLightGrey // Light text on dark charcoal

// Backgrounds and Surfaces
val backgroundDark = PocketChefDarkCharcoal // Dark charcoal background
val onBackgroundDark = PocketChefWhite // White text on dark background
val surfaceDark = PocketChefDarkCharcoal // Dark charcoal for cards/content areas
val onSurfaceDark = PocketChefWhite // White text on dark surface
val surfaceVariantDark = Color(0xFF3F4947) // Dark teal-grey for variants
val onSurfaceVariantDark = PocketChefLightGrey // Light grey text on dark variant

// Custom Tertiary (Softer Orange for Dark Mode)
val tertiaryDark = Color(0xFFFFB68E)
val onTertiaryDark = Color(0xFF552100)
val tertiaryContainerDark = Color(0xFF783200)
val onTertiaryContainerDark = Color(0xFFFFDBC9)

// Default Material 3 colors (Error, Outline)
val errorDark = Color(0xFFFFB4AB)
val onErrorDark = Color(0xFF690005)
val errorContainerDark = Color(0xFF93000A)
val onErrorContainerDark = Color(0xFFFFDAD6)
val outlineDark = Color(0xFF899390)
val outlineVariantDark = Color(0xFF3F4947)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFE1E3E2)
val inverseOnSurfaceDark = PocketChefDarkCharcoal
val inversePrimaryDark = Color(0xFF006A60)
val surfaceDimDark = PocketChefDarkCharcoal
val surfaceBrightDark = Color(0xFF353A39)
val surfaceContainerLowestDark = Color(0xFF0D100F)
val surfaceContainerLowDark = Color(0xFF1A1D1C)
val surfaceContainerDark = PocketChefDarkCharcoal
val surfaceContainerHighDark = Color(0xFF262A29)
val surfaceContainerHighestDark = Color(0xFF313534)