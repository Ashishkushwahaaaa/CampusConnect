package com.ashish.campusconnect.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class SessionManager(private val context: Context) {
    companion object {
        val IS_GUEST = booleanPreferencesKey("is_guest")
    }

    val isGuest: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_GUEST] ?: false
        }

    suspend fun setGuestMode(isGuestMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_GUEST] = isGuestMode
        }
    }

    suspend fun setLoggedInUser() {
        context.dataStore.edit { preferences ->
            preferences[IS_GUEST] = false
        }
    }
}
