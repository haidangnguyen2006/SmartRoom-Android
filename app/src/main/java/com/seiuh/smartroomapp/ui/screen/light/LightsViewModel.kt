package com.seiuh.smartroomapp.ui.screen.light

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seiuh.smartroomapp.data.model.device.Light
import com.seiuh.smartroomapp.data.network.NetworkResult
import com.seiuh.smartroomapp.data.repository.SmartHomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LightsUiState(
    val isLoading: Boolean = false,
    val roomName: String = "Đang tải...",
    val lights: List<Light> = emptyList(),
    val errorMessage: String? = null
)

class LightsViewModel(
    private val roomId: Long,
    private val repository: SmartHomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LightsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadLights()
    }

    private fun loadLights() {
        viewModelScope.launch {
            val name = repository.getRoomName(roomId)
            _uiState.update { it.copy(roomName = name) }
            // Gọi API lấy danh sách đèn
            repository.getLights(roomId).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is NetworkResult.Success -> {
                        _uiState.update { it.copy(isLoading = false, lights = result.data ?: emptyList()) }
                    }
                    is NetworkResult.Error -> {
                        _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                    }
                }
            }
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
                    // Show error (ví dụ qua Toast/Snackbar ở View)
                    _uiState.update { it.copy(errorMessage = result.message) }
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
}
