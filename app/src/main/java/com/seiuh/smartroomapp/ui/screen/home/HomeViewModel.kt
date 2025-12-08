package com.seiuh.smartroomapp.ui.screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seiuh.smartroomapp.data.model.structure.Floor
import com.seiuh.smartroomapp.data.model.structure.Room
import com.seiuh.smartroomapp.data.network.NetworkResult
import com.seiuh.smartroomapp.data.repository.SmartHomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull

data class HomeUiState(
    val isLoading: Boolean = false,
    val floors: List<Floor> = emptyList(), // Thêm danh sách tầng để lấy tên hiển thị
    val rooms: List<Room> = emptyList(),
    val livingRoomTemp: Double? = null, // Placeholder cho nhiệt độ phòng khách
    val isAtHome: Boolean = true,
    val errorMessage: String? = null,
    val selectedTab: Int = 0
)

class HomeViewModel(
    private val repository: SmartHomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Step 1: Load Floors
            var floorsLoaded = false
            repository.getFloors().collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                    }

                    is NetworkResult.Success -> {
                        if (!floorsLoaded) {
                            floorsLoaded = true
                            val floors = result.data ?: emptyList()

                            _uiState.update { it.copy(floors = floors) }

                            if (floors.isEmpty()) {
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        errorMessage = "Không có tầng nào trong hệ thống"
                                    )
                                }
                            } else {
                                // Step 2: Load Rooms for each floor
                                loadAllRooms(floors)
                            }
                        }
                    }

                    is NetworkResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Lỗi tải tầng: ${result.message}"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loadAllRooms(floors: List<Floor>) {
        viewModelScope.launch {
            val allRooms = mutableListOf<Room>()
            var loadedFloorCount = 0

            floors.forEach { floor ->
                launch {

                    var roomsLoadedForFloor = false
                    repository.getRooms(floor.id).collect { result ->
                        when (result) {
                            is NetworkResult.Loading -> {
                            }

                            is NetworkResult.Success -> {
                                if (!roomsLoadedForFloor) {
                                    roomsLoadedForFloor = true
                                    val rooms = result.data ?: emptyList()

                                    // Ensure floorId is set correctly
                                    val roomsWithFloorId = rooms.map { room ->
                                        if (room.floorId != floor.id) {

                                            room.copy(floorId = floor.id)
                                        } else {
                                            room
                                        }
                                    }

                                    synchronized(allRooms) {
                                        allRooms.addAll(roomsWithFloorId)
                                        loadedFloorCount++

                                        // Update UI immediately
                                        _uiState.update { it.copy(rooms = allRooms.toList()) }

                                        // If all floors loaded
                                        if (loadedFloorCount == floors.size) {
                                            _uiState.update {
                                                it.copy(
                                                    isLoading = false,
                                                    rooms = allRooms.toList()
                                                )
                                            }

                                            // Log final result
                                            floors.forEach { f ->
                                                val roomsInFloor = allRooms.filter { it.floorId == f.id }
                                            }
                                        }
                                    }
                                }
                            }

                            is NetworkResult.Error -> {
                                if (!roomsLoadedForFloor) {
                                    roomsLoadedForFloor = true

                                    synchronized(allRooms) {
                                        loadedFloorCount++
                                        if (loadedFloorCount == floors.size) {
                                            _uiState.update {
                                                it.copy(
                                                    isLoading = false,
                                                    rooms = allRooms.toList()
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }
    fun setMode(isHome: Boolean) {
        _uiState.update { it.copy(isAtHome = isHome) }
    }
    fun retry() {
        loadData()
    }
}