package com.junaidjamshid.healthloop.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.junaidjamshid.healthloop.data.local.entity.HealthEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthEntry(healthEntry: HealthEntryEntity): Long
    
    @Update
    suspend fun updateHealthEntry(healthEntry: HealthEntryEntity)
    
    @Delete
    suspend fun deleteHealthEntry(healthEntry: HealthEntryEntity)
    
    @Query("SELECT * FROM health_entries ORDER BY date DESC")
    fun getAllHealthEntries(): Flow<List<HealthEntryEntity>>
    
    @Query("SELECT * FROM health_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getHealthEntriesByDateRange(startDate: Long, endDate: Long): Flow<List<HealthEntryEntity>>
    
    @Query("SELECT * FROM health_entries WHERE id = :id")
    suspend fun getHealthEntryById(id: Long): HealthEntryEntity?
    
    @Query("SELECT * FROM health_entries ORDER BY date DESC LIMIT 1")
    fun getLatestEntry(): Flow<HealthEntryEntity?>
    
    @Query("SELECT * FROM health_entries ORDER BY date DESC LIMIT :limit")
    fun getRecentEntries(limit: Int): Flow<List<HealthEntryEntity>>
    
    @Query("SELECT * FROM health_entries WHERE date >= :todayStart AND date <= :todayEnd LIMIT 1")
    fun getTodayEntry(todayStart: Long, todayEnd: Long): Flow<HealthEntryEntity?>
    
    @Query("SELECT * FROM health_entries WHERE date >= :todayStart AND date <= :todayEnd LIMIT 1")
    suspend fun getTodayEntryOnce(todayStart: Long, todayEnd: Long): HealthEntryEntity?
    
    @Query("SELECT COUNT(*) FROM health_entries")
    fun getTotalEntriesCount(): Flow<Int>
    
    @Query("SELECT COUNT(DISTINCT date / 86400000) FROM health_entries")
    fun getTotalDaysLogged(): Flow<Int>
    
    @Query("DELETE FROM health_entries")
    suspend fun deleteAllEntries()
}