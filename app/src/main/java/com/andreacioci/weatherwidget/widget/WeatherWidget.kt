package com.andreacioci.weatherwidget.widget

import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.text.Text

class WeatherWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: android.content.Context, id: GlanceId) {
        provideContent {
            // TODO: card meteo con 5 colonne orarie (step "widget")
            Text("Weather Widget")
        }
    }
}
