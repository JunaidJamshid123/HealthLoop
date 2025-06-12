package com.example.healthloop

import android.app.Application
import com.example.healthloop.data.local.database.HealthLoopDatabase
import com.example.healthloop.data.repository.HealthRepositoryImpl
import com.example.healthloop.domain.repository.HealthRepository
import com.example.healthloop.domain.usecase.AddHealthEntryUseCase
import com.example.healthloop.domain.usecase.GetHealthEntriesUseCase
import com.example.healthloop.util.NotificationManager
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
        
        // Initialize notification manager
        notificationManager = NotificationManager(this)
        
        // Schedule daily reminder
        notificationManager.scheduleDailyReminder()
    }
}