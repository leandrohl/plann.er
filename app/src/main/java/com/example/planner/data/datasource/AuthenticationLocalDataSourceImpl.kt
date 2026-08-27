package com.example.planner.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val AUTHENTICATION_PREFERENCES_FILE_NAME = "authentication_preferences"
private const val TOKEN_KEY = "token"
private val TOKEN_PREFERENCES_KEY = stringPreferencesKey(TOKEN_KEY)
private const val EXPIRATION_DATETIME_KEY = "expiration_datetime"
private val EXPIRATION_DATETIME_PREFERENCES_KEY = longPreferencesKey(EXPIRATION_DATETIME_KEY)

private const val ADDITIONAL_EXPIRATION_TIME_MILLIS = 60_000 * 60 // 1 hour in milliseconds

class AuthenticationLocalDataSourceImpl(
    private val applicationContext: Context
) : AuthenticationLocalDataSource {
    val Context.authenticationPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
        name = AUTHENTICATION_PREFERENCES_FILE_NAME
    )

    override val token: Flow<String>
        get() = applicationContext.authenticationPreferencesDataStore.data.map { settings ->
            settings[TOKEN_PREFERENCES_KEY].orEmpty()
        }
    override val expirationDatetime: Flow<Long>
        get() = applicationContext.authenticationPreferencesDataStore.data
            .map { settings ->
                settings[EXPIRATION_DATETIME_PREFERENCES_KEY] ?: 0
            }



    override suspend fun insertToken(token: String) {
        applicationContext.authenticationPreferencesDataStore.edit { settings ->
            settings[TOKEN_PREFERENCES_KEY] = token
            settings[EXPIRATION_DATETIME_PREFERENCES_KEY] =
                System.currentTimeMillis() + ADDITIONAL_EXPIRATION_TIME_MILLIS // 10 seconds in milliseconds
        }
    }
}