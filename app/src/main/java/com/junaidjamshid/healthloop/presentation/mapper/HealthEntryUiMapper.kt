package com.junaidjamshid.healthloop.presentation.mapper

import com.junaidjamshid.healthloop.domain.model.HealthEntry
import com.junaidjamshid.healthloop.presentation.model.HealthEntryUiModel
import java.text.SimpleDateFormat
import java.util.*

// Date formatters for conversion
private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

fun HealthEntry.toUiModel(): HealthEntryUiModel {
    return HealthEntryUiModel(
        id = id,
        date = date, // Pass Date object directly
        waterIntake = waterIntake,
        sleepHours = sleepHours,
        stepCount = stepCount,
        mood = mood,
        weight = weight,
        calories = calories,
        exerciseMinutes = exerciseMinutes
    )
}

fun HealthEntryUiModel.toDomain(): HealthEntry {
    return HealthEntry(
        id = id,
        date = date, // Pass Date object directly
        waterIntake = waterIntake,
        sleepHours = sleepHours,
        stepCount = stepCount,
        mood = mood,
        weight = weight,
        calories = calories,
        exerciseMinutes = exerciseMinutes
    )
}

fun List<HealthEntry>.toUiModels(): List<HealthEntryUiModel> {
    return map { it.toUiModel() }
}

fun List<HealthEntryUiModel>.toDomainModels(): List<HealthEntry> {
    return map { it.toDomain() }
}