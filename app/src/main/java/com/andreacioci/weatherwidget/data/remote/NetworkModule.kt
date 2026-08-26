package com.andreacioci.weatherwidget.data.remote

import com.andreacioci.weatherwidget.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object NetworkModule {

    private val json = Json { ignoreUnknownKeys = true }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    private fun retrofit(baseUrl: String): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    val forecastApi: OpenMeteoForecastApi by lazy {
        retrofit("https://api.open-meteo.com/").create(OpenMeteoForecastApi::class.java)
    }

    val geocodingApi: OpenMeteoGeocodingApi by lazy {
        retrofit("https://geocoding-api.open-meteo.com/").create(OpenMeteoGeocodingApi::class.java)
    }
}
