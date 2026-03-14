package com.example.historycalendar.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.historycalendar.data.backup.BackupManager
import com.example.historycalendar.data.db.entity.AppSettingsEntity
import com.example.historycalendar.data.repository.SettingsRepository
import com.example.historycalendar.notification.NotificationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val backupManager: BackupManager,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {
    private val message = MutableStateFlow<String?>(null)

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.observeSettings(),
        message
    ) { settings, message ->
        SettingsUiState(
            notifyEnabled = settings.notifyEnabled,
            notifyHour = settings.notifyHour,
            notifyMinute = settings.notifyMinute,
            message = message
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

    fun clearMessage() { message.value = null }
}

data class SettingsUiState(
    val notifyEnabled: Boolean = true,
    val notifyHour: Int = 7,
    val notifyMinute: Int = 0,
    val message: String? = null
)
