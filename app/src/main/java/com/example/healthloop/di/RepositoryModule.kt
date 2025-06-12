package com.example.healthloop.di

import com.example.healthloop.data.local.database.HealthLoopDatabase
import com.example.healthloop.data.repository.HealthRepositoryImpl
import com.example.healthloop.domain.repository.HealthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideHealthRepository(
        database: HealthLoopDatabase
    ): HealthRepository {
        return HealthRepositoryImpl(database.healthEntryDao())
    }
} 