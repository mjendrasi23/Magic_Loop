package com.example.magicloop.data.repository

import com.example.magicloop.data.local.dao.BadgeDao
import com.example.magicloop.data.local.entity.UnlockedBadgeEntity
import com.example.magicloop.gamification.BadgeId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BadgeRepository(
    private val badgeDao: BadgeDao
) {
    fun observeUnlockedIds(): Flow<Set<BadgeId>> =
        badgeDao.observeUnlocked().map { list ->
            list.mapNotNull { entity ->
                runCatching { BadgeId.valueOf(entity.badgeId) }.getOrNull()
            }.toSet()
        }

    suspend fun unlock(badgeId: BadgeId): Boolean {
        if (badgeDao.isUnlocked(badgeId.name)) return false
        badgeDao.insert(UnlockedBadgeEntity(badgeId = badgeId.name))
        return true
    }
}