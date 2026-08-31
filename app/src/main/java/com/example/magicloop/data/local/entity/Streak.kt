package com.example.magicloop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "streaks")
data class StreakEntity(
    @PrimaryKey val id: Long = 1,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastActiveEpochDay: Long? = null // null = korisnik još nije bio aktivan
)