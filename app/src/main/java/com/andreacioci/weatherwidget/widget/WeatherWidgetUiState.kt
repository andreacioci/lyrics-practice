package com.andreacioci.weatherwidget.widget

import com.andreacioci.weatherwidget.data.model.WeatherSnapshot

sealed interface WeatherWidgetUiState {
    data object Loading : WeatherWidgetUiState
    data class Success(val snapshot: WeatherSnapshot) : WeatherWidgetUiState
    data class Error(val hasLocationConfigured: Boolean) : WeatherWidgetUiState
}
