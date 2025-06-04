package com.example.healthloop.domain.repository

import com.example.healthloop.domain.model.HealthEntry
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface HealthRepository {
    suspend fun insertHealthEntry(healthEntry: HealthEntry): Long
    fun getAllHealthEntries(): Flow<List<HealthEntry>>
    fun getHealthEntriesByDateRange(startDate: Date, endDate: Date): Flow<List<HealthEntry>>
}