package com.seiuh.smartroomapp.ui.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.seiuh.smartroomapp.data.model.structure.Floor
import com.seiuh.smartroomapp.ui.theme.BrandBlue
import com.seiuh.smartroomapp.ui.theme.SurfaceWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloorSelector(
    floors: List<Floor>,
    selectedFloor: Floor?,
    onFloorSelected: (Floor) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Tiêu đề nhỏ
        Text(
            text = "Khu vực / Tầng",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Danh sách cuộn ngang
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(floors) { floor ->
                val isSelected = floor.id == selectedFloor?.id

                FilterChip(
                    selected = isSelected,
                    onClick = { onFloorSelected(floor) },
                    label = { Text(floor.name) },
                    leadingIcon = if (isSelected) {
                        { Icon(Icons.Default.Apartment, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BrandBlue,
                        selectedLabelColor = SurfaceWhite,
                        selectedLeadingIconColor = SurfaceWhite
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = Color.Transparent,
                        selectedBorderColor = Color.Transparent
                    )
                )
            }
        }
    }
}