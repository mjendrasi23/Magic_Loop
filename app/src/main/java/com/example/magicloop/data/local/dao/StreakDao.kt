package com.example.magicloop.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.magicloop.data.local.entity.StreakEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StreakDao {
    @Query("SELECT * FROM streaks WHERE id = 1")
    fun observeStreak(): Flow<StreakEntity?>

    @Query("SELECT * FROM streaks WHERE id = 1")
    suspend fun getStreak(): StreakEntity?

    @Upsert
    suspend fun upsert(streak: StreakEntity)
}