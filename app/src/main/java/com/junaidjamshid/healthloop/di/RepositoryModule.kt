package com.junaidjamshid.healthloop.di

import com.junaidjamshid.healthloop.data.local.dao.UserProfileDao
import com.junaidjamshid.healthloop.data.local.database.HealthLoopDatabase
import com.junaidjamshid.healthloop.data.repository.HealthRepositoryImpl
import com.junaidjamshid.healthloop.data.repository.UserProfileRepositoryImpl
import com.junaidjamshid.healthloop.domain.repository.HealthRepository
import com.junaidjamshid.healthloop.domain.repository.UserProfileRepository
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
    
    @Provides
    @Singleton
    fun provideUserProfileRepository(
        userProfileDao: UserProfileDao
    ): UserProfileRepository {
        return UserProfileRepositoryImpl(userProfileDao)
    }
} 