package com.seiuh.smartroomapp.ui.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.seiuh.smartroomapp.ui.theme.TextGrayV2
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Một Composable để chọn khoảng ngày cho việc hiển thị dữ liệu biểu đồ.
 * Cho phép chọn ngày bắt đầu và ngày kết thúc.
 *
 * @param startDate Ngày bắt đầu đã chọn
 * @param endDate Ngày kết thúc đã chọn
 * @param onStartDateChange Callback khi ngày bắt đầu thay đổi
 * @param onEndDateChange Callback khi ngày kết thúc thay đổi
 * @param modifier Modifier cho toàn bộ composable
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeSelector(
    startDate: LocalDate,
    endDate: LocalDate,
    onStartDateChange: (LocalDate) -> Unit,
    onEndDateChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.US)

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    // State cho DatePicker
    val startDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = startDate.toEpochDay() * 24 * 60 * 60 * 1000
    )
    val endDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = endDate.toEpochDay() * 24 * 60 * 60 * 1000
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Tiêu đề
        Text(
            text = "Chọn ngày hiển thị",
            style = MaterialTheme.typography.labelSmall,
            color = TextGrayV2,
            modifier = Modifier.padding(start = 0.dp)
        )

        // Hàng chứa hai trường ngày
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Trường ngày bắt đầu
            OutlinedTextField(
                value = startDate.format(dateFormatter),
                onValueChange = {},
                readOnly = true,
                label = { Text("Ngày bắt đầu") },
                leadingIcon = {
                    Icon(Icons.Default.Event, contentDescription = "Ngày bắt đầu")
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                textStyle = MaterialTheme.typography.bodyMedium,
                trailingIcon = {
                    if (startDate != LocalDate.now()) {
                        IconButton(onClick = { onStartDateChange(LocalDate.now()) }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                }
            )

            // Nút mở DatePicker cho ngày bắt đầu
            Button(
                onClick = { showStartDatePicker = true },
                modifier = Modifier.size(56.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(Icons.Default.Event, contentDescription = "Pick Start Date")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Trường ngày kết thúc
            OutlinedTextField(
                value = endDate.format(dateFormatter),
                onValueChange = {},
                readOnly = true,
                label = { Text("Ngày kết thúc") },
                leadingIcon = {
                    Icon(Icons.Default.Event, contentDescription = "Ngày kết thúc")
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                textStyle = MaterialTheme.typography.bodyMedium,
                trailingIcon = {
                    if (endDate != LocalDate.now()) {
                        IconButton(onClick = { onEndDateChange(LocalDate.now()) }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                }
            )

            // Nút mở DatePicker cho ngày kết thúc
            Button(
                onClick = { showEndDatePicker = true },
                modifier = Modifier.size(56.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(Icons.Default.Event, contentDescription = "Pick End Date")
            }
        }
    }

    // DatePicker Dialog cho ngày bắt đầu
    if (showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        val selectedMillis = startDatePickerState.selectedDateMillis ?: return@Button
                        val selectedDate = LocalDate.ofEpochDay(selectedMillis / (24 * 60 * 60 * 1000))
                        onStartDateChange(selectedDate)
                        showStartDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showStartDatePicker = false }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = startDatePickerState)
        }
    }

    // DatePicker Dialog cho ngày kết thúc
    if (showEndDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        val selectedMillis = endDatePickerState.selectedDateMillis ?: return@Button
                        val selectedDate = LocalDate.ofEpochDay(selectedMillis / (24 * 60 * 60 * 1000))
                        onEndDateChange(selectedDate)
                        showEndDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showEndDatePicker = false }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = endDatePickerState)
        }
    }
}
/*@Preview(showBackground = true)
@Composable
fun DateRangeSelectorPreview() {
    DateRangeSelector(
        startDate = LocalDate.now().minusDays(7),
        endDate = LocalDate.now(),
        onStartDateChange = {},
        onEndDateChange = {}
    )
}*/
