package com.example.historycalendar.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.historycalendar.data.db.converter.Converters
import com.example.historycalendar.data.db.dao.HistoricalEventDao
import com.example.historycalendar.data.db.dao.SettingsDao
import com.example.historycalendar.data.db.entity.AppSettingsEntity
import com.example.historycalendar.data.db.entity.HistoricalEventEntity

@Database(
    entities = [HistoricalEventEntity::class, AppSettingsEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class HistoryCalendarDb : RoomDatabase() {
    abstract fun historicalEventDao(): HistoricalEventDao
    abstract fun settingsDao(): SettingsDao
}
