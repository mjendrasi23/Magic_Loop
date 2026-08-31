package com.example.magicloop.ui.stash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.magicloop.data.local.entity.YarnEntity
import com.example.magicloop.data.repository.YarnRepository
import com.example.magicloop.gamification.BadgeChecker
import com.example.magicloop.gamification.BadgeUnlockEvents
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StashViewModel(
    private val repository: YarnRepository,
    private val badgeChecker: BadgeChecker
) : ViewModel() {

    val yarnList: StateFlow<List<YarnEntity>> = repository.getAllYarn()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addYarn(
        name: String,
        brand: String?,
        color: String,
        weightCategory: String,
        totalGrams: Double,
        notes: String?
    ) {
        if (name.isBlank() || color.isBlank() || totalGrams <= 0) return
        viewModelScope.launch {
            repository.addYarn(
                YarnEntity(
                    name = name.trim(),
                    brand = brand?.trim()?.ifBlank { null },
                    color = color.trim(),
                    weightCategory = weightCategory,
                    totalGrams = totalGrams,
                    remainingGrams = totalGrams,
                    notes = notes?.trim()?.ifBlank { null }
                )
            )
            val badges = badgeChecker.onYarnAdded()
            BadgeUnlockEvents.emitAll(badges)
        }
    }

    fun updateYarn(yarn: YarnEntity) {
        viewModelScope.launch { repository.updateYarn(yarn) }
    }

    fun deleteYarn(yarn: YarnEntity) {
        viewModelScope.launch { repository.deleteYarn(yarn) }
    }
}