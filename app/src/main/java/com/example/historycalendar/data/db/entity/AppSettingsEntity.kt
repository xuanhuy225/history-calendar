package com.example.historycalendar.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int = 1,
    @ColumnInfo(name = "notify_enabled")
    val notifyEnabled: Boolean = true,
    @ColumnInfo(name = "notify_hour")
    val notifyHour: Int = 7,
    @ColumnInfo(name = "notify_minute")
    val notifyMinute: Int = 0,
    @ColumnInfo(name = "dnd_enabled")
    val dndEnabled: Boolean = false,
    @ColumnInfo(name = "dnd_start_minute_of_day")
    val dndStartMinuteOfDay: Int = 22 * 60,
    @ColumnInfo(name = "dnd_end_minute_of_day")
    val dndEndMinuteOfDay: Int = 7 * 60
)
