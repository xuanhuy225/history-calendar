package com.example.historycalendar.data.db.converter

import androidx.room.TypeConverter
import com.example.historycalendar.data.db.entity.CalendarType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromCalendarType(value: CalendarType): String = value.name

    @TypeConverter
    fun toCalendarType(value: String): CalendarType = CalendarType.valueOf(value)

    @TypeConverter
    fun fromTags(tags: List<String>): String = Json.encodeToString(tags)

    @TypeConverter
    fun toTags(value: String): List<String> = try {
        Json.decodeFromString<List<String>>(value)
    } catch (_: Exception) {
        emptyList()
    }
}
