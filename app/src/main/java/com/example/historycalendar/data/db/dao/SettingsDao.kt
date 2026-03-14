package com.example.historycalendar.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.historycalendar.data.db.entity.AppSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(settings: AppSettingsEntity)

    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsOnce(): AppSettingsEntity?

    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    fun observeSettings(): Flow<AppSettingsEntity?>

    @Query("""
        INSERT OR IGNORE INTO app_settings (
            id, notify_enabled, notify_hour, notify_minute,
            dnd_enabled, dnd_start_minute_of_day, dnd_end_minute_of_day
        ) VALUES (1, 1, 7, 0, 0, 1320, 420)
    """)
    suspend fun ensureDefaultSettings()
}
