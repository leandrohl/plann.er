package com.example.planner.domain.model

import android.icu.util.Calendar
import com.example.planner.domain.utils.createCalendarFromTimeInMillis
import com.example.planner.domain.utils.toPlannerActivityDateString
import com.example.planner.domain.utils.toPlannerActivityDatetimeString
import com.example.planner.domain.utils.toPlannerActivityTimeString

data class PlannerActivity(
    val uuid: String,
    val name: String,
    val datetime: Long,
    val isCompleted: Boolean
){
    private val datetimeCalendar: Calendar = createCalendarFromTimeInMillis(datetime)
    val dateString: String = datetimeCalendar.toPlannerActivityDateString()
    val timeString: String = datetimeCalendar.toPlannerActivityTimeString()
    val datetimeString: String = datetimeCalendar.toPlannerActivityDatetimeString()

}