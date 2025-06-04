package com.example.healthloop.presentation.model

import java.util.Date

data class HealthEntryUiModel(
    val id: Long = 0,
    val date: Date,
    val waterIntake: Int, // in ml
    val sleepHours: Float,
    val stepCount: Int,
    val mood: String,
    val weight: Float // in kg
)