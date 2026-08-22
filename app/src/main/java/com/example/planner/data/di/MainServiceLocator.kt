package com.example.planner.data.di

import android.app.Application
import com.example.planner.data.datasource.UserRegistrationLocalDataSource
import com.example.planner.data.datasource.UserRegistrationLocalDataSourceImpl

object MainServiceLocator {
    private var _application: Application? = null
    private val application: Application get() = _application!!

    val userRegistrationLocalDataSource: UserRegistrationLocalDataSource by lazy {
        UserRegistrationLocalDataSourceImpl(application)
    }

    fun initialize(application: Application) {
        _application = application
    }

    fun clear() {
        _application = null
    }


}