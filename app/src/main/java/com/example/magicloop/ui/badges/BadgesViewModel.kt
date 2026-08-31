package com.example.magicloop.ui.badges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.magicloop.data.repository.BadgeRepository
import com.example.magicloop.gamification.BadgeCatalog
import com.example.magicloop.gamification.BadgeDefinition
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class BadgeUiItem(
    val definition: BadgeDefinition,
    val isUnlocked: Boolean
)

class BadgesViewModel(
    repository: BadgeRepository
) : ViewModel() {

    val badges: StateFlow<List<BadgeUiItem>> = repository.observeUnlockedIds()
        .map { unlockedIds ->
            BadgeCatalog.all.map { def ->
                BadgeUiItem(definition = def, isUnlocked = def.id in unlockedIds)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}