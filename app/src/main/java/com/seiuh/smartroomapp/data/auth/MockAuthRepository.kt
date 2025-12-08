package com.seiuh.smartroomapp.data.auth

import kotlinx.coroutines.delay
/**
 * Phiên bản Mock của AuthRepository để phát triển và demo.
 */
class MockAuthRepository : AuthenticateRepository {

    override suspend fun login(username: String, password: String): LoginResult {
        // Giả lập một cuộc gọi mạng
        delay(1500)

        // Logic mock đơn giản
        return if (username == "admin" && password == "admin") {
            LoginResult.Success(username = "admin")
        } else if (username.isEmpty() || password.isEmpty()) {
            LoginResult.Error("Username and password cannot be empty.")
        } else {
            LoginResult.Error("Invalid username or password.")
        }
    }
}