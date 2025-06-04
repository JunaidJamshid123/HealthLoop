package com.example.healthloop.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.healthloop.data.local.entity.HealthEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthEntry(healthEntry: HealthEntryEntity): Long
    
    @Query("SELECT * FROM health_entries ORDER BY date DESC")
    fun getAllHealthEntries(): Flow<List<HealthEntryEntity>>
    
    @Query("SELECT * FROM health_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getHealthEntriesByDateRange(startDate: Long, endDate: Long): Flow<List<HealthEntryEntity>>
}