package com.example.planner.ui.component

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.icu.util.Calendar
import android.os.Bundle
import android.text.format.DateFormat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.planner.R

class PlannerActivityTimePickerDialogFragment(
    private val onConfirm: (hour: Int, minute: Int) -> Unit,
    private val onCancel: () -> Unit
): DialogFragment(), TimePickerDialog.OnTimeSetListener {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        return TimePickerDialog(
            requireContext(),
            this,
            hour,
            minute,
            DateFormat.is24HourFormat(requireContext()))
    }

    fun TimePickerDialog.setupPlannerTimePickerDialog(): TimePickerDialog =
        this.apply {
            window?.decorView?.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.lime_950
                )
            )

            setButton(
                DialogInterface.BUTTON_POSITIVE,
                getString(R.string.confirmar)
            ) { _, _ ->

            }

            setButton(
                DialogInterface.BUTTON_NEGATIVE,
                getString(R.string.cancelar)
            ) { _, _ ->
                onCancel()
            }
        }

    override fun onTimeSet(p0: android.widget.TimePicker?, hourOfDay: Int, minute: Int) {
        onConfirm(hourOfDay, minute)
    }

    companion object {
        const val TAG = "PlannerActivityTimePickerDialogFragment"
    }
}