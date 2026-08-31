package com.andreacioci.weatherwidget.widget.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.andreacioci.weatherwidget.data.model.HourlyForecast
import com.andreacioci.weatherwidget.widget.theme.WidgetColors
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

private val hourFormatter = DateTimeFormatter.ofPattern("HH")

@Composable
fun HourlyColumn(
    forecast: HourlyForecast,
    iconSize: Dp = 38.dp,
    modifier: GlanceModifier = GlanceModifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = forecast.time.format(hourFormatter),
            style = TextStyle(
                fontSize = 11.sp,
                color = WidgetColors.SecondaryText,
                textAlign = TextAlign.Center,
            ),
        )
        WeatherIcon(
            category = forecast.category,
            size = iconSize,
            modifier = GlanceModifier.padding(vertical = 2.dp),
        )
        Text(
            text = "${forecast.temperatureCelsius.roundToInt()}°",
            style = TextStyle(
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = WidgetColors.PrimaryText,
                textAlign = TextAlign.Center,
            ),
        )
    }
}
