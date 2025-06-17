package com.example.healthloop.presentation.model

data class HealthEntryUiModel(
    val id: Long = 0,
    val date: java.util.Date,
    val waterIntake: Int, // in ml
    val sleepHours: Float,
    val stepCount: Int,
    val mood: String,
    val weight: Float // in kg
)