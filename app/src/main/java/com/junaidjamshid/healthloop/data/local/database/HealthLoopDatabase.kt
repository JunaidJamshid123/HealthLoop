package com.junaidjamshid.healthloop.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.junaidjamshid.healthloop.data.local.dao.HealthEntryDao
import com.junaidjamshid.healthloop.data.local.dao.UserProfileDao
import com.junaidjamshid.healthloop.data.local.entity.HealthEntryEntity
import com.junaidjamshid.healthloop.data.local.entity.UserGoalsEntity
import com.junaidjamshid.healthloop.data.local.entity.UserProfileEntity
import com.junaidjamshid.healthloop.data.local.entity.UserStatsEntity
import com.junaidjamshid.healthloop.data.local.util.DateConverter

@Database(
    entities = [
        HealthEntryEntity::class,
        UserProfileEntity::class,
        UserGoalsEntity::class,
        UserStatsEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class HealthLoopDatabase : RoomDatabase() {
    abstract fun healthEntryDao(): HealthEntryDao
    abstract fun userProfileDao(): UserProfileDao
    
    companion object {
        @Volatile
        private var INSTANCE: HealthLoopDatabase? = null
        
        fun getDatabase(context: Context): HealthLoopDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HealthLoopDatabase::class.java,
                    "health_loop_database"
                )
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}