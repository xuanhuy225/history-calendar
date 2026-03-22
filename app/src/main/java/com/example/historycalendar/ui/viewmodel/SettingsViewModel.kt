package com.example.historycalendar.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.historycalendar.data.backup.BackupManager
import com.example.historycalendar.data.db.entity.AppSettingsEntity
import com.example.historycalendar.data.db.entity.HistoricalEventEntity
import com.example.historycalendar.data.excel.DuplicateEvent
import com.example.historycalendar.data.excel.ExcelImporter
import com.example.historycalendar.data.repository.HistoricalEventRepository
import com.example.historycalendar.data.repository.SettingsRepository
import com.example.historycalendar.notification.NotificationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val backupManager: BackupManager,
    private val notificationScheduler: NotificationScheduler,
    private val historicalEventRepository: HistoricalEventRepository,
    private val excelImporter: ExcelImporter
) : ViewModel() {
    private val message = MutableStateFlow<String?>(null)
    private val _pendingDuplicates = MutableStateFlow<List<DuplicateEvent>>(emptyList())

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.observeSettings(),
        message,
        _pendingDuplicates
    ) { settings, msg, duplicates ->
        SettingsUiState(
            notifyEnabled = settings.notifyEnabled,
            notifyHour = settings.notifyHour,
            notifyMinute = settings.notifyMinute,
            message = msg,
            pendingDuplicates = duplicates
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    fun saveNotification(enabled: Boolean, hour: Int, minute: Int) {
        viewModelScope.launch {
            settingsRepository.save(
                AppSettingsEntity(
                    notifyEnabled = enabled,
                    notifyHour = hour,
                    notifyMinute = minute
                )
            )
            notificationScheduler.scheduleNextDailyCheck()
            message.value = "Đã lưu cài đặt"
        }
    }

    suspend fun exportBackup(uri: Uri) {
        backupManager.exportToUri(uri)
        message.value = "Đã export JSON"
    }

    suspend fun importBackup(uri: Uri) {
        backupManager.importFromUri(uri)
        notificationScheduler.scheduleNextDailyCheck()
        message.value = "Đã import JSON"
    }

    /** Step 1: parse the Excel file, show conflict dialog if there are duplicates */
    fun importFromExcel(uri: Uri) {
        viewModelScope.launch {
            try {
                val result = excelImporter.parse(uri) { title ->
                    historicalEventRepository.getByTitle(title)
                }
                if (result.newEvents.isNotEmpty()) {
                    historicalEventRepository.insertAll(result.newEvents)
                }
                if (result.duplicates.isEmpty()) {
                    message.value = "Đã import ${result.newEvents.size} sự kiện"
                } else {
                    _pendingDuplicates.value = result.duplicates
                    message.value = "Đã import ${result.newEvents.size} sự kiện mới. " +
                            "Có ${result.duplicates.size} sự kiện trùng tên – hãy chọn cách xử lý."
                }
            } catch (e: Exception) {
                message.value = "Lỗi import: ${e.message}"
            }
        }
    }

    /** Overwrite all duplicate events with incoming data */
    fun resolveOverwriteAll() {
        viewModelScope.launch {
            val toUpdate = _pendingDuplicates.value.map { it.incoming.copy(id = it.existing.id) }
            toUpdate.forEach { historicalEventRepository.update(it) }
            _pendingDuplicates.value = emptyList()
            message.value = "Đã ghi đè ${toUpdate.size} sự kiện trùng"
        }
    }

    /** Keep existing records, discard duplicates from the file */
    fun resolveSkipAll() {
        _pendingDuplicates.value = emptyList()
        message.value = "Đã bỏ qua các sự kiện trùng"
    }

    /** Resolve a single duplicate entry */
    fun resolveSingle(duplicate: DuplicateEvent, overwrite: Boolean) {
        viewModelScope.launch {
            if (overwrite) {
                historicalEventRepository.update(duplicate.incoming.copy(id = duplicate.existing.id))
            }
            _pendingDuplicates.update { list -> list - duplicate }
            if (_pendingDuplicates.value.isEmpty()) {
                message.value = "Đã xử lý xong các sự kiện trùng"
            }
        }
    }

    fun clearMessage() { message.value = null }
}

data class SettingsUiState(
    val notifyEnabled: Boolean = true,
    val notifyHour: Int = 7,
    val notifyMinute: Int = 0,
    val message: String? = null,
    val pendingDuplicates: List<DuplicateEvent> = emptyList()
)
