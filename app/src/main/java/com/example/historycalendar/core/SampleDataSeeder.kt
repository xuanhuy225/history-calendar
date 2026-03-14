package com.example.historycalendar.core

import com.example.historycalendar.data.db.entity.CalendarType
import com.example.historycalendar.data.db.entity.HistoricalEventEntity
import com.example.historycalendar.data.repository.HistoricalEventRepository
import java.util.UUID
import javax.inject.Inject

class SampleDataSeeder @Inject constructor(
    private val historicalEventRepository: HistoricalEventRepository
) {
    suspend fun seedIfNeeded() {
        if (historicalEventRepository.count() > 0) return
        val now = System.currentTimeMillis()
        historicalEventRepository.insert(
            HistoricalEventEntity(
                id = UUID.randomUUID().toString(),
                title = "Tuyên ngôn Độc lập",
                description = "Ngày Chủ tịch Hồ Chí Minh đọc Tuyên ngôn Độc lập tại Ba Đình.",
                calendarType = CalendarType.SOLAR,
                day = 2,
                month = 9,
                year = 1945,
                tags = listOf("Việt Nam", "Lịch sử"),
                notifyEnabled = true,
                createdAt = now,
                updatedAt = now
            )
        )
        historicalEventRepository.insert(
            HistoricalEventEntity(
                id = UUID.randomUUID().toString(),
                title = "Chiến thắng Điện Biên Phủ",
                description = "Một mốc quan trọng trong lịch sử Việt Nam hiện đại.",
                calendarType = CalendarType.SOLAR,
                day = 7,
                month = 5,
                year = 1954,
                tags = listOf("Việt Nam", "Kháng chiến"),
                notifyEnabled = true,
                createdAt = now,
                updatedAt = now
            )
        )
    }
}
