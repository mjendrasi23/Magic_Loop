package com.example.magicloop.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.magicloop.data.local.entity.UnlockedBadgeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BadgeDao {
    @Query("SELECT * FROM unlocked_badges")
    fun observeUnlocked(): Flow<List<UnlockedBadgeEntity>>

    @Query("SELECT * FROM unlocked_badges")
    suspend fun getUnlocked(): List<UnlockedBadgeEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM unlocked_badges WHERE badgeId = :badgeId)")
    suspend fun isUnlocked(badgeId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(badge: UnlockedBadgeEntity)
}