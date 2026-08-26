package com.andreacioci.weatherwidget.worker

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.andreacioci.weatherwidget.widget.WeatherWidget

/**
 * Non contiene logica di fetch propria: si limita a forzare Glance a ricomporre il widget,
 * che nel suo `provideGlance` interroga [com.andreacioci.weatherwidget.data.repository.WeatherRepository]
 * (rete con fallback su cache Room). Così la pipeline di caricamento dati resta in un unico posto.
 */
class WeatherUpdateWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            WeatherWidget().updateAll(applicationContext)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
