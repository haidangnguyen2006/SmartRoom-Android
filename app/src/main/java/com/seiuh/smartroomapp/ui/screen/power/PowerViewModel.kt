package com.seiuh.smartroomapp.ui.screen.power

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.seiuh.smartroomapp.data.model.device.PowerSensor // [Model thật]
import com.seiuh.smartroomapp.data.network.NetworkResult
import com.seiuh.smartroomapp.data.repository.SmartHomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class PowerUiState(
    val isLoading: Boolean = true,
    val isChartLoading: Boolean = false,
    val roomName: String = "Đang tải...", // [Thêm mới]
    val sensors: List<PowerSensor> = emptyList(),
    val selectedSensorIds: Set<Long> = emptySet(),
    val startDate: LocalDate = LocalDate.now().minusDays(1),
    val endDate: LocalDate = LocalDate.now(),
    val chartModel: ChartEntryModel? = null,
    val chartBottomLabels: List<String> = emptyList(),
    val chartXStep: Int = 12,
    val errorMessage: String? = null
)

class PowerViewModel(
    private val roomId: Long,
    private val repository: SmartHomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PowerUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // 1. Lấy tên phòng
            val name = repository.getRoomName(roomId)

            // 2. Lấy danh sách cảm biến điện
            repository.getPowerSensors(roomId).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val sensors = result.data ?: emptyList()
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                roomName = name,
                                sensors = sensors
                            )
                        }
                        if (sensors.isNotEmpty()) {
                            val allIds = sensors.map { it.id }.toSet()
                            _uiState.update { it.copy(selectedSensorIds = allIds) }
                            // loadChartData()
                        }
                    }
                    is NetworkResult.Error -> {
                        _uiState.update { it.copy(isLoading = false, roomName = name, errorMessage = result.message) }
                    }
                    is NetworkResult.Loading -> _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    // ... (Các hàm loadChartData, onSensorSelected... tương tự TemperatureViewModel) ...
    // Lưu ý dùng repository.getPowerHistory()
    private fun loadChartData() {
        viewModelScope.launch {
            val selectedIds = _uiState.value.selectedSensorIds

            // 1. Kiểm tra nếu chưa chọn sensor nào thì xóa chart
            if (selectedIds.isEmpty()) {
                _uiState.update { it.copy(chartModel = null, isChartLoading = false) }
                return@launch
            }

            _uiState.update { it.copy(isChartLoading = true) }

            // 2. Xử lý thời gian chuẩn UX (User Timezone -> UTC)
            // Ví dụ: User chọn ngày 20/11 ở VN.
            // startStr sẽ là: 2025-11-19T17:00:00Z (tức là 00:00 ngày 20/11 giờ VN)
            val startStr = _uiState.value.startDate
                .atStartOfDay(ZoneId.systemDefault()) // Lấy 00:00 theo giờ máy người dùng
                .toInstant() // Chuyển đổi sang UTC (Server hiểu)
                .toString() // Định dạng ISO-8601 có chữ Z

            val endStr = _uiState.value.endDate
                .atTime(23, 59, 59) // 23:59:59 theo giờ máy người dùng
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toString()

            // 3. Gọi API & Mapping dữ liệu
            repository.getPowerHistory(roomId, startStr, endStr).collect { res ->
                when (res) {
                    is NetworkResult.Success -> {
                        // Mapping: List<TempHistory> -> ChartEntryModel của Vico
                        val historyList = res.data ?: emptyList()

                        val formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm")
                        val zoneId = ZoneId.systemDefault() // Múi giờ người dùng

                        // Map dữ liệu
                        val entries = historyList.mapIndexed { index, item ->
                            FloatEntry(x = index.toFloat(), y = item.avgWatt!!.toFloat())
                        }

                        // Tạo danh sách nhãn tương ứng với từng điểm dữ liệu
                        val labels = historyList.map { item ->
                            try {
                                val instant = Instant.parse(item.timestamp)
                                val zdt = ZonedDateTime.ofInstant(instant, zoneId)
                                zdt.format(formatter)
                            } catch (e: Exception) {
                                "" // Fallback nếu lỗi parse
                            }
                        }

                        val model = if (entries.isNotEmpty()) entryModelOf(entries) else null

                        _uiState.update {
                            it.copy(
                                isChartLoading = false,
                                chartModel = model,
                                chartBottomLabels = labels // [Lưu Labels]
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        _uiState.update { it.copy(isChartLoading = false, chartModel = null, errorMessage = res.message) }
                    }
                    else -> {}
                }
            }
        }
    }

    fun onSensorSelected(sensorId: Long, isSelected: Boolean) {
        _uiState.update { state ->
            val newIds = state.selectedSensorIds.toMutableSet()
            if (isSelected) newIds.add(sensorId) else newIds.remove(sensorId)
            state.copy(selectedSensorIds = newIds)
        }
        loadChartData()
    }

    fun onStartDateChange(date: LocalDate) {
        _uiState.update { it.copy(startDate = date) }
        loadChartData()
    }

    fun onEndDateChange(date: LocalDate) {
        _uiState.update { it.copy(endDate = date) }
        loadChartData()
    }
}