package com.example.historycalendar.core

import com.example.historycalendar.data.repository.HistoricalEventRepository
import com.example.historycalendar.data.repository.SettingsRepository
import com.example.historycalendar.notification.NotificationScheduler
import com.example.historycalendar.widget.HistoryCalendarWidgetUpdater
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppInitializer @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val historicalEventRepository: HistoricalEventRepository,
    private val sampleDataSeeder: SampleDataSeeder,
    private val notificationScheduler: NotificationScheduler,
    @ApplicationContext private val context: Context
) {
    @Volatile private var initialized = false

    suspend fun initialize() {
        if (initialized) return
        settingsRepository.ensureDefaultSettings()
        sampleDataSeeder.seedIfNeeded()
        notificationScheduler.scheduleNextDailyCheck()
        HistoryCalendarWidgetUpdater.requestUpdate(context)
        initialized = true
    }
}
