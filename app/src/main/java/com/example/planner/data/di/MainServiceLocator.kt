package com.example.planner.data.di

import android.app.Application
import com.example.planner.data.datasource.AuthenticationLocalDataSource
import com.example.planner.data.datasource.AuthenticationLocalDataSourceImpl
import com.example.planner.data.datasource.UserRegistrationLocalDataSource
import com.example.planner.data.datasource.UserRegistrationLocalDataSourceImpl


// uma instancia para ser entregue aos viewModels os data sources
object MainServiceLocator {
    private var _application: Application? = null
    private val application: Application get() = _application!!

    val userRegistrationLocalDataSource: UserRegistrationLocalDataSource by lazy {
        UserRegistrationLocalDataSourceImpl(application)
    }

    val authenticationLocalDataSource: AuthenticationLocalDataSource by lazy {
        AuthenticationLocalDataSourceImpl(application)
    }

    fun initialize(application: Application) {
        _application = application
    }

    fun clear() {
        _application = null
    }


}