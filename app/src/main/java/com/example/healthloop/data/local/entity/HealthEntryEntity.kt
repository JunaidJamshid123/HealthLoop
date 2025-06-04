package com.example.healthloop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "health_entries")
data class HealthEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Date,
    val waterIntake: Int,
    val sleepHours: Float,
    val stepCount: Int,
    val mood: String,
    val weight: Float
)