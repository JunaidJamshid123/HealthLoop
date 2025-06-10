package com.example.healthloop.presentation.mapper

import com.example.healthloop.domain.model.HealthEntry
import com.example.healthloop.presentation.model.HealthEntryUiModel
import java.text.SimpleDateFormat
import java.util.*

// Date formatters for conversion
private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

fun HealthEntry.toUiModel(): HealthEntryUiModel {
    return HealthEntryUiModel(
        id = id,
        date = dateFormatter.format(date), // Convert Date to String
        waterIntake = waterIntake,
        sleepHours = sleepHours,
        stepCount = stepCount,
        mood = mood,
        weight = weight
    )
}

fun HealthEntryUiModel.toDomain(): HealthEntry {
    return HealthEntry(
        id = id,
        date = try {
            dateFormatter.parse(date) ?: Date() // Convert String to Date, fallback to current date
        } catch (e: Exception) {
            Date() // Fallback to current date if parsing fails
        },
        waterIntake = waterIntake,
        sleepHours = sleepHours,
        stepCount = stepCount,
        mood = mood,
        weight = weight
    )
}

fun List<HealthEntry>.toUiModels(): List<HealthEntryUiModel> {
    return map { it.toUiModel() }
}

fun List<HealthEntryUiModel>.toDomainModels(): List<HealthEntry> {
    return map { it.toDomain() }
}