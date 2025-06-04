package com.example.healthloop.domain.usecase

import com.example.healthloop.domain.model.HealthEntry
import com.example.healthloop.domain.repository.HealthRepository

class AddHealthEntryUseCase(private val repository: HealthRepository) {
    suspend operator fun invoke(healthEntry: HealthEntry): Long {
        return repository.insertHealthEntry(healthEntry)
    }
}