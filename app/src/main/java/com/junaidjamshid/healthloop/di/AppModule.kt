package com.junaidjamshid.healthloop.di

import com.junaidjamshid.healthloop.data.local.dao.HealthEntryDao
import com.junaidjamshid.healthloop.data.local.database.HealthLoopDatabase
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