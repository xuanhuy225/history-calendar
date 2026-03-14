package com.example.historycalendar.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.historycalendar.MainActivity
import com.example.historycalendar.R
import com.example.historycalendar.data.db.entity.CalendarType
import com.example.historycalendar.domain.LunarCalendarUtils
import com.example.historycalendar.notification.EntryPointAccessorsProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

object HistoryCalendarWidgetUpdater {

    fun requestUpdate(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            updateAll(context.applicationContext)
        }
    }

    suspend fun updateAll(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, HistoryCalendarWidgetProvider::class.java)
        val ids = appWidgetManager.getAppWidgetIds(componentName)
        if (ids.isEmpty()) return

        val today = LocalDate.now()
        val lunar = LunarCalendarUtils.solarToLunar(today)
        val repo = EntryPointAccessorsProvider.eventRepository(context)
        val solarEvents = repo.getTodaySolarEvents(today.monthValue, today.dayOfMonth)
        val lunarEvents = repo.getTodayLunarEvents(lunar.month, lunar.day)
        val merged = (solarEvents + lunarEvents).sortedWith(compareBy({ it.year }, { it.title.lowercase() }))

        ids.forEach { appWidgetId ->
            val views = RemoteViews(context.packageName, R.layout.widget_history_calendar)
            val openAppIntent = Intent(context, MainActivity::class.java)
            val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            val pendingIntent = PendingIntent.getActivity(context, 701, openAppIntent, flags)
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            views.setTextViewText(R.id.widget_month, "Tháng ${today.monthValue}/${today.year}")
            views.setTextViewText(R.id.widget_day, today.dayOfMonth.toString())
            views.setTextViewText(R.id.widget_lunar, "Âm lịch ${lunar.day}/${lunar.month}")
            views.setTextViewText(R.id.widget_count, if (merged.isEmpty()) "Hôm nay chưa có sự kiện" else "${merged.size} sự kiện hôm nay")
            views.setTextViewText(R.id.widget_list, formatLines(merged, today.year))

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private fun formatLines(events: List<com.example.historycalendar.data.db.entity.HistoricalEventEntity>, currentYear: Int): String {
        if (events.isEmpty()) return "Mở app để thêm ngày lịch sử đầu tiên của bạn."
        return events.take(5).joinToString("\n") { event ->
            val years = (currentYear - event.year).coerceAtLeast(0)
            val prefix = if (event.calendarType == CalendarType.SOLAR) {
                "%02d/%02d".format(event.day, event.month)
            } else {
                "%02d/%02d ÂL".format(event.day, event.month)
            }
            "• $prefix — ${event.title} (${years} năm)"
        } + if (events.size > 5) "\n+${events.size - 5} sự kiện khác" else ""
    }
}
