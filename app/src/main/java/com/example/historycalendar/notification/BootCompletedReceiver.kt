package com.example.historycalendar.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.historycalendar.widget.HistoryCalendarWidgetUpdater

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        EntryPointAccessorsProvider.notificationScheduler(context).scheduleNextDailyCheck()
        HistoryCalendarWidgetUpdater.requestUpdate(context)
    }
}
