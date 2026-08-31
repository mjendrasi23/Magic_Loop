package com.example.magicloop.notification

import android.content.Context
import androidx.work.*
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    private const val WORK_NAME = "daily_knitting_reminder"

    fun schedule(context: Context, hour: Int, minute: Int) {
        val now = LocalDateTime.now()
        var target = now.toLocalDate().atTime(LocalTime.of(hour, minute))
        if (target.isBefore(now)) {
            target = target.plusDays(1)
        }
        val initialDelayMinutes = java.time.Duration.between(now, target).toMinutes()

        val request = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelayMinutes, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}