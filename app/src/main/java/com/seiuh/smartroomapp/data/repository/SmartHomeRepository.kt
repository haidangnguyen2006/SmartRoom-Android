package com.seiuh.smartroomapp.data.repository

import com.seiuh.smartroomapp.data.model.auth.LoginRequest
import com.seiuh.smartroomapp.data.model.auth.LoginResponse
import com.seiuh.smartroomapp.data.model.device.Light
import com.seiuh.smartroomapp.data.model.device.PowerSensor
import com.seiuh.smartroomapp.data.model.device.TempSensor
import com.seiuh.smartroomapp.data.model.history.PowerHistory
import com.seiuh.smartroomapp.data.model.history.TempHistory
import com.seiuh.smartroomapp.data.model.structure.Floor
import com.seiuh.smartroomapp.data.model.structure.Room
import com.seiuh.smartroomapp.data.network.NetworkResult
import com.seiuh.smartroomapp.data.network.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class SmartHomeRepository {
    private val api = RetrofitClient.apiService

    // --- Helper để xử lý Response chung ---
    private fun <T> handleResponse(response: Response<com.seiuh.smartroomapp.data.model.base.ApiResponse<T>>): NetworkResult<T> {
        return if (response.isSuccessful && response.body()?.status in 200..299) {
            val data = response.body()?.data
            if (data != null) {
                NetworkResult.Success(data)
            } else {
                NetworkResult.Error("Dữ liệu trống từ server")
            }
        } else {
            // Đây là chỗ sau này ta sẽ bắt message lỗi để hiển thị Alert
            val msg = response.body()?.message ?: "Lỗi: ${response.message()}"
            NetworkResult.Error(msg)
        }
    }

    // --- AUTH ---
    fun login(username: String, password: String): Flow<NetworkResult<LoginResponse>> = flow {
        emit(NetworkResult.Loading())
        try {
            val resp = api.login(LoginRequest(username, password))
            emit(handleResponse(resp))
        } catch (e: Exception) {
            emit(NetworkResult.Error("Lỗi kết nối: ${e.message}"))
        }
    }

    // --- ROOMS ---
    // Mặc định lấy phòng tầng 1 (floorId = 1) để demo
    fun getRooms(floorId: Long): Flow<NetworkResult<List<Room>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val resp = api.getRoomsByFloor(floorId)
            if (resp.isSuccessful && resp.body()?.status in 200..299) {
                val data = resp.body()?.data
                if (data != null && data.content.isNotEmpty()) {
                    emit(NetworkResult.Success(data.content))
                } else {
                    // Trường hợp không có phòng trong tầng này
                    emit(NetworkResult.Success(emptyList()))
                }
            } else {
                val msg = resp.body()?.message ?: "Không tải được phòng của tầng $floorId"
                emit(NetworkResult.Error(msg))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Lỗi mạng"))
        }
    }

    // --- LIGHTS ---
    fun  getLights(roomId: Long): Flow<NetworkResult<List<Light>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val resp = api.getLights(roomId)
            if (resp.isSuccessful && resp.body()?.data != null) {
                emit(NetworkResult.Success(resp.body()!!.data!!.content))
            } else {
                emit(NetworkResult.Error("Lỗi tải đèn"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Lỗi mạng"))
        }
    }

    fun toggleLight(id: Long): Flow<NetworkResult<Light>> = flow {
        try {
            val resp = api.toggleLight(id)
            emit(handleResponse(resp))
        } catch (e: Exception) {
            emit(NetworkResult.Error("Không thể điều khiển: ${e.message}"))
        }
    }

    // --- TEMPERATURE ---
    fun getTempSensors(roomId: Long): Flow<NetworkResult<List<TempSensor>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val resp = api.getTempSensors(roomId)
            if (resp.isSuccessful && resp.body()?.data != null) {
                emit(NetworkResult.Success(resp.body()!!.data!!.content))
            } else {
                emit(NetworkResult.Error("Lỗi tải cảm biến nhiệt"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Lỗi mạng"))
        }
    }

    fun getTempHistory(roomId: Long, start: String, end: String): Flow<NetworkResult<List<TempHistory>>> = flow {
        try {
            val resp = api.getTempHistory(roomId, start, end)
            emit(handleResponse(resp))
        } catch (e: Exception) {
            emit(NetworkResult.Error("Lỗi tải biểu đồ"))
        }
    }

    // --- POWER ---
    // Tương tự như Temperature (bạn có thể tự viết dựa trên mẫu trên)
    fun getPowerSensors(roomId: Long): Flow<NetworkResult<List<PowerSensor>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val resp = api.getPowerSensors(roomId)
            if (resp.isSuccessful && resp.body()?.data != null) {
                emit(NetworkResult.Success(resp.body()!!.data!!.content))
            } else {
                emit(NetworkResult.Error("Lỗi tải cảm biến điện"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Lỗi mạng"))
        }
    }

    fun getPowerHistory(roomId: Long, start: String, end: String): Flow<NetworkResult<List<PowerHistory>>> = flow {
        try {
            val resp = api.getPowerHistory(roomId, start, end)
            emit(handleResponse(resp))
            // Lưu ý: data trả về có thể null nếu API chưa có dữ liệu lịch sử
            // Cần xử lý null safety ở đây hoặc ở UI
            if (resp.isSuccessful && resp.body()?.data != null) {
                emit(NetworkResult.Success(resp.body()!!.data!!))
            } else {
                emit(NetworkResult.Error("Không có dữ liệu lịch sử"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error("Lỗi tải biểu đồ"))
        }
    }
    suspend fun getRoomName(roomId: Long): String {
        return try {
            val response = api.getRoomDetail(roomId)
            if (response.isSuccessful && response.body()?.data != null) {
                response.body()!!.data!!.name
            } else {
                "Phòng $roomId" // Fallback nếu không lấy được tên
            }
        } catch (e: Exception) {
            "Phòng $roomId"
        }
    }
    // --- FLOORS ---
    fun getFloors(): Flow<NetworkResult<List<Floor>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val resp = api.getFloors()
            if (resp.isSuccessful && resp.body()?.data != null) {
                // Trả về danh sách tầng từ server
                emit(NetworkResult.Success(resp.body()!!.data!!.content))
            } else {
                val msg = resp.body()?.message ?: "Lỗi tải danh sách tầng"
                emit(NetworkResult.Error(msg))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error("Lỗi kết nối: ${e.message}"))
        }
    }
}