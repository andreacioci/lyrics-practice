package com.andreacioci.weatherwidget

import android.content.Context
import com.andreacioci.weatherwidget.data.local.SettingsDataStore
import com.andreacioci.weatherwidget.data.local.WeatherDatabase
import com.andreacioci.weatherwidget.data.location.LocationProvider
import com.andreacioci.weatherwidget.data.remote.NetworkModule
import com.andreacioci.weatherwidget.data.repository.WeatherRepository

/** Contenitore manuale delle dipendenze condivise, legato al ciclo di vita di [WeatherApp]. */
class AppContainer(context: Context) {

    val settingsDataStore = SettingsDataStore(context)

    val locationProvider = LocationProvider(context, settingsDataStore)

    val weatherRepository = WeatherRepository(
        forecastApi = NetworkModule.forecastApi,
        geocodingApi = NetworkModule.geocodingApi,
        dao = WeatherDatabase.getInstance(context).weatherDao(),
    )

    companion object {
        fun from(context: Context): AppContainer =
            (context.applicationContext as WeatherApp).container
    }
}
