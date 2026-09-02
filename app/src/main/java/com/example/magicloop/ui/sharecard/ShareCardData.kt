package com.example.magicloop.ui.sharecard

data class ShareCardData(
    val projectName: String,
    val coverImagePath: String?,
    val completedDateText: String?,
    val needleSize: String?,
    val yarnInfo: String?,
    val currentStreak: Int
)