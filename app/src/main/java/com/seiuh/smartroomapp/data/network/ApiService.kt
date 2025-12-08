package com.seiuh.smartroomapp.data.network

import com.seiuh.smartroomapp.data.model.auth.*
import com.seiuh.smartroomapp.data.model.base.*
import com.seiuh.smartroomapp.data.model.device.*
import com.seiuh.smartroomapp.data.model.history.*
import com.seiuh.smartroomapp.data.model.structure.Floor
import com.seiuh.smartroomapp.data.model.structure.Room
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- AUTH ---
    @POST("auth/signin")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginResponse>>

    // --- ROOMS ---
    // Lấy danh sách phòng của 1 tầng (Ví dụ tầng 1)
    @GET("floors/{floorId}/rooms")
    suspend fun getRoomsByFloor(
        @Path("floorId") floorId: Long,
        @Query("size") size: Int = 100
    ): Response<ApiResponse<PagedResponse<Room>>>

    // --- LIGHTS ---
    @GET("lights/room/{roomId}")
    suspend fun getLights(
        @Path("roomId") roomId: Long,
        @Query("size") size: Int = 100
    ): Response<ApiResponse<PagedResponse<Light>>>

    @PUT("lights/{id}/toggle-state")
    suspend fun toggleLight(@Path("id") id: Long): Response<ApiResponse<Light>>

    // --- TEMPERATURE ---
    @GET("rooms/{roomId}/temperatures")
    suspend fun getTempSensors(
        @Path("roomId") roomId: Long
    ): Response<ApiResponse<PagedResponse<TempSensor>>>

    @GET("rooms/{roomId}/temperatures/average-history")
    suspend fun getTempHistory(
        @Path("roomId") roomId: Long,
        @Query("startedAt") startedAt: String,
        @Query("endedAt") endedAt: String
    ): Response<ApiResponse<List<TempHistory>>>

    // --- POWER ---
    @GET("rooms/{roomId}/power-consumptions")
    suspend fun getPowerSensors(
        @Path("roomId") roomId: Long
    ): Response<ApiResponse<PagedResponse<PowerSensor>>>

    @GET("rooms/{roomId}/power-consumptions/average-history")
    suspend fun getPowerHistory(
        @Path("roomId") roomId: Long,
        @Query("startedAt") startedAt: String,
        @Query("endedAt") endedAt: String
    ): Response<ApiResponse<List<PowerHistory>>>

    //Lấy thông tin chi tiết 1 phòng
    @GET("rooms/{id}")
    suspend fun getRoomDetail(
        @Path("id") roomId: Long
    ): Response<ApiResponse<Room>>

    // --- FLOORS ---
    @GET("floors")
    suspend fun getFloors(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 100 // Lấy tối đa 100 tầng
    ): Response<ApiResponse<PagedResponse<Floor>>>

}