package com.seiuh.smartroomapp.ui.screen.light

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.seiuh.smartroomapp.data.model.device.Light
import com.seiuh.smartroomapp.ui.SmartViewModelFactory
import com.seiuh.smartroomapp.ui.composable.*
import com.seiuh.smartroomapp.ui.theme.*

@Composable
fun LightsScreenV2(
    navController: NavController,
    roomId: Long
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val repository = androidx.compose.runtime.remember { com.seiuh.smartroomapp.data.repository.SmartHomeRepository() }
    val viewModel: LightsViewModel = viewModel(factory = SmartViewModelFactory(repository, roomId=roomId))
    val uiState by viewModel.uiState.collectAsState()

    SmartRoomScaffoldV2(
        title = uiState.roomName,
        onBackClick = { navController.popBackStack() }
    ) {
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NeonYellowV2)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // 1. Master Switch (Nút lớn điều khiển tất cả)
                item {
                    MasterLightSwitchV2(
                        lights = uiState.lights,
                        onToggleAll = viewModel::onToggleAll
                    )
                }

                item {
                    Text(
                        "Danh sách thiết bị",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextGrayV2,
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                    )
                }

                // 2. Danh sách đèn
                items(uiState.lights) { light ->
                    LightItemRowV2(
                        light = light,
                        onToggle = viewModel::onLightToggle
                    )
                }
            }
        }
    }
}

@Composable
fun MasterLightSwitchV2(
    lights: List<Light>,
    onToggleAll: (Boolean) -> Unit
) {
    val allOn = lights.isNotEmpty() && lights.all { it.isActive }

    // Nút Master có nền Gradient rực rỡ nếu đang Bật
    val backgroundBrush = if (allOn) GreenYellowGradientV2 else Brush.linearGradient(listOf(GlassSurfaceV2, GlassSurfaceV2))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundBrush)
            .clickable { onToggleAll(!allOn) }
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Tất cả đèn",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextWhiteV2
                )
                Text(
                    text = if (allOn) "ĐANG SÁNG" else "ĐÃ TẮT",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (allOn) TextWhiteV2.copy(alpha = 0.8f) else TextGrayV2
                )
            }

            // Icon nguồn lớn
            Icon(
                imageVector = Icons.Default.PowerSettingsNew,
                contentDescription = null,
                tint = TextWhiteV2,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
fun LightItemRowV2(
    light: Light,
    onToggle: (Long, Boolean) -> Unit
) {
    val activeGradient = if (light.isActive) GreenYellowGradientV2 else Brush.linearGradient(listOf(Color.Gray, Color.Gray))

    GlassCard(
        onClick = { onToggle(light.id, !light.isActive) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Box
            GradientIconBox(
                icon = Icons.Default.Lightbulb,
                gradient = activeGradient
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = light.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextWhiteV2
                )
                // Thanh độ sáng giả lập (Progress Bar nhỏ)
                if (light.isActive) {
                    Spacer(Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { light.level / 100f },
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = NeonYellowV2,
                        trackColor = Color.White.copy(alpha = 0.2f),
                    )
                } else {
                    Text("Đã tắt", style = MaterialTheme.typography.bodySmall, color = TextGrayV2)
                }
            }

            Switch(
                checked = light.isActive,
                onCheckedChange = { onToggle(light.id, it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = NeonYellowV2,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = DarkBackgroundV2
                )
            )
        }
    }
}