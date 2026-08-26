package com.andreacioci.weatherwidget.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface WeatherDao {

    @Query("SELECT * FROM cached_location WHERE id = 0")
    suspend fun getLocation(): CachedLocationEntity?

    @Query("SELECT * FROM cached_hourly_forecast ORDER BY timeEpochMillis ASC")
    suspend fun getHourly(): List<CachedHourlyEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLocation(location: CachedLocationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHourly(hourly: List<CachedHourlyEntity>)

    @Query("DELETE FROM cached_hourly_forecast")
    suspend fun clearHourly()

    @Transaction
    suspend fun replaceCache(location: CachedLocationEntity, hourly: List<CachedHourlyEntity>) {
        clearHourly()
        upsertLocation(location)
        insertHourly(hourly)
    }
}
