package com.example.healthloop.data.repository

import com.example.healthloop.data.local.dao.HealthEntryDao
import com.example.healthloop.data.mapper.toDomain
import com.example.healthloop.data.mapper.toEntity
import com.example.healthloop.domain.model.HealthEntry
import com.example.healthloop.domain.repository.HealthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date

class HealthRepositoryImpl(private val healthEntryDao: HealthEntryDao) : HealthRepository {
    override suspend fun insertHealthEntry(healthEntry: HealthEntry): Long {
        return healthEntryDao.insertHealthEntry(healthEntry.toEntity())
    }
    
    override fun getAllHealthEntries(): Flow<List<HealthEntry>> {
        return healthEntryDao.getAllHealthEntries().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getHealthEntriesByDateRange(startDate: Date, endDate: Date): Flow<List<HealthEntry>> {
        return healthEntryDao.getHealthEntriesByDateRange(startDate.time, endDate.time).map { entities ->
            entities.map { it.toDomain() }
        }
    }
}