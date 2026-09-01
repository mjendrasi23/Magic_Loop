package com.example.magicloop.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.magicloop.data.local.ReminderPreferences
import com.example.magicloop.data.local.ReminderSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReminderSettingsViewModel(
    private val preferences: ReminderPreferences,
    private val onScheduleChanged: (enabled: Boolean, hour: Int, minute: Int) -> Unit,
) : ViewModel() {

    val settings: StateFlow<ReminderSettings> = preferences.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReminderSettings())

    fun updateReminder(enabled: Boolean, hour: Int, minute: Int) {
        viewModelScope.launch {
            preferences.update(enabled, hour, minute)
            onScheduleChanged(enabled, hour, minute)
        }
    }

}