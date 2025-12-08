package com.seiuh.smartroomapp.data.model.structure

import com.google.gson.annotations.SerializedName

//
data class Room(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("floorId") val floorId: Long
)