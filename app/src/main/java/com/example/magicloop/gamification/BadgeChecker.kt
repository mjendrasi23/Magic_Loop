package com.example.magicloop.gamification

import com.example.magicloop.data.repository.BadgeRepository
import com.example.magicloop.data.repository.ProjectRepository
import com.example.magicloop.data.repository.StreakRepository


class BadgeChecker(
    private val badgeRepository: BadgeRepository,
    private val projectRepository: ProjectRepository,
    private val streakRepository: StreakRepository
) {

    suspend fun onProjectCreated(): List<BadgeDefinition> {
        val newlyUnlocked = mutableListOf<BadgeDefinition>()
        if (badgeRepository.unlock(BadgeId.FIRST_PROJECT_STARTED)) {
            newlyUnlocked += BadgeCatalog.get(BadgeId.FIRST_PROJECT_STARTED)
        }
        return newlyUnlocked
    }

    suspend fun onProjectCompleted(): List<BadgeDefinition> {
        val newlyUnlocked = mutableListOf<BadgeDefinition>()

        if (badgeRepository.unlock(BadgeId.FIRST_PROJECT_COMPLETED)) {
            newlyUnlocked += BadgeCatalog.get(BadgeId.FIRST_PROJECT_COMPLETED)
        }

        val completedCount = projectRepository.getAllProjectsSnapshot()
            .count { it.status == "COMPLETED" }
        if (completedCount >= 5 && badgeRepository.unlock(BadgeId.FIVE_PROJECTS_COMPLETED)) {
            newlyUnlocked += BadgeCatalog.get(BadgeId.FIVE_PROJECTS_COMPLETED)
        }

        return newlyUnlocked
    }

    suspend fun onStreakUpdated(): List<BadgeDefinition> {
        val newlyUnlocked = mutableListOf<BadgeDefinition>()
        val streak = streakRepository.getCurrentStreak() ?: return newlyUnlocked

        val milestones = listOf(
            3 to BadgeId.STREAK_3_DAYS,
            7 to BadgeId.STREAK_7_DAYS,
            30 to BadgeId.STREAK_30_DAYS
        )

        milestones.forEach { (days, badgeId) ->
            if (streak.currentStreak >= days && badgeRepository.unlock(badgeId)) {
                newlyUnlocked += BadgeCatalog.get(badgeId)
            }
        }

        return newlyUnlocked
    }

    suspend fun onPatternImported(): List<BadgeDefinition> {
        val newlyUnlocked = mutableListOf<BadgeDefinition>()
        if (badgeRepository.unlock(BadgeId.FIRST_PATTERN_IMPORTED)) {
            newlyUnlocked += BadgeCatalog.get(BadgeId.FIRST_PATTERN_IMPORTED)
        }
        return newlyUnlocked
    }

    suspend fun onCounterTargetReached(): List<BadgeDefinition> {
        val newlyUnlocked = mutableListOf<BadgeDefinition>()
        if (badgeRepository.unlock(BadgeId.FIRST_COUNTER_TARGET_REACHED)) {
            newlyUnlocked += BadgeCatalog.get(BadgeId.FIRST_COUNTER_TARGET_REACHED)
        }
        return newlyUnlocked
    }

    suspend fun onPhotoAdded(): List<BadgeDefinition> {
        val newlyUnlocked = mutableListOf<BadgeDefinition>()
        if (badgeRepository.unlock(BadgeId.FIRST_PHOTO_ADDED)) {
            newlyUnlocked += BadgeCatalog.get(BadgeId.FIRST_PHOTO_ADDED)
        }
        return newlyUnlocked
    }

    suspend fun onYarnAdded(): List<BadgeDefinition> {
        val newlyUnlocked = mutableListOf<BadgeDefinition>()
        if (badgeRepository.unlock(BadgeId.STASH_FIRST_YARN_ADDED)) {
            newlyUnlocked += BadgeCatalog.get(BadgeId.STASH_FIRST_YARN_ADDED)
        }
        return newlyUnlocked
    }
}