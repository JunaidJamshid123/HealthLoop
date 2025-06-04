package com.example.healthloop.presentation.model

data class TodayHealthDataUiModel(
    val waterIntake: Int,
    val targetWater: Int,
    val sleepHours: Float,
    val targetSleep: Float,
    val stepCount: Int,
    val targetSteps: Int,
    val mood: String,
    val weight: Float
)