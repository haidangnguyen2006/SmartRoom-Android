package com.seiuh.smartroomapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CleanScheme = lightColorScheme(
    primary = PrimaryPurple,
    onPrimary = SurfaceLight,
    background = AppBackground,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    secondary = AccentPink,
    tertiary = PrimaryDark
)

@Composable
fun SmartRoomThemeV4(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = AppBackground.toArgb()
            // Icons on status bar should be dark
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = CleanScheme,
        typography = Typography, // Default Material3 typography is fine for now
        content = content
    )
}