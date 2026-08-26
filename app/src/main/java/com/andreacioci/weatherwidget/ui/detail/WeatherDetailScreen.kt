package com.andreacioci.weatherwidget.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.updateAll
import com.andreacioci.weatherwidget.AppContainer
import com.andreacioci.weatherwidget.data.model.WeatherSnapshot
import com.andreacioci.weatherwidget.widget.WeatherWidget
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
fun WeatherDetailScreen(
    hasLocationPermission: Boolean,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val context = LocalContext.current
    val container = remember { AppContainer.from(context) }
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var snapshot by remember { mutableStateOf<WeatherSnapshot?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    suspend fun refresh() {
        isLoading = true
        errorMessage = null
        val location = container.locationProvider.resolveLocation()
        val result = if (location != null) {
            container.weatherRepository.getWeather(
                latitude = location.latitude,
                longitude = location.longitude,
                locationName = location.name,
            )
        } else {
            container.weatherRepository.getCachedSnapshot()
        }
        snapshot = result
        errorMessage = when {
            result != null -> null
            location == null -> "Nessuna posizione disponibile: consenti la localizzazione o imposta una città manuale."
            else -> "Impossibile scaricare le previsioni al momento."
        }
        isLoading = false
        WeatherWidget().updateAll(context)
    }

    LaunchedEffect(hasLocationPermission) {
        refresh()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Meteo", style = MaterialTheme.typography.headlineSmall)
            TextButton(onClick = onOpenSettings) { Text("Impostazioni") }
        }

        if (!hasLocationPermission) {
            Spacer(Modifier.height(12.dp))
            Text("Consenti l'accesso alla posizione per le previsioni automatiche, oppure imposta una città manuale nelle impostazioni.")
            Spacer(Modifier.height(8.dp))
            Button(onClick = onRequestPermission) { Text("Consenti localizzazione") }
        }

        Spacer(Modifier.height(16.dp))

        when {
            isLoading -> CircularProgressIndicator()
            errorMessage != null -> Text(
                text = errorMessage.orEmpty(),
                color = MaterialTheme.colorScheme.error,
            )
            snapshot != null -> WeatherDetailContent(snapshot!!)
        }

        Spacer(Modifier.height(16.dp))
        Button(onClick = { scope.launch { refresh() } }, enabled = !isLoading) {
            Text("Aggiorna")
        }
    }
}

@Composable
private fun WeatherDetailContent(snapshot: WeatherSnapshot) {
    Column {
        Text(snapshot.locationName, style = MaterialTheme.typography.titleLarge)
        Text(
            text = "${snapshot.currentTemperatureCelsius.roundToInt()}°C",
            style = MaterialTheme.typography.displayMedium,
        )
        if (snapshot.isStale) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Dati offline (ultimo aggiornamento: ${snapshot.updatedAt.format(DateTimeFormatter.ofPattern("HH:mm"))})",
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Spacer(Modifier.height(16.dp))

        LazyRow {
            items(snapshot.hourly) { hourly ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 12.dp),
                ) {
                    Text(hourly.time.format(DateTimeFormatter.ofPattern("HH:mm")))
                    Text(hourly.category.name, style = MaterialTheme.typography.bodySmall)
                    Text("${hourly.temperatureCelsius.roundToInt()}°")
                }
            }
        }
    }
}
