package com.seiuh.smartroomapp.ui.screen.roomdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seiuh.smartroomapp.data.model.device.Light
import com.seiuh.smartroomapp.data.model.device.TempSensor
import com.seiuh.smartroomapp.data.network.NetworkResult
import com.seiuh.smartroomapp.data.repository.SmartHomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RoomDetailUiState(
    val isLoading: Boolean = true,
    val roomName: String = "",
    val lights: List<Light> = emptyList(),
    val currentTemp: Double? = null,
    val isACOn: Boolean = false
)

class RoomDetailViewModel(
    private val roomId: Long,
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

            val roomName = repository.getRoomName(roomId)

            // Load Lights
            launch {
                repository.getLights(roomId).collect { res ->
                    if (res is NetworkResult.Success) {
                        _uiState.update { it.copy(lights = res.data ?: emptyList()) }
                    }
                }
            }

            // Load Temp
            launch {
                repository.getTempSensors(roomId).collect { res ->
                    if (res is NetworkResult.Success) {
                        val temp = res.data?.firstOrNull()?.currentValue
                        _uiState.update { it.copy(currentTemp = temp) }
                    }
                }
            }

            _uiState.update { it.copy(isLoading = false, roomName = roomName) }
        }
    }
    fun onLightToggle(lightId: Long, isChecked: Boolean) {
        viewModelScope.launch {
            // Gọi API Toggle
            // Lưu ý: isChecked không được dùng trực tiếp vì API toggle chỉ đảo trạng thái
            // Nhưng để UI phản hồi nhanh, ta có thể update local trước
            repository.toggleLight(lightId).collect { result ->
                if (result is NetworkResult.Success) {
                    // Update thành công từ server, cập nhật lại list
                    val updatedLight = result.data
                    if (updatedLight != null) {
                        _uiState.update { state ->
                            state.copy(lights = state.lights.map {
                                if (it.id == updatedLight.id) updatedLight else it
                            })
                        }
                    }
                } else if (result is NetworkResult.Error) {
                    
                }
            }
        }
    }

    fun onToggleAll(isOn: Boolean) {
        // API hiện tại chưa có endpoint "Toggle All".
        // Giải pháp tạm thời: Loop qua list và gọi toggle từng cái (Không khuyến khích cho production)
        // Hoặc yêu cầu Backend thêm API /lights/room/{id}/toggle-all
        viewModelScope.launch {
            _uiState.value.lights.forEach { light ->
                if (light.isActive != isOn) { // Chỉ toggle cái nào khác trạng thái mong muốn
                    repository.toggleLight(light.id).collect {}
                }
            }
        }
    }
    // Add toggle functions...

}