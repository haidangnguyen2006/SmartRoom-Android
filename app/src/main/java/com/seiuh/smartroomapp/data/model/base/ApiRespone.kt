package com.seiuh.smartroomapp.data.model.base

import com.google.gson.annotations.SerializedName

// Wrapper chung cho mọi phản hồi API
data class ApiResponse<T>(
    @SerializedName("status") val status: Int,       // Ví dụ: 200, 201, 400
    @SerializedName("message") val message: String,  // Ví dụ: "Success", "Bad credentials"
    @SerializedName("data") val data: T?,            // Dữ liệu thực tế (Light, Room...), có thể null
    @SerializedName("timestamp") val timestamp: String
)