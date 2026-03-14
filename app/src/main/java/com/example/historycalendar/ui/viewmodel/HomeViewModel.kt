package com.example.historycalendar.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.historycalendar.data.db.entity.CalendarType
import com.example.historycalendar.data.db.entity.HistoricalEventEntity
import com.example.historycalendar.data.repository.HistoricalEventRepository
import com.example.historycalendar.domain.LunarCalendarUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val historicalEventRepository: HistoricalEventRepository
) : ViewModel() {
    private val search = MutableStateFlow("")
    private val currentMonth = MutableStateFlow(YearMonth.now())

    val uiState: StateFlow<HomeUiState> = combine(
        currentMonth,
        search,
        historicalEventRepository.observeAll()
    ) { month, keyword, events ->
        val filtered = if (keyword.isBlank()) events else events.filter {
            it.title.contains(keyword, true) || (it.description?.contains(keyword, true) == true)
        }
        val days = buildMonthCells(month, events)
        HomeUiState(
            currentMonthLabel = "Tháng ${month.monthValue}/${month.year}",
            monthCells = days,
            events = filtered.map(::toUi),
            search = keyword
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun updateSearch(value: String) { search.value = value }
    fun nextMonth() { currentMonth.update { it.plusMonths(1) } }
    fun previousMonth() { currentMonth.update { it.minusMonths(1) } }

    fun deleteEvent(id: String) {
        viewModelScope.launch { historicalEventRepository.deleteById(id) }
    }

    private fun toUi(entity: HistoricalEventEntity): EventListItemUi {
        val typeLabel = if (entity.calendarType == CalendarType.SOLAR) "Dương" else "Âm"
        val years = LocalDate.now().year - entity.year
        val subtitle = "%02d/%02d/%d • %s lịch • %d năm".format(entity.day, entity.month, entity.year, typeLabel, years)
        return EventListItemUi(entity.id, entity.title, subtitle, entity.description.orEmpty())
    }

    private fun buildMonthCells(month: YearMonth, events: List<HistoricalEventEntity>): List<CalendarCellUi> {
        val first = month.atDay(1)
        val startOffset = first.dayOfWeek.value % 7
        val totalCells = 42
        val list = mutableListOf<CalendarCellUi>()
        val firstDisplayed = first.minusDays(startOffset.toLong())
        repeat(totalCells) { index ->
            val date = firstDisplayed.plusDays(index.toLong())
            val lunar = LunarCalendarUtils.solarToLunar(date)
            val count = events.count { event ->
                when (event.calendarType) {
                    CalendarType.SOLAR -> event.day == date.dayOfMonth && event.month == date.monthValue
                    CalendarType.LUNAR -> event.day == lunar.day && event.month == lunar.month
                }
            }
            list += CalendarCellUi(
                solarDay = date.dayOfMonth,
                lunarText = "%d/%d".format(lunar.day, lunar.month),
                isCurrentMonth = date.monthValue == month.monthValue,
                isToday = date == LocalDate.now(),
                eventCount = count
            )
        }
        return list
    }
}

data class HomeUiState(
    val currentMonthLabel: String = "",
    val monthCells: List<CalendarCellUi> = emptyList(),
    val events: List<EventListItemUi> = emptyList(),
    val search: String = ""
)

data class CalendarCellUi(
    val solarDay: Int,
    val lunarText: String,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val eventCount: Int
)

data class EventListItemUi(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String
)
