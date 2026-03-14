package com.example.historycalendar.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Blue600,
    onPrimary = White,
    primaryContainer = Blue100,
    onPrimaryContainer = Ink900,
    secondary = Amber600,
    onSecondary = Ink900,
    secondaryContainer = Amber100,
    onSecondaryContainer = Ink900,
    tertiary = Slate500,
    background = Slate50,
    onBackground = Ink900,
    surface = White,
    onSurface = Ink900,
    surfaceVariant = Slate100,
    outline = Slate300
)

private val DarkColors = darkColorScheme(
    primary = Blue300,
    onPrimary = Ink900,
    primaryContainer = Blue900,
    onPrimaryContainer = Blue50,
    secondary = Amber300,
    onSecondary = Ink900,
    secondaryContainer = Ink700,
    onSecondaryContainer = Amber100,
    tertiary = Slate300,
    background = Ink900,
    onBackground = Slate50,
    surface = Ink800,
    onSurface = Slate50,
    surfaceVariant = Ink700,
    outline = Slate500
)

@Composable
fun HistoryCalendarTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
