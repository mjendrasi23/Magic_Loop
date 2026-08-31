package com.example.magicloop.ui.projectlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.magicloop.data.local.entity.ProjectEntity
import com.example.magicloop.data.repository.ProjectRepository
import com.example.magicloop.gamification.BadgeChecker
import com.example.magicloop.gamification.BadgeUnlockEvents
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProjectListViewModel(
    private val repository: ProjectRepository,
    private val badgeChecker: BadgeChecker

) : ViewModel() {

    val projects: StateFlow<List<ProjectEntity>> = repository.getAllProjects()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createProject(name: String, onCreated: (Long) -> Unit) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val id = repository.createProject(
                ProjectEntity(name = name.trim())
            )
            val unlocked = badgeChecker.onProjectCreated()
            BadgeUnlockEvents.emitAll(unlocked)
            onCreated(id)
        }
    }

    fun deleteProject(project: ProjectEntity) {
        viewModelScope.launch {
            repository.deleteProject(project)
        }
    }
}