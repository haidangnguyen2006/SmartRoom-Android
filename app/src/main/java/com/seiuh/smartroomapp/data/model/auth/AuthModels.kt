package com.seiuh.smartroomapp.data.model.auth

import com.google.gson.annotations.SerializedName

//
data class LoginRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

//
data class LoginResponse(
    @SerializedName("token") val token: String,
    @SerializedName("type") val type: String, // "Bearer"
    @SerializedName("username") val username: String,
    @SerializedName("roles") val roles: List<String>
)