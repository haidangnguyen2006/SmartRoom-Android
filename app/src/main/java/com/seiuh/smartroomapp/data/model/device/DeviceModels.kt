package com.seiuh.smartroomapp.data.model.device

import com.google.gson.annotations.SerializedName
//
data class Light(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("isActive") val isActive: Boolean,
    @SerializedName("level") val level: Int,
    @SerializedName("roomId") val roomId: Long
)

//
data class TempSensor(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("currentValue") val currentValue: Double?, // Có thể null
    @SerializedName("roomId") val roomId: Long
)

//
data class PowerSensor(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("currentWatt") val currentWatt: Double?,
    @SerializedName("currentWattHour") val currentWattHour: Double?,
    @SerializedName("roomId") val roomId: Long
)