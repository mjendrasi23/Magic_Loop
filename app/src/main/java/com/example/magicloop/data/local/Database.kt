package com.example.magicloop.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.magicloop.data.local.dao.*
import com.example.magicloop.data.local.entity.*

@Database(
    entities = [
        ProjectEntity::class,
        CounterEntity::class,
        PatternSheetEntity::class,
        PatternAnnotationEntity::class,
        ProjectImageEntity::class,
        StreakEntity::class,
        UnlockedBadgeEntity::class,
        YarnEntity::class,
        ProjectYarnUsageEntity::class
    ],
    version = 4,
    exportSchema = true
)
abstract class MagicLoopDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun counterDao(): CounterDao
    abstract fun patternDao(): PatternDao
    abstract fun projectImageDao(): ProjectImageDao
    abstract fun streakDao(): StreakDao

    abstract fun badgeDao(): BadgeDao

    abstract fun yarnDao(): YarnDao



    companion object {
        @Volatile private var INSTANCE: MagicLoopDatabase? = null

        fun getInstance(context: Context): MagicLoopDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MagicLoopDatabase::class.java,
                    "magic_loop.db"
                ).build().also { INSTANCE = it }
            }
    }
}