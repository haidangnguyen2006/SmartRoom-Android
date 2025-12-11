package com.seiuh.smartroomapp.ui.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seiuh.smartroomapp.data.local.UserPreferences
import com.seiuh.smartroomapp.data.network.NetworkResult
import com.seiuh.smartroomapp.data.network.RetrofitClient
import com.seiuh.smartroomapp.data.repository.SmartHomeRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// State & Event
data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val rememberMe: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val serverUrl: String = ""
)

sealed class LoginEvent {
    data class NavigateToHome(val username: String) : LoginEvent()
}

class LoginViewModel(
    private val repository: SmartHomeRepository,
    private val userPrefs: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _loginEvent = MutableSharedFlow<LoginEvent>()
    val loginEvent = _loginEvent.asSharedFlow()

    var serverUrlInput by mutableStateOf("")
    init {
       //Tự động load thông tin đã lưu
        viewModelScope.launch {
            userPrefs.userPreferencesFlow.collect { credentials ->
                if (credentials != null) {
                    _uiState.update {
                        it.copy(
                            username = credentials.username,
                            password = credentials.password,
                            rememberMe = true
                        )
                    }
                }
            }
            userPrefs.serverUrl.collect { savedUrl ->
                // Loại bỏ http:// và /api/v1/ để hiển thị cho gọn nếu muốn
                // Hoặc cứ để nguyên chuỗi đầy đủ
                serverUrlInput = savedUrl

                // Cấu hình Retrofit ngay khi app mở
                RetrofitClient.configureBaseUrl(savedUrl)
            }
        }
    }

    fun onUsernameChanged(v: String) = _uiState.update { it.copy(username = v, errorMessage = null) }
    fun onPasswordChanged(v: String) = _uiState.update { it.copy(password = v, errorMessage = null) }
    fun onRememberMeChanged(v: Boolean) = _uiState.update { it.copy(rememberMe = v) }
    fun onServerUrlChanged(newValue: String) {
        serverUrlInput = newValue
    }

    fun onLoginClicked() {
        if (_uiState.value.username.isBlank() || _uiState.value.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Vui lòng nhập đầy đủ thông tin") }
            return
        }

        viewModelScope.launch {
            userPrefs.saveServerUrl(serverUrlInput)
            RetrofitClient.configureBaseUrl(serverUrlInput)
            // Gọi Repository Login
            repository.login(_uiState.value.username, _uiState.value.password).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is NetworkResult.Success -> {
                        val token = result.data?.token
                        if (token != null) {
                            // Lưu token vào Singleton để dùng cho các request sau
                            RetrofitClient.authToken = token

                            userPrefs.saveCredentials(
                                username = _uiState.value.username,
                                pass = _uiState.value.password,
                                remember = _uiState.value.rememberMe
                            )

                            _uiState.update { it.copy(isLoading = false) }
                            _loginEvent.emit(LoginEvent.NavigateToHome(result.data.username))
                        } else {
                            _uiState.update { it.copy(isLoading = false, errorMessage = "Lỗi: Token rỗng") }
                        }
                    }
                    is NetworkResult.Error -> {
                        _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                    }
                }
            }
        }
    }
}