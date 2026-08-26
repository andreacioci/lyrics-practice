package com.andreacioci.weatherwidget.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.core.content.ContextCompat
import com.andreacioci.weatherwidget.data.local.SettingsDataStore
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale

data class ResolvedLocation(
    val name: String,
    val latitude: Double,
    val longitude: Double,
)

/**
 * Risolve la posizione da usare per le previsioni: se il permesso di localizzazione è
 * concesso legge l'ultima posizione nota (nessun nuovo fix GPS attivo, quindi sicura da
 * chiamare anche dal Worker in background senza richiedere ACCESS_BACKGROUND_LOCATION);
 * altrimenti ripiega sulla città impostata manualmente nelle impostazioni.
 */
class LocationProvider(
    private val context: Context,
    private val settingsDataStore: SettingsDataStore,
) {

    private val fusedClient by lazy { LocationServices.getFusedLocationProviderClient(context) }

    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
    }

    suspend fun resolveLocation(): ResolvedLocation? {
        val fromDevice = getLastKnownDeviceLocation()
        if (fromDevice != null) return fromDevice

        val manual = settingsDataStore.manualLocation.first() ?: return null
        return ResolvedLocation(name = manual.name, latitude = manual.latitude, longitude = manual.longitude)
    }

    @SuppressLint("MissingPermission")
    private suspend fun getLastKnownDeviceLocation(): ResolvedLocation? {
        if (!hasLocationPermission()) return null

        val location = try {
            fusedClient.lastLocation.await()
        } catch (e: SecurityException) {
            null
        } ?: return null

        val name = reverseGeocode(location.latitude, location.longitude) ?: DEFAULT_LOCATION_NAME
        return ResolvedLocation(name = name, latitude = location.latitude, longitude = location.longitude)
    }

    private suspend fun reverseGeocode(latitude: Double, longitude: Double): String? = withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocodeAsync(geocoder, latitude, longitude)
            } else {
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(latitude, longitude, 1)
            }
            addresses?.firstOrNull()?.locality ?: addresses?.firstOrNull()?.subAdminArea
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun geocodeAsync(geocoder: Geocoder, latitude: Double, longitude: Double): List<Address>? =
        suspendCancellableCoroutine { continuation ->
            geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                continuation.resume(addresses)
            }
        }

    companion object {
        private const val DEFAULT_LOCATION_NAME = "Posizione attuale"
    }
}
