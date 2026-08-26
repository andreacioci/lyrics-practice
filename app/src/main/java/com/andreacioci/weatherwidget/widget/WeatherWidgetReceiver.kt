package com.andreacioci.weatherwidget.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.andreacioci.weatherwidget.worker.WorkScheduler

class WeatherWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WeatherWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        // Prima istanza del widget aggiunta alla home: avvia l'aggiornamento periodico.
        WorkScheduler.schedulePeriodic(context)
    }

    override fun onDisabled(context: Context) {
        // Ultima istanza rimossa: nessun widget da aggiornare, ferma il worker.
        WorkScheduler.cancel(context)
        super.onDisabled(context)
    }
}
