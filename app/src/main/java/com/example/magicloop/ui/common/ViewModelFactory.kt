package com.example.magicloop.ui.common

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.magicloop.data.repository.BadgeRepository
import com.example.magicloop.data.repository.ProjectRepository
import com.example.magicloop.data.repository.StreakRepository
import com.example.magicloop.data.repository.YarnRepository
import com.example.magicloop.gamification.BadgeChecker
import com.example.magicloop.ui.archive.ArchiveViewModel
import com.example.magicloop.ui.badges.BadgesViewModel
import com.example.magicloop.ui.pattern.PatternViewModel
import com.example.magicloop.ui.projectdetail.ProjectDetailViewModel
import com.example.magicloop.ui.projectdetail.YarnPickerViewModel
import com.example.magicloop.ui.projectlist.ProjectListViewModel
import com.example.magicloop.ui.stash.StashViewModel
import com.example.magicloop.ui.streak.StreakViewModel

class ViewModelFactory(
    private val repository: ProjectRepository,
    private val streakRepository: StreakRepository,
    private val badgeRepository: BadgeRepository,
    private val badgeChecker: BadgeChecker,
    private val yarnRepository: YarnRepository,
    private val projectId: Long? = null,
    private val appContext: Context? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ProjectListViewModel::class.java) ->
                ProjectListViewModel(repository,badgeChecker) as T

            modelClass.isAssignableFrom(ProjectDetailViewModel::class.java) -> {
                requireNotNull(projectId) { "projectId je obavezan" }
                requireNotNull(appContext) { "appContext je obavezan" }
                ProjectDetailViewModel(repository, streakRepository, projectId ,badgeChecker,appContext) as T
            }

            modelClass.isAssignableFrom(PatternViewModel::class.java) -> {
                requireNotNull(projectId) { "projectId je obavezan" }
                requireNotNull(appContext) { "appContext je obavezan" }
                PatternViewModel(repository, streakRepository, projectId,badgeChecker ,appContext) as T
            }

            modelClass.isAssignableFrom(StreakViewModel::class.java) ->
                StreakViewModel(streakRepository) as T

            modelClass.isAssignableFrom(BadgesViewModel::class.java) ->
                BadgesViewModel(badgeRepository) as T

            modelClass.isAssignableFrom(ArchiveViewModel::class.java) ->
                ArchiveViewModel(repository) as T

            modelClass.isAssignableFrom(StashViewModel::class.java) ->
                StashViewModel(yarnRepository, badgeChecker) as T

            modelClass.isAssignableFrom(YarnPickerViewModel::class.java) -> {
                requireNotNull(projectId)
                YarnPickerViewModel(yarnRepository, projectId) as T
            }


            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}