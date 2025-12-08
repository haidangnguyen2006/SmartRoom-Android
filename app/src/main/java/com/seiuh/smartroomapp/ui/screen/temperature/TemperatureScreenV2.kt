package com.seiuh.smartroomapp.ui.screen.temperature

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.seiuh.smartroomapp.data.model.device.TempSensor
import com.seiuh.smartroomapp.ui.SmartViewModelFactory
import com.seiuh.smartroomapp.ui.composable.*
import com.seiuh.smartroomapp.ui.theme.*

// Helper lấy màu chart (để ở đây cho tiện)
fun getChartColorV2(index: Int): Color {
    val colors = listOf(
        Color(0xFFE63946), Color(0xFF457B9D), Color(0xFF52B69A),
        Color(0xFFFCA311), Color(0xFF9D4EDD), Color(0xFFF4A261)
    )
    return colors[index % colors.size]
}

@Composable
fun TemperatureScreenV2(
    navController: NavController,
    roomId: Long
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val repository = androidx.compose.runtime.remember { com.seiuh.smartroomapp.data.repository.SmartHomeRepository() }
    val viewModel: TemperatureViewModel = viewModel(factory = SmartViewModelFactory(repository, roomId = roomId))
    val uiState by viewModel.uiState.collectAsState()

    SmartRoomScaffoldV2(
        title = uiState.roomName,
        onBackClick = { navController.popBackStack() }
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {

            // 1. Selector Ngày
            GlassCard {
                DateRangeSelector(
                    startDate = uiState.startDate,
                    endDate = uiState.endDate,
                    onStartDateChange = viewModel::onStartDateChange,
                    onEndDateChange = viewModel::onEndDateChange,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 2. Chart
            GlassCard(borderBrush = OrangeRedGradientV2) {
                ReusableMultiLineChart(
                    title = "Biểu đồ nhiệt độ",
                    chartModel = uiState.chartModel,
                    isLoading = uiState.isChartLoading,
                    bottomAxisLabels = uiState.chartBottomLabels,
                    xStep = uiState.chartXStep,
                    modifier = Modifier.fillMaxWidth().height(250.dp)
                )
            }

            // 3. List
            Text("Cảm biến", style = MaterialTheme.typography.titleMedium, color = TextGrayV2)

            if (uiState.isLoading) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = NeonOrangeV2) }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    itemsIndexed(uiState.sensors) { index, sensor ->
                        TempItemRowV2(
                            sensor = sensor,
                            colorIndex = index,
                            isSelected = sensor.id in uiState.selectedSensorIds,
                            onSelectionChanged = viewModel::onSensorSelected
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TempItemRowV2(
    sensor: TempSensor,
    colorIndex: Int,
    isSelected: Boolean,
    onSelectionChanged: (Long, Boolean) -> Unit
) {
    val sensorColor = getChartColorV2(colorIndex)
    // Gradient đơn sắc dựa trên màu chart
    val itemGradient = Brush.linearGradient(listOf(sensorColor, sensorColor.copy(alpha = 0.5f)))

    GlassCard(onClick = { onSelectionChanged(sensor.id, !isSelected) }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GradientIconBox(icon = Icons.Default.Thermostat, gradient = itemGradient)
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(sensor.name, style = MaterialTheme.typography.bodyMedium, color = TextGrayV2)
                Text(
                    "${sensor.currentValue ?: "--"}°C",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextWhiteV2
                )
            }
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onSelectionChanged(sensor.id, it) },
                colors = CheckboxDefaults.colors(
                    checkedColor = sensorColor,
                    checkmarkColor = TextWhiteV2,
                    uncheckedColor = TextGrayV2
                )
            )
        }
    }
}