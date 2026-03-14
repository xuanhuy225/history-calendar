package com.example.historycalendar.data.backup

import com.example.historycalendar.data.db.entity.AppSettingsEntity
import com.example.historycalendar.data.db.entity.CalendarType
import com.example.historycalendar.data.db.entity.HistoricalEventEntity
import kotlinx.serialization.Serializable

@Serializable
data class AppBackup(
    val schemaVersion: Int = 1,
    val settings: BackupSettings,
    val events: List<BackupEvent>
)

@Serializable
data class BackupSettings(
    val notifyEnabled: Boolean,
    val notifyHour: Int,
    val notifyMinute: Int,
    val dndEnabled: Boolean,
    val dndStartMinuteOfDay: Int,
    val dndEndMinuteOfDay: Int
)

@Serializable
data class BackupEvent(
    val id: String,
    val title: String,
    val description: String? = null,
    val calendarType: String,
    val day: Int,
    val month: Int,
    val year: Int,
    val tags: List<String> = emptyList(),
    val notifyEnabled: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

fun AppSettingsEntity.toBackup() = BackupSettings(
    notifyEnabled = notifyEnabled,
    notifyHour = notifyHour,
    notifyMinute = notifyMinute,
    dndEnabled = dndEnabled,
    dndStartMinuteOfDay = dndStartMinuteOfDay,
    dndEndMinuteOfDay = dndEndMinuteOfDay
)

fun HistoricalEventEntity.toBackup() = BackupEvent(
    id = id,
    title = title,
    description = description,
    calendarType = calendarType.name,
    day = day,
    month = month,
    year = year,
    tags = tags,
    notifyEnabled = notifyEnabled,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun BackupSettings.toEntity() = AppSettingsEntity(
    notifyEnabled = notifyEnabled,
    notifyHour = notifyHour,
    notifyMinute = notifyMinute,
    dndEnabled = dndEnabled,
    dndStartMinuteOfDay = dndStartMinuteOfDay,
    dndEndMinuteOfDay = dndEndMinuteOfDay
)

fun BackupEvent.toEntity() = HistoricalEventEntity(
    id = id,
    title = title,
    description = description,
    calendarType = CalendarType.valueOf(calendarType),
    day = day,
    month = month,
    year = year,
    tags = tags,
    notifyEnabled = notifyEnabled,
    createdAt = createdAt,
    updatedAt = updatedAt
)
