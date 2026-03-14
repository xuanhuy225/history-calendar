package com.example.historycalendar.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.historycalendar.data.db.entity.HistoricalEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoricalEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: HistoricalEventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<HistoricalEventEntity>)

    @Update
    suspend fun update(event: HistoricalEventEntity)

    @Query("DELETE FROM historical_event WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM historical_event")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM historical_event")
    suspend fun count(): Int

    @Query("SELECT * FROM historical_event WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): HistoricalEventEntity?

    @Query("""
        SELECT * FROM historical_event
        WHERE calendar_type = 'SOLAR' AND month = :month AND day = :day AND notify_enabled = 1
        ORDER BY year ASC, title COLLATE NOCASE ASC
    """)
    suspend fun getEventsBySolarDay(month: Int, day: Int): List<HistoricalEventEntity>

    @Query("""
        SELECT * FROM historical_event
        WHERE calendar_type = 'LUNAR' AND month = :month AND day = :day AND notify_enabled = 1
        ORDER BY year ASC, title COLLATE NOCASE ASC
    """)
    suspend fun getEventsByLunarDay(month: Int, day: Int): List<HistoricalEventEntity>

    @Query("""
        SELECT * FROM historical_event
        ORDER BY month ASC, day ASC, year ASC, title COLLATE NOCASE ASC
    """)
    fun observeAll(): Flow<List<HistoricalEventEntity>>

    @Query("""
        SELECT * FROM historical_event
        WHERE title LIKE '%' || :keyword || '%' OR description LIKE '%' || :keyword || '%'
        ORDER BY month ASC, day ASC, year ASC, title COLLATE NOCASE ASC
    """)
    fun search(keyword: String): Flow<List<HistoricalEventEntity>>
}
