package com.seiuh.smartroomapp.ui.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.compose.style.currentChartStyle
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import androidx.compose.foundation.layout.height
import com.patrykandpatrick.vico.core.chart.line.LineChart.LineSpec
import java.util.Locale
/**
 * Một component Card có thể tái sử dụng để hiển thị một biểu đồ đường (line chart) duy nhất.
 *
 * @param title Tiêu đề hiển thị phía trên biểu đồ.
 * @param chartModel Model dữ liệu (chỉ chứa MỘT series) để Vico vẽ.
 * @param isLoading Trạng thái đang tải.
 * @param modifier Modifier cho Card.
 */
@Composable
fun ReusableSmartChart(
    title: String,
    chartModel: ChartEntryModel?,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    lineColor: Color
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 1. Tiêu đề
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 2. Hộp chứa biểu đồ (để hiển thị loading)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Đảm bảo Box lấp đầy không gian còn lại
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else if (chartModel != null) {
                    // Tạo formatter cho trục hoành (thời gian - gap 1h)
                    val xAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
                        val hour = value.toInt() % 24
                        String.format(Locale.US, "%02d:00", hour)
                    }

                    // Tạo formatter cho trục tung (giá trị)
                    val yAxisValueFormatter = AxisValueFormatter<AxisPosition.Vertical.Start> { value, _ ->
                        String.format(Locale.US, "%.1f", value)
                    }

                    // Cấu hình trục hoành (X-axis - thời gian)
                    val bottomAxis = rememberBottomAxis(
                        valueFormatter = xAxisValueFormatter
                    )

                    // Cấu hình trục tung (Y-axis - giá trị)
                    val startAxis = rememberStartAxis(
                        valueFormatter = yAxisValueFormatter
                    )

                    ProvideChartStyle(chartStyle = currentChartStyle) {
                        Chart(
                            chart = lineChart(
                                lines = listOf(
                                    LineSpec(
                                        lineColor = lineColor.hashCode(), // Màu vàng
                                        lineBackgroundShader = null
                                    )
                                )
                            ),
                            model = chartModel,
                            startAxis = startAxis,
                            bottomAxis = bottomAxis,
                            chartScrollState = rememberChartScrollState(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        )
                    }
                } else {
                    Text("No data available.")
                }
            }
        }
    }
}