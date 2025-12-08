package com.seiuh.smartroomapp.data.model

import com.patrykandpatrick.vico.core.entry.FloatEntry

/**
 * DTO cho dữ liệu biểu đồ (Backend trả về).
 * Chứa MỘT danh sách các điểm dữ liệu (cho 1 sensor).
 */
typealias ChartSeriesData = List<FloatEntry>

/**
 * DTO cho dữ liệu biểu đồ (Backend trả về).
 * Chứa NHIỀU danh sách các điểm dữ liệu (cho nhiều sensor).
 */
typealias MultiSeriesChartData = List<List<FloatEntry>>