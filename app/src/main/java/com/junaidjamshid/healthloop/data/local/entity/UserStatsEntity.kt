package com.junaidjamshid.healthloop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey
    val id: Int = 1, // Single stats record, always use id = 1
    val totalDays: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val healthScore: Int = 0,
    val lastActiveDate: Long = System.currentTimeMillis()
)
