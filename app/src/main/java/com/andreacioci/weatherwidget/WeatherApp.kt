package com.andreacioci.weatherwidget

import android.app.Application

class WeatherApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
