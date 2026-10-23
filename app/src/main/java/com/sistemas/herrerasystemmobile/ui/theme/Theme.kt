package com.sistemas.herrerasystemmobile.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Esquema Claro con la Identidad de Sorbetería Herrera
private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreenButton,
    onPrimary = SurfaceWhite,
    secondary = CoralAccent,
    onSecondary = SurfaceWhite,
    background = ScreenBackground,
    onBackground = TextDark,
    surface = CardBackground,
    onSurface = TextDark
)

// Esquema Oscuro
private val DarkColorScheme = darkColorScheme(
    primary = HeaderGradientStart,
    onPrimary = SurfaceWhite,
    secondary = CoralAccent,
    onSecondary = SurfaceWhite,
    background = TextDark,
    onBackground = ScreenBackground,
    surface = Color(0xFF1E293B),
    onSurface = ScreenBackground
)

@Composable
fun HerreraSystemMobileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = HeaderGradientStart.toArgb() // Barra superior en verde menta
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}