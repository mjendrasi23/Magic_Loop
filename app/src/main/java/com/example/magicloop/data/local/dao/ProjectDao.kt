package com.example.magicloop.data.local.dao

import androidx.room.*
import com.example.magicloop.data.local.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :projectId")
    fun getProjectById(projectId: Long): Flow<ProjectEntity?>

    @Query("SELECT * FROM projects")
    suspend fun getAllProjectsSnapshot(): List<ProjectEntity>

    @Query("SELECT * FROM projects WHERE status = 'COMPLETED' ORDER BY completedAt DESC")
    fun getCompletedProjects(): Flow<List<ProjectEntity>>

    @Insert
    suspend fun insert(project: ProjectEntity): Long

    @Update
    suspend fun update(project: ProjectEntity)

    @Delete
    suspend fun delete(project: ProjectEntity)
}