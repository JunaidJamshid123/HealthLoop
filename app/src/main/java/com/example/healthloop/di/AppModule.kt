package com.example.healthloop.di

import com.example.healthloop.data.local.dao.HealthEntryDao
import com.example.healthloop.data.local.database.HealthLoopDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHealthEntryDao(database: HealthLoopDatabase): HealthEntryDao {
        return database.healthEntryDao()
    }
}