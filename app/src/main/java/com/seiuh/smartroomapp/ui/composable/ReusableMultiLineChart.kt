package com.seiuh.smartroomapp.ui.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
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
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.line.LineChart.LineSpec
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import java.util.Locale

// Các màu mặc định cho các đường
val chartLineColors = listOf(
    Color(0xFFE63946), // Red
    Color(0xFF457B9D), // Blue
    Color(0xFF52B69A), // Green
    Color(0xFFFCA311), // Yellow
    Color(0xFF9D4EDD)  // Purple
)
// Lấy màu dựa trên index (để đồng bộ giữa Chart và List)
fun getChartColor(index: Int): Color {
    val colors = listOf(
        Color(0xFFE63946), // Đỏ
        Color(0xFF457B9D), // Xanh đậm
        Color(0xFF52B69A), // Xanh lục
        Color(0xFFFCA311), // Vàng
        Color(0xFF9D4EDD), // Tím
        Color(0xFFF4A261)  // Cam
    )
    return colors[index % colors.size]
}
/**
 * Component biểu đồ có khả năng vẽ NHIỀU đường (series) trên cùng một biểu đồ.
 *
 * @param title Tiêu đề biểu đồ.
 * @param chartModel Model dữ liệu (chứa NHIỀU series).
 * @param isLoading Trạng thái tải.
 * @param lineColors Danh sách các màu. Mỗi màu tương ứng với một series.
 */
@Composable
fun ReusableMultiLineChart(
    title: String,
    chartModel: ChartEntryModel?,
    isLoading: Boolean,
    bottomAxisLabels: List<String> = emptyList(),
    modifier: Modifier = Modifier,
    xStep: Int = 12,
    lineColors: List<Color> = chartLineColors // Dùng danh sách màu mặc định
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp), // Chiều cao cố định cho biểu đồ
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else if (chartModel != null) {

                    val xAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
                        val index = value.toInt()
                        // Trả về nhãn nếu index hợp lệ, ngược lại trả về rỗng
                        bottomAxisLabels.getOrNull(index) ?: ""
                    }
                    val yAxisValueFormatter = AxisValueFormatter<AxisPosition.Vertical.Start> { value, _ ->
                        String.format(Locale.US, "%.1f", value)
                    }
                    val bottomAxis = rememberBottomAxis(
                        valueFormatter = xAxisValueFormatter,
                        itemPlacer = AxisItemPlacer.Horizontal.default(
                            spacing = xStep, // Chỉ hiện nhãn mỗi `xStep` điểm
                            offset = 0,
                            shiftExtremeTicks = true
                        )
                        // Để tạo khoảng cách (gap), Vico tự động tính toán dựa trên độ rộng.
                        // Tuy nhiên, nếu bạn muốn ép buộc hiển thị thưa hơn, có thể dùng labelSpacing (phiên bản Vico mới).
                        // Với setup hiện tại, ta sẽ format dữ liệu đầu vào để đạt được mục đích.
                    )
                    val startAxis = rememberStartAxis(valueFormatter = yAxisValueFormatter)

                    // Tạo danh sách LineSpec từ danh sách màu
                    val lines = lineColors.map { color ->
                        LineSpec(lineColor = color.hashCode(), lineBackgroundShader = null)
                    }

                    ProvideChartStyle(chartStyle = currentChartStyle) {
                        Chart(
                            chart = lineChart(
                                lines = lines // Truyền danh sách LineSpec
                            ),
                            model = chartModel, // Model này phải chứa nhiều series
                            startAxis = startAxis,
                            bottomAxis = bottomAxis,
                            chartScrollState = rememberChartScrollState(),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    Text("No data available.")
                }
            }
        }
    }
}