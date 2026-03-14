package com.example.historycalendar.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.historycalendar.data.db.entity.CalendarType
import com.example.historycalendar.domain.LunarCalendarUtils
import java.time.LocalDate
import kotlinx.coroutines.runBlocking
import com.example.historycalendar.widget.HistoryCalendarWidgetUpdater

class DailyReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val eventRepository = EntryPointAccessorsProvider.eventRepository(context)
        val helper = EntryPointAccessorsProvider.notificationHelper(context)
        val scheduler = EntryPointAccessorsProvider.notificationScheduler(context)

        runBlocking {
            val today = LocalDate.now()
            val lunar = LunarCalendarUtils.solarToLunar(today)
            val solar = eventRepository.getTodaySolarEvents(today.monthValue, today.dayOfMonth)
            val lunarEvents = eventRepository.getTodayLunarEvents(lunar.month, lunar.day)
            val lines = (solar + lunarEvents).map {
                val years = today.year - it.year
                val label = if (it.calendarType == CalendarType.SOLAR) {
                    "%02d/%02d/%d".format(it.day, it.month, it.year)
                } else {
                    "%02d/%02d ÂL/%d".format(it.day, it.month, it.year)
                }
                "$label — ${it.title} (${years} năm)"
            }
            if (lines.isNotEmpty()) helper.showGroupedTodayNotification(lines)
            HistoryCalendarWidgetUpdater.updateAll(context)
            scheduler.scheduleNextDailyCheck()
        }
    }
}
