package com.example.historycalendar.di

import android.content.Context
import androidx.room.Room
import com.example.historycalendar.data.db.HistoryCalendarDb
import com.example.historycalendar.data.db.dao.HistoricalEventDao
import com.example.historycalendar.data.db.dao.SettingsDao
import com.example.historycalendar.data.backup.BackupManager
import com.example.historycalendar.notification.NotificationHelper
import com.example.historycalendar.notification.NotificationScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HistoryCalendarDb =
        Room.databaseBuilder(context, HistoryCalendarDb::class.java, "history_calendar.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideHistoricalEventDao(db: HistoryCalendarDb): HistoricalEventDao = db.historicalEventDao()

    @Provides
    fun provideSettingsDao(db: HistoryCalendarDb): SettingsDao = db.settingsDao()

    @Provides
    @Singleton
    fun provideNotificationScheduler(@ApplicationContext context: Context): NotificationScheduler = NotificationScheduler(context)

    @Provides
    @Singleton
    fun provideNotificationHelper(@ApplicationContext context: Context): NotificationHelper = NotificationHelper(context)

    @Provides
    @Singleton
    fun provideBackupManager(@ApplicationContext context: Context, settingsRepository: com.example.historycalendar.data.repository.SettingsRepository, historicalEventRepository: com.example.historycalendar.data.repository.HistoricalEventRepository): BackupManager =
        BackupManager(context, settingsRepository, historicalEventRepository)
}
