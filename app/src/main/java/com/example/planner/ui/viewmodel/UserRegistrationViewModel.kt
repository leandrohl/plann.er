package com.example.planner.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planner.data.datasource.AuthenticationLocalDataSource
import com.example.planner.data.datasource.UserRegistrationLocalDataSource
import com.example.planner.core.di.MainServiceLocator
import com.example.planner.domain.model.Profile
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

private val mockToken = """
    eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.
    eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.
    KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30
""".trimIndent() //removido todas as margem e espaços em branco do token

class UserRegistrationViewModel: ViewModel() {
    private val userRegistrationLocalDataSource: UserRegistrationLocalDataSource by lazy {
        MainServiceLocator.userRegistrationLocalDataSource
    }

    private val authenticationLocalDataSource: AuthenticationLocalDataSource by lazy {
        MainServiceLocator.authenticationLocalDataSource
    }

    private val _isProfileValid: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isProfileValid: StateFlow<Boolean> = _isProfileValid.asStateFlow()
    private val _profile: MutableStateFlow<Profile> = MutableStateFlow(Profile())
    val profile: StateFlow<Profile> = _profile.asStateFlow()

    private val _isTokenValid: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val isTokenValid: StateFlow<Boolean?> = _isTokenValid.asStateFlow()

    init {
        viewModelScope.launch {
            launch {
                userRegistrationLocalDataSource.profile.collect { profile ->
                    _profile.value = profile
                }
            }
            launch {
                while (true) {
                    val tokenExpirationDatetime = authenticationLocalDataSource.expirationDatetime.firstOrNull()
                    tokenExpirationDatetime?.let { expirationDatetime ->
                        val currentTime = System.currentTimeMillis()
                        _isTokenValid.value = expirationDatetime > currentTime

                        Log.d("CheckIsTokenValid", "viewModelScope: isTokenValid = ${_isTokenValid.value}, expirationDatetime = $expirationDatetime, currentTime = $currentTime")
                    }
                    delay(5_000.milliseconds) // Check every 50 seconds
                }
            }
        }
    }

    fun getIsUserRegistered(): Boolean {
        return userRegistrationLocalDataSource.getIsUserRegistered()
    }

    fun updateProfile(
        name: String? = null,
        email: String? = null,
        phone: String? = null,
        image: String? = null
    ) {
        if (name == null && email == null && phone == null && image == null) {
            return
        }

        _profile.update { currentProfile ->
            val updatedProfile = currentProfile.copy(
                name = name ?: currentProfile.name,
                email = email ?: currentProfile.email,
                phone = phone ?: currentProfile.phone,
                image = image ?: currentProfile.image
            )
            _isProfileValid.update { updatedProfile.isValid() }
            updatedProfile
        }

        Log.d("UserRegistrationViewModel", "Profile updated: ${_profile.value}")
    }

    fun saveProfile(onCompleted: () -> Unit) {
        viewModelScope.launch {
            async {
                userRegistrationLocalDataSource.saveProfile(_profile.value)
                userRegistrationLocalDataSource.saveIsUserRegistered(true)
                authenticationLocalDataSource.insertToken(mockToken)
                _isTokenValid.value = true
            }.await()
            onCompleted()
        }
    }

    fun obtainNewToken() {
        viewModelScope.launch {
            authenticationLocalDataSource.insertToken(mockToken)
            _isTokenValid.value = true
        }
    }
}