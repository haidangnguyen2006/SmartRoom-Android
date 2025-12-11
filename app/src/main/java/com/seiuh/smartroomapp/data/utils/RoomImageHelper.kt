package com.seiuh.smartroomapp.data.utils

fun getRoomImageUrl(roomName: String): String {
    val safeName = roomName ?: ""
    return when {
        safeName.contains("Living", true) || roomName.contains("Khách", true) ->
            "https://images.unsplash.com/photo-1598928506311-c55ded91a20c?q=80&w=870&auto=format&fit=crop" +
            "&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
        safeName.contains("Bed", true) || roomName.contains("Ngủ", true) ->
            "https://images.unsplash.com/photo-1616594039964-ae9021a400a0?q=80&w=580&auto=format&fit=crop" +
                    "&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
        safeName.contains("Kitchen", true) || roomName.contains("Bếp", true) ->
            "https://images.unsplash.com/photo-1556910096-6f5e72db6803?q=80&w=870&auto=format&fit=crop" +
                    "&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
        safeName.contains("Bath", true) || roomName.contains("Tắm", true) ->
            "https://images.unsplash.com/photo-1552321554-5fefe8c9ef14?q=80&w=1000&auto=format&fit=crop"
        else ->
            "https://images.unsplash.com/photo-1628012209120-d9db7abf7eab?q=80&w=436&auto=format&fit=crop" +
                    "&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
    }
}