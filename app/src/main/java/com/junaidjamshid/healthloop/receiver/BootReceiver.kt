package com.junaidjamshid.healthloop.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.junaidjamshid.healthloop.util.NotificationManager

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = context.getSharedPreferences("profile_settings", Context.MODE_PRIVATE)
            val reminderEnabled = prefs.getBoolean("reminders", true)

            if (reminderEnabled) {
                val hour = prefs.getInt("reminder_hour", 23)
                val minute = prefs.getInt("reminder_minute", 0)
                val notificationManager = NotificationManager(context)
                notificationManager.scheduleDailyReminder(hour, minute)
            }
        }
    }
}
