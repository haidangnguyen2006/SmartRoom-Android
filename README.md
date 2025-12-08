# SmartRoom - Android IoT Controller

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)

**SmartRoom** là ứng dụng Android hiện đại giúp quản lý và điều khiển các thiết bị IoT trong nhà thông minh. Ứng dụng được xây dựng hoàn toàn bằng **Kotlin** và **Jetpack Compose**, tuân thủ kiến trúc **MVVM**.
Ứng dụng kết nối với hệ thống backend sử dụng **Jakarta EE** và **MySQL** để cung cấp trải nghiệm người dùng mượt mà và bảo mật.
## 📱 Tính năng chính

* **Dashboard tổng quan:** Hiển thị thông tin thời tiết, nhiệt độ phòng khách và các chế độ nhanh (At Home, Left Home).
* **Quản lý theo phòng:** Danh sách phòng được nhóm theo tầng (Ground Floor, First Floor...).
* **Chi tiết phòng:**
    * Theo dõi nhiệt độ, độ ẩm theo thời gian thực.
    * Điều khiển thiết bị bật/tắt (Đèn, Quạt, Máy lọc không khí).
    * Điều chỉnh mức độ (Level/Brightness) cho thiết bị.
* **Biểu đồ dữ liệu:** Theo dõi lịch sử tiêu thụ điện năng và biến động nhiệt độ (Sử dụng thư viện Vico).
* **Bảo mật:** Đăng nhập, Đăng ký tài khoản người dùng.

## 🛠 Tech Stack & Kiến trúc

### Android Client
* **Ngôn ngữ:** Kotlin.
* **UI Toolkit:** Jetpack Compose (Material Design 3).
* **Layout Strategy:** ConstraintLayout & Standard Compose layouts.
* **Architecture:** MVVM (Model-View-ViewModel) + Repository Pattern.
* **Navigation:** Navigation Compose.
* **Charts:** Vico Chart Library (v1.14.0).
* **Dependency Injection:** Manual DI (Hiện tại) / Hilt (Dự kiến).

### Backend System (Overview)
* **Server:** Ubuntu Server (OpenVPN Secured).
* **Proxy:** Apache HTTPD (Reverse Proxy).
* **Application Server:** Apache Tomcat 10 (Jakarta EE).
* **Database:** MySQL.
* **API Standard:** RESTful API.

## 📂 Cấu trúc dự án

```text
com.seiuh.smartroom
├── data
│   ├── network        # Network Layer (API Service, Retrofit Config...)
│   ├── di             # Dependency Injection (Manual DI hiện tại)
│   ├── model           # Data classes (Room, Device, Temperature, Auth...)
│   └── repository      # Repository Interfaces & Implementations
│       ├── SmartHomeRepository.kt      # Interface chung
│     
├── ui
│   ├── theme           # Color, Type, Shape
│   ├── components      # Các UI nhỏ tái sử dụng (DeviceCard, RoomItem...)
│   ├── screens         # Các màn hình chính
│   │   ├── home        # HomeScreen
│   │   ├── login       # LoginScreen
│   │   └── room        # RoomDetailScreen
│   └── navigation      # NavGraph & Destinations
├── viewmodel           # ViewModel quản lý state cho UI
└── MainActivity.kt