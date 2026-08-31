package com.example.magicloop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val needleSize: String? = null,
    val yarnInfo: String? = null,
    val notes: String? = null,
    val status: String = "ACTIVE", // ACTIVE, COMPLETED, ARCHIVED
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)