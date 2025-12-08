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

private val ModernDarkSchemeV2 = darkColorScheme(
    primary = NeonBlueV2,
    onPrimary = Color.White,
    background = DarkBackgroundV2,
    surface = DarkSurfaceV2,
    onSurface = TextWhiteV2,
    secondary = NeonOrangeV2
)

@Composable
fun SmartRoomThemeV2(
    content: @Composable () -> Unit
) {
    val colorScheme = ModernDarkSchemeV2
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Đặt status bar trong suốt để nền Gradient tràn lên trên
            window.statusBarColor = Color.Transparent.toArgb()
            // Icon trên status bar màu trắng (vì nền tối)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Sử dụng lại Typography cũ (hoặc cập nhật nếu cần)
        content = content
    )
}