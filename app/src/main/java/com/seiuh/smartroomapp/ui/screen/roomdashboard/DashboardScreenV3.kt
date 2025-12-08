package com.seiuh.smartroomapp.ui.screen.roomdashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.seiuh.smartroomapp.R
import com.seiuh.smartroomapp.ui.composable.*
import com.seiuh.smartroomapp.ui.theme.*
import kotlin.math.roundToInt
import androidx.compose.ui.res.painterResource

@Composable
fun DashboardScreenV3(
    navController: NavController,
    viewModel: RoomDashboardViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    // Navigation Effect
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is DashboardEvent.NavigateToLights -> navController.navigate("lights/${event.roomId}")
                is DashboardEvent.NavigateToPower -> navController.navigate("power/${event.roomId}")
                is DashboardEvent.NavigateToTemp -> navController.navigate("temperature/${event.roomId}")
                is DashboardEvent.Logout -> navController.navigate("login") { popUpTo(0) { inclusive = true } }
            }
        }
    }

    Scaffold(
        containerColor = RichBlack,
        topBar = {
            // Header Custom: Greetings + Add Device Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, bottom = 24.dp, start = 24.dp, end = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Logo Icon
                Image(
                    painter = painterResource(id = R.drawable.ic_app_logo_login_foregound),
                    contentDescription = "Logo",
                    modifier = Modifier.size(45.dp),
                    // Nếu logo là SVG đen, dùng dòng dưới để tô màu Neon. Nếu là PNG màu thì xóa dòng này.
                    colorFilter = ColorFilter.tint(LimeGreen)
                )

                // Nút Add Device màu vàng nổi bật
                Button(
                    onClick = { viewModel.onLogoutClicked() },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGray, contentColor = Danger, FireOrange),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    modifier = Modifier.height(44.dp)
                ) {
                    Icon(Icons.Default.Logout, null, modifier = Modifier.size(20.dp))
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // 1. ROOM SELECTOR (Tabs ngang)
            if (uiState.availableRooms.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.availableRooms) { room ->
                        RoomTab(
                            text = room.name,
                            isSelected = room.id == uiState.selectedRoom?.id,
                            onClick = { viewModel.onRoomSelected(room) }
                        )
                    }
                }
            } else {
                // Loading rooms state
                Box(Modifier.fillMaxWidth().height(50.dp), contentAlignment = Alignment.Center) {
                    Text("Loading rooms...", color = MutedGray)
                }
            }

            if (uiState.selectedRoom != null) {

                // 2. HERO CARD: AC / TEMPERATURE
                // Thiết kế giống hệt phần "Air Cooler" trong ảnh
                NeoCard(
                    onClick = { viewModel.onTempClicked() } // Nhấn vào để xem Chart Nhiệt độ
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                    ) {
                        // Header Card: Tên + Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Air Cooler", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = White)
                                Text("GTA AC00L4", style = MaterialTheme.typography.bodySmall, color = MutedGray)
                            }
                            NeoSwitch(checked = true, onCheckedChange = {})
                        }

                        Spacer(Modifier.height(24.dp))

                        // Body: Visualization (Gradient xanh) + Temp
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Cục hiển thị nhiệt độ (Gradient xanh dương - ElectricBlue)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp)
                                    .background(
                                        Brush.verticalGradient(listOf(Color(0xFF2C3E50), ElectricBlue)),
                                        RoundedCornerShape(40.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Room Temp", color = White.copy(alpha = 0.7f), fontSize = 12.sp)
                                    Text(
                                        "${uiState.currentTemp?.roundToInt() ?: "--"}°",
                                        color = White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 48.sp
                                    )
                                }
                            }

                            Spacer(Modifier.width(16.dp))

                            // Ảnh minh họa phòng (Dùng Box xám làm placeholder)
                            // Bạn có thể thay bằng Image(painterResource...)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp)
                                    .clip(RoundedCornerShape(32.dp))
                                    .background(RichBlack), // Nền đen bên trong card xám
                                contentAlignment = Alignment.Center
                            ) {
                                // Giả lập ảnh phòng mờ
                                Box(Modifier.fillMaxSize().background(Color.White.copy(0.1f)))
                                Text("Living\nRoom", color = MutedGray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        // Footer: Mode Buttons (Heat, Dry, Cool, Auto)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            ACModeButton(Icons.Outlined.WbSunny, "Heat", false, {}, Modifier.weight(1f))
                            Spacer(Modifier.width(8.dp))
                            ACModeButton(Icons.Outlined.WaterDrop, "Dry", false, {}, Modifier.weight(1f))
                            Spacer(Modifier.width(8.dp))
                            ACModeButton(Icons.Outlined.AcUnit, "Cool", true, {}, Modifier.weight(1f)) // Selected
                            Spacer(Modifier.width(8.dp))
                            ACModeButton(Icons.Filled.Autorenew, "Auto", false, {}, Modifier.weight(1f))
                        }
                    }
                }

                // 3. DEVICE LIST (Các thiết bị khác xếp dọc)

                // Smart Light
                DeviceCardHorizontal(
                    title = "Smart Light",
                    subtitle = if (uiState.activeLightsCount > 0) "${uiState.activeLightsCount} lights active" else "All lights off",
                    icon = Icons.Default.Lightbulb,
                    isOn = uiState.activeLightsCount > 0,
                    onClick = { viewModel.onLightsClicked() }, // Vào xem Chart/Control Đèn
                    onToggle = { /* Logic bật tắt nhanh */ }
                )

                // (Đại diện cho Công suất/Năng lượng)
                DeviceCardHorizontal(
                    title = "Energy Monitor",
                    subtitle = "${uiState.currentPower?.toInt() ?: 0} kWh usage",
                    icon = Icons.Default.FlashOn,
                    isOn = true,
                    onClick = { viewModel.onPowerClicked() }, // Vào xem Chart Năng lượng
                    onToggle = { }
                )

                //  Temperature card
                DeviceCardHorizontal(
                    title = "Temperature",
                    subtitle = "${uiState.currentTemp?.toInt() ?: 0} kWh usage",
                    icon = Icons.Default.DeviceThermostat,
                    isOn = true,
                    onClick = { viewModel.onTempClicked() }, // Vào xem Chart Năng lượng
                    onToggle = { }
                )
                // Add more devices logic here if needed...

            } else {
                // Empty state if no room selected
                Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text("Select a room to controls", color = MutedGray)
                }
            }

            // Padding đáy để scroll không bị che
            Spacer(Modifier.height(80.dp))
        }
    }
}