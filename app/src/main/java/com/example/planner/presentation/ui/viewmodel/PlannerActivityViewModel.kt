package com.example.planner.presentation.ui.viewmodel

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planner.core.di.MainServiceLocator
import com.example.planner.core.di.MainServiceLocator.ioDispatcher
import com.example.planner.core.di.MainServiceLocator.mainDispatcher
import com.example.planner.data.datasource.PlannerActivityLocalDataSource
import com.example.planner.domain.model.PlannerActivity
import com.example.planner.domain.utils.createCalendarFromTimeInMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
class PlannerActivityViewModel : ViewModel() {

    val plannerActivityLocalDataSource: PlannerActivityLocalDataSource by lazy {
        MainServiceLocator.plannerActivityLocalDataSource
    }

    private val _activities: MutableStateFlow<List<PlannerActivity>> = MutableStateFlow(emptyList())
    val activities: StateFlow<List<PlannerActivity>> = _activities.asStateFlow()

    private val newActivity: MutableStateFlow<NewPlannerActivity> =
        MutableStateFlow(NewPlannerActivity())

    private val updatedActivity: MutableStateFlow<PlannerActivity?> =
        MutableStateFlow(null)

    fun setSelectedActivity(selectedActivity: PlannerActivity) {
        updatedActivity.value = selectedActivity
    }

    fun clearSelectedActivity() {
        updatedActivity.value = null
    }


    fun updateSelectedActivity(
        name: String? = null,
        date: SetDate? = null,
        time: SetTime? = null
    ) {
        if (name == null && date == null && time == null) return

        updatedActivity.update { currentUpdatedActivity ->
            currentUpdatedActivity?.let { currentUpdatedActivity ->
                val updateDateTimeCalendar =
                    createCalendarFromTimeInMillis(currentUpdatedActivity.datetime)

                updateDateTimeCalendar.apply {
                    if (date != null) {
                        set(Calendar.YEAR, date.year)
                        set(Calendar.MONTH, date.month)
                        set(Calendar.DAY_OF_MONTH, date.dayOfMonth)
                    }
                    if (time != null) {
                        set(Calendar.HOUR_OF_DAY, time.hourOfDay)
                        set(Calendar.MINUTE, time.minute)
                    }
                }
                currentUpdatedActivity.copy(
                    name = name ?: currentUpdatedActivity.name,
                    datetime = updateDateTimeCalendar.timeInMillis
                )
            }
        }
    }

    fun saveUpdatedSelectedActivity() {
        updatedActivity.value?.let { updatedActivity ->
            updatePlannerActivity(updatedActivity)
        }
    }

    fun updateNewActivity(
        name: String? = null,
        date: SetDate? = null,
        time: SetTime? = null
    ) {
        if (name == null && date == null && time == null) return

        newActivity.update { currentNewActivity ->
            currentNewActivity.copy(
                name = name ?: currentNewActivity.name,
                date = date ?: currentNewActivity.date,
                time = time ?: currentNewActivity.time
            )
        }
    }

    fun saveNewActivity(
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        newActivity.value.let { newActivity ->
            if (newActivity.isFilled()) {
                insert(
                    name = newActivity.name.orEmpty(),
                    dateTime = createNewFilledCalendar().timeInMillis
                )
                this@PlannerActivityViewModel.newActivity.update { NewPlannerActivity() }

                onSuccess()
            } else {
                onError()
            }
        }
    }

    private fun createNewFilledCalendar(): Calendar {
        val calendar = Calendar.getInstance()
        return calendar.apply {
            newActivity.value.let { newActivity ->
                set(Calendar.YEAR, newActivity.date?.year ?: 0)
                set(Calendar.MONTH, newActivity.date?.month ?: 0)
                set(Calendar.DAY_OF_MONTH, newActivity.date?.dayOfMonth ?: 0)
                set(Calendar.HOUR_OF_DAY, newActivity.time?.hourOfDay ?: 0)
                set(Calendar.MINUTE, newActivity.time?.minute ?: 0)
            }
        }
    }

    fun fetchActivities() {
        viewModelScope.launch {
            plannerActivityLocalDataSource.plannerActivities
                .flowOn(ioDispatcher)
                .collect { activities ->
                    withContext(mainDispatcher) {
                        _activities.value = activities
                    }
                }
        }
    }

    private fun insert(name: String, dateTime: Long) {
        viewModelScope.launch {
            val plannerActivity = PlannerActivity(
                uuid = UUID.randomUUID().toString(),
                name = name,
                datetime = dateTime,
                isCompleted = false
            )
            withContext(ioDispatcher) {
                plannerActivityLocalDataSource.insert(plannerActivity = plannerActivity)
            }
        }
    }

    private fun updatePlannerActivity(plannerActivity: PlannerActivity) {
        viewModelScope.launch {
            withContext(ioDispatcher) {
                plannerActivityLocalDataSource.update(plannerActivity = plannerActivity)
            }
        }
    }

    fun updateIsCompleted(uuid: String, isCompleted: Boolean) {
        viewModelScope.launch {
            withContext(ioDispatcher) {
                plannerActivityLocalDataSource.updateIsCompletedByUuid(
                    uuid = uuid,
                    isCompleted = isCompleted
                )
            }
        }
    }

    fun deletePlannerActivity(uuid: String) {
        viewModelScope.launch {
            withContext(ioDispatcher) {
                plannerActivityLocalDataSource.deleteByUuid(uuid = uuid)
            }
        }
    }
}