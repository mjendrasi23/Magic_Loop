package com.example.magicloop.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.magicloop.MagicLoopApplication
import com.example.magicloop.MainActivity
import com.example.magicloop.R
import java.time.LocalDate

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val CHANNEL_ID = "daily_reminder_channel"
        const val NOTIFICATION_ID = 1001
    }

    override suspend fun doWork(): Result {
        val app = applicationContext as MagicLoopApplication
        val streakRepository = app.streakRepository

        val isTest = inputData.getBoolean("is_test", false)
        val current = streakRepository.getCurrentStreak()
        val today = LocalDate.now().toEpochDay()
        val alreadyActiveToday = current?.lastActiveEpochDay == today

        if (alreadyActiveToday && !isTest) {
            return Result.success()
        }

        showNotification(currentStreak = current?.currentStreak ?: 0)
        return Result.success()
    }

    private fun showNotification(currentStreak: Int) {
        createChannelIfNeeded()

        val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                applicationContext, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else true

        if (!hasPermission) return

        val intent = android.content.Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val message = if (currentStreak > 0)
            "Ne prekidaj niz od $currentStreak dana — odvoji par minuta za pletenje danas."
        else
            "Vrijeme je za malo pletenja. Otvori Magic Loop i nastavi svoj projekt."

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Magic Loop")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun createChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Dnevni podsjetnik",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Podsjetnik za pletenje i održavanje niza"
            }
            manager.createNotificationChannel(channel)
        }
    }
}