package com.example.healthloop.domain.model

data class UserGoals(
    val id: Int = 1,
    val waterGoal: Int = 8,
    val sleepGoal: Float = 8f,
    val stepsGoal: Int = 10000,
    val caloriesGoal: Int = 2000,
    val exerciseGoal: Int = 30,
    val weightGoal: Float = 65f
) {
    companion object {
        fun default() = UserGoals()
    }
}
