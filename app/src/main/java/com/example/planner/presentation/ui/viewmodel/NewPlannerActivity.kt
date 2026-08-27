package com.example.planner.presentation.ui.viewmodel

data class NewPlannerActivity(
    val name: String? = null,
    val date: SetDate? = null,
    val time: SetTime? = null
) {
    fun isFilled(): Boolean {
        return !name.isNullOrBlank() && date != null && time != null
    }
}

data class SetDate(
    val year: Int,
    val month: Int,
    val dayOfMonth: Int
)

data class SetTime(
    val hourOfDay: Int,
    val minute: Int,
)