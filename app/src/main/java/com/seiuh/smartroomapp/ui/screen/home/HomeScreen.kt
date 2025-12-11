package com.seiuh.smartroomapp.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.seiuh.smartroomapp.R
import com.seiuh.smartroomapp.data.model.structure.Room
import com.seiuh.smartroomapp.ui.composable.HeroWeatherCard
import com.seiuh.smartroomapp.ui.composable.RoomCard
import com.seiuh.smartroomapp.ui.composable.SegmentedControl
import com.seiuh.smartroomapp.ui.composable.SegmentedControlV2
import com.seiuh.smartroomapp.ui.composable.SmartModeCard
import com.seiuh.smartroomapp.ui.theme.*

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel,
    bottomBarPadding: PaddingValues
) {
    val uiState by viewModel.uiState.collectAsState()

    // Temp state for Smart Modes
    var activeMode by remember { mutableStateOf("home") }

    // Group rooms by Floor ID for display
    val roomsByFloor = remember(uiState.rooms) {
        uiState.rooms.groupBy { it.floorId }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        // TOP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 8.dp, bottom = 8.dp, start = 24.dp, end = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "My Home",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Image(
                painter = painterResource(id = R.drawable.ic_app_logo_login_foregound),
                contentDescription = "Logo",
                modifier = Modifier.size(40.dp),
                colorFilter = ColorFilter.tint(LimeGreen)
            )
        }

        // SCROLLABLE CONTENT
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(
                bottom = bottomBarPadding.calculateBottomPadding() + 24.dp // Cộng thêm chút khoảng trống
            )
        ) {
            // 1. HERO CARD
            item {
                HeroWeatherCard(
                    location = "Montreal",
                    weather = "Partly Cloudy",
                    temp = "-10°"
                )
            }

            // 2. SMART MODES
            item {
                Column {
                    Text(
                        text = "Smart Modes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SmartModeCard(
                            title = "At Home",
                            subtitle = "All Active",
                            icon = Icons.Default.Home,
                            isActive = activeMode == "home",
                            onClick = { activeMode = "home" },
                            modifier = Modifier.weight(1f)
                        )

                        SmartModeCard(
                            title = "Left Home",
                            subtitle = "Security On",
                            icon = Icons.Default.ExitToApp,
                            isActive = activeMode == "away",
                            onClick = { activeMode = "away" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 3. SEGMENTED CONTROL
            item {
                SegmentedControlV2(
                    items = listOf("Room", "Devices"),
                    selectedIndex = uiState.selectedTab,
                    onItemSelected = viewModel::onTabSelected
                )
            }

            // 4. ROOM LIST
            if (uiState.isLoading && uiState.floors.isEmpty()) {
                item {
                    Box(
                        Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(8.dp))
                            Text("Đang tải dữ liệu...", color = TextSecondary)
                        }
                    }
                }
            } else if (uiState.errorMessage != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "❌ Lỗi",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                uiState.errorMessage ?: "Unknown error",
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = { viewModel.retry() }) {
                                Text("Thử lại")
                            }
                        }
                    }
                }
            } else if (uiState.selectedTab == 0) { // Tab "Room"
                // Debug info
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = SurfaceLight
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                "📊 Debug Info:",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextSecondary
                            )
                            Text(
                                "• ${uiState.floors.size} tầng",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                            Text(
                                "• ${uiState.rooms.size} phòng",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                            if (uiState.isLoading) {
                                Text(
                                    "• Đang tải...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LimeGreen
                                )
                            }
                        }
                    }
                }

                if (uiState.floors.isEmpty()) {
                    item {
                        Box(
                            Modifier.fillMaxWidth().padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Không có tầng nào.", color = TextSecondary)
                        }
                    }
                } else {
                    uiState.floors.forEach { floor ->
                        val roomsInFloor = uiState.rooms.filter { it.floorId == floor.id }

                        item {
                            FloorSection(
                                floorName = "${floor.name} • ${roomsInFloor.size} phòng",
                                rooms = roomsInFloor,
                                onRoomClick = { roomId ->
                                    navController.navigate("room/$roomId")
                                }
                            )
                        }

                        if (roomsInFloor.isEmpty() && !uiState.isLoading) {
                            item {
                                Text(
                                    "Không có phòng nào trong ${floor.name}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                // Tab "Devices"
                item {
                    Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        Text("Devices view coming soon...", color = TextSecondary)
                    }
                }
            }
        }
    }
}

// Component hiển thị tiêu đề tầng và danh sách phòng ngang
@Composable
fun FloorSection(
    floorName: String,
    rooms: List<Room>,
    onRoomClick: (Long) -> Unit
) {
    Column {
        Text(
            text = floorName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 8.dp, end = 16.dp)
        ) {
            items(rooms) { room ->
                val safeName = room.name ?: ""

                val randomImage = when {
                    safeName.contains("Living", ignoreCase = true) || room.name.contains("Khách", ignoreCase = true)
                        -> "https://images.unsplash.com/photo-1598928506311-c55ded91a20c?q=80&w=870&auto=format&fit=crop" +
                            "&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"

                    safeName.contains("Bed", ignoreCase = true) || room.name.contains("Ngủ", ignoreCase = true)
                        -> "https://images.unsplash.com/photo-1616594039964-ae9021a400a0?q=80&w=580&auto=format&fit=crop" +
                            "&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"

                    safeName.contains("Kitchen", ignoreCase = true) || room.name.contains("Bếp", ignoreCase = true)
                        -> "https://images.unsplash.com/photo-1556910096-6f5e72db6803?q=80&w=870&auto=format&fit=crop" +
                            "&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"

                    else -> "https://images.unsplash.com/photo-1628012209120-d9db7abf7eab?q=80&w=436&auto=format&fit=crop" +
                            "&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D" // Ảnh mặc định
                }
                // -----------------------------

                RoomCard(
                    roomName = safeName.ifEmpty { "Unknown Room" },
                    deviceCount = 4, // Số liệu giả lập
                    isOn = true,     // Trạng thái giả lập
                    imageUrl = randomImage, // [Truyền ảnh vào đây]
                    onClick = { onRoomClick(room.id) },
                    onToggle = { /* Xử lý tắt/bật nhanh */ }
                )
            }
        }
    }
}