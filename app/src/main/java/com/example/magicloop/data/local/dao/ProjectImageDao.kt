package com.example.magicloop.data.local.dao

import androidx.room.*
import com.example.magicloop.data.local.entity.ProjectImageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectImageDao {
    @Query("SELECT * FROM project_images WHERE projectId = :projectId ORDER BY addedAt DESC")
    fun getImagesForProject(projectId: Long): Flow<List<ProjectImageEntity>>

    @Insert
    suspend fun insert(image: ProjectImageEntity): Long

    @Delete
    suspend fun delete(image: ProjectImageEntity)
}