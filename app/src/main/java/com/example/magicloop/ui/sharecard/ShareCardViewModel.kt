package com.example.magicloop.ui.sharecard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.magicloop.data.repository.ProjectRepository
import com.example.magicloop.data.repository.StreakRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ShareCardViewModel(
    private val repository: ProjectRepository,
    private val streakRepository: StreakRepository,
    private val projectId: Long
) : ViewModel() {

    private val _cardData = MutableStateFlow<ShareCardData?>(null)
    val cardData: StateFlow<ShareCardData?> = _cardData

    init {
        viewModelScope.launch {
            val project = repository.getProject(projectId).first()
            val counters = repository.getCounters(projectId).first()
            val images = repository.getImages(projectId).first()
            val streak = streakRepository.observeStreak().first()

            if (project == null) return@launch

            val dateFormat = SimpleDateFormat("d. MMMM yyyy.", Locale("hr"))
            val completedText = project.completedAt?.let { dateFormat.format(Date(it)) }

            val counterSummary = counters.firstOrNull()?.let { counter ->
                "${counter.currentValue} ${counterLabelSuffix(counter.label)}"
            }

            _cardData.value = ShareCardData(
                projectName = project.name,
                coverImagePath = images.firstOrNull()?.imagePath,
                completedDateText = completedText,
                needleSize = project.needleSize,
                yarnInfo = project.yarnInfo,
                counterSummary = counterSummary,
                currentStreak = streak?.currentStreak ?: 0
            )
        }
    }

    private fun counterLabelSuffix(label: String): String = label.lowercase()
}