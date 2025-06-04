package com.example.healthloop.data.mapper

import com.example.healthloop.data.local.entity.HealthEntryEntity
import com.example.healthloop.domain.model.HealthEntry

fun HealthEntry.toEntity(): HealthEntryEntity {
    return HealthEntryEntity(
        id = id,
        date = date,
        waterIntake = waterIntake,
        sleepHours = sleepHours,
        stepCount = stepCount,
        mood = mood,
        weight = weight
    )
}

fun HealthEntryEntity.toDomain(): HealthEntry {
    return HealthEntry(
        id = id,
        date = date,
        waterIntake = waterIntake,
        sleepHours = sleepHours,
        stepCount = stepCount,
        mood = mood,
        weight = weight
    )
}