package com.example.healthloop.data.mapper

import com.example.healthloop.data.local.entity.UserGoalsEntity
import com.example.healthloop.data.local.entity.UserProfileEntity
import com.example.healthloop.data.local.entity.UserStatsEntity
import com.example.healthloop.domain.model.UserGoals
import com.example.healthloop.domain.model.UserProfile
import com.example.healthloop.domain.model.UserStats

// ==================== USER PROFILE MAPPERS ====================

fun UserProfileEntity.toDomain(): UserProfile {
    return UserProfile(
        id = id,
        name = name,
        email = email,
        age = age,
        weight = weight,
        height = height,
        profilePictureBase64 = profilePictureBase64,
        memberSince = memberSince,
        isPro = isPro
    )
}

fun UserProfile.toEntity(): UserProfileEntity {
    return UserProfileEntity(
        id = id,
        name = name,
        email = email,
        age = age,
        weight = weight,
        height = height,
        profilePictureBase64 = profilePictureBase64,
        memberSince = memberSince,
        isPro = isPro
    )
}

// ==================== USER GOALS MAPPERS ====================

fun UserGoalsEntity.toDomain(): UserGoals {
    return UserGoals(
        id = id,
        waterGoal = waterGoal,
        sleepGoal = sleepGoal,
        stepsGoal = stepsGoal,
        caloriesGoal = caloriesGoal,
        exerciseGoal = exerciseGoal,
        weightGoal = weightGoal
    )
}

fun UserGoals.toEntity(): UserGoalsEntity {
    return UserGoalsEntity(
        id = id,
        waterGoal = waterGoal,
        sleepGoal = sleepGoal,
        stepsGoal = stepsGoal,
        caloriesGoal = caloriesGoal,
        exerciseGoal = exerciseGoal,
        weightGoal = weightGoal
    )
}

// ==================== USER STATS MAPPERS ====================

fun UserStatsEntity.toDomain(): UserStats {
    return UserStats(
        id = id,
        totalDays = totalDays,
        currentStreak = currentStreak,
        bestStreak = bestStreak,
        healthScore = healthScore,
        lastActiveDate = lastActiveDate
    )
}

fun UserStats.toEntity(): UserStatsEntity {
    return UserStatsEntity(
        id = id,
        totalDays = totalDays,
        currentStreak = currentStreak,
        bestStreak = bestStreak,
        healthScore = healthScore,
        lastActiveDate = lastActiveDate
    )
}
