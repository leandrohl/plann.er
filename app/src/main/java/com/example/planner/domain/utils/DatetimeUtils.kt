package com.example.planner.domain.utils

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import java.util.Locale

// EEE - Day of the week (e.g., Mon, Tue, Wed)
// MMMM - Full month name (e.g., January, February, March)
// dd - Day of the month (01 to 31)
// HH - Hour in 24-hour format (00 to 23)
// MM - Minute (00 to 59)
private val sdfPlannerActivityDatetime = SimpleDateFormat("EEE dd'\n'HH:mm", Locale("pt", "BR"))
private val sdfPlannerActivityDate = SimpleDateFormat("dd 'de' MMMM", Locale("pt", "BR"))
private val sdfPlannerActivityTime = SimpleDateFormat("HH:mm", Locale("pt", "BR"))

fun createCalendarFromTimeInMillis(timeInMillis: Long): Calendar {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeInMillis
    return calendar
}

fun Calendar.toPlannerActivityDatetimeString(): String = sdfPlannerActivityDatetime.format(this.time)
fun Calendar.toPlannerActivityDateString(): String = sdfPlannerActivityDate.format(this.time)
fun Calendar.toPlannerActivityTimeString(): String = sdfPlannerActivityTime.format(this.time)
