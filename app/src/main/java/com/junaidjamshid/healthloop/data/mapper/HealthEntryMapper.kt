package com.junaidjamshid.healthloop.data.mapper

import com.junaidjamshid.healthloop.data.local.entity.HealthEntryEntity
import com.junaidjamshid.healthloop.domain.model.HealthEntry

fun HealthEntry.toEntity(): HealthEntryEntity {
    return HealthEntryEntity(
        id = id,
        date = date,
        waterIntake = waterIntake,
        sleepHours = sleepHours,
        stepCount = stepCount,
        mood = mood,
        weight = weight,
        calories = calories,
        exerciseMinutes = exerciseMinutes
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
        weight = weight,
        calories = calories,
        exerciseMinutes = exerciseMinutes
    )
}