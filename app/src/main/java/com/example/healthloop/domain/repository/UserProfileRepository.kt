package com.example.healthloop.domain.repository

import com.example.healthloop.domain.model.UserGoals
import com.example.healthloop.domain.model.UserProfile
import com.example.healthloop.domain.model.UserStats
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    
    // ==================== USER PROFILE ====================
    
    fun getUserProfile(): Flow<UserProfile?>
    
    suspend fun getUserProfileOnce(): UserProfile?
    
    suspend fun saveUserProfile(profile: UserProfile)
    
    suspend fun updateProfileInfo(
        name: String,
        email: String,
        age: Int,
        weight: Float,
        height: Int
    )
    
    suspend fun updateProfilePicture(base64Image: String?)
    
    suspend fun deleteProfile()
    
    // ==================== USER GOALS ====================
    
    fun getUserGoals(): Flow<UserGoals?>
    
    suspend fun getUserGoalsOnce(): UserGoals?
    
    suspend fun saveUserGoals(goals: UserGoals)
    
    suspend fun updateGoals(
        waterGoal: Int,
        sleepGoal: Float,
        stepsGoal: Int,
        caloriesGoal: Int,
        exerciseGoal: Int,
        weightGoal: Float
    )
    
    // ==================== USER STATS ====================
    
    fun getUserStats(): Flow<UserStats?>
    
    suspend fun getUserStatsOnce(): UserStats?
    
    suspend fun saveUserStats(stats: UserStats)
    
    suspend fun updateStats(
        totalDays: Int,
        currentStreak: Int,
        bestStreak: Int,
        healthScore: Int
    )
    
    suspend fun updateStreak(streak: Int)
    
    suspend fun updateHealthScore(score: Int)
    
    // ==================== INITIALIZATION ====================
    
    suspend fun initializeDefaultData()
}
