package com.seiuh.smartroomapp.data.model.base

import com.google.gson.annotations.SerializedName

// Wrapper cho dữ liệu dạng danh sách có phân trang
data class PagedResponse<T>(
    @SerializedName("content") val content: List<T>,
    @SerializedName("page") val page: Int,
    @SerializedName("size") val size: Int,
    @SerializedName("totalElements") val totalElements: Long,
    @SerializedName("totalPages") val totalPages: Int
)