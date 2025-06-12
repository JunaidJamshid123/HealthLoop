package com.example.healthloop.util

import android.content.Context
import androidx.work.*
import com.example.healthloop.worker.DailyReminderWorker
import java.util.concurrent.TimeUnit

class NotificationManager(private val context: Context) {

    fun scheduleDailyReminder() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val dailyReminderRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(
            24, TimeUnit.HOURS,
            15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "daily_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyReminderRequest
        )
    }

    fun cancelDailyReminder() {
        WorkManager.getInstance(context).cancelUniqueWork("daily_reminder")
    }
} 