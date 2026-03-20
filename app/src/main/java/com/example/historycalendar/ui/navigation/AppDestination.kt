package com.example.historycalendar.ui.navigation

object AppDestination {
    const val Home = "home"
    const val AddEvent = "add_event"
    const val EditEvent = "edit_event/{eventId}"
    const val Today = "today"
    const val DayEvents = "day_events/{date}"
    const val Settings = "settings"

    fun dayEvents(date: String) = "day_events/$date"
}
