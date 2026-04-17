package com.junaidjamshid.healthloop.presentation.model

data class HealthEntryUiModel(
    val id: Long = 0,
    val date: java.util.Date,
    val waterIntake: Int, // in glasses
    val sleepHours: Float,
    val stepCount: Int,
    val mood: String,
    val weight: Float, // in kg
    val calories: Int = 0, // calories consumed
    val exerciseMinutes: Int = 0 // exercise duration in minutes
)