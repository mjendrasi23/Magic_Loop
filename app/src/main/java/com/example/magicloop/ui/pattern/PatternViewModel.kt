package com.example.magicloop.ui.pattern

import android.content.Context
import android.net.Uri
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.magicloop.data.local.entity.PatternAnnotationEntity
import com.example.magicloop.data.local.entity.PatternSheetEntity
import com.example.magicloop.data.local.util.PdfFileManager
import com.example.magicloop.data.repository.ProjectRepository
import com.example.magicloop.data.repository.StreakRepository
import com.example.magicloop.gamification.BadgeChecker
import com.example.magicloop.gamification.BadgeUnlockEvents
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PatternViewModel(
    private val repository: ProjectRepository,
    private val streakRepository: StreakRepository,
    private val projectId: Long,
    private val badgeChecker: BadgeChecker,
    private val appContext: Context
) : ViewModel() {

    val sheet: StateFlow<PatternSheetEntity?> = repository.getPatternSheet(projectId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage

    val annotations: StateFlow<List<PatternAnnotationEntity>> =
        combine(sheet, currentPage) { s, page -> s to page }
            .flatMapLatest { (s, page) ->
                if (s == null) flowOf(emptyList())
                else repository.getAnnotations(s.id, page)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun importPdf(uri: Uri) {
        viewModelScope.launch {
            val path = PdfFileManager.importPdf(appContext, uri, projectId)
            val pageCount = PdfFileManager.getPageCount(path)
            repository.importPatternSheet(
                PatternSheetEntity(
                    projectId = projectId,
                    pdfUriPath = path,
                    pageCount = pageCount.coerceAtLeast(1)
                )
            )
            _currentPage.value = 0

            val badges = badgeChecker.onPatternImported()
            BadgeUnlockEvents.emitAll(badges)

        }
    }

    fun goToPage(index: Int) {
        val max = (sheet.value?.pageCount ?: 1) - 1
        _currentPage.value = index.coerceIn(0, max.coerceAtLeast(0))
    }

    fun saveStroke(points: List<Offset>, colorHex: String) {
        val currentSheet = sheet.value ?: return
        if (points.size < 2) return
        viewModelScope.launch {
            repository.addAnnotation(
                PatternAnnotationEntity(
                    sheetId = currentSheet.id,
                    pageIndex = _currentPage.value,
                    pathData = PathEncoding.encode(points),
                    colorHex = colorHex
                )
            )
            streakRepository.recordActivity()

        }
    }

    fun clearPageAnnotations() {
        val currentSheet = sheet.value ?: return
        viewModelScope.launch {
            repository.clearAnnotations(currentSheet.id, _currentPage.value)
        }
    }
}