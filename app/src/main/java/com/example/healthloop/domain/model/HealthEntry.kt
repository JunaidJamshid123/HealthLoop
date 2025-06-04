package com.example.healthloop.domain.model

import java.util.Date

data class HealthEntry(
    val id: Long = 0,
    val date: Date,
    val waterIntake: Int,
    val sleepHours: Float,
    val stepCount: Int,
    val mood: String,
    val weight: Float
)