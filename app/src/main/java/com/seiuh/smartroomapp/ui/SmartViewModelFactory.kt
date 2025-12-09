package com.seiuh.smartroomapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.seiuh.smartroomapp.data.local.UserPreferences
import com.seiuh.smartroomapp.data.repository.SmartHomeRepository
import com.seiuh.smartroomapp.ui.screen.chartdetail.ChartDetailViewModel
import com.seiuh.smartroomapp.ui.screen.home.HomeViewModel
import com.seiuh.smartroomapp.ui.screen.light.LightsViewModel
import com.seiuh.smartroomapp.ui.screen.login.LoginViewModel
import com.seiuh.smartroomapp.ui.screen.power.PowerViewModel
import com.seiuh.smartroomapp.ui.screen.roomdashboard.RoomDashboardViewModel
import com.seiuh.smartroomapp.ui.screen.roomdetail.RoomDetailViewModel
import com.seiuh.smartroomapp.ui.screen.temperature.TemperatureViewModel

/**
 * Factory chung để khởi tạo tất cả ViewModel với Repository.
 * Cách này giúp code gọn hơn so với viết Factory riêng cho từng cái.
 */
class SmartViewModelFactory(
    private val repository: SmartHomeRepository,
    private val userPrefs: UserPreferences? = null,
    private val roomId: Long = 0L // Dùng cho các màn hình chi tiết
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                // Truyền userPrefs vào LoginViewModel (bắt buộc phải có)
                if (userPrefs == null) throw IllegalArgumentException("UserPreferences required for LoginViewModel")
                LoginViewModel(repository, userPrefs) as T
            }
            modelClass.isAssignableFrom(RoomDashboardViewModel::class.java) -> {
                RoomDashboardViewModel(repository) as T
            }
            modelClass.isAssignableFrom(LightsViewModel::class.java) -> {
                LightsViewModel(roomId, repository) as T
            }
            modelClass.isAssignableFrom(TemperatureViewModel::class.java) -> {
                TemperatureViewModel(roomId, repository) as T
            }
            modelClass.isAssignableFrom(PowerViewModel::class.java) -> {
                PowerViewModel(roomId, repository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(RoomDetailViewModel::class.java) -> {
                RoomDetailViewModel(roomId, repository) as T
            }
            modelClass.isAssignableFrom(ChartDetailViewModel::class.java) -> {
                ChartDetailViewModel(roomId, repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}