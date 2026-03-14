package com.example.historycalendar.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context

class HistoryCalendarWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        HistoryCalendarWidgetUpdater.requestUpdate(context)
    }

    override fun onEnabled(context: Context) {
        HistoryCalendarWidgetUpdater.requestUpdate(context)
    }
}
