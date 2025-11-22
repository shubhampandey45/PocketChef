package com.sp45.pocketchef.presentation.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// --- LIGHT THEME COLOR SCHEME (Using PocketChef Teal Palette) ---
private val lightScheme = lightColorScheme(
    primary = primaryLight, // #009688 (Teal)
    onPrimary = onPrimaryLight, // #FFFFFF (White)
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight, // #2D2D2D (Dark Charcoal)
    onSecondary = onSecondaryLight, // #FFFFFF (White)
    secondaryContainer = secondaryContainerLight, // #F4F4F2 (Light Grey)
    onSecondaryContainer = onSecondaryContainerLight, // #2D2D2D (Dark Charcoal)
    tertiary = tertiaryLight, // Spiced Orange
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight, // #F4F4F2 (Light Grey)
    onBackground = onBackgroundLight, // #2D2D2D (Dark Charcoal)
    surface = surfaceLight, // #FFFFFF (White)
    onSurface = onSurfaceLight, // #2D2D2D (Dark Charcoal)
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

// --- DARK THEME COLOR SCHEME (Inverted PocketChef Palette) ---
private val darkScheme = darkColorScheme(
    primary = primaryDark, // #80CBC4 (Light Teal)
    onPrimary = onPrimaryDark, // #003732 (Deep Teal)
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark, // #F4F4F2 (Light Grey)
    onSecondary = onSecondaryDark, // #2D2D2D (Dark Charcoal)
    secondaryContainer = secondaryContainerDark, // #2D2D2D (Dark Charcoal)
    onSecondaryContainer = onSecondaryContainerDark, // #F4F4F2 (Light Grey)
    tertiary = tertiaryDark, // Soft Orange
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark, // #2D2D2D (Dark Charcoal)
    onBackground = onBackgroundDark, // #FFFFFF (White)
    surface = surfaceDark, // #2D2D2D (Dark Charcoal)
    onSurface = onSurfaceDark, // #FFFFFF (White)
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

// Placeholder for Typography, since no specifics were provided
val AppTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)

@Composable
fun PocketChefTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> darkScheme
        else -> lightScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}