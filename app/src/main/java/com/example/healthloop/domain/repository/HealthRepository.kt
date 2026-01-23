package com.example.healthloop.domain.repository

import com.example.healthloop.domain.model.HealthEntry
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface HealthRepository {
    suspend fun insertHealthEntry(healthEntry: HealthEntry): Long
    suspend fun updateHealthEntry(healthEntry: HealthEntry)
    suspend fun deleteHealthEntry(healthEntry: HealthEntry)
    fun getAllHealthEntries(): Flow<List<HealthEntry>>
    fun getHealthEntriesByDateRange(startDate: Date, endDate: Date): Flow<List<HealthEntry>>
    suspend fun getHealthEntryById(id: Long): HealthEntry?
    fun getLatestEntry(): Flow<HealthEntry?>
    fun getRecentEntries(limit: Int): Flow<List<HealthEntry>>
    fun getTodayEntry(): Flow<HealthEntry?>
    suspend fun getTodayEntryOnce(): HealthEntry?
    fun getTotalEntriesCount(): Flow<Int>
    fun getTotalDaysLogged(): Flow<Int>
}