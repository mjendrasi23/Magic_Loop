package com.example.magicloop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "yarn_stash")
data class YarnEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val brand: String? = null,
    val color: String,
    val weightCategory: String,
    val totalGrams: Double,
    val remainingGrams: Double,
    val notes: String? = null,
    val addedAt: Long = System.currentTimeMillis()
)