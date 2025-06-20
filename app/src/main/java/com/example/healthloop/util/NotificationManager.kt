package com.example.healthloop.util

import android.content.Context
import androidx.work.*
import com.example.healthloop.worker.DailyReminderWorker
import java.util.concurrent.TimeUnit
import android.app.PendingIntent
import android.content.Intent
import com.example.healthloop.MainActivity

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

    fun showEntryAddedNotification() {
        val channelId = "entry_added_channel"
        val notificationId = 1002
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

        // Create channel if needed
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                channelId,
                "Entry Added",
                android.app.NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifies when a health entry is added successfully."
            }
            notificationManager.createNotificationChannel(channel)
        }

        // PendingIntent to open MainActivity
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val notification = androidx.core.app.NotificationCompat.Builder(context, channelId)
            .setSmallIcon(com.example.healthloop.R.drawable.ic_notification)
            .setContentTitle("Entry Added")
            .setContentText("Your health entry was added successfully!")
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationId, notification)
    }
} 