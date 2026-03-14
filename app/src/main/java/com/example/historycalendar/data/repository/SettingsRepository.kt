package com.example.historycalendar.data.repository

import android.content.Context
import com.example.historycalendar.data.db.dao.SettingsDao
import com.example.historycalendar.data.db.entity.AppSettingsEntity
import com.example.historycalendar.widget.HistoryCalendarWidgetUpdater
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val settingsDao: SettingsDao,
    @ApplicationContext private val context: Context
) {
    fun observeSettings(): Flow<AppSettingsEntity> = settingsDao.observeSettings().map { it ?: AppSettingsEntity() }
    suspend fun getSettings(): AppSettingsEntity = settingsDao.getSettingsOnce() ?: AppSettingsEntity()
    suspend fun save(settings: AppSettingsEntity) {
        settingsDao.upsert(settings)
        HistoryCalendarWidgetUpdater.requestUpdate(context)
    }
    suspend fun ensureDefaultSettings() {
        settingsDao.ensureDefaultSettings()
        HistoryCalendarWidgetUpdater.requestUpdate(context)
    }
}
