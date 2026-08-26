package com.andreacioci.weatherwidget.data.remote

import com.andreacioci.weatherwidget.data.model.HourlyForecast
import com.andreacioci.weatherwidget.data.model.WeatherCodeMapper
import com.andreacioci.weatherwidget.data.model.WeatherSnapshot
import java.time.LocalDateTime

private const val HOURS_TO_SHOW = 5

fun OpenMeteoForecastResponse.toDomain(locationName: String): WeatherSnapshot {
    val nowHour = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0)
    val times = hourly.time.map { LocalDateTime.parse(it) }

    val startIndex = times.indexOfFirst { !it.isBefore(nowHour) }.let { if (it == -1) 0 else it }
    val endIndex = minOf(startIndex + HOURS_TO_SHOW, times.size)

    val nextHours = (startIndex until endIndex).map { i ->
        HourlyForecast(
            time = times[i],
            temperatureCelsius = hourly.temperature2m[i],
            category = WeatherCodeMapper.toCategory(hourly.weatherCode[i]),
        )
    }

    return WeatherSnapshot(
        locationName = locationName,
        currentTemperatureCelsius = current.temperature2m,
        currentCategory = WeatherCodeMapper.toCategory(current.weatherCode),
        hourly = nextHours,
        updatedAt = LocalDateTime.now(),
        isStale = false,
    )
}
