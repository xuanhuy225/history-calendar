package com.example.historycalendar.data.excel

import android.content.Context
import android.net.Uri
import com.example.historycalendar.data.db.entity.CalendarType
import com.example.historycalendar.data.db.entity.HistoricalEventEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.dhatim.fastexcel.reader.ReadableWorkbook
import java.util.UUID
import javax.inject.Inject

/**
 * Holds the result of parsing an Excel import.
 * [newEvents]  – events with no title match in DB (inserted immediately).
 * [duplicates] – events whose title already exists in DB (require user resolution).
 */
data class ExcelImportResult(
    val newEvents: List<HistoricalEventEntity>,
    val duplicates: List<DuplicateEvent>
)

data class DuplicateEvent(
    val incoming: HistoricalEventEntity,
    val existing: HistoricalEventEntity
)

class ExcelImporter @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Parses the .xlsx file at [uri].
     *
     * Expected first-row headers (case-insensitive):
     *   date | title | description | type
     *
     * date format : YYYY-MM-DD
     * type values : HISTORY or blank → SOLAR; LUNAR → LUNAR
     */
    suspend fun parse(
        uri: Uri,
        existingTitleLookup: suspend (String) -> HistoricalEventEntity?
    ): ExcelImportResult = withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: error("Cannot open file: $uri")

        val newEvents  = mutableListOf<HistoricalEventEntity>()
        val duplicates = mutableListOf<DuplicateEvent>()

        ReadableWorkbook(inputStream).use { wb ->
            val sheet = wb.firstSheet
            val rows  = sheet.openStream().iterator()
            if (!rows.hasNext()) return@use

            // Parse header row to find column indices
            val header = rows.next()
            fun colOf(name: String) = (0 until header.cellCount)
                .firstOrNull { header.getCell(it).rawValue?.trim()?.lowercase() == name } ?: -1

            val dateCol  = colOf("date").takeIf { it >= 0 } ?: 0
            val titleCol = colOf("title").takeIf { it >= 0 } ?: 1
            val descCol  = colOf("description").takeIf { it >= 0 } ?: 2
            val typeCol  = colOf("type").takeIf { it >= 0 } ?: 3

            while (rows.hasNext()) {
                val row = rows.next()
                val dateStr = row.getOptionalCell(dateCol).orElse(null)?.rawValue?.trim() ?: continue
                val title   = row.getOptionalCell(titleCol).orElse(null)?.rawValue?.trim() ?: continue
                if (title.isEmpty()) continue
                val description = row.getOptionalCell(descCol).orElse(null)?.rawValue?.trim()
                    ?.takeIf { it.isNotEmpty() }
                val typeStr = row.getOptionalCell(typeCol).orElse(null)?.rawValue?.trim() ?: "HISTORY"

                val entity = rowToEntity(dateStr, title, description, typeStr) ?: continue

                val existing = existingTitleLookup(title)
                if (existing != null) {
                    duplicates.add(DuplicateEvent(incoming = entity, existing = existing))
                } else {
                    newEvents.add(entity)
                }
            }
        }

        ExcelImportResult(newEvents = newEvents, duplicates = duplicates)
    }

    private fun rowToEntity(
        dateStr: String,
        title: String,
        description: String?,
        typeStr: String
    ): HistoricalEventEntity? = runCatching {
        val parts = dateStr.split("-")
        val year  = parts[0].toInt()
        val month = parts[1].toInt()
        val day   = parts[2].toInt()
        val calType = if (typeStr.uppercase() == "LUNAR") CalendarType.LUNAR else CalendarType.SOLAR
        val now = System.currentTimeMillis()
        HistoricalEventEntity(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            calendarType = calType,
            day = day,
            month = month,
            year = year,
            tags = emptyList(),
            notifyEnabled = true,
            createdAt = now,
            updatedAt = now
        )
    }.getOrNull()
}
