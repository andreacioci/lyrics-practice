package com.andreacioci.weatherwidget.widget.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.layout.size
import com.andreacioci.weatherwidget.R
import com.andreacioci.weatherwidget.data.model.WeatherCategory

fun weatherIconRes(category: WeatherCategory): Int = when (category) {
    WeatherCategory.CLEAR -> R.drawable.ic_weather_clear
    WeatherCategory.PARTLY_CLOUDY -> R.drawable.ic_weather_partly_cloudy
    WeatherCategory.CLOUDY -> R.drawable.ic_weather_cloudy
    WeatherCategory.RAIN -> R.drawable.ic_weather_rain
    WeatherCategory.STORM -> R.drawable.ic_weather_storm
    WeatherCategory.SNOW -> R.drawable.ic_weather_snow
}

@Composable
fun WeatherIcon(
    category: WeatherCategory,
    modifier: GlanceModifier = GlanceModifier,
    size: Dp = 28.dp,
) {
    Image(
        provider = ImageProvider(weatherIconRes(category)),
        contentDescription = category.name,
        modifier = modifier.size(size),
    )
}
