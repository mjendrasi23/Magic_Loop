package com.example.magicloop

import android.app.Application
import com.example.magicloop.data.local.MagicLoopDatabase
import com.example.magicloop.data.local.ReminderPreferences
import com.example.magicloop.data.repository.BadgeRepository
import com.example.magicloop.data.repository.ProjectRepository
import com.example.magicloop.data.repository.StreakRepository
import com.example.magicloop.data.repository.YarnRepository
import com.example.magicloop.gamification.BadgeChecker

class MagicLoopApplication : Application() {
    private val database by lazy { MagicLoopDatabase.getInstance(this) }
    val repository by lazy {
        ProjectRepository(
            projectDao = database.projectDao(),
            counterDao = database.counterDao(),
            patternDao = database.patternDao(),
            imageDao = database.projectImageDao()
        )
    }

    val streakRepository by lazy {
        StreakRepository(streakDao = database.streakDao())
    }

    val reminderPreferences by lazy { ReminderPreferences(this) }

    val badgeRepository by lazy {
        BadgeRepository(badgeDao = database.badgeDao())
    }

    val badgeChecker by lazy {
        BadgeChecker(badgeRepository, repository, streakRepository)
    }

    val yarnRepository by lazy { YarnRepository(database) }


}