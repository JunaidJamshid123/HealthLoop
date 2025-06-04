package com.example.healthloop.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.healthloop.data.local.dao.HealthEntryDao
import com.example.healthloop.data.local.entity.HealthEntryEntity
import com.example.healthloop.data.local.util.DateConverter

@Database(entities = [HealthEntryEntity::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class HealthLoopDatabase : RoomDatabase() {
    abstract fun healthEntryDao(): HealthEntryDao
    
    companion object {
        @Volatile
        private var INSTANCE: HealthLoopDatabase? = null
        
        fun getDatabase(context: Context): HealthLoopDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HealthLoopDatabase::class.java,
                    "health_loop_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}