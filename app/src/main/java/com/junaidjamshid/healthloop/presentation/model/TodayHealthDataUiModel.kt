package com.junaidjamshid.healthloop.presentation.model

data class TodayHealthDataUiModel(
    val waterIntake: Int = 0,
    val targetWater: Int = 8,
    val sleepHours: Float = 0f,
    val targetSleep: Float = 8f,
    val stepCount: Int = 0,
    val targetSteps: Int = 10000,
    val mood: String = "",
    val weight: Float = 0f,
    val targetWeight: Float = 65f,
    val calories: Int = 0,
    val targetCalories: Int = 2000,
    val exerciseMinutes: Int = 0,
    val targetExercise: Int = 30,
    val hasEntry: Boolean = false
) {
    // Progress calculations (0f to 1f)
    val waterProgress: Float get() = if (targetWater > 0) (waterIntake.toFloat() / targetWater).coerceIn(0f, 1f) else 0f
    val sleepProgress: Float get() = if (targetSleep > 0) (sleepHours / targetSleep).coerceIn(0f, 1f) else 0f
    val stepsProgress: Float get() = if (targetSteps > 0) (stepCount.toFloat() / targetSteps).coerceIn(0f, 1f) else 0f
    val caloriesProgress: Float get() = if (targetCalories > 0) (calories.toFloat() / targetCalories).coerceIn(0f, 1f) else 0f
    val exerciseProgress: Float get() = if (targetExercise > 0) (exerciseMinutes.toFloat() / targetExercise).coerceIn(0f, 1f) else 0f
    
    companion object {
        fun empty() = TodayHealthDataUiModel()
    }
}