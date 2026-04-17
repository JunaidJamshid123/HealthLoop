package com.junaidjamshid.healthloop.data.repository

import com.junaidjamshid.healthloop.data.local.dao.HealthEntryDao
import com.junaidjamshid.healthloop.data.mapper.toDomain
import com.junaidjamshid.healthloop.data.mapper.toEntity
import com.junaidjamshid.healthloop.domain.model.HealthEntry
import com.junaidjamshid.healthloop.domain.repository.HealthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.util.Calendar
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
    
    override suspend fun updateHealthEntry(healthEntry: HealthEntry) {
        try {
            healthEntryDao.updateHealthEntry(healthEntry.toEntity())
        } catch (e: Exception) {
            throw Exception("Failed to update health entry: ${e.message}")
        }
    }
    
    override suspend fun deleteHealthEntry(healthEntry: HealthEntry) {
        try {
            healthEntryDao.deleteHealthEntry(healthEntry.toEntity())
        } catch (e: Exception) {
            throw Exception("Failed to delete health entry: ${e.message}")
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
    
    override suspend fun getHealthEntryById(id: Long): HealthEntry? {
        return try {
            healthEntryDao.getHealthEntryById(id)?.toDomain()
        } catch (e: Exception) {
            throw Exception("Failed to get health entry: ${e.message}")
        }
    }
    
    override fun getLatestEntry(): Flow<HealthEntry?> {
        return healthEntryDao.getLatestEntry()
            .map { it?.toDomain() }
            .catch { e ->
                throw Exception("Failed to get latest entry: ${e.message}")
            }
    }
    
    override fun getRecentEntries(limit: Int): Flow<List<HealthEntry>> {
        return healthEntryDao.getRecentEntries(limit)
            .map { entities -> entities.map { it.toDomain() } }
            .catch { e ->
                throw Exception("Failed to get recent entries: ${e.message}")
            }
    }
    
    override fun getTodayEntry(): Flow<HealthEntry?> {
        val (todayStart, todayEnd) = getTodayRange()
        return healthEntryDao.getTodayEntry(todayStart, todayEnd)
            .map { it?.toDomain() }
            .catch { e ->
                throw Exception("Failed to get today's entry: ${e.message}")
            }
    }
    
    override suspend fun getTodayEntryOnce(): HealthEntry? {
        val (todayStart, todayEnd) = getTodayRange()
        return try {
            healthEntryDao.getTodayEntryOnce(todayStart, todayEnd)?.toDomain()
        } catch (e: Exception) {
            throw Exception("Failed to get today's entry: ${e.message}")
        }
    }
    
    override fun getTotalEntriesCount(): Flow<Int> {
        return healthEntryDao.getTotalEntriesCount()
    }
    
    override fun getTotalDaysLogged(): Flow<Int> {
        return healthEntryDao.getTotalDaysLogged()
    }
    
    private fun getTodayRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val todayStart = calendar.timeInMillis
        
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val todayEnd = calendar.timeInMillis
        
        return todayStart to todayEnd
    }
}