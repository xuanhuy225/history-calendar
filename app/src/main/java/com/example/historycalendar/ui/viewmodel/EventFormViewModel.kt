package com.example.historycalendar.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.historycalendar.data.db.entity.CalendarType
import com.example.historycalendar.data.db.entity.HistoricalEventEntity
import com.example.historycalendar.data.repository.HistoricalEventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EventFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: HistoricalEventRepository
) : ViewModel() {
    private val eventId: String? = savedStateHandle["eventId"]
    private val _uiState = MutableStateFlow(EventFormUiState())
    val uiState: StateFlow<EventFormUiState> = _uiState.asStateFlow()

    init {
        if (eventId != null) {
            viewModelScope.launch {
                repository.getById(eventId)?.let {
                    _uiState.value = EventFormUiState(
                        id = it.id,
                        title = it.title,
                        description = it.description.orEmpty(),
                        calendarType = it.calendarType,
                        day = it.day.toString(),
                        month = it.month.toString(),
                        year = it.year.toString(),
                        tags = it.tags.joinToString(", "),
                        notifyEnabled = it.notifyEnabled,
                        isEdit = true
                    )
                }
            }
        }
    }

    fun updateTitle(value: String) = _uiState.update { it.copy(title = value) }
    fun updateDescription(value: String) = _uiState.update { it.copy(description = value) }
    fun updateDay(value: String) = _uiState.update { it.copy(day = value.filter(Char::isDigit).take(2)) }
    fun updateMonth(value: String) = _uiState.update { it.copy(month = value.filter(Char::isDigit).take(2)) }
    fun updateYear(value: String) = _uiState.update { it.copy(year = value.filter(Char::isDigit).take(4)) }
    fun updateTags(value: String) = _uiState.update { it.copy(tags = value) }
    fun updateCalendarType(value: CalendarType) = _uiState.update { it.copy(calendarType = value) }
    fun updateNotifyEnabled(value: Boolean) = _uiState.update { it.copy(notifyEnabled = value) }

    fun save(onDone: () -> Unit) {
        val state = _uiState.value
        val day = state.day.toIntOrNull()
        val month = state.month.toIntOrNull()
        val year = state.year.toIntOrNull()
        if (state.title.isBlank() || day == null || month == null || year == null) {
            _uiState.update { it.copy(error = "Vui lòng nhập đủ tiêu đề, ngày, tháng, năm") }
            return
        }
        val now = System.currentTimeMillis()
        val entity = HistoricalEventEntity(
            id = state.id ?: UUID.randomUUID().toString(),
            title = state.title.trim(),
            description = state.description.trim().ifBlank { null },
            calendarType = state.calendarType,
            day = day,
            month = month,
            year = year,
            tags = state.tags.split(',').map { it.trim() }.filter { it.isNotBlank() },
            notifyEnabled = state.notifyEnabled,
            createdAt = now,
            updatedAt = now
        )
        viewModelScope.launch {
            if (state.isEdit) repository.update(entity) else repository.insert(entity)
            onDone()
        }
    }
}

private inline fun MutableStateFlow<EventFormUiState>.update(transform: (EventFormUiState) -> EventFormUiState) {
    value = transform(value)
}

data class EventFormUiState(
    val id: String? = null,
    val title: String = "",
    val description: String = "",
    val calendarType: CalendarType = CalendarType.SOLAR,
    val day: String = "",
    val month: String = "",
    val year: String = "",
    val tags: String = "",
    val notifyEnabled: Boolean = true,
    val isEdit: Boolean = false,
    val error: String? = null
)
