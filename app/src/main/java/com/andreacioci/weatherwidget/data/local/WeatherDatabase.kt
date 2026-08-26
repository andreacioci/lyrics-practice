package com.andreacioci.weatherwidget.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CachedLocationEntity::class, CachedHourlyEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao

    companion object {
        @Volatile
        private var instance: WeatherDatabase? = null

        fun getInstance(context: Context): WeatherDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    "weather.db",
                ).build().also { instance = it }
            }
    }
}
