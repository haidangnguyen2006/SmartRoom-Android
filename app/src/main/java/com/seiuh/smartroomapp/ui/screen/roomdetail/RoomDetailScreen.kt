package com.seiuh.smartroomapp.ui.screen.roomdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.seiuh.smartroomapp.ui.composable.Circular3DGauge
import com.seiuh.smartroomapp.ui.composable.CleanCard
import com.seiuh.smartroomapp.ui.composable.DeviceControlCard
import com.seiuh.smartroomapp.ui.theme.*
import com.seiuh.smartroomapp.data.utils.getRoomImageUrl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomDetailScreen(
    navController: NavController,
    viewModel: RoomDetailViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(uiState.roomName, fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = AppBackground)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 1. ẢNH HEADER (1/5 Màn hình)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.2f) // Chiếm 20% chiều cao
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(getRoomImageUrl(uiState.roomName))
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                )
            }

            // 2. NỘI DUNG CHÍNH (Cuộn được)
            LazyColumn(
                modifier = Modifier
                    .weight(0.8f) // Chiếm 80% còn lại
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = 40.dp)
            ) {
                // A. HERO GAUGE (Nhiệt độ 3D)
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Circular3DGauge(
                            value = uiState.currentTemp,
                            size = 220.dp
                        )
                    }
                }

                // B. NAVIGATION CARDS (Nhiệt độ & Công suất) - Loại Card 2
                item {
                    Text("Monitoring", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Card Nhiệt độ
                        NavDetailCard(
                            title = "Climate",
                            icon = Icons.Default.Thermostat,
                            color = AccentPink,
                            onClick = { navController.navigate("room/${viewModel.roomId}/chart/temp") }, // Navigate
                            modifier = Modifier.weight(1f)
                        )
                        // Card Công suất
                        NavDetailCard(
                            title = "Power",
                            icon = Icons.Default.FlashOn,
                            color = PrimaryPurple,
                            onClick = { navController.navigate("room/${viewModel.roomId}/chart/power") }, // Navigate
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // C. DEVICE LIST (Thiết bị điều khiển) - Loại Card 1
                item {
                    Text("Devices", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                if (uiState.lights.isEmpty()) {
                    item { Text("No devices found", color = TextSecondary) }
                } else {
                    items(uiState.lights) { light ->
                        DeviceControlCard(
                            name = light.name,
                            status = if (light.isActive) "${light.level}%" else "Off",
                            icon = Icons.Default.Lightbulb,
                            isOn = light.isActive,
                            level = light.level, // Hiện slider
                            onToggle = { viewModel.toggleLight(light.id) },
                            onLevelChange = { viewModel.setLightLevel(light.id, it.toInt()) }
                        )
                    }
                }
            }
        }
    }
}

// Helper UI: Card điều hướng nhỏ
@Composable
fun NavDetailCard(
    title: String,
    icon: ImageVector,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CleanCard(
        onClick = onClick,
        modifier = modifier.height(100.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}