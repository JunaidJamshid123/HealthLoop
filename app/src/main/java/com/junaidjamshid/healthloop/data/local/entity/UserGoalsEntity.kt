package com.junaidjamshid.healthloop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_goals")
data class UserGoalsEntity(
    @PrimaryKey
    val id: Int = 1, // Single goals record, always use id = 1
    val waterGoal: Int = 8, // glasses per day
    val sleepGoal: Float = 8f, // hours per day
    val stepsGoal: Int = 10000, // steps per day
    val caloriesGoal: Int = 2000, // kcal per day
    val exerciseGoal: Int = 30, // minutes per day
    val weightGoal: Float = 65f // target weight in kg
)
