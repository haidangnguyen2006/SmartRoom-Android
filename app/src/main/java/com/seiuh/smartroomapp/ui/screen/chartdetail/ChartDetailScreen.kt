package com.seiuh.smartroomapp.ui.screen.chartdetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.seiuh.smartroomapp.ui.composable.CleanCard
import com.seiuh.smartroomapp.ui.composable.DateRangeSelector
import com.seiuh.smartroomapp.ui.composable.ReusableMultiLineChart
import com.seiuh.smartroomapp.ui.theme.AppBackground
import com.seiuh.smartroomapp.ui.theme.PrimaryPurple
import com.seiuh.smartroomapp.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartDetailScreen(
    navController: NavController,
    viewModel: ChartDetailViewModel,
    chartType: String
) {
    val uiState by viewModel.uiState.collectAsState()
    val title = if (chartType == "temp") "Temperature History" else "Power Consumption"

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = AppBackground)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Date Range
            CleanCard {
                DateRangeSelector(
                    startDate = uiState.startDate,
                    endDate = uiState.endDate,
                    onStartDateChange = { viewModel.onDateChange(it, uiState.endDate) }, // Cần sửa DateRangeSelector để support update từng cái
                    onEndDateChange = { viewModel.onDateChange(uiState.startDate, it) }
                )
            }

            // 2. Chart
            ReusableMultiLineChart(
                title = "Analytics",
                chartModel = uiState.chartModel,
                bottomAxisLabels = uiState.chartLabels,
                xStep = 2,
                isLoading = uiState.isLoading,
                modifier = Modifier.fillMaxWidth().height(400.dp)
            )

            // 3. Sensor List (Checkbox)
            Text("Select Sensors", style = MaterialTheme.typography.titleMedium, color = TextPrimary)

            LazyColumn {
                items(uiState.sensors) { sensor ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(sensor.name, style = MaterialTheme.typography.bodyMedium)
                        Checkbox(
                            checked = sensor.isSelected,
                            onCheckedChange = { viewModel.toggleSensor(sensor.id, it) },
                            colors = CheckboxDefaults.colors(checkedColor = PrimaryPurple)
                        )
                    }
                }
            }
        }
    }
}