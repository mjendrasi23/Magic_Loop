package com.example.magicloop.data.repository

import com.example.magicloop.data.local.dao.*
import com.example.magicloop.data.local.entity.*
import kotlinx.coroutines.flow.Flow

class ProjectRepository(
    private val projectDao: ProjectDao,
    private val counterDao: CounterDao,
    private val patternDao: PatternDao,
    private val imageDao: ProjectImageDao
) {
    fun getAllProjects(): Flow<List<ProjectEntity>> = projectDao.getAllProjects()
    fun getProject(id: Long): Flow<ProjectEntity?> = projectDao.getProjectById(id)
    suspend fun createProject(project: ProjectEntity): Long = projectDao.insert(project)
    suspend fun updateProject(project: ProjectEntity) = projectDao.update(project)
    suspend fun deleteProject(project: ProjectEntity) = projectDao.delete(project)

    suspend fun getAllProjectsSnapshot(): List<ProjectEntity> = projectDao.getAllProjectsSnapshot()

    fun getCounters(projectId: Long): Flow<List<CounterEntity>> =
        counterDao.getCountersForProject(projectId)
    suspend fun addCounter(counter: CounterEntity): Long = counterDao.insert(counter)
    suspend fun incrementCounter(counterId: Long) = counterDao.increment(counterId)
    suspend fun decrementCounter(counterId: Long) = counterDao.decrement(counterId)
    suspend fun updateCounter(counter: CounterEntity) = counterDao.update(counter)

    fun getPatternSheet(projectId: Long): Flow<PatternSheetEntity?> =
        patternDao.getSheetForProject(projectId)
    suspend fun importPatternSheet(sheet: PatternSheetEntity): Long = patternDao.insertSheet(sheet)
    fun getAnnotations(sheetId: Long, page: Int): Flow<List<PatternAnnotationEntity>> =
        patternDao.getAnnotations(sheetId, page)
    suspend fun addAnnotation(annotation: PatternAnnotationEntity): Long =
        patternDao.insertAnnotation(annotation)
    suspend fun clearAnnotations(sheetId: Long, page: Int) =
        patternDao.clearAnnotationsForPage(sheetId, page)

    fun getImages(projectId: Long): Flow<List<ProjectImageEntity>> =
        imageDao.getImagesForProject(projectId)
    suspend fun addImage(image: ProjectImageEntity): Long = imageDao.insert(image)
    suspend fun deleteImage(image: ProjectImageEntity) = imageDao.delete(image)
}