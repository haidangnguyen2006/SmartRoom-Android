package com.seiuh.smartroomapp.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seiuh.smartroomapp.ui.theme.*

/**
 * 1. SmartRoomScaffoldV2
 * Khung màn hình chính với nền Gradient tối.
 * Hỗ trợ Title, Nút Back và Actions (bên phải).
 */
@Composable
fun SmartRoomScaffoldV2(
    title: String? = null,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppDarkGradientV2) // Sử dụng nền Gradient tối V2
    ) {
        Column(Modifier.fillMaxSize()) {
            // --- Custom Top App Bar ---
            if (title != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp, start = 20.dp, end = 20.dp, bottom = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Nút Back
                    if (onBackClick != null) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .background(GlassSurfaceV2, CircleShape) // Nền nút bán trong suốt
                                .size(40.dp)
                                .border(1.dp, GlassBorderGradientV2, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = TextWhiteV2
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                    }

                    // Tiêu đề
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextWhiteV2
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    // Các nút hành động (ví dụ Logout)
                    actions()
                }
            }

            // --- Nội dung chính ---
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                content = content
            )
        }
    }
}

/**
 * 2. GlassCard
 * Thẻ chứa nội dung với hiệu ứng kính mờ (Glassmorphism).
 * Có viền gradient nhẹ để tạo điểm nhấn.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    // Mặc định viền trắng mờ, có thể đổi thành màu Neon để highlight
    borderBrush: Brush = GlassBorderGradientV2,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(24.dp)

    // Xử lý Clickable
    val cardModifier = if (onClick != null) {
        modifier
            .clip(shape)
            .clickable(onClick = onClick)
    } else {
        modifier.clip(shape)
    }

    Box(
        modifier = cardModifier
            .background(GlassSurfaceV2) // Màu nền bán trong suốt
            .border(1.dp, borderBrush, shape) // Viền gradient
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}

/**
 * 3. GradientIconBox
 * Hộp chứa Icon với nền Gradient rực rỡ.
 * Dùng để làm nổi bật các thiết bị hoặc chức năng.
 */
@Composable
fun GradientIconBox(
    icon: ImageVector,
    gradient: Brush, // Truyền vào BlueCyan, OrangeRed...
    modifier: Modifier = Modifier,
    iconColor: Color = Color.White
) {
    Box(
        modifier = modifier
            .size(52.dp)
            .background(gradient, RoundedCornerShape(16.dp))
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp)), // Viền nhẹ
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(28.dp)
        )
    }
}