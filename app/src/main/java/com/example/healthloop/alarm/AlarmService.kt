package com.example.healthloop.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.example.healthloop.R

class AlarmService : Service() {

    companion object {
        const val ACTION_START = "com.example.healthloop.alarm.START"
        const val ACTION_STOP = "com.example.healthloop.alarm.STOP"
        private const val FOREGROUND_ID = 3001
        private const val CHANNEL_ID = "alarm_service_channel"
        private const val AUTO_STOP_MS = 60_000L // Auto-stop after 60 seconds
    }

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private val handler = Handler(Looper.getMainLooper())
    private val autoStopRunnable = Runnable { stopAlarm(); stopForeground(STOP_FOREGROUND_REMOVE); stopSelf() }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                handler.removeCallbacks(autoStopRunnable)
                stopAlarm()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
            else -> {
                acquireWakeLock()
                startForegroundNotification()
                startAlarm()
                // Auto-stop after 60 seconds if user doesn't respond
                handler.postDelayed(autoStopRunnable, AUTO_STOP_MS)
            }
        }
        return START_NOT_STICKY
    }

    private fun startAlarm() {
        // Play a gentle notification sound (not the harsh alarm ringtone)
        try {
            // Prefer notification sound for a pleasant reminder
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                setDataSource(this@AlarmService, soundUri)
                isLooping = true
                setVolume(0.7f, 0.7f) // Slightly reduced volume for gentleness
                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Gentle vibration pattern: short buzz, long pause
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        // Gentle pattern: 0ms wait, 300ms buzz, 1s pause, 300ms buzz, 2s pause
        val pattern = longArrayOf(0, 300, 1000, 300, 2000)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val amplitudes = intArrayOf(0, 150, 0, 150, 0) // Medium intensity
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, 0))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(pattern, 0)
        }
    }

    private fun stopAlarm() {
        mediaPlayer?.let {
            try {
                if (it.isPlaying) it.stop()
            } catch (_: Exception) { }
            it.release()
        }
        mediaPlayer = null

        vibrator?.cancel()
        vibrator = null

        releaseWakeLock()
    }

    private fun acquireWakeLock() {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "HealthLoop::AlarmWakeLock"
        ).apply {
            acquire(AUTO_STOP_MS + 5_000) // Release slightly after auto-stop
        }
    }

    private fun releaseWakeLock() {
        wakeLock?.let {
            if (it.isHeld) it.release()
        }
        wakeLock = null
    }

    private fun startForegroundNotification() {
        // Create channel for the foreground service notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alarm Playing",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows when the health reminder alarm is playing"
                setSound(null, null)
            }
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }

        val stopIntent = Intent(this, AlarmService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Health Reminder")
            .setContentText("Alarm is ringing — tap to stop")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .addAction(R.drawable.ic_notification, "Stop", stopPendingIntent)
            .build()

        startForeground(FOREGROUND_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(autoStopRunnable)
        stopAlarm()
    }
}
