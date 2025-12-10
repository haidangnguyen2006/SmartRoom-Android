package com.seiuh.smartroomapp.ui.composable

import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.core.chart.dimensions.HorizontalDimensions
import com.patrykandpatrick.vico.core.chart.insets.Insets
import com.patrykandpatrick.vico.core.component.OverlayingComponent
import com.patrykandpatrick.vico.core.component.marker.MarkerComponent
import com.patrykandpatrick.vico.core.component.shape.LineComponent // [MỚI] Dùng cái này cho Guideline
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.shape.cornered.Corner
import com.patrykandpatrick.vico.core.component.shape.cornered.MarkerCorneredShape
import com.patrykandpatrick.vico.core.context.MeasureContext
import com.patrykandpatrick.vico.core.marker.Marker
import com.seiuh.smartroomapp.ui.theme.PrimaryPurple
import com.seiuh.smartroomapp.ui.theme.SurfaceWhite

@Composable
fun rememberMarker(): Marker {
    // Định nghĩa các thông số Shadow để dùng lại khi tính toán
    val shadowRadiusDp = 4f
    val shadowDyDp = 2f

    // 1. Label Background
    val labelBackground = shapeComponent(
        shape = MarkerCorneredShape(Corner.FullyRounded),
        color = SurfaceWhite
    ).setShadow(
        radius = shadowRadiusDp,
        dy = shadowDyDp,
        color = Color.Black.copy(alpha = 0.2f).toArgb()
    )

    // 2. Label
    val label = textComponent(
        color = Color.Black,
        background = labelBackground,
        lineCount = 1,
        padding = dimensionsOf(8.dp, 4.dp),
        typeface = Typeface.MONOSPACE
    )

    // 3. Indicator
    val indicatorOuter = shapeComponent(
        shape = Shapes.pillShape,
        color = PrimaryPurple
    )

    val indicatorInner = shapeComponent(
        shape = Shapes.pillShape,
        color = Color.White
    )

    val indicator = remember(indicatorOuter, indicatorInner) {
        OverlayingComponent(
            outer = indicatorOuter,
            inner = indicatorInner,
            innerPaddingAllDp = 2f // 2dp viền trắng
        )
    }

    // 4. Guideline (Đường kẻ dọc)
    // [FIX LỖI 1]: Dùng LineComponent thay vì ShapeComponent
    val guideline = LineComponent(
        color = Color.LightGray.copy(alpha = 0.5f).toArgb(),
        thicknessDp = 1f
    )

    return remember(label, indicator, guideline) {
        object : MarkerComponent(label, indicator, guideline) {
            init {
                indicatorSizeDp = 12f
                onApplyEntryColor = { entryColor ->
                    indicatorOuter.color = entryColor
                }
            }

            // [FIX LỖI 2]: Tính toán Shadow thủ công thay vì gọi property
            override fun getInsets(
                context: MeasureContext,
                outInsets: Insets,
                horizontalDimensions: HorizontalDimensions,
            ) {
                with(context) {
                    val shadowRadiusPx = shadowRadiusDp.pixels
                    val shadowDyPx = shadowDyDp.pixels

                    outInsets.top = label.getHeight(context) + shadowRadiusPx * 1.3f - shadowDyPx
                }
            }
        }
    }
}