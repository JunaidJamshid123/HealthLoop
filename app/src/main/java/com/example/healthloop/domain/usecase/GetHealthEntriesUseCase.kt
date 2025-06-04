package com.example.healthloop.domain.usecase

import com.example.healthloop.domain.model.HealthEntry
import com.example.healthloop.domain.repository.HealthRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date

class GetHealthEntriesUseCase(private val repository: HealthRepository) {
    operator fun invoke(): Flow<List<HealthEntry>> {
        return repository.getAllHealthEntries()
    }
    
    operator fun invoke(startDate: Date, endDate: Date): Flow<List<HealthEntry>> {
        return repository.getHealthEntriesByDateRange(startDate, endDate)
    }
}