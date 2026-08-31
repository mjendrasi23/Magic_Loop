package com.example.magicloop.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.magicloop.MagicLoopApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val app = context.applicationContext as MagicLoopApplication
        CoroutineScope(Dispatchers.IO).launch {
            val settings = app.reminderPreferences.settings.first()
            if (settings.enabled) {
                ReminderScheduler.schedule(app, settings.hour, settings.minute)
            }
        }
    }
}