package com.example.magicloop.data.local.dao

import androidx.room.*
import com.example.magicloop.data.local.entity.ProjectYarnUsageEntity
import com.example.magicloop.data.local.entity.YarnEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface YarnDao {
    @Query("SELECT * FROM yarn_stash ORDER BY addedAt DESC")
    fun getAllYarn(): Flow<List<YarnEntity>>

    @Query("SELECT * FROM yarn_stash WHERE id = :id")
    fun getYarnById(id: Long): Flow<YarnEntity?>

    @Insert
    suspend fun insert(yarn: YarnEntity): Long

    @Update
    suspend fun update(yarn: YarnEntity)

    @Delete
    suspend fun delete(yarn: YarnEntity)

    @Query("UPDATE yarn_stash SET remainingGrams = remainingGrams - :grams WHERE id = :yarnId")
    suspend fun deductGrams(yarnId: Long, grams: Double)

    @Query("UPDATE yarn_stash SET remainingGrams = remainingGrams + :grams WHERE id = :yarnId")
    suspend fun restoreGrams(yarnId: Long, grams: Double)

    @Query("SELECT * FROM project_yarn_usage WHERE projectId = :projectId")
    fun getUsageForProject(projectId: Long): Flow<List<ProjectYarnUsageEntity>>

    @Insert
    suspend fun insertUsage(usage: ProjectYarnUsageEntity): Long

    @Query("SELECT * FROM project_yarn_usage WHERE id = :usageId")
    suspend fun getUsageById(usageId: Long): ProjectYarnUsageEntity?

    @Delete
    suspend fun deleteUsage(usage: ProjectYarnUsageEntity)
}