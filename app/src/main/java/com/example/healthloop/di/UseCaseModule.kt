package com.example.healthloop.di

import com.example.healthloop.domain.usecase.AddHealthEntryUseCase
import com.example.healthloop.domain.usecase.GetHealthEntriesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    
    @Provides
    @Singleton
    fun provideAddHealthEntryUseCase(
        repository: com.example.healthloop.domain.repository.HealthRepository
    ): AddHealthEntryUseCase {
        return AddHealthEntryUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetHealthEntriesUseCase(
        repository: com.example.healthloop.domain.repository.HealthRepository
    ): GetHealthEntriesUseCase {
        return GetHealthEntriesUseCase(repository)
    }
} 