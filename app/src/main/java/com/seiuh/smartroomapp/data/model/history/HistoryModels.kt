package com.seiuh.smartroomapp.data.model.history

import com.google.gson.annotations.SerializedName

//
data class TempHistory(
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("avgTempC") val avgTempC: Double
)

//
data class PowerHistory(
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("avgWatt") val avgWatt: Double? = null,
    @SerializedName("avgWattHour") val avgWattHour: Double? = null
)