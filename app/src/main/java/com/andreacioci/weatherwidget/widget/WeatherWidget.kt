package com.andreacioci.weatherwidget.widget

import android.content.Context
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import com.andreacioci.weatherwidget.AppContainer

class WeatherWidget : GlanceAppWidget() {

    // Ricompone provideGlance ad ogni resize reale del widget, così WeatherWidgetContent
    // può scegliere quante colonne orarie mostrare in base alla larghezza effettiva.
    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val container = AppContainer.from(context)

        provideContent {
            var uiState by remember { mutableStateOf<WeatherWidgetUiState>(WeatherWidgetUiState.Loading) }

            LaunchedEffect(id) {
                uiState = loadUiState(container)
            }

            WeatherWidgetContent(state = uiState)
        }
    }

    private suspend fun loadUiState(container: AppContainer): WeatherWidgetUiState {
        val location = container.locationProvider.resolveLocation()
        val snapshot = if (location != null) {
            container.weatherRepository.getWeather(
                latitude = location.latitude,
                longitude = location.longitude,
                locationName = location.name,
            )
        } else {
            container.weatherRepository.getCachedSnapshot()
        }

        return if (snapshot != null) {
            WeatherWidgetUiState.Success(snapshot)
        } else {
            WeatherWidgetUiState.Error(hasLocationConfigured = location != null)
        }
    }
}
