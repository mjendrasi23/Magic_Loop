package com.example.magicloop.ui.streak

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.magicloop.data.repository.StreakRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class StreakUiState(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val isActiveToday: Boolean = false
)

class StreakViewModel(
    repository: StreakRepository
) : ViewModel() {

    val uiState: StateFlow<StreakUiState> = repository.observeStreak()
        .map { streak ->
            if (streak == null) {
                StreakUiState()
            } else {
                val today = java.time.LocalDate.now().toEpochDay()
                StreakUiState(
                    currentStreak = streak.currentStreak,
                    longestStreak = streak.longestStreak,
                    isActiveToday = streak.lastActiveEpochDay == today
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StreakUiState())
}