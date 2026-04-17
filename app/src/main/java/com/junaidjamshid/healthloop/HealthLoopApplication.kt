package com.junaidjamshid.healthloop

import android.app.Application
import android.content.Context
import com.junaidjamshid.healthloop.data.local.database.HealthLoopDatabase
import com.junaidjamshid.healthloop.data.repository.HealthRepositoryImpl
import com.junaidjamshid.healthloop.domain.repository.HealthRepository
import com.junaidjamshid.healthloop.domain.usecase.AddHealthEntryUseCase
import com.junaidjamshid.healthloop.domain.usecase.GetHealthEntriesUseCase
import com.junaidjamshid.healthloop.util.NotificationManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HealthLoopApplication : Application() {
    
    // Lazy initialization of database
    private val database by lazy { HealthLoopDatabase.getDatabase(this) }
    
    // Repository
    val repository: HealthRepository by lazy { 
        HealthRepositoryImpl(database.healthEntryDao()) 
    }
    
    // Use cases
    val addHealthEntryUseCase by lazy { AddHealthEntryUseCase(repository) }
    val getHealthEntriesUseCase by lazy { GetHealthEntriesUseCase(repository) }
    
    private lateinit var notificationManager: NotificationManager
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize notification manager and schedule only if user has reminders enabled
        notificationManager = NotificationManager(this)
        val prefs = getSharedPreferences("profile_settings", Context.MODE_PRIVATE)
        val reminderEnabled = prefs.getBoolean("reminders", true)
        if (reminderEnabled) {
            val hour = prefs.getInt("reminder_hour", 23)
            val minute = prefs.getInt("reminder_minute", 0)
            notificationManager.scheduleDailyReminder(hour, minute)
        }
    }
}