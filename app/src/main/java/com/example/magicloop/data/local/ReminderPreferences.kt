package com.example.magicloop.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "reminder_prefs")

data class ReminderSettings(
    val enabled: Boolean = false,
    val hour: Int = 19,
    val minute: Int = 0
)

class ReminderPreferences(private val context: Context) {

    private object Keys {
        val ENABLED = booleanPreferencesKey("reminder_enabled")
        val HOUR = intPreferencesKey("reminder_hour")
        val MINUTE = intPreferencesKey("reminder_minute")
    }

    val settings: Flow<ReminderSettings> = context.dataStore.data.map { prefs ->
        ReminderSettings(
            enabled = prefs[Keys.ENABLED] ?: false,
            hour = prefs[Keys.HOUR] ?: 19,
            minute = prefs[Keys.MINUTE] ?: 0
        )
    }

    suspend fun update(enabled: Boolean, hour: Int, minute: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ENABLED] = enabled
            prefs[Keys.HOUR] = hour
            prefs[Keys.MINUTE] = minute
        }
    }
}