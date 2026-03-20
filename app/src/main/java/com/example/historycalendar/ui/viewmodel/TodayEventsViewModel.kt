package com.example.historycalendar.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.historycalendar.data.db.entity.CalendarType
import com.example.historycalendar.data.repository.HistoricalEventRepository
import com.example.historycalendar.domain.LunarCalendarUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TodayEventsViewModel @Inject constructor(
    private val repository: HistoricalEventRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(TodayEventsUiState())
    val uiState: StateFlow<TodayEventsUiState> = _uiState.asStateFlow()

    init {
        // "date" arg is ISO format (yyyy-MM-dd); falls back to today if absent
        val dateArg = savedStateHandle.get<String>("date")
        val date = if (dateArg != null) runCatching { LocalDate.parse(dateArg) }.getOrNull()
                   else null
        loadDay(date ?: LocalDate.now())
    }

    private fun loadDay(date: LocalDate) {
        viewModelScope.launch {
            val lunar = LunarCalendarUtils.solarToLunar(date)
            val solar = repository.getTodaySolarEvents(date.monthValue, date.dayOfMonth)
            val lunarEvents = repository.getTodayLunarEvents(lunar.month, lunar.day)
            val today = LocalDate.now()
            val items = (solar + lunarEvents).map {
                val years = today.year - it.year
                TodayEventUi(
                    title = it.title,
                    subtitle = "%02d/%02d/%d • %s lịch • %d năm".format(
                        it.day, it.month, it.year,
                        if (it.calendarType == CalendarType.SOLAR) "Dương" else "Âm",
                        years
                    ),
                    description = it.description.orEmpty()
                )
            }
            val isToday = date == today
            val dayLabel = if (isToday) "Hôm nay ${date.dayOfMonth}/${date.monthValue}/${date.year}"
                           else "${date.dayOfMonth}/${date.monthValue}/${date.year}"
            _uiState.value = TodayEventsUiState(
                title = dayLabel,
                subtitle = "Âm lịch: ${lunar.day}/${lunar.month}",
                events = items
            )
        }
    }
}

data class TodayEventsUiState(
    val title: String = "",
    val subtitle: String = "",
    val events: List<TodayEventUi> = emptyList()
)

data class TodayEventUi(
    val title: String,
    val subtitle: String,
    val description: String
)

