package com.seiuh.smartroomapp.ui.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.compose.component.shape.shader.verticalGradient
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.compose.style.currentChartStyle
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis // [FIX 1] Import đúng vị trí enum
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.shape.LineComponent // [FIX 2] Import LineComponent cho Guideline
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.component.text.TextComponent
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.views.component.shape.shader.verticalGradient
import com.seiuh.smartroomapp.ui.theme.*
import java.util.Locale
import com.patrykandpatrick.vico.core.chart.DefaultPointConnector

// Bảng màu hiện đại
val professionalChartColors = listOf(
    PrimaryPurple,
    AccentPink,
    Color(0xFF4FC3F7),
    Color(0xFFFFD54F),
    Color(0xFF2ECC71)
)

@Composable
fun  ReusableMultiLineChart(
    title: String,
    chartModel: ChartEntryModel?,
    isLoading: Boolean,
    bottomAxisLabels: List<String> = emptyList(),
    modifier: Modifier = Modifier,
    xStep: Int = 24,
    lineColors: List<Color> = professionalChartColors
) {
    CleanCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = PrimaryPurple)
                } else if (chartModel != null) {

                    // --- TRỤC X (Gap 2h) ---
                    val xAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
                        val index = value.toInt()
                        bottomAxisLabels.getOrNull(index) ?: ""
                    }

                    val bottomAxis = rememberBottomAxis(
                        valueFormatter = xAxisValueFormatter,
                        // Điều chỉnh khoảng cách hiển thị nhãn
                        itemPlacer = AxisItemPlacer.Horizontal.default(
                            spacing = xStep, // Cách nhau xStep điểm mới hiện 1 nhãn
                            offset = 0,
                            shiftExtremeTicks = true
                        ),
                        guideline = null,
                        tick = null,
                        label = TextComponent.Builder().apply {
                            color = TextSecondary.toArgb()
                            textSizeSp = 11f // Tăng size chữ chút cho dễ đọc
                        }.build()
                    )

                    // --- TRỤC Y ---
                    val yAxisValueFormatter = AxisValueFormatter<AxisPosition.Vertical.Start> { value, _ ->
                        String.format(Locale.US, "%.0f", value)
                    }

                    val startAxis = rememberStartAxis(
                        valueFormatter = yAxisValueFormatter,
                        // [FIX 1]: Sử dụng VerticalAxis.HorizontalLabelPosition
                        horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Inside,
                        label = TextComponent.Builder().apply {
                            color = TextSecondary.toArgb()
                            textSizeSp = 10f
                        }.build(),
                        // [FIX 2]: Dùng LineComponent cho guideline thay vì ShapeComponent
                        guideline = LineComponent(
                            color = SurfaceLight.toArgb(),
                            thicknessDp = 0.5f
                        )
                    )

                    // --- LINE SPECS ---
                    val lines = lineColors.map { color ->
                        LineChart.LineSpec(
                            lineColor = color.toArgb(),
                            lineThicknessDp = 3f,
                            lineBackgroundShader = DynamicShaders.verticalGradient(
                                intArrayOf(
                                    color.copy(alpha = 0.4f).toArgb(),
                                    color.copy(alpha = 0.0f).toArgb()
                                )
                            ),
                            lineCap = android.graphics.Paint.Cap.ROUND,

                            //Làm mềm đường vẽ (Curved Line)
                            pointConnector = DefaultPointConnector(
                                cubicStrength = 0.2f // Độ cong (0.0 là thẳng, 1.0 là rất cong)
                            )
                        )
                    }

                    ProvideChartStyle(chartStyle = currentChartStyle) {
                        Chart(
                            chart = lineChart(lines = lines),
                            model = chartModel,
                            startAxis = startAxis,
                            bottomAxis = bottomAxis,
                            chartScrollState = rememberChartScrollState(),
                            modifier = Modifier.fillMaxSize(),
                            marker = rememberMarker()
                        )
                    }
                } else {
                    Text("No data available.", color = TextSecondary)
                }
            }
        }
    }
}