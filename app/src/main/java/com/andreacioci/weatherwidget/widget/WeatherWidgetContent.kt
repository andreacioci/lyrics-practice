package com.andreacioci.weatherwidget.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.defaultWeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.andreacioci.weatherwidget.R
import com.andreacioci.weatherwidget.data.model.WeatherSnapshot
import com.andreacioci.weatherwidget.ui.MainActivity
import com.andreacioci.weatherwidget.widget.components.HourlyColumn
import com.andreacioci.weatherwidget.widget.theme.WidgetColors
import kotlin.math.roundToInt

@Composable
fun WeatherWidgetContent(state: WeatherWidgetUiState) {
    val size = LocalSize.current
    val columnsToShow = when {
        size.width < 140.dp -> 2
        size.width < 190.dp -> 3
        size.width < 220.dp -> 4
        else -> 5
    }

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(R.drawable.widget_card_background))
            .padding(12.dp)
            .clickable(actionStartActivity<MainActivity>()),
    ) {
        when (state) {
            is WeatherWidgetUiState.Loading -> LoadingContent()
            is WeatherWidgetUiState.Success -> SuccessContent(state.snapshot, columnsToShow)
            is WeatherWidgetUiState.Error -> ErrorContent(state.hasLocationConfigured)
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(modifier = GlanceModifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Caricamento…",
            style = TextStyle(fontSize = 13.sp, color = WidgetColors.SecondaryText),
        )
    }
}

@Composable
private fun ErrorContent(hasLocationConfigured: Boolean) {
    Box(modifier = GlanceModifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = if (hasLocationConfigured) {
                "Impossibile aggiornare le previsioni"
            } else {
                "Imposta una posizione nelle impostazioni dell'app"
            },
            style = TextStyle(
                fontSize = 12.sp,
                color = WidgetColors.ErrorText,
                textAlign = TextAlign.Center,
            ),
        )
    }
}

@Composable
private fun SuccessContent(snapshot: WeatherSnapshot, columnsToShow: Int) {
    Column(modifier = GlanceModifier.fillMaxSize()) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    text = snapshot.locationName,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = WidgetColors.PrimaryText,
                    ),
                    maxLines = 1,
                )
                if (snapshot.isStale) {
                    Text(
                        text = "dati offline",
                        style = TextStyle(fontSize = 10.sp, color = WidgetColors.SecondaryText),
                    )
                }
            }
            Text(
                text = "${snapshot.currentTemperatureCelsius.roundToInt()}°",
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = WidgetColors.PrimaryText,
                ),
            )
        }

        Row(
            modifier = GlanceModifier.fillMaxWidth().padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            snapshot.hourly.take(columnsToShow).forEach { hourly ->
                HourlyColumn(forecast = hourly, modifier = GlanceModifier.defaultWeight())
            }
        }
    }
}
