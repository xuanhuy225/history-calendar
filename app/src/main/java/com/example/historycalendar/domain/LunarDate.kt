package com.example.historycalendar.domain

data class LunarDate(
    val day: Int,
    val month: Int,
    val year: Int,
    val isLeap: Boolean = false
)
