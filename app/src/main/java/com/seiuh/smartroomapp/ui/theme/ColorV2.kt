package com.seiuh.smartroomapp.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// --- NỀN TỐI & SURFACE (GLASS EFFECT) ---
val DarkBackgroundV2 = Color(0xFF121212) // Đen than (Background chính)
val DarkSurfaceV2 = Color(0xFF1E1E2C)    // Xanh đen đậm
val GlassSurfaceV2 = Color(0xFF2C2C3E).copy(alpha = 0.6f) // Màu kính bán trong suốt (60%)

// --- TEXT ---
val TextWhiteV2 = Color(0xFFFFFFFF)
val TextGrayV2 = Color(0xFFAAAAAA)

// --- NEON ACCENTS (Màu đơn) ---
val NeonBlueV2 = Color(0xFF00C6FF)
val NeonOrangeV2 = Color(0xFFFF512F)
val NeonYellowV2 = Color(0xFFFFD700)

// --- GRADIENT ACCENTS (Dùng cho Icon/Button) ---

// 1. Blue Cyan (Dùng cho Công suất / Nút chính)
val BlueCyanGradientV2 = Brush.linearGradient(
    colors = listOf(Color(0xFF00C6FF), Color(0xFF0072FF))
)

// 2. Orange Red (Dùng cho Nhiệt độ / Cảnh báo)
val OrangeRedGradientV2 = Brush.linearGradient(
    colors = listOf(Color(0xFFFF512F), Color(0xFFDD2476))
)

// 3. Green Yellow (Dùng cho Đèn / Trạng thái tốt)
val GreenYellowGradientV2 = Brush.linearGradient(
    colors = listOf(Color(0xFFF09819), Color(0xFFEDDE5D))
)

// 4. Background Gradient (Nền toàn app - Hiệu ứng chiều sâu)
val AppDarkGradientV2 = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF1F1F2E), // Trên cùng sáng hơn một chút
        Color(0xFF121212)  // Dưới cùng đen hoàn toàn
    )
)

// 5. Border Gradient (Viền nhẹ cho các thẻ kính)
val GlassBorderGradientV2 = Brush.linearGradient(
    colors = listOf(
        Color.White.copy(alpha = 0.15f),
        Color.White.copy(alpha = 0.05f)
    )
)