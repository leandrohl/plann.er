package com.example.planner.presentation.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.planner.R
import com.example.planner.domain.utils.base64ToImageBitmap
import com.example.planner.databinding.FragmentHomeBinding
import com.example.planner.presentation.ui.component.PlannerActivityAdapter
import com.example.planner.presentation.ui.component.PlannerActivityDatePickerDialogFragment
import com.example.planner.presentation.ui.component.PlannerActivityTimePickerDialogFragment
import com.example.planner.presentation.ui.viewmodel.UserRegistrationViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import com.example.planner.presentation.ui.viewmodel.PlannerActivityViewModel
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val userRegistrationViewModel by activityViewModels<UserRegistrationViewModel>()
    private val plannerActivityViewModel by activityViewModels<PlannerActivityViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        with (binding) {
            plannerActivityViewModel.fetchActivities()

            tietNewPlannerActivityDate.setOnClickListener {
                PlannerActivityDatePickerDialogFragment(
                    onConfirm = { year, month, dayOfMonth ->
//                        tietNewPlannerActivityDate.setText(
//                            getString(R.string.data_formatada, dayOfMonth, month + 1, year)
//                        )
                    },
                    onCancel = {
//                        tietNewPlannerActivityDate.text?.clear()
                    }
                ).show(childFragmentManager, PlannerActivityDatePickerDialogFragment.TAG)
            }

            tietNewPlannerActivityTime.setOnClickListener {
                PlannerActivityTimePickerDialogFragment(
                    onConfirm = { hour, minute ->
//                        tietNewPlannerActivityDate.setText(
//                            getString(R.string.data_formatada, dayOfMonth, month + 1, year)
//                        )
                    },
                    onCancel = {
//                        tietNewPlannerActivityDate.text?.clear()
                    }
                ).show(childFragmentManager, PlannerActivityTimePickerDialogFragment.TAG)
            }

            btnSaveNewPlannerActivity.setOnClickListener {
                UpdatePlannerActivityDialogFragment().show(
                    childFragmentManager,
                    UpdatePlannerActivityDialogFragment.TAG
                )
            }
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            launch {
                userRegistrationViewModel.profile.collect { profile ->
                    binding.tvUserName.text = getString(R.string.ola_usuario, profile.name)
                    base64ToImageBitmap(profile.image)?.let { imageBitmap ->
                        binding.ivUserPhoto.setImageBitmap(imageBitmap)
                    }
                }
            }
            launch {
                userRegistrationViewModel.isTokenValid.distinctUntilChanged {
                    old, new -> old == new
                }.collect { isTokenValid ->
                    Log.d("CheckIsTokenValid", "setupObservers: isTokenValid: $isTokenValid")
                    if (isTokenValid == false) {
                        showNewTokenSnackBar()
                    }
                }
            }
            launch {
                plannerActivityViewModel.activities.collect { activities ->
                    with(binding) {
                        if(rvPlannerActivities.adapter == null) {
                            rvPlannerActivities.adapter = PlannerActivityAdapter()
                        }
                        (rvPlannerActivities.adapter as PlannerActivityAdapter).submitList(
                            activities
                        )
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showNewTokenSnackBar() {
        Snackbar.make(requireView(), "Oops... o seu token expirou, por favor, faça login novamente.", Snackbar.LENGTH_INDEFINITE)
            .setAction("Obter novo token") {
                userRegistrationViewModel.obtainNewToken()
            }
            .setActionTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.lime_300
                )
            ).show()
    }
}