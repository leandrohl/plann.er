package com.example.planner.data.datasource

import com.example.planner.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface UserRegistrationLocalDataSource {

    fun getIsUserRegistered(): Boolean

    fun saveIsUserRegistered(isRegistered: Boolean)

    val profile: Flow<Profile>

    suspend fun saveProfile(profile: Profile)
}