package com.seiuh.smartroomapp.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CyberpunkScheme = darkColorScheme(
    primary = LimeGreen,        // Màu chính là Vàng Chanh
    onPrimary = RichBlack,      // Chữ trên nền Vàng Chanh phải là màu Đen
    background = RichBlack,
    surface = DarkGray,
    onSurface = White,
    secondary = ElectricBlue,
    error = Danger
)

@Composable
fun SmartRoomThemeV3(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = RichBlack.toArgb() // Status bar màu đen
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false // Icon trắng
        }
    }

    MaterialTheme(
        colorScheme = CyberpunkScheme,
        typography = AppTypography,
        content = content
    )
}