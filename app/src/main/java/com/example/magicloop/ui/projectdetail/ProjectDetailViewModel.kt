package com.example.magicloop.ui.projectdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.magicloop.data.local.entity.CounterEntity
import com.example.magicloop.data.local.entity.ProjectEntity
import com.example.magicloop.data.repository.ProjectRepository
import com.example.magicloop.data.repository.StreakRepository
import com.example.magicloop.gamification.BadgeUnlockEvents
import com.example.magicloop.gamification.BadgeChecker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProjectDetailViewModel(
    private val repository: ProjectRepository,
    private val streakRepository: StreakRepository,
    private val projectId: Long,
    private val badgeChecker: BadgeChecker
) : ViewModel() {

    val project: StateFlow<ProjectEntity?> = repository.getProject(projectId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val counters: StateFlow<List<CounterEntity>> = repository.getCounters(projectId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addCounter(label: String) {
        if (label.isBlank()) return
        viewModelScope.launch {
            repository.addCounter(
                CounterEntity(projectId = projectId, label = label.trim())
            )
        }
    }

    fun increment(counterId: Long) {
        viewModelScope.launch {
            repository.incrementCounter(counterId)
            streakRepository.recordActivity()

            val streakBadges = badgeChecker.onStreakUpdated()
            BadgeUnlockEvents.emitAll(streakBadges)

            val counter = counters.value.find { it.id == counterId }
            if (counter?.targetValue != null && counter.currentValue + 1 >= counter.targetValue) {
                val targetBadges = badgeChecker.onCounterTargetReached()
                BadgeUnlockEvents.emitAll(targetBadges)
            }
        }
    }

    fun decrement(counterId: Long) {
        viewModelScope.launch {
            repository.decrementCounter(counterId)
            streakRepository.recordActivity()
        }
    }

    fun updateTarget(counter: CounterEntity, target: Int?) {
        viewModelScope.launch {
            repository.updateCounter(counter.copy(targetValue = target))
        }
    }

    fun updateNote(counter: CounterEntity, note: String) {
        viewModelScope.launch {
            repository.updateCounter(counter.copy(note = note.ifBlank { null }))
        }
    }

    fun resetCounter(counter: CounterEntity) {
        viewModelScope.launch {
            repository.updateCounter(counter.copy(currentValue = 0))
        }
    }

    fun markCompleted() {
        viewModelScope.launch {
            val current = project.value ?: return@launch
            repository.updateProject(
                current.copy(status = "COMPLETED", completedAt = System.currentTimeMillis())
            )
            val badges = badgeChecker.onProjectCompleted()
            BadgeUnlockEvents.emitAll(badges)
        }
    }
}