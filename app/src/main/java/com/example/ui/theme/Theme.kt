package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = EmergencyRed,
    onPrimary = Color.White,
    primaryContainer = EmergencyRedDark,
    onPrimaryContainer = Color.White,
    secondary = NavyAccent,
    onSecondary = Color.White,
    secondaryContainer = NavyDarkSurfaceVariant,
    onSecondaryContainer = TextWhite,
    background = NavyDarkBackground,
    onBackground = TextWhite,
    surface = NavyDarkSurface,
    onSurface = TextWhite,
    surfaceVariant = NavyDarkSurfaceVariant,
    onSurfaceVariant = TextMutedDark,
    error = StatusError,
    onError = Color.White,
    outline = NavyBorder
)

private val LightColorScheme = lightColorScheme(
    primary = EmergencyRed,
    onPrimary = Color.White,
    primaryContainer = EmergencyRedContainer,
    onPrimaryContainer = EmergencyRedText,
    secondary = NavyPrimary,
    onSecondary = Color.White,
    secondaryContainer = NavyLightSurfaceVariant,
    onSecondaryContainer = NavyPrimary,
    background = NavyLightBackground,
    onBackground = TextDarkNavy,
    surface = NavyLightSurface,
    onSurface = TextDarkNavy,
    surfaceVariant = NavyLightSurfaceVariant,
    onSurfaceVariant = TextMutedLight,
    error = StatusError,
    onError = Color.White,
    outline = NavyLightOutline
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep branded Navy/Red safety colors consistent
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
