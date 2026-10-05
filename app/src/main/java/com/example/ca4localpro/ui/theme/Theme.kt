package com.example.ca4localpro.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = LocalProTealLight,
    onPrimary = DarkOnPrimary,
    primaryContainer = LocalProTealDark,
    onPrimaryContainer = LocalProTealContainer,
    secondary = LocalProAmberSecondary,
    onSecondary = DarkOnPrimary,
    secondaryContainer = LocalProAmberContainer,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    error = LocalProEmergencyRed,
    errorContainer = LocalProEmergencyContainer
)

private val LightColorScheme = lightColorScheme(
    primary = LocalProTealPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LocalProTealContainer,
    onPrimaryContainer = LocalProTealDark,
    secondary = LocalProAmberSecondary,
    onSecondary = LightOnPrimary,
    secondaryContainer = LocalProAmberContainer,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    error = LocalProEmergencyRed,
    errorContainer = LocalProEmergencyContainer
)

@Composable
fun Ca4LocalproTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = LocalProTypography,
        content = content
    )
}
