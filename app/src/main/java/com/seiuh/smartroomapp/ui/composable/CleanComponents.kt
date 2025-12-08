package com.seiuh.smartroomapp.ui.composable

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seiuh.smartroomapp.ui.theme.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.seiuh.smartroomapp.R
import androidx.compose.ui.unit.IntSize

// 1. GRADIENT HERO CARD (Weather/Location)
@Composable
fun HeroWeatherCard(
    location: String,
    weather: String,
    temp: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .shadow(10.dp, RoundedCornerShape(24.dp), spotColor = PrimaryPurple.copy(0.3f))
            .background(
                brush = Brush.horizontalGradient(PurplePinkGradient),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(24.dp)
    ) {
        Column(modifier = Modifier.align(Alignment.TopStart)) {
            Text("My Location", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(location, color = Color.White.copy(0.8f), fontSize = 14.sp)
            Spacer(Modifier.height(32.dp))
            Text(weather, color = Color.White.copy(0.9f), fontSize = 14.sp)
        }

        Column(modifier = Modifier.align(Alignment.TopEnd), horizontalAlignment = Alignment.End) {
            Text(temp, color = Color.White, fontWeight = FontWeight.Light, fontSize = 48.sp)
            Text("H:2° L:12°", color = Color.White.copy(0.8f), fontSize = 14.sp)
        }
    }
}

// 2. ROOM CARD (The grid items)
@Composable
fun RoomCard(
    roomName: String,
    deviceCount: Int,
    isOn: Boolean,
    imageUrl: Any?, // Nhận URL (String) hoặc Drawable (Int)
    onClick: () -> Unit,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(180.dp) // Tăng độ rộng chút để hình đẹp hơn
            .height(200.dp)
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(20.dp), spotColor = Color(0x1A000000))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. PHẦN HÌNH ẢNH (Chiếm nửa trên)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(SurfaceLight) // Màu nền khi ảnh chưa load
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop, // Cắt ảnh để lấp đầy
                    modifier = Modifier.fillMaxSize(),
                    // Placeholder nếu ảnh lỗi hoặc đang load (Bạn cần tạo ảnh placeholder trong res/drawable)
                    error = painterResource(id = R.drawable.ic_launcher_background),
                    placeholder = painterResource(id = R.drawable.ic_launcher_background)
                )

                // Overlay trạng thái (Optional: Hiển thị chấm xanh nếu phòng đang có thiết bị bật)
                if (isOn) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Success)
                            .align(Alignment.TopEnd)
                    )
                }
            }

            // 2. PHẦN THÔNG TIN & TOGGLE (Nửa dưới)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = roomName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "$deviceCount Devices",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isOn) "ON" else "OFF",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (isOn) PrimaryPurple else TextSecondary
                    )

                    Switch(
                        checked = isOn,
                        onCheckedChange = onToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PrimaryPurple,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = SurfaceLight,
                            uncheckedBorderColor = Color.Transparent
                        ),
                        modifier = Modifier.scale(0.7f) // Thu nhỏ switch cho cân đối
                    )
                }
            }
        }
    }
}

// 3. SEGMENTED CONTROL (Room | Devices)
@Composable
fun SegmentedControl(
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(SurfaceWhite, RoundedCornerShape(16.dp))
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, text ->
            val isSelected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) SurfaceWhite else Color.Transparent)
                    .then(if (isSelected) Modifier.shadow(4.dp, RoundedCornerShape(12.dp)) else Modifier)
                    .clickable { onItemSelected(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) TextPrimary else TextSecondary
                )
            }
        }
    }
}
@Composable
fun SegmentedControlV2(
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedIndexState by remember { mutableStateOf(selectedIndex) }
    var containerWidth by remember { mutableStateOf(0) }

    // Update state when prop changes
    LaunchedEffect(selectedIndex) {
        selectedIndexState = selectedIndex
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(
                color = SurfaceLight,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(4.dp)
            .onSizeChanged { containerWidth = it.width }
    ) {
        // Animated Background Indicator
        val animatedOffset by animateFloatAsState(
            targetValue = selectedIndexState.toFloat(),
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "segment_animation"
        )

        // Background indicator
        if (containerWidth > 0) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(1f / items.size)
                        .offset {
                            IntOffset(
                                x = (animatedOffset * (containerWidth / items.size)).toInt(),
                                y = 0
                            )
                        }
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(12.dp),
                            clip = false
                        )
                )
            }
        }

        // Segments
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, text ->
                val isSelected = index == selectedIndexState

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            selectedIndexState = index
                            onItemSelected(index)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Text with animation
                    val textColor by animateColorAsState(
                        targetValue = if (isSelected) TextPrimary else TextSecondary,
                        animationSpec = tween(300),
                        label = "text_color"
                    )

                    val textScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.05f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "text_scale"
                    )

                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = textColor,
                        modifier = Modifier.graphicsLayer {
                            scaleX = textScale
                            scaleY = textScale
                        }
                    )
                }
            }
        }
    }
}
@Composable
fun CleanCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null, // Hỗ trợ click (tùy chọn)
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            // Đổ bóng nhẹ (shadow) thay vì elevation mặc định để trông hiện đại hơn
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color(0x1A000000) // Màu bóng đen nhạt (10% opacity)
            )
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(0.dp) // Tắt elevation mặc định để dùng custom shadow
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}
@Composable
fun SmartModeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Màu sắc dựa trên trạng thái Active
    val containerColor = if (isActive) PrimaryPurple else SurfaceWhite
    val contentColor = if (isActive) Color.White else TextPrimary
    val iconBgColor = if (isActive) Color.White.copy(alpha = 0.2f) else SurfaceLight
    val iconColor = if (isActive) Color.White else PrimaryPurple

    Card(
        modifier = modifier
            .height(80.dp) // Chiều cao cố định cho đều
            .clickable(onClick = onClick)
            .shadow(
                elevation = if (isActive) 8.dp else 4.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = if (isActive) PrimaryPurple.copy(0.4f) else Color(0x1A000000)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // Icon tròn
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text Info
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = contentColor
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isActive) Color.White.copy(alpha = 0.8f) else TextSecondary
                )
            }
        }
    }
}
// Helper

fun Modifier.scale(scale: Float) = this.then(Modifier.graphicsLayer(scaleX = scale, scaleY = scale))