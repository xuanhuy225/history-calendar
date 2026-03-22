package com.example.historycalendar.data.repository

import android.content.Context
import com.example.historycalendar.data.db.dao.HistoricalEventDao
import com.example.historycalendar.data.db.entity.HistoricalEventEntity
import com.example.historycalendar.widget.HistoryCalendarWidgetUpdater
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HistoricalEventRepository @Inject constructor(
    private val dao: HistoricalEventDao,
    @ApplicationContext private val context: Context
) {
    fun observeAll(): Flow<List<HistoricalEventEntity>> = dao.observeAll()
    fun search(keyword: String): Flow<List<HistoricalEventEntity>> = dao.search(keyword)
    suspend fun count(): Int = dao.count()
    suspend fun getById(id: String): HistoricalEventEntity? = dao.getById(id)
    suspend fun getByTitle(title: String): HistoricalEventEntity? = dao.getByTitle(title)
    suspend fun insert(event: HistoricalEventEntity) {
        dao.insert(event)
        HistoryCalendarWidgetUpdater.requestUpdate(context)
    }
    suspend fun insertAll(events: List<HistoricalEventEntity>) {
        dao.insertAll(events)
        HistoryCalendarWidgetUpdater.requestUpdate(context)
    }
    suspend fun update(event: HistoricalEventEntity) {
        dao.update(event)
        HistoryCalendarWidgetUpdater.requestUpdate(context)
    }
    suspend fun deleteById(id: String) {
        dao.deleteById(id)
        HistoryCalendarWidgetUpdater.requestUpdate(context)
    }
    suspend fun clearAll() {
        dao.clearAll()
        HistoryCalendarWidgetUpdater.requestUpdate(context)
    }
    suspend fun getTodaySolarEvents(month: Int, day: Int): List<HistoricalEventEntity> = dao.getEventsBySolarDay(month, day)
    suspend fun getTodayLunarEvents(month: Int, day: Int): List<HistoricalEventEntity> = dao.getEventsByLunarDay(month, day)
}
