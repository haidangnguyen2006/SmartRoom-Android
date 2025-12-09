package com.seiuh.smartroomapp.ui.screen.roomdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seiuh.smartroomapp.data.model.device.Light
import com.seiuh.smartroomapp.data.network.NetworkResult
import com.seiuh.smartroomapp.data.repository.SmartHomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RoomDetailUiState(
    val isLoading: Boolean = true,
    val roomName: String = "",
    val currentTemp: Double = 0.0, // Mặc định 0.0 để Gauge không lỗi
    val lights: List<Light> = emptyList(),
    // Các thiết bị khác (nếu có API)
    val errorMessage: String? = null
)

class RoomDetailViewModel(
    val roomId: Long,
    private val repository: SmartHomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoomDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // 1. Info Phòng
            val name = repository.getRoomName(roomId)

            // 2. Nhiệt độ hiện tại (Lấy trung bình các cảm biến)
            var temp = 0.0
            repository.getTempSensors(roomId).collect { res ->
                if (res is NetworkResult.Success) {
                    val sensors = res.data ?: emptyList()
                    if (sensors.isNotEmpty()) {
                        temp = sensors.mapNotNull { it.currentValue }.average()
                    }
                }
            }

            // 3. Danh sách đèn
            var lights: List<Light> = emptyList()
            repository.getLights(roomId).collect { res ->
                if (res is NetworkResult.Success) {
                    lights = res.data ?: emptyList()
                }
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    roomName = name,
                    currentTemp = temp,
                    lights = lights
                )
            }
        }
    }

    // Toggle đèn
    fun toggleLight(id: Long) {
        viewModelScope.launch {
            // Optimistic Update
            _uiState.update { state ->
                state.copy(lights = state.lights.map {
                    if (it.id == id) it.copy(isActive = !it.isActive) else it
                })
            }
            repository.toggleLight(id).collect { /* Handle error if needed */ }
        }
    }

    // Giả lập slider level (API chưa có endpoint update level realtime)
    fun setLightLevel(id: Long, level: Int) {
        _uiState.update { state ->
            state.copy(lights = state.lights.map {
                if (it.id == id) it.copy(level = level) else it
            })
        }
    }
}