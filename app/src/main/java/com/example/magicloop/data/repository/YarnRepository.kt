package com.example.magicloop.data.repository

import androidx.room.withTransaction
import com.example.magicloop.data.local.MagicLoopDatabase
import com.example.magicloop.data.local.entity.ProjectYarnUsageEntity
import com.example.magicloop.data.local.entity.YarnEntity
import kotlinx.coroutines.flow.Flow

class YarnRepository(
    private val database: MagicLoopDatabase
) {
    private val yarnDao = database.yarnDao()

    fun getAllYarn(): Flow<List<YarnEntity>> = yarnDao.getAllYarn()
    fun getYarn(id: Long): Flow<YarnEntity?> = yarnDao.getYarnById(id)

    suspend fun addYarn(yarn: YarnEntity): Long = yarnDao.insert(yarn)
    suspend fun updateYarn(yarn: YarnEntity) = yarnDao.update(yarn)
    suspend fun deleteYarn(yarn: YarnEntity) = yarnDao.delete(yarn)

    fun getUsageForProject(projectId: Long): Flow<List<ProjectYarnUsageEntity>> =
        yarnDao.getUsageForProject(projectId)

    suspend fun assignYarnToProject(projectId: Long, yarnId: Long, grams: Double) {
        database.withTransaction {
            yarnDao.insertUsage(
                ProjectYarnUsageEntity(projectId = projectId, yarnId = yarnId, gramsUsed = grams)
            )
            yarnDao.deductGrams(yarnId, grams)
        }
    }

    suspend fun removeYarnUsage(usageId: Long) {
        database.withTransaction {
            val usage = yarnDao.getUsageById(usageId) ?: return@withTransaction
            yarnDao.deleteUsage(usage)
            yarnDao.restoreGrams(usage.yarnId, usage.gramsUsed)
        }
    }
}