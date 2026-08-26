package com.andreacioci.weatherwidget.data.repository

import com.andreacioci.weatherwidget.data.local.CachedHourlyEntity
import com.andreacioci.weatherwidget.data.local.CachedLocationEntity
import com.andreacioci.weatherwidget.data.local.WeatherDao
import com.andreacioci.weatherwidget.data.model.HourlyForecast
import com.andreacioci.weatherwidget.data.model.ManualLocation
import com.andreacioci.weatherwidget.data.model.WeatherCategory
import com.andreacioci.weatherwidget.data.model.WeatherSnapshot
import com.andreacioci.weatherwidget.data.remote.OpenMeteoForecastApi
import com.andreacioci.weatherwidget.data.remote.OpenMeteoGeocodingApi
import com.andreacioci.weatherwidget.data.remote.toDomain
import java.time.Instant
import java.time.ZoneId

class WeatherRepository(
    private val forecastApi: OpenMeteoForecastApi,
    private val geocodingApi: OpenMeteoGeocodingApi,
    private val dao: WeatherDao,
) {

    /**
     * Prova a scaricare le previsioni aggiornate e le mette in cache; se la rete fallisce
     * (o la risposta non è valida) ripiega sull'ultimo snapshot salvato in Room, marcato
     * come [WeatherSnapshot.isStale]. Ritorna null solo se non c'è né rete né cache.
     */
    suspend fun getWeather(latitude: Double, longitude: Double, locationName: String): WeatherSnapshot? {
        val fresh = runCatching {
            forecastApi.getForecast(latitude = latitude, longitude = longitude).toDomain(locationName)
        }.getOrNull()

        if (fresh != null) {
            cacheSnapshot(latitude, longitude, fresh)
            return fresh
        }

        return getCachedSnapshot()
    }

    suspend fun getCachedSnapshot(): WeatherSnapshot? {
        val location = dao.getLocation() ?: return null
        val hourly = dao.getHourly()
        if (hourly.isEmpty()) return null

        return WeatherSnapshot(
            locationName = location.locationName,
            currentTemperatureCelsius = location.currentTemperature,
            currentCategory = WeatherCategory.valueOf(location.currentCategory),
            hourly = hourly.map {
                HourlyForecast(
                    time = Instant.ofEpochMilli(it.timeEpochMillis).atZone(ZoneId.systemDefault()).toLocalDateTime(),
                    temperatureCelsius = it.temperature,
                    category = WeatherCategory.valueOf(it.category),
                )
            },
            updatedAt = Instant.ofEpochMilli(location.updatedAtEpochMillis).atZone(ZoneId.systemDefault()).toLocalDateTime(),
            isStale = true,
        )
    }

    /** Geocodifica una città digitata manualmente (fallback quando la localizzazione non è disponibile). */
    suspend fun geocodeCity(cityName: String): ManualLocation? {
        val result = runCatching { geocodingApi.search(name = cityName) }.getOrNull()
            ?.results?.firstOrNull()
            ?: return null
        return ManualLocation(name = result.name, latitude = result.latitude, longitude = result.longitude)
    }

    private suspend fun cacheSnapshot(latitude: Double, longitude: Double, snapshot: WeatherSnapshot) {
        val locationEntity = CachedLocationEntity(
            locationName = snapshot.locationName,
            latitude = latitude,
            longitude = longitude,
            currentTemperature = snapshot.currentTemperatureCelsius,
            currentCategory = snapshot.currentCategory.name,
            updatedAtEpochMillis = snapshot.updatedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        )
        val hourlyEntities = snapshot.hourly.map {
            CachedHourlyEntity(
                timeEpochMillis = it.time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                temperature = it.temperatureCelsius,
                category = it.category.name,
            )
        }
        dao.replaceCache(locationEntity, hourlyEntities)
    }
}
