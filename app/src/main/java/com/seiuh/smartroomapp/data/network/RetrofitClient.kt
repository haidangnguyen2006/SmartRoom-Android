package com.seiuh.smartroomapp.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // Thay đổi IP này thành IP máy tính chạy server của bạn (VD: 192.168.1.X)
    private var baseUrl = "http://192.168.2.29:8080/api/v1/"

    var authToken: String? = null
    // Biến lưu instance hiện tại
    @Volatile
    private var retrofit: Retrofit? = null

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()

            // Tự động gắn Token nếu đã đăng nhập
            authToken?.let {
                requestBuilder.header("Authorization", "Bearer $it")
            }

            chain.proceed(requestBuilder.build())
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    // --- HÀM CHUẨN HÓA URL THÔNG MINH ---
    private fun normalizeUrl(input: String): String {
        var url = input.trim()

        // 1. Kiểm tra Protocol (http/https)
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://$url"
        }

        // 2. Kiểm tra Port (:8080)
        // Logic: Lấy phần host (đoạn giữa http:// và dấu / đầu tiên) để check xem có dấu ":" chưa
        val protocol = if (url.startsWith("https://")) "https://" else "http://"
        val textAfterProtocol = url.substringAfter(protocol)

        // Lấy host (ví dụ: "192.168.1.5" hoặc "192.168.1.5:3000/api...")
        val hostPart = if (textAfterProtocol.contains("/")) {
            textAfterProtocol.substringBefore("/")
        } else {
            textAfterProtocol
        }

        // Nếu host chưa có dấu hai chấm (tức là chưa có port), thì thêm :8080
        if (!hostPart.contains(":")) {
            // Thay thế host cũ bằng host mới có port
            // Cẩn thận chỉ thay thế đoạn đầu để không ảnh hưởng các tham số phía sau (nếu có)
            val newHostPart = "$hostPart:443"
            url = url.replaceFirst(hostPart, newHostPart)
        }

        // 3. Kiểm tra Base Path (/api/v1/)
        // Nếu URL chưa chứa "/api/v1", thì nối thêm vào
        /*if (!url.contains("/api/v1")) {
            url = if (url.endsWith("/")) {
                "${url}api/v1/"
            } else {
                "${url}/api/v1/"
            }
        }*/

        // 4. Đảm bảo luôn kết thúc bằng dấu "/" (Yêu cầu bắt buộc của Retrofit)
        if (!url.endsWith("/")) {
            url += "/"
        }

        return url
    }

    // --- CẤU HÌNH URL ---
    fun configureBaseUrl(rawUrl: String) {
        val formattedUrl = normalizeUrl(rawUrl)

        if (baseUrl != formattedUrl) {
            baseUrl = formattedUrl
            retrofit = null // Reset để tạo instance mới với URL mới
        }
    }
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
    fun clearToken() {
        authToken = null
    }
}