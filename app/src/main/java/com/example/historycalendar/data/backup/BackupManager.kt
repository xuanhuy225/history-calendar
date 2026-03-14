package com.example.historycalendar.data.backup

import android.content.Context
import android.net.Uri
import com.example.historycalendar.data.repository.HistoricalEventRepository
import com.example.historycalendar.data.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class BackupManager @Inject constructor(
    private val context: Context,
    private val settingsRepository: SettingsRepository,
    private val historicalEventRepository: HistoricalEventRepository
) {
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    suspend fun exportToUri(uri: Uri) = withContext(Dispatchers.IO) {
        val backup = AppBackup(
            settings = settingsRepository.getSettings().toBackup(),
            events = historicalEventRepository.observeAll().first().map { it.toBackup() }
        )
        context.contentResolver.openOutputStream(uri)?.use { stream ->
            stream.write(json.encodeToString(backup).toByteArray())
        }
    }

    suspend fun importFromUri(uri: Uri) = withContext(Dispatchers.IO) {
        val text = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() } ?: return@withContext
        val backup = json.decodeFromString<AppBackup>(text)
        settingsRepository.save(backup.settings.toEntity())
        historicalEventRepository.clearAll()
        historicalEventRepository.insertAll(backup.events.map { it.toEntity() })
    }
}
