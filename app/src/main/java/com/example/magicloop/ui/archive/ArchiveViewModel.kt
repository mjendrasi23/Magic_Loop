package com.example.magicloop.ui.archive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.magicloop.data.local.entity.ProjectEntity
import com.example.magicloop.data.repository.ProjectRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ArchivedProjectUiItem(
    val project: ProjectEntity,
    val coverImagePath: String?
)

class ArchiveViewModel(
    private val repository: ProjectRepository
) : ViewModel() {

    private val _items = MutableStateFlow<List<ArchivedProjectUiItem>>(emptyList())
    val items: StateFlow<List<ArchivedProjectUiItem>> = _items

    init {
        viewModelScope.launch {
            repository.getCompletedProjects().collect { projects ->
                val withCovers = projects.map { project ->
                    val cover = repository.getFirstImageForProject(project.id)
                    ArchivedProjectUiItem(project, cover?.imagePath)
                }
                _items.value = withCovers
            }
        }
    }
}