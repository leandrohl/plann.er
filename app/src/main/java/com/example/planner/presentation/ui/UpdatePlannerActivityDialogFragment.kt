package com.example.planner.presentation.ui

import android.content.DialogInterface
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import com.example.planner.databinding.FragmentUpdatePlannerActivityDialogBinding
import com.example.planner.domain.model.PlannerActivity
import com.example.planner.domain.utils.createCalendarFromTimeInMillis
import com.example.planner.domain.utils.toPlannerActivityDateString
import com.example.planner.domain.utils.toPlannerActivityTimeString
import com.example.planner.presentation.ui.component.PlannerActivityDatePickerDialogFragment
import com.example.planner.presentation.ui.component.PlannerActivityTimePickerDialogFragment
import com.example.planner.presentation.ui.extension.hideKeyboard
import com.example.planner.presentation.ui.viewmodel.PlannerActivityViewModel
import com.example.planner.presentation.ui.viewmodel.SetDate
import com.example.planner.presentation.ui.viewmodel.SetTime
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class UpdatePlannerActivityDialogFragment(
    private val selectedActivity: PlannerActivity
) : BottomSheetDialogFragment() {
    private var _binding: FragmentUpdatePlannerActivityDialogBinding? = null
    private val binding get() = _binding!!

    private val plannerActivityViewModel: PlannerActivityViewModel by activityViewModels()

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        plannerActivityViewModel.clearSelectedActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUpdatePlannerActivityDialogBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        plannerActivityViewModel.setSelectedActivity(selectedActivity = selectedActivity)

        with (binding) {
            val selectedActivityDateTimeCalendar = createCalendarFromTimeInMillis(timeInMillis = selectedActivity.datetime)
            tietUpdatedPlannerActivityName.setText(selectedActivity.name)
            tietUpdatedPlannerActivityDate.setText(selectedActivityDateTimeCalendar.toPlannerActivityDateString())
            tietUpdatedPlannerActivityTime.setText(selectedActivityDateTimeCalendar.toPlannerActivityTimeString())


            tietUpdatedPlannerActivityName.doOnTextChanged { text, _, _, _ ->
                if (text.toString().isEmpty()) {
                    tietUpdatedPlannerActivityName.clearFocus()
                    requireContext().hideKeyboard(fromView = tietUpdatedPlannerActivityName)
                }
                plannerActivityViewModel.updateSelectedActivity(name = text.toString())
            }

            tietUpdatedPlannerActivityDate.setOnClickListener {
                PlannerActivityDatePickerDialogFragment(
                    initialDate = createCalendarFromTimeInMillis(selectedActivity.datetime),
                    onConfirm = { year, month, dayOfMonth ->
                        val filledCalendar = Calendar.getInstance().apply {
                            set(Calendar.YEAR, year)
                            set(Calendar.MONTH, month)
                            set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        }

                        tietUpdatedPlannerActivityDate.setText(filledCalendar.toPlannerActivityDateString())
                        plannerActivityViewModel.updateSelectedActivity(
                            date = SetDate(
                                year = year,
                                month = month,
                                dayOfMonth = dayOfMonth
                            )
                        )
                    },
                    onCancel = {
                    }
                ).show(childFragmentManager, PlannerActivityDatePickerDialogFragment.TAG)
            }

            tietUpdatedPlannerActivityTime.setOnClickListener {
                PlannerActivityTimePickerDialogFragment(
                    initialTime = createCalendarFromTimeInMillis(selectedActivity.datetime),
                    onConfirm = { hourOfDay, minute->
                        val filledCalendar = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, hourOfDay)
                            set(Calendar.MINUTE, minute)
                        }

                        tietUpdatedPlannerActivityTime.setText(filledCalendar.toPlannerActivityTimeString())
                        plannerActivityViewModel.updateSelectedActivity(
                            time = SetTime(
                                hourOfDay = hourOfDay,
                                minute = minute
                            )
                        )
                    },
                    onCancel = {
                    }
                ).show(childFragmentManager, PlannerActivityTimePickerDialogFragment.TAG)
            }

            tvUpdatedPlannerActivityDelete.setOnClickListener {
                plannerActivityViewModel.deletePlannerActivity(
                    uuid = selectedActivity.uuid
                )
                dialog?.dismiss()
            }

            btnSaveUpdatedPlannerActivity.setOnClickListener {
                plannerActivityViewModel.saveUpdatedSelectedActivity()
                dialog?.dismiss()
            }
        }
    }

    companion object {
        const val TAG = "UpdatePlannerActivityDialogFragment"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}