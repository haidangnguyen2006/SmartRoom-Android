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
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

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
                        sensorsList.addAll((res.data ?: emptyList()).map { SensorSelection(it.id, it.name) })
                    }
                }
            } else {
                repository.getPowerSensors(roomId).collect { res ->
                    if (res is NetworkResult.Success) {
                        sensorsList.addAll((res.data ?: emptyList()).map { SensorSelection(it.id, it.name) })
                    }
                }
            }

            _uiState.update { it.copy(sensors = sensorsList) }
            loadChartData()
        }
    }

    // Tương tự logic loadChartData cũ nhưng áp dụng bộ lọc sensor
    fun loadChartData() {
        viewModelScope.launch {
            val selectedIds = _uiState.value.sensors.filter { it.isSelected }.map { it.id }
            if (selectedIds.isEmpty()) {
                _uiState.update { it.copy(chartModel = null, isLoading = false) }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true) }

            // Format Date (UTC)
            val startStr = _uiState.value.startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toString()
            val endStr = _uiState.value.endDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toString()

            // Gọi API History (Tùy type)
            // LƯU Ý: API hiện tại getTempHistory trả về AVG của cả phòng,
            // chưa hỗ trợ filter theo từng ID sensor cụ thể trong params (dựa trên ApiService hiện tại).
            // Nếu muốn multi-line cho từng sensor, API phải hỗ trợ hoặc client phải gọi loop.
            // Ở đây tạm thời ta hiển thị biểu đồ tổng quan, logic filter sẽ áp dụng nếu API nâng cấp.

            if (_uiState.value.chartType == "temp") {
                repository.getTempHistory(roomId, startStr, endStr).collect { res ->
                    // ... Mapping logic (FloatEntry) ...
                    // Để ngắn gọn, tôi dùng lại logic mapping cũ
                    if (res is NetworkResult.Success) {
                        val entries = (res.data ?: emptyList()).mapIndexed { index, item ->
                            FloatEntry(index.toFloat(), item.avgTempC.toFloat())
                        }
                        val model = if (entries.isNotEmpty()) entryModelOf(entries) else null
                        _uiState.update { it.copy(chartModel = model, isLoading = false) }
                    }
                }
            } else {
                repository.getPowerHistory(roomId, startStr, endStr).collect { res ->
                    if (res is NetworkResult.Success) {
                        val entries = (res.data ?: emptyList()).mapIndexed { index, item ->
                            FloatEntry(index.toFloat(), (item.avgWatt ?: 0.0).toFloat())
                        }
                        val model = if (entries.isNotEmpty()) entryModelOf(entries) else null
                        _uiState.update { it.copy(chartModel = model, isLoading = false) }
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