package com.andreacioci.weatherwidget.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Riga singola (id fisso = 0): rappresenta l'ultima location/snapshot correnti in cache. */
@Entity(tableName = "cached_location")
data class CachedLocationEntity(
    @PrimaryKey val id: Int = 0,
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val currentTemperature: Double,
    val currentCategory: String,
    val updatedAtEpochMillis: Long,
)

@Entity(tableName = "cached_hourly_forecast")
data class CachedHourlyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timeEpochMillis: Long,
    val temperature: Double,
    val category: String,
)
