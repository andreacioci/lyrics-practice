package com.andreacioci.weatherwidget.data.model

enum class WeatherCategory {
    CLEAR,
    PARTLY_CLOUDY,
    CLOUDY,
    RAIN,
    STORM,
    SNOW,
}

/**
 * Mappa i weather code WMO restituiti da Open-Meteo alle categorie usate da UI/widget.
 * Tabella ufficiale: https://open-meteo.com/en/docs (sezione "WMO Weather interpretation codes").
 */
object WeatherCodeMapper {
    fun toCategory(wmoCode: Int): WeatherCategory = when (wmoCode) {
        0 -> WeatherCategory.CLEAR
        1, 2 -> WeatherCategory.PARTLY_CLOUDY
        3, 45, 48 -> WeatherCategory.CLOUDY
        51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82 -> WeatherCategory.RAIN
        71, 73, 75, 77, 85, 86 -> WeatherCategory.SNOW
        95, 96, 99 -> WeatherCategory.STORM
        // codice non documentato: trattato come nuvoloso, la categoria più "neutra"
        else -> WeatherCategory.CLOUDY
    }
}
