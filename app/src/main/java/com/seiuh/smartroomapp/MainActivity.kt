package com.seiuh.smartroomapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.seiuh.smartroomapp.data.local.UserPreferences
import com.seiuh.smartroomapp.data.repository.SmartHomeRepository
import com.seiuh.smartroomapp.ui.navigation.AppNavigation
import com.seiuh.smartroomapp.ui.navigation.BottomNavItem
import com.seiuh.smartroomapp.ui.theme.PrimaryPurple
import com.seiuh.smartroomapp.ui.theme.SmartRoomThemeV4
import com.seiuh.smartroomapp.ui.theme.SurfaceWhite
import com.seiuh.smartroomapp.ui.theme.TextSecondary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Khởi tạo dependencies
        val repository = SmartHomeRepository()
        val userPrefs = UserPreferences(applicationContext)

        setContent {
            SmartRoomThemeV4 {
                val navController = rememberNavController()

                // Theo dõi màn hình hiện tại để Ẩn/Hiện BottomBar
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Danh sách các màn hình CÓ hiển thị BottomBar
                val showBottomBar = currentRoute in listOf(
                    BottomNavItem.Home.route,
                    BottomNavItem.Equipment.route,
                    BottomNavItem.Properties.route
                )

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            // Thiết kế Bottom Bar theo style Clean Modern
                            NavigationBar(
                                containerColor = SurfaceWhite,
                                tonalElevation = 8.dp
                            ) {
                                val items = listOf(
                                    BottomNavItem.Home,
                                    BottomNavItem.Equipment,
                                    BottomNavItem.Properties
                                )

                                items.forEach { item ->
                                    NavigationBarItem(
                                        icon = { Icon(item.icon, contentDescription = item.label) },
                                        label = { Text(item.label) },
                                        selected = currentRoute == item.route,
                                        onClick = {
                                            navController.navigate(item.route) {
                                                // Pop up to the start destination of the graph to
                                                // avoid building up a large stack of destinations
                                                popUpTo("home") { saveState = true }
                                                // Avoid multiple copies of the same destination
                                                launchSingleTop = true
                                                // Restore state when reselecting a previously selected item
                                                restoreState = true
                                            }
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = PrimaryPurple, // Màu tím hiện đại
                                            selectedTextColor = PrimaryPurple,
                                            indicatorColor = PrimaryPurple.copy(alpha = 0.1f), // Nền icon khi chọn
                                            unselectedIconColor = TextSecondary,
                                            unselectedTextColor = TextSecondary
                                        )
                                    )
                                }
                            }
                        }
                    }
                ) { paddingValues ->
                    // Truyền paddingValues xuống để nội dung không bị BottomBar che khuất
                    AppNavigation(
                        navController = navController,
                        paddingValues = paddingValues,
                        repository = repository,
                        userPrefs = userPrefs
                    )
                }
            }
        }
    }
}

