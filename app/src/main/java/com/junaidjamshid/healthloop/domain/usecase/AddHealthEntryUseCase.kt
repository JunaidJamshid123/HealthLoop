package com.junaidjamshid.healthloop.domain.usecase

import com.junaidjamshid.healthloop.domain.model.HealthEntry
import com.junaidjamshid.healthloop.domain.repository.HealthRepository
import javax.inject.Inject

class AddHealthEntryUseCase @Inject constructor(
    private val repository: HealthRepository
) {
    suspend operator fun invoke(healthEntry: HealthEntry): Long {
        return repository.insertHealthEntry(healthEntry)
    }
}

class UpdateHealthEntryUseCase @Inject constructor(
    private val repository: HealthRepository
) {
    suspend operator fun invoke(healthEntry: HealthEntry) {
        repository.updateHealthEntry(healthEntry)
    }
}