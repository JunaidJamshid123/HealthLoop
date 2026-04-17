package com.junaidjamshid.healthloop.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.junaidjamshid.healthloop.data.local.dao.UserProfileDao
import com.junaidjamshid.healthloop.data.local.database.HealthLoopDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Create user_profile table
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS `user_profile` (
                    `id` INTEGER NOT NULL PRIMARY KEY,
                    `name` TEXT NOT NULL,
                    `email` TEXT NOT NULL,
                    `age` INTEGER NOT NULL,
                    `weight` REAL NOT NULL,
                    `height` INTEGER NOT NULL,
                    `profilePictureBase64` TEXT,
                    `memberSince` INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()},
                    `isPro` INTEGER NOT NULL DEFAULT 0
                )
            """.trimIndent())

            // Create user_goals table
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS `user_goals` (
                    `id` INTEGER NOT NULL PRIMARY KEY,
                    `waterGoal` INTEGER NOT NULL DEFAULT 8,
                    `sleepGoal` REAL NOT NULL DEFAULT 8.0,
                    `stepsGoal` INTEGER NOT NULL DEFAULT 10000,
                    `caloriesGoal` INTEGER NOT NULL DEFAULT 2000,
                    `exerciseGoal` INTEGER NOT NULL DEFAULT 30,
                    `weightGoal` REAL NOT NULL DEFAULT 65.0
                )
            """.trimIndent())

            // Create user_stats table
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS `user_stats` (
                    `id` INTEGER NOT NULL PRIMARY KEY,
                    `totalDays` INTEGER NOT NULL DEFAULT 0,
                    `currentStreak` INTEGER NOT NULL DEFAULT 0,
                    `bestStreak` INTEGER NOT NULL DEFAULT 0,
                    `healthScore` INTEGER NOT NULL DEFAULT 0,
                    `lastActiveDate` INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}
                )
            """.trimIndent())
        }
    }

    @Provides
    @Singleton
    fun provideHealthLoopDatabase(
        @ApplicationContext context: Context
    ): HealthLoopDatabase {
        return Room.databaseBuilder(
            context,
            HealthLoopDatabase::class.java,
            "health_loop_db"
        )
        .addMigrations(MIGRATION_1_2)
        .build()
    }
    
    @Provides
    @Singleton
    fun provideUserProfileDao(database: HealthLoopDatabase): UserProfileDao {
        return database.userProfileDao()
    }
} 