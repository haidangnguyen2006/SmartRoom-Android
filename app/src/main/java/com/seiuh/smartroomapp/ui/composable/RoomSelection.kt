package com.seiuh.smartroomapp.ui.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.seiuh.smartroomapp.data.model.structure.Room

/**
 * Một Composable ExposedDropdownMenu để chọn phòng.
 * Có thể tái sử dụng ở bất cứ đâu cần chọn phòng.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomSelection(
    rooms: List<Room>,
    selectedRoom: Room?,
    onRoomSelected: (Room) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Select a room"
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedRoom?.name ?: label,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            rooms.forEach { room ->
                DropdownMenuItem(
                    text = { Text(room.name) },
                    onClick = {
                        onRoomSelected(room)
                        expanded = false
                    }
                )
            }
        }
    }
}