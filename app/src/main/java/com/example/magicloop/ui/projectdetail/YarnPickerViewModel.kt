package com.example.magicloop.ui.projectdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.magicloop.data.local.entity.ProjectYarnUsageEntity
import com.example.magicloop.data.local.entity.YarnEntity
import com.example.magicloop.data.repository.YarnRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class YarnUsageUiItem(
    val usage: ProjectYarnUsageEntity,
    val yarn: YarnEntity?
)

class YarnPickerViewModel(
    private val repository: YarnRepository,
    private val projectId: Long
) : ViewModel() {

    val availableYarn: StateFlow<List<YarnEntity>> = repository.getAllYarn()
        .map { list -> list.filter { it.remainingGrams > 0 } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val usedYarn: StateFlow<List<YarnUsageUiItem>> = combine(
        repository.getUsageForProject(projectId),
        repository.getAllYarn()
    ) { usageList, yarnList ->
        usageList.map { usage ->
            YarnUsageUiItem(usage = usage, yarn = yarnList.find { it.id == usage.yarnId })
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun assignYarn(yarnId: Long, grams: Double) {
        if (grams <= 0) return
        viewModelScope.launch {
            repository.assignYarnToProject(projectId, yarnId, grams)
        }
    }

    fun removeUsage(usageId: Long) {
        viewModelScope.launch {
            repository.removeYarnUsage(usageId)
        }
    }
}