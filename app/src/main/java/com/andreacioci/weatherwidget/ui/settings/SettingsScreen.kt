package com.andreacioci.weatherwidget.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.andreacioci.weatherwidget.widget.WeatherWidget
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val container = remember { AppContainer.from(context) }
    val scope = rememberCoroutineScope()

    val currentManualLocation by container.settingsDataStore.manualLocation.collectAsState(initial = null)

    var cityQuery by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Impostazioni", style = MaterialTheme.typography.headlineSmall)
            TextButton(onClick = onBack) { Text("Chiudi") }
        }

        Spacer(Modifier.height(16.dp))
        Text(
            "Città manuale, usata come fallback quando il permesso di localizzazione non è concesso.",
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(8.dp))

        currentManualLocation?.let { manual ->
            Text("Attuale: ${manual.name}", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
        }

        OutlinedTextField(
            value = cityQuery,
            onValueChange = { cityQuery = it },
            label = { Text("Nome città") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                val query = cityQuery.trim()
                if (query.isEmpty()) return@Button
                isSaving = true
                statusMessage = null
                scope.launch {
                    val resolved = container.weatherRepository.geocodeCity(query)
                    if (resolved != null) {
                        container.settingsDataStore.setManualLocation(resolved)
                        statusMessage = "Città impostata: ${resolved.name}"
                        WeatherWidget().updateAll(context)
                    } else {
                        statusMessage = "Città non trovata, riprova con un altro nome."
                    }
                    isSaving = false
                }
            },
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (isSaving) "Ricerca…" else "Salva città")
        }

        statusMessage?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, style = MaterialTheme.typography.bodySmall)
        }
    }
}
