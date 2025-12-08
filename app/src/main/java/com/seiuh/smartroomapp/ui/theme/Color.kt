package com.seiuh.smartroomapp.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Primary Brand Colors
val BrandBlue = Color(0xFF4361EE)
val BrandLightBlue = Color(0xFF4CC9F0)
val BrandDarkBlue = Color(0xFF3F37C9)
val BrandPurple = Color(0xFF7209B7)

// Accent Colors
val AccentOrange = Color(0xFFFB8500) // Cho nhiệt độ/năng lượng
val AccentYellow = Color(0xFFFFB703) // Cho đèn
val AccentGreen = Color(0xFF06D6A0) // Cho trạng thái tốt
val AccentRed = Color(0xFFEF476F)   // Cho cảnh báo

// Background & Surface
val BackgroundLight = Color(0xFFF8F9FA)
val SurfaceWhiteV2 = Color(0xFFFFFFFF)
val TextPrimaryV1 = Color(0xFF2B2D42)
val TextSecondaryV2 = Color(0xFF8D99AE)

// Gradient Background cho toàn màn hình (Rất đẹp!)
val AppBackgroundGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFF0F4FF), // Xanh nhạt pha trắng
        Color(0xFFFFFFFF)  // Trắng
    )
)

// Gradient cho Card Header
val CardGradient = Brush.linearGradient(
    colors = listOf(BrandBlue, BrandLightBlue)
)