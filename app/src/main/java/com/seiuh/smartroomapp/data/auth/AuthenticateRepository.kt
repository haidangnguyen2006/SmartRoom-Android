package com.seiuh.smartroomapp.data.auth

// Sử dụng một sealed class để quản lý các kết quả login một cách rõ ràng
sealed class LoginResult {
    data class Success(val username: String) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

/**
 * Interface cho Authentication Repository.
 * Cho phép tráo đổi giữa Mock và API thật.
 */
interface AuthenticateRepository {
    /**
     * Cố gắng đăng nhập người dùng.
     * Trong tương lai, khi dùng API thật, LoginResult.Success sẽ chứa
     * Access Token và Refresh Token (JWT).
     */
    suspend fun login(username: String, password: String): LoginResult
}