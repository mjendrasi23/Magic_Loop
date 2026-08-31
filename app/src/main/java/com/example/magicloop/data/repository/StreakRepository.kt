package com.example.magicloop.data.repository

import com.example.magicloop.data.local.dao.StreakDao
import com.example.magicloop.data.local.entity.StreakEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class StreakRepository(
    private val streakDao: StreakDao
) {
    fun observeStreak(): Flow<StreakEntity?> = streakDao.observeStreak()

    suspend fun recordActivity() {
        val today = LocalDate.now().toEpochDay()
        val existing = streakDao.getStreak()

        val updated = when {
            existing == null -> StreakEntity(
                currentStreak = 1,
                longestStreak = 1,
                lastActiveEpochDay = today
            )

            existing.lastActiveEpochDay == today -> {
                return
            }

            existing.lastActiveEpochDay == today - 1 -> {
                val newStreak = existing.currentStreak + 1
                existing.copy(
                    currentStreak = newStreak,
                    longestStreak = maxOf(existing.longestStreak, newStreak),
                    lastActiveEpochDay = today
                )
            }

            else -> {
                existing.copy(
                    currentStreak = 1,
                    longestStreak = maxOf(existing.longestStreak, 1),
                    lastActiveEpochDay = today
                )
            }
        }

        streakDao.upsert(updated)
    }
}