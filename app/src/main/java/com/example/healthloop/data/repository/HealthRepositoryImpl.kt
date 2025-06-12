package com.example.healthloop.data.repository

import com.example.healthloop.data.local.dao.HealthEntryDao
import com.example.healthloop.data.mapper.toDomain
import com.example.healthloop.data.mapper.toEntity
import com.example.healthloop.domain.model.HealthEntry
import com.example.healthloop.domain.repository.HealthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class HealthRepositoryImpl @Inject constructor(
    private val healthEntryDao: HealthEntryDao
) : HealthRepository {
    
    override suspend fun insertHealthEntry(healthEntry: HealthEntry): Long {
        return try {
            healthEntryDao.insertHealthEntry(healthEntry.toEntity())
        } catch (e: Exception) {
            throw Exception("Failed to insert health entry: ${e.message}")
        }
    }
    
    override fun getAllHealthEntries(): Flow<List<HealthEntry>> {
        return healthEntryDao.getAllHealthEntries()
            .map { entities ->
                entities.map { it.toDomain() }
            }
            .catch { e ->
                throw Exception("Failed to get health entries: ${e.message}")
            }
    }
    
    override fun getHealthEntriesByDateRange(startDate: Date, endDate: Date): Flow<List<HealthEntry>> {
        return healthEntryDao.getHealthEntriesByDateRange(startDate.time, endDate.time)
            .map { entities ->
                entities.map { it.toDomain() }
            }
            .catch { e ->
                throw Exception("Failed to get health entries by date range: ${e.message}")
            }
    }
}