package com.seiuh.smartroomapp.ui.screen.chartdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.seiuh.smartroomapp.data.network.NetworkResult
import com.seiuh.smartroomapp.data.repository.SmartHomeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class SensorSelection(
    val id: Long,
    val name: String,
    val isSelected: Boolean = true
)

data class ChartDetailUiState(
    val isLoading: Boolean = false,
    val chartType: String = "temp", // "temp" or "power"
    val sensors: List<SensorSelection> = emptyList(), // Danh sách sensor để filter
    val chartModel: ChartEntryModel? = null,
    val chartLabels: List<String> = emptyList(),
    val startDate: LocalDate = LocalDate.now().minusDays(3), // Mặc định 3 ngày
    val endDate: LocalDate = LocalDate.now()
)

class ChartDetailViewModel(
    private val roomId: Long,
    private val repository: SmartHomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChartDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        .withZone(ZoneId.systemDefault())

    fun setType(type: String) {
        _uiState.update { it.copy(chartType = type) }
        loadSensors()
    }

    private fun loadSensors() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val sensorsList = mutableListOf<SensorSelection>()

            if (_uiState.value.chartType == "temp") {
                repository.getTempSensors(roomId).collect { res ->
                    if (res is NetworkResult.Success) {
                        // [FIX]: Xử lý Null Safety cho name
                        val data = res.data ?: emptyList()
                        // Xóa dữ liệu cũ (nếu có) trước khi thêm mới để tránh duplicate
                        sensorsList.clear()
                        sensorsList.addAll(data.map {
                            SensorSelection(it.id, it.name ?: "Sensor ${it.id}")
                        })
                    }
                }
            } else {
                repository.getPowerSensors(roomId).collect { res ->
                    if (res is NetworkResult.Success) {
                        // [FIX]: Tương tự cho Power Sensor
                        val data = res.data ?: emptyList()
                        sensorsList.clear()
                        sensorsList.addAll(data.map {
                            SensorSelection(it.id, it.name ?: "Sensor ${it.id}")
                        })
                    }
                }
            }

            _uiState.update { it.copy(sensors = sensorsList) }
            loadChartData()
        }
    }
    private fun <T> aggregateDataByHour(
        data: List<T>,
        timestampSelector: (T) -> String,
        valueSelector: (T) -> Double
    ): Map<String, Double> {
        return data.groupBy { item ->
            // Parse timestamp và làm tròn xuống theo giờ (Truncate to Hour)
            // Ví dụ: 10:05, 10:55 -> gom chung thành 10:00
            try {
                val instant = Instant.parse(timestampSelector(item))
                val hourInstant = instant.truncatedTo(ChronoUnit.HOURS)
                hourInstant.toString()
            } catch (e: Exception) {
                ""
            }
        }.mapValues { entry ->
            // Tính trung bình cộng của các giá trị trong giờ đó
            entry.value.map { valueSelector(it) }.average()
        }.filterKeys { it.isNotEmpty() }
            // Sắp xếp lại theo thời gian để vẽ đúng thứ tự
            .toSortedMap()
    }
    // Tương tự logic loadChartData cũ nhưng áp dụng bộ lọc sensor
    fun loadChartData() {
        viewModelScope.launch {
            // Logic filter sensor (Hiện tại API trả về All, sau này có thể filter client side hoặc gọi API khác)
            val selectedIds = _uiState.value.sensors.filter { it.isSelected }.map { it.id }
            if (selectedIds.isEmpty()) {
                _uiState.update { it.copy(chartModel = null, chartLabels = emptyList(), isLoading = false) }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true) }

            // Chuyển đổi ngày sang Instant (UTC) để gọi API
            val startStr = _uiState.value.startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toString()
            val endStr = _uiState.value.endDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toString()

            if (_uiState.value.chartType == "temp") {
                repository.getTempHistory(roomId, startStr, endStr).collect { res ->
                    if (res is NetworkResult.Success) {
                        val rawData = res.data ?: emptyList()

                        // [BƯỚC 1]: GOM NHÓM DỮ LIỆU (1 giờ 1 điểm)
                        val aggregatedData = aggregateDataByHour(
                            data = rawData,
                            timestampSelector = { it.timestamp },
                            valueSelector = { it.avgTempC }
                        )

                        // [BƯỚC 2]: Map sang FloatEntry và Labels
                        val entries = aggregatedData.values.mapIndexed { index, value ->
                            FloatEntry(index.toFloat(), value.toFloat())
                        }

                        val labels = aggregatedData.keys.map { isoString ->
                            try {
                                timeFormatter.format(Instant.parse(isoString))
                            } catch (e: Exception) { "" }
                        }.toList()

                        val model = if (entries.isNotEmpty()) entryModelOf(entries) else null

                        _uiState.update {
                            it.copy(
                                chartModel = model,
                                chartLabels = labels,
                                isLoading = false
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false, chartModel = null) }
                    }
                }
            } else {
                repository.getPowerHistory(roomId, startStr, endStr).collect { res ->
                    if (res is NetworkResult.Success) {
                        val rawData = res.data ?: emptyList()

                        // [BƯỚC 1]: GOM NHÓM DỮ LIỆU (1 giờ 1 điểm)
                        val aggregatedData = aggregateDataByHour(
                            data = rawData,
                            timestampSelector = { it.timestamp },
                            valueSelector = { it.avgWatt ?:0.0}
                        )

                        // [BƯỚC 2]: Map sang FloatEntry và Labels
                        val entries = aggregatedData.values.mapIndexed { index, value ->
                            FloatEntry(index.toFloat(), value.toFloat())
                        }

                        val labels = aggregatedData.keys.map { isoString ->
                            try {
                                timeFormatter.format(Instant.parse(isoString))
                            } catch (e: Exception) { "" }
                        }.toList()

                        val model = if (entries.isNotEmpty()) entryModelOf(entries) else null

                        _uiState.update {
                            it.copy(
                                chartModel = model,
                                chartLabels = labels,
                                isLoading = false
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false, chartModel = null) }
                    }
                }
            }
        }
    }

    fun toggleSensor(id: Long, isSelected: Boolean) {
        _uiState.update { state ->
            state.copy(sensors = state.sensors.map { if (it.id == id) it.copy(isSelected = isSelected) else it })
        }
        loadChartData()
    }

    fun onDateChange(start: LocalDate, end: LocalDate) {
        _uiState.update { it.copy(startDate = start, endDate = end) }
        loadChartData()
    }
}