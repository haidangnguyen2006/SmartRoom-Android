package com.seiuh.smartroomapp.data.di

import com.seiuh.smartroomapp.data.repository.SmartHomeRepository

/**
 * Một object đơn giản để "cung cấp" (provide) repository cho các ViewModel.
 * Khi chuyển sang API thật, bạn chỉ cần thay đổi 1 dòng ở đây.
 */
object AppContainer {
    // *** ĐÂY LÀ CHỖ ĐỂ TRÁO ĐỔI DATA SAU NÀY ***
    //
    // Dùng dòng này cho Demo:
    val repository: SmartHomeRepository = SmartHomeRepository()
    //
    // Dùng dòng này cho Production (sau khi bạn tạo class ApiSmartHomeRepository):
    // val repository: SmartHomeRepository = ApiSmartHomeRepository(RetrofitInstance.api)
}