package com.example.planner.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.planner.data.datasource.UserRegistrationLocalDataSource
import com.example.planner.data.di.MainServiceLocator

class UserRegistrationViewModel: ViewModel() {
    private val userRegistrationLocalDataSource: UserRegistrationLocalDataSource by lazy {
        MainServiceLocator.userRegistrationLocalDataSource
    }

    fun getIsUserRegistered(): Boolean {
        return userRegistrationLocalDataSource.getIsUserRegistered()
    }

    fun saveIsUserRegistered(isRegistered: Boolean) {
        userRegistrationLocalDataSource.saveIsUserRegistered(isRegistered = isRegistered)
    }
}