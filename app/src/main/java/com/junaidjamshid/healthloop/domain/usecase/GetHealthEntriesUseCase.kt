package com.junaidjamshid.healthloop.domain.usecase

import com.junaidjamshid.healthloop.domain.model.HealthEntry
import com.junaidjamshid.healthloop.domain.repository.HealthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import java.util.Date

class GetHealthEntriesUseCase @Inject constructor(
    private val repository: HealthRepository
) {
    operator fun invoke(): Flow<List<HealthEntry>> {
        return repository.getAllHealthEntries()
    }
    
    operator fun invoke(startDate: Date, endDate: Date): Flow<List<HealthEntry>> {
        return repository.getHealthEntriesByDateRange(startDate, endDate)
    }
}

class GetTodayEntryUseCase @Inject constructor(
    private val repository: HealthRepository
) {
    operator fun invoke(): Flow<HealthEntry?> {
        return repository.getTodayEntry()
    }
    
    suspend fun getOnce(): HealthEntry? {
        return repository.getTodayEntryOnce()
    }
}

class GetRecentEntriesUseCase @Inject constructor(
    private val repository: HealthRepository
) {
    operator fun invoke(limit: Int = 7): Flow<List<HealthEntry>> {
        return repository.getRecentEntries(limit)
    }
}

class GetLatestEntryUseCase @Inject constructor(
    private val repository: HealthRepository
) {
    operator fun invoke(): Flow<HealthEntry?> {
        return repository.getLatestEntry()
    }
}

class GetTotalDaysLoggedUseCase @Inject constructor(
    private val repository: HealthRepository
) {
    operator fun invoke(): Flow<Int> {
        return repository.getTotalDaysLogged()
    }
}