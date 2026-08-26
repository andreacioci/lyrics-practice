package com.andreacioci.weatherwidget.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenMeteoForecastResponse(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val current: CurrentWeatherDto,
    val hourly: HourlyWeatherDto,
)

@Serializable
data class CurrentWeatherDto(
    val time: String,
    @SerialName("temperature_2m") val temperature2m: Double,
    @SerialName("weather_code") val weatherCode: Int,
)

@Serializable
data class HourlyWeatherDto(
    val time: List<String>,
    @SerialName("temperature_2m") val temperature2m: List<Double>,
    @SerialName("weather_code") val weatherCode: List<Int>,
)

@Serializable
data class GeocodingResponse(
    val results: List<GeocodingResultDto>? = null,
)

@Serializable
data class GeocodingResultDto(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String? = null,
    val admin1: String? = null,
)
