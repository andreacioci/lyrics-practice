package com.andreacioci.weatherwidget.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoForecastApi {
    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("hourly") hourly: String = "temperature_2m,weather_code",
        @Query("current") current: String = "temperature_2m,weather_code",
        @Query("timezone") timezone: String = "auto",
        @Query("forecast_days") forecastDays: Int = 2,
    ): OpenMeteoForecastResponse
}

interface OpenMeteoGeocodingApi {
    @GET("v1/search")
    suspend fun search(
        @Query("name") name: String,
        @Query("count") count: Int = 1,
        @Query("language") language: String = "it",
        @Query("format") format: String = "json",
    ): GeocodingResponse
}
