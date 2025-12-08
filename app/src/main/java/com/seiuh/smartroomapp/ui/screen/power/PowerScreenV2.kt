package com.seiuh.smartroomapp.ui.screen.power

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.seiuh.smartroomapp.data.model.device.PowerSensor
import com.seiuh.smartroomapp.ui.SmartViewModelFactory
import com.seiuh.smartroomapp.ui.composable.*
import com.seiuh.smartroomapp.ui.screen.temperature.getChartColorV2 // Dùng lại hàm helper
import com.seiuh.smartroomapp.ui.theme.*

@Composable
fun PowerScreenV2(
    navController: NavController,
    roomId: Long
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val repository = androidx.compose.runtime.remember { com.seiuh.smartroomapp.data.repository.SmartHomeRepository() }
    val viewModel: PowerViewModel = viewModel(factory = SmartViewModelFactory(repository, roomId = roomId))
    val uiState by viewModel.uiState.collectAsState()

    SmartRoomScaffoldV2(
        title = uiState.roomName,
        onBackClick = { navController.popBackStack() }
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {

            GlassCard {
                DateRangeSelector(
                    startDate = uiState.startDate,
                    endDate = uiState.endDate,
                    onStartDateChange = viewModel::onStartDateChange,
                    onEndDateChange = viewModel::onEndDateChange,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            GlassCard(borderBrush = BlueCyanGradientV2) {
                ReusableMultiLineChart(
                    title = "Tiêu thụ điện năng",
                    chartModel = uiState.chartModel,
                    isLoading = uiState.isChartLoading,
                    bottomAxisLabels = uiState.chartBottomLabels,
                    xStep = uiState.chartXStep,
                    modifier = Modifier.fillMaxWidth().height(250.dp)
                )
            }

            Text("Thiết bị tiêu thụ", style = MaterialTheme.typography.titleMedium, color = TextGrayV2)

            if (uiState.isLoading) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = NeonBlueV2) }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    itemsIndexed(uiState.sensors) { index, sensor ->
                        PowerItemRowV2(
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
fun PowerItemRowV2(
    sensor: PowerSensor,
    colorIndex: Int,
    isSelected: Boolean,
    onSelectionChanged: (Long, Boolean) -> Unit
) {
    val sensorColor = getChartColorV2(colorIndex)
    val itemGradient = Brush.linearGradient(listOf(sensorColor, sensorColor.copy(alpha = 0.5f)))

    GlassCard(onClick = { onSelectionChanged(sensor.id, !isSelected) }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GradientIconBox(icon = Icons.Default.FlashOn, gradient = itemGradient)
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(sensor.name, style = MaterialTheme.typography.bodyMedium, color = TextGrayV2)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        "${sensor.currentWatt?.toInt() ?: "--"}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextWhiteV2
                    )
                    Text(" W", style = MaterialTheme.typography.bodyMedium, color = TextGrayV2, modifier = Modifier.padding(bottom = 4.dp))
                }
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