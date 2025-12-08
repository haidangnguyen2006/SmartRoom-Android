package com.seiuh.smartroomapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Devices
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    object Equipment : BottomNavItem("equipment", Icons.Default.Devices, "Equipment")
    object Properties : BottomNavItem("properties", Icons.Default.Settings, "Properties")
}