package com.example.planner.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.planner.R
import com.example.planner.data.utils.imageBitmapToBase64
import com.example.planner.data.utils.imageUriToBitmap
import com.example.planner.databinding.FragmentUserRegistrationBinding
import com.example.planner.ui.viewmodel.UserRegistrationViewModel
import kotlinx.coroutines.launch

class UserRegistrationFragment : Fragment() {

    private var _binding: FragmentUserRegistrationBinding? = null
    private val binding get() = _binding!!

    private val navController by lazy { findNavController() }

    private val userRegistrationViewModel by activityViewModels<UserRegistrationViewModel>()

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val imageBitmap = requireContext().imageUriToBitmap(uri)
            imageBitmap?.let {
                val imageBase64 = imageBitmapToBase64(imageBitmap)
                userRegistrationViewModel.updateProfile(image = imageBase64)
                binding.ivAddPhoto.setImageURI(uri)
            }
        } else {
            Toast.makeText(
                requireContext(),
                "Oops... Nenhuma foto seleciona.",
            Toast.LENGTH_SHORT
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUserRegistrationBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObservers()
        with (binding) {
            ivAddPhoto.setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            tietName.addTextChangedListener { text ->
                userRegistrationViewModel.updateProfile(name = text.toString())
            }

            tietEmail.addTextChangedListener { text ->
                userRegistrationViewModel.updateProfile(email = text.toString())
            }

            tietPhone.addTextChangedListener { text ->
                userRegistrationViewModel.updateProfile(phone = text.toString())
            }


            btnSaveUser.setOnClickListener {
                userRegistrationViewModel.saveProfile(
                    onCompleted = {
                        navController.navigate(R.id.action_userRegistrationFragment_to_homeFragment)
                    }
                )
            }
        }
    }

    private fun setObservers() {
        lifecycleScope.launch {
            userRegistrationViewModel.isProfileValid.collect { isValid ->
                binding.btnSaveUser.isEnabled = isValid
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}