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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seiuh.smartroomapp.ui.theme.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.sp


// 1. Khung màn hình chuẩn (Nền đen tuyệt đối)
@Composable
fun CyberScaffold(
    title: String? = null,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        containerColor = RichBlack,
        topBar = {
            if (title != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp, start = 24.dp, end = 24.dp, bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (onBackClick != null) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .background(DarkGray, CircleShape)
                                .size(44.dp)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = White)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.weight(1f)
                    )
                    actions()
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            content()
        }
    }
}

// 2. Card chuẩn phong cách mới (Màu DarkGray, bo góc 24-32dp)
@Composable
fun NeoCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = DarkGray,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(28.dp)
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(0.dp), // Thiết kế phẳng, không bóng đổ
        onClick = onClick ?: {}
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            content()
        }
    }
}

// 3. Nút chính màu Vàng Chanh (CTA Button)
@Composable
fun LimeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = LimeGreen,
            contentColor = RichBlack,
            disabledContainerColor = LimeGreen.copy(alpha = 0.5f)
        ),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = RichBlack, modifier = Modifier.size(24.dp))
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

// 4. Toggle Switch Custom (Màu xanh lá khi bật)
@Composable
fun NeoSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = RichBlack,
            checkedTrackColor = LimeGreen,
            uncheckedThumbColor = LightGray,
            uncheckedTrackColor = Charcoal,
            uncheckedBorderColor = Color.Transparent
        ),
        modifier = Modifier.scale(0.8f) // Làm nhỏ lại chút cho tinh tế
    )
}
@Composable
fun RoomTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Nếu chọn: Viền vàng, Text vàng, Nền vàng mờ
    // Nếu không: Không viền, Text xám, Nền Surface
    val borderColor = if (isSelected) LimeGreen else Color.Transparent
    val textColor = if (isSelected) LimeGreen else MutedGray
    val bgColor = if (isSelected) LimeGreen.copy(alpha = 0.1f) else DarkGray

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

// 2. Device Card Horizontal (Thẻ thiết bị nằm ngang - Giống Smart Light/TV trong ảnh)
@Composable
fun DeviceCardHorizontal(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isOn: Boolean,
    onClick: () -> Unit, // Click để vào chi tiết (Chart)
    onToggle: (Boolean) -> Unit, // Click switch để bật tắt nhanh
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(88.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkGray),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Icon Box
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(if (isOn) LimeGreen.copy(alpha = 0.2f) else RichBlack, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isOn) LimeGreen else MutedGray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Info
                Column {
                    Text(title, style = MaterialTheme.typography.titleMedium, color = White)
                    Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MutedGray, fontSize = 12.sp)
                }
            }

            // Switch
            NeoSwitch(checked = isOn, onCheckedChange = onToggle)
        }
    }
}

// 3. Nút chế độ AC (AC Mode Button) - Hình vuông bo góc nằm trong thẻ AC
@Composable
fun ACModeButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isSelected) RichBlack else Color.Transparent
    val contentColor = if (isSelected) LimeGreen else MutedGray
    val borderColor = if (isSelected) LimeGreen else Color.Transparent

    Column(
        modifier = modifier
            .height(70.dp) // Chiều cao cố định
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(24.dp))
        Spacer(Modifier.height(4.dp))
        Text(label, color = contentColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
