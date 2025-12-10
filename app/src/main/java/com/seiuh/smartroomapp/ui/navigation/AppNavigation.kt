package com.seiuh.smartroomapp.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.seiuh.smartroomapp.data.local.UserPreferences
import com.seiuh.smartroomapp.data.repository.SmartHomeRepository
import com.seiuh.smartroomapp.ui.SmartViewModelFactory
import com.seiuh.smartroomapp.ui.screen.home.HomeScreen
import com.seiuh.smartroomapp.ui.screen.home.HomeViewModel
import com.seiuh.smartroomapp.ui.screen.light.LightsScreenV2
import com.seiuh.smartroomapp.ui.screen.login.LoginScreenV3
import com.seiuh.smartroomapp.ui.screen.login.LoginViewModel
import com.seiuh.smartroomapp.ui.screen.power.PowerScreenV2
import com.seiuh.smartroomapp.ui.screen.roomdetail.RoomDetailScreen
import com.seiuh.smartroomapp.ui.screen.roomdetail.RoomDetailViewModel
import com.seiuh.smartroomapp.ui.screen.temperature.TemperatureScreenV2
import androidx.compose.ui.Modifier
import com.seiuh.smartroomapp.ui.screen.chartdetail.ChartDetailScreen
import com.seiuh.smartroomapp.ui.screen.chartdetail.ChartDetailViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    paddingValues: PaddingValues,
    repository: SmartHomeRepository,
    userPrefs: UserPreferences
) {

    NavHost(
        navController = navController,
        startDestination = "login" ,
        //modifier = Modifier.padding(paddingValues)
    ) {
        composable("login") {
            // Khởi tạo ViewModel dùng Factory chung
            val viewModel: LoginViewModel = viewModel(
                factory = SmartViewModelFactory(repository, userPrefs)
            )
            LoginScreenV3(navController = navController, viewModel = viewModel)
        }
        // 2. Home / Dashboard
        composable("home") {
            // We need a HomeScreen ViewModel
            val viewModel: HomeViewModel = viewModel(factory = SmartViewModelFactory(repository))

            HomeScreen(navController, viewModel, paddingValues)
        }
        // 3. Các màn hình Placeholder cho Bottom Bar
        composable("equipment") {
            // Tạm thời dùng HomeScreen hoặc tạo màn hình rỗng để test
            androidx.compose.material3.Text("Equipment Screen")
        }
        composable("properties") {
            androidx.compose.material3.Text("Properties Screen")
        }
        // Room Detail
        composable(
            route = "room/{roomId}",
            arguments = listOf(navArgument("roomId") { type = NavType.LongType })
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getLong("roomId") ?: 0L
            val viewModel: RoomDetailViewModel = viewModel(
                factory = SmartViewModelFactory(repository, roomId = roomId)
            )
            RoomDetailScreen(navController = navController, viewModel = viewModel)
        }
        // 5. [MỚI] Chart Screen (Dùng chung cho cả Temp và Power, phân biệt bằng type)
        // type: "temp" hoặc "power"
        composable(
            route = "room/{roomId}/chart/{type}",
            arguments = listOf(
                navArgument("roomId") { type = NavType.LongType },
                navArgument("type") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getLong("roomId") ?: 0L
            val type = backStackEntry.arguments?.getString("type") ?: "temp"

            // ViewModel riêng cho Chart
            val viewModel: ChartDetailViewModel = viewModel(
                factory = SmartViewModelFactory(repository, roomId = roomId)
            )

            // Gọi hàm init dựa trên type
            LaunchedEffect(type) { viewModel.setType(type) }

            ChartDetailScreen(navController = navController, viewModel = viewModel, chartType = type)
        }
    }
}