package com.example.magicloop.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pattern_annotations",
    foreignKeys = [ForeignKey(
        entity = PatternSheetEntity::class,
        parentColumns = ["id"],
        childColumns = ["sheetId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("sheetId")]
)
data class PatternAnnotationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sheetId: Long,
    val pageIndex: Int,
    val pathData: String,
    val colorHex: String = "#FF0000",
    val strokeWidth: Float = 4f,
    val createdAt: Long = System.currentTimeMillis()
)