package com.seiuh.smartroomapp.ui.screen.roomdashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seiuh.smartroomapp.data.model.structure.Floor
import com.seiuh.smartroomapp.data.model.structure.Room
import com.seiuh.smartroomapp.data.network.NetworkResult
import com.seiuh.smartroomapp.data.repository.SmartHomeRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class DashboardUiState(
    val isLoading: Boolean = false,
    val availableFloors: List<Floor> = emptyList(),
    val selectedFloor: Floor? = null,
    val availableRooms: List<Room> = emptyList(),
    val selectedRoom: Room? = null,

    // [CẬP NHẬT] Thông số tóm tắt cho Dashboard V3
    val currentTemp: Double? = null,      // Nhiệt độ hiện tại
    val currentPower: Double? = null,     // Công suất hiện tại
    val activeLightsCount: Int = 0,       // Số đèn đang bật

    val startDate: LocalDate = LocalDate.now().minusDays(7),
    val endDate: LocalDate = LocalDate.now(),
    val errorMessage: String? = null
)

sealed class DashboardEvent {
    data class NavigateToLights(val roomId: Long) : DashboardEvent()
    data class NavigateToTemp(val roomId: Long) : DashboardEvent()
    data class NavigateToPower(val roomId: Long) : DashboardEvent()
    object Logout : DashboardEvent()
}

class RoomDashboardViewModel(
    private val repository: SmartHomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<DashboardEvent>()
    val events = _events.asSharedFlow()

    init {
        loadFloors()
    }

    private fun loadFloors() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getFloors().collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val floors = result.data ?: emptyList()
                        _uiState.update { it.copy(availableFloors = floors) }

                        val firstFloor = floors.firstOrNull()
                        if (firstFloor != null) {
                            onFloorSelected(firstFloor)
                        } else {
                            _uiState.update { it.copy(isLoading = false) }
                        }
                    }
                    is NetworkResult.Error -> {
                        _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                    }
                    is NetworkResult.Loading -> {}
                }
            }
        }
    }

    fun onFloorSelected(floor: Floor) {
        if (_uiState.value.selectedFloor?.id == floor.id) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    selectedFloor = floor,
                    isLoading = true,
                    selectedRoom = null,
                    availableRooms = emptyList(),
                    currentTemp = null,
                    currentPower = null,
                    activeLightsCount = 0
                )
            }

            repository.getRooms(floorId = floor.id).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val rooms = result.data ?: emptyList()
                        _uiState.update { it.copy(availableRooms = rooms) }

                        rooms.firstOrNull()?.let { onRoomSelected(it) }
                            ?: _uiState.update { it.copy(isLoading = false) }
                    }
                    is NetworkResult.Error -> {
                        _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                    }
                    else -> {}
                }
            }
        }
    }

    fun onRoomSelected(room: Room) {
        _uiState.update { it.copy(selectedRoom = room) }
        loadDashboardData(room)
    }

    // [CẬP NHẬT] Hàm tải dữ liệu tóm tắt (Snapshot) thay vì biểu đồ lịch sử
    private fun loadDashboardData(room: Room) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Gọi song song 3 API để lấy trạng thái mới nhất của thiết bị
            // Lưu ý: repository.getTempSensors trả về Flow, ta dùng first() hoặc collect 1 lần để lấy giá trị
            // Tuy nhiên, để đơn giản và hiệu quả, ta giả định repository trả về Flow và ta sẽ collect kết quả đầu tiên

            // Cách tiếp cận an toàn với Flow: Chạy từng job và update state dần dần hoặc dùng combine
            // Ở đây tôi dùng cách chạy tuần tự trong coroutine scope chung để đơn giản hóa việc xử lý lỗi

            // 1. Tải cảm biến nhiệt độ -> Tính trung bình
            launch {
                repository.getTempSensors(room.id).collect { res ->
                    if (res is NetworkResult.Success) {
                        val sensors = res.data ?: emptyList()
                        val avgTemp = if (sensors.isNotEmpty()) {
                            sensors.mapNotNull { it.currentValue }.average().takeIf { !it.isNaN() }
                        } else null
                        _uiState.update { it.copy(currentTemp = avgTemp) }
                    }
                }
            }

            // 2. Tải cảm biến công suất -> Tính tổng
            launch {
                repository.getPowerSensors(room.id).collect { res ->
                    if (res is NetworkResult.Success) {
                        val sensors = res.data ?: emptyList()
                        val totalPower = sensors.sumOf { it.currentWatt ?: 0.0 }
                        _uiState.update { it.copy(currentPower = totalPower) }
                    }
                }
            }

            // 3. Tải đèn -> Đếm số đèn đang bật
            launch {
                repository.getLights(room.id).collect { res ->
                    if (res is NetworkResult.Success) {
                        val lights = res.data ?: emptyList()
                        val activeCount = lights.count { it.isActive }
                        _uiState.update { it.copy(activeLightsCount = activeCount) }
                    }
                }
            }

            // Tạm thời tắt loading sau một khoảng delay nhỏ để UI không bị giật,
            // hoặc lý tưởng nhất là khi cả 3 job trên đều emit giá trị đầu tiên.
            // Trong thực tế, Flow của repository có thể emit Loading trước, nên ta cần cẩn trọng.
            // Giải pháp đơn giản: set isLoading = false ngay sau khi launch các job (vì các job chạy background update UI sau)
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    // Navigation & Actions
    fun onStartDateChange(date: LocalDate) {
        _uiState.update { it.copy(startDate = date) }
        // Dashboard V3 không dùng date để filter chart ngay tại home nữa, nên không cần reload data
    }

    fun onEndDateChange(date: LocalDate) {
        _uiState.update { it.copy(endDate = date) }
    }

    fun onLightsClicked() = viewModelScope.launch {
        _uiState.value.selectedRoom?.id?.let { _events.emit(DashboardEvent.NavigateToLights(it)) }
    }
    fun onTempClicked() = viewModelScope.launch {
        _uiState.value.selectedRoom?.id?.let { _events.emit(DashboardEvent.NavigateToTemp(it)) }
    }
    fun onPowerClicked() = viewModelScope.launch {
        _uiState.value.selectedRoom?.id?.let { _events.emit(DashboardEvent.NavigateToPower(it)) }
    }

    fun onLogoutClicked() {
        viewModelScope.launch {
            com.seiuh.smartroomapp.data.network.RetrofitClient.clearToken()
            _events.emit(DashboardEvent.Logout)
        }
    }
}