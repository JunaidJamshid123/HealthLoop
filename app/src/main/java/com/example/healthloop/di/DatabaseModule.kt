package com.example.healthloop.di

import android.content.Context
import androidx.room.Room
import com.example.healthloop.data.local.database.HealthLoopDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideHealthLoopDatabase(
        @ApplicationContext context: Context
    ): HealthLoopDatabase {
        return Room.databaseBuilder(
            context,
            HealthLoopDatabase::class.java,
            "health_loop_db"
        ).build()
    }
} 