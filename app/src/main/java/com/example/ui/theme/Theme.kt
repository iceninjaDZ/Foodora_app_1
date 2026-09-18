package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = FoodoraOrange,
    onPrimary = Color.White,
    primaryContainer = FoodoraOrangeContainer,
    onPrimaryContainer = FoodoraOrangeLight,
    secondary = FoodoraOrangeLight,
    onSecondary = Color.Black,
    background = CarbonBlack,
    onBackground = OffWhiteText,
    surface = DarkSurface,
    onSurface = OffWhiteText,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = GrayTextSecondary,
    outline = DarkBorder,
    outlineVariant = DarkBorderSubtle
)

private val LightColorScheme = lightColorScheme(
    primary = FoodoraOrange,
    onPrimary = Color.White,
    primaryContainer = FoodoraOrangeContainerLight,
    onPrimaryContainer = FoodoraOrangeDark,
    secondary = Color(0xFF2C2C35),
    onSecondary = Color.White,
    background = Color(0xFFF6F6F8),
    onBackground = Color(0xFF16161A),
    surface = Color.White,
    onSurface = Color(0xFF16161A),
    surfaceVariant = Color(0xFFEDEDF2),
    onSurfaceVariant = Color(0xFF5C5C68),
    outline = Color(0xFFDCDCE5),
    outlineVariant = Color(0xFFEBEBF0)
)

@Composable
fun FoodoraManagerTheme(
    darkTheme: Boolean = true, // Professional kitchen & POS default to sleek dark mode
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Keep backward compatibility for any existing test
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    FoodoraManagerTheme(darkTheme = darkTheme, content = content)
}
