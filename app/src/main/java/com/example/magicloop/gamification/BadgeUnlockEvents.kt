// gamification/BadgeUnlockEvents.kt
package com.example.magicloop.gamification

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

object BadgeUnlockEvents {
    private val _events = MutableSharedFlow<BadgeDefinition>(extraBufferCapacity = 5)
    val events: SharedFlow<BadgeDefinition> = _events

    suspend fun emit(badge: BadgeDefinition) {
        _events.emit(badge)
    }

    suspend fun emitAll(badges: List<BadgeDefinition>) {
        badges.forEach { _events.emit(it) }
    }
}