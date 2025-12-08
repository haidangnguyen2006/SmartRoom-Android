package com.seiuh.smartroomapp.data.model.structure

import com.google.gson.annotations.SerializedName

//
data class Floor(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("level") val level: Int
)