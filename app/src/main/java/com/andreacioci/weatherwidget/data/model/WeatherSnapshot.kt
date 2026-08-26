package com.andreacioci.weatherwidget.data.model

import java.time.LocalDateTime

data class HourlyForecast(
    val time: LocalDateTime,
    val temperatureCelsius: Double,
    val category: WeatherCategory,
)

data class WeatherSnapshot(
    val locationName: String,
    val currentTemperatureCelsius: Double,
    val currentCategory: WeatherCategory,
    val hourly: List<HourlyForecast>,
    val updatedAt: LocalDateTime,
    val isStale: Boolean,
)

data class ManualLocation(
    val name: String,
    val latitude: Double,
    val longitude: Double,
)
