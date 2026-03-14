package com.example.historycalendar.notification

import android.content.Context
import com.example.historycalendar.data.repository.HistoricalEventRepository
import com.example.historycalendar.data.repository.SettingsRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface HistoryCalendarReceiverEntryPoint {
    fun settingsRepository(): SettingsRepository
    fun eventRepository(): HistoricalEventRepository
    fun notificationHelper(): NotificationHelper
    fun notificationScheduler(): NotificationScheduler
}

object EntryPointAccessorsProvider {
    private fun entryPoint(context: Context): HistoryCalendarReceiverEntryPoint =
        EntryPointAccessors.fromApplication(context.applicationContext, HistoryCalendarReceiverEntryPoint::class.java)

    fun settingsRepository(context: Context): SettingsRepository = entryPoint(context).settingsRepository()
    fun eventRepository(context: Context): HistoricalEventRepository = entryPoint(context).eventRepository()
    fun notificationHelper(context: Context): NotificationHelper = entryPoint(context).notificationHelper()
    fun notificationScheduler(context: Context): NotificationScheduler = entryPoint(context).notificationScheduler()
}
