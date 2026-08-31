package com.example.magicloop.data.local.dao

import androidx.room.*
import com.example.magicloop.data.local.entity.PatternAnnotationEntity
import com.example.magicloop.data.local.entity.PatternSheetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PatternDao {
    @Query("SELECT * FROM pattern_sheets WHERE projectId = :projectId")
    fun getSheetForProject(projectId: Long): Flow<PatternSheetEntity?>

    @Insert
    suspend fun insertSheet(sheet: PatternSheetEntity): Long

    @Delete
    suspend fun deleteSheet(sheet: PatternSheetEntity)

    @Query("SELECT * FROM pattern_annotations WHERE sheetId = :sheetId AND pageIndex = :page")
    fun getAnnotations(sheetId: Long, page: Int): Flow<List<PatternAnnotationEntity>>

    @Insert
    suspend fun insertAnnotation(annotation: PatternAnnotationEntity): Long

    @Delete
    suspend fun deleteAnnotation(annotation: PatternAnnotationEntity)

    @Query("DELETE FROM pattern_annotations WHERE sheetId = :sheetId AND pageIndex = :page")
    suspend fun clearAnnotationsForPage(sheetId: Long, page: Int)
}