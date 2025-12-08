package com.seiuh.smartroomapp.ui.screen.roomdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.seiuh.smartroomapp.ui.composable.CleanCard
import com.seiuh.smartroomapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomDetailScreen(
    navController: NavController,
    // Pass ViewModel instance from NavGraph
    viewModel: RoomDetailViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(uiState.roomName, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = AppBackground)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // 1. Temperature Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "${uiState.currentTemp?.toInt() ?: "--"}",
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "°C",
                        fontSize = 32.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }

            // 2. Device List (Lights)
            items(uiState.lights) { light ->
                DeviceItemRow(
                    name= light.name,
                    isOn= light.isActive,
                    onToggle= { viewModel.onLightToggle(light.id, it) }
                )
            }
        }
    }
}

@Composable
fun DeviceItemRow(
    name: String,
    isOn: Boolean,
    onToggle: (Boolean) -> Unit
) {
    CleanCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isOn) PrimaryPurple else SurfaceLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Lightbulb,
                        null,
                        tint = if (isOn) androidx.compose.ui.graphics.Color.White else TextSecondary
                    )
                }
                Spacer(Modifier.width(16.dp))
                Text(name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }

            Switch(
                checked = isOn,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedTrackColor = PrimaryPurple
                )
            )
        }
    }
}