package com.andreacioci.weatherwidget.data.local

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.andreacioci.weatherwidget.data.model.ManualLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "weather_settings")

/** Città impostata manualmente nelle impostazioni, usata come fallback se il permesso di localizzazione non è concesso. */
class SettingsDataStore(private val context: Context) {

    private object Keys {
        val MANUAL_CITY_NAME = stringPreferencesKey("manual_city_name")
        val MANUAL_CITY_LAT = doublePreferencesKey("manual_city_lat")
        val MANUAL_CITY_LON = doublePreferencesKey("manual_city_lon")
    }

    val manualLocation: Flow<ManualLocation?> = context.dataStore.data.map { prefs ->
        val name = prefs[Keys.MANUAL_CITY_NAME]
        val lat = prefs[Keys.MANUAL_CITY_LAT]
        val lon = prefs[Keys.MANUAL_CITY_LON]
        if (name != null && lat != null && lon != null) {
            ManualLocation(name = name, latitude = lat, longitude = lon)
        } else {
            null
        }
    }

    suspend fun setManualLocation(location: ManualLocation) {
        context.dataStore.edit { prefs ->
            prefs[Keys.MANUAL_CITY_NAME] = location.name
            prefs[Keys.MANUAL_CITY_LAT] = location.latitude
            prefs[Keys.MANUAL_CITY_LON] = location.longitude
        }
    }

    suspend fun clearManualLocation() {
        context.dataStore.edit { prefs ->
            prefs.remove(Keys.MANUAL_CITY_NAME)
            prefs.remove(Keys.MANUAL_CITY_LAT)
            prefs.remove(Keys.MANUAL_CITY_LON)
        }
    }
}
