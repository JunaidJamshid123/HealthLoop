package com.example.healthloop

import android.app.Application
import com.example.healthloop.data.local.database.HealthLoopDatabase
import com.example.healthloop.data.repository.HealthRepositoryImpl
import com.example.healthloop.domain.repository.HealthRepository
import com.example.healthloop.domain.usecase.AddHealthEntryUseCase
import com.example.healthloop.domain.usecase.GetHealthEntriesUseCase

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
    
    companion object {
        private lateinit var instance: HealthLoopApplication
        
        fun getInstance(): HealthLoopApplication {
            return instance
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}