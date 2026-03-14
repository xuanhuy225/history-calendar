package com.example.historycalendar.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "historical_event",
    indices = [
        Index(value = ["calendar_type", "month", "day"], name = "idx_event_lookup"),
        Index(value = ["title"], name = "idx_event_title")
    ]
)
data class HistoricalEventEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "description")
    val description: String? = null,
    @ColumnInfo(name = "calendar_type")
    val calendarType: CalendarType,
    @ColumnInfo(name = "day")
    val day: Int,
    @ColumnInfo(name = "month")
    val month: Int,
    @ColumnInfo(name = "year")
    val year: Int,
    @ColumnInfo(name = "tags")
    val tags: List<String> = emptyList(),
    @ColumnInfo(name = "notify_enabled")
    val notifyEnabled: Boolean = true,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)
