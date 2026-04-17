package com.junaidjamshid.healthloop.data.repository

import com.junaidjamshid.healthloop.data.local.dao.UserProfileDao
import com.junaidjamshid.healthloop.data.mapper.toDomain
import com.junaidjamshid.healthloop.data.mapper.toEntity
import com.junaidjamshid.healthloop.domain.model.UserGoals
import com.junaidjamshid.healthloop.domain.model.UserProfile
import com.junaidjamshid.healthloop.domain.model.UserStats
import com.junaidjamshid.healthloop.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserProfileRepositoryImpl @Inject constructor(
    private val userProfileDao: UserProfileDao
) : UserProfileRepository {

    // ==================== USER PROFILE ====================
    
    override fun getUserProfile(): Flow<UserProfile?> {
        return userProfileDao.getUserProfile()
            .map { entity -> entity?.toDomain() }
            .catch { e ->
                throw Exception("Failed to get user profile: ${e.message}")
            }
    }
    
    override suspend fun getUserProfileOnce(): UserProfile? {
        return try {
            userProfileDao.getUserProfileOnce()?.toDomain()
        } catch (e: Exception) {
            throw Exception("Failed to get user profile: ${e.message}")
        }
    }
    
    override suspend fun saveUserProfile(profile: UserProfile) {
        try {
            userProfileDao.insertOrUpdateProfile(profile.toEntity())
        } catch (e: Exception) {
            throw Exception("Failed to save user profile: ${e.message}")
        }
    }
    
    override suspend fun updateProfileInfo(
        name: String,
        email: String,
        age: Int,
        weight: Float,
        height: Int
    ) {
        try {
            userProfileDao.updateProfileInfo(name, email, age, weight, height)
        } catch (e: Exception) {
            throw Exception("Failed to update profile info: ${e.message}")
        }
    }
    
    override suspend fun updateProfilePicture(base64Image: String?) {
        try {
            userProfileDao.updateProfilePicture(base64Image)
        } catch (e: Exception) {
            throw Exception("Failed to update profile picture: ${e.message}")
        }
    }
    
    override suspend fun deleteProfile() {
        try {
            userProfileDao.deleteProfile()
        } catch (e: Exception) {
            throw Exception("Failed to delete profile: ${e.message}")
        }
    }

    // ==================== USER GOALS ====================
    
    override fun getUserGoals(): Flow<UserGoals?> {
        return userProfileDao.getUserGoals()
            .map { entity -> entity?.toDomain() }
            .catch { e ->
                throw Exception("Failed to get user goals: ${e.message}")
            }
    }
    
    override suspend fun getUserGoalsOnce(): UserGoals? {
        return try {
            userProfileDao.getUserGoalsOnce()?.toDomain()
        } catch (e: Exception) {
            throw Exception("Failed to get user goals: ${e.message}")
        }
    }
    
    override suspend fun saveUserGoals(goals: UserGoals) {
        try {
            userProfileDao.insertOrUpdateGoals(goals.toEntity())
        } catch (e: Exception) {
            throw Exception("Failed to save user goals: ${e.message}")
        }
    }
    
    override suspend fun updateGoals(
        waterGoal: Int,
        sleepGoal: Float,
        stepsGoal: Int,
        caloriesGoal: Int,
        exerciseGoal: Int,
        weightGoal: Float
    ) {
        try {
            userProfileDao.updateGoals(waterGoal, sleepGoal, stepsGoal, caloriesGoal, exerciseGoal, weightGoal)
        } catch (e: Exception) {
            throw Exception("Failed to update goals: ${e.message}")
        }
    }

    // ==================== USER STATS ====================
    
    override fun getUserStats(): Flow<UserStats?> {
        return userProfileDao.getUserStats()
            .map { entity -> entity?.toDomain() }
            .catch { e ->
                throw Exception("Failed to get user stats: ${e.message}")
            }
    }
    
    override suspend fun getUserStatsOnce(): UserStats? {
        return try {
            userProfileDao.getUserStatsOnce()?.toDomain()
        } catch (e: Exception) {
            throw Exception("Failed to get user stats: ${e.message}")
        }
    }
    
    override suspend fun saveUserStats(stats: UserStats) {
        try {
            userProfileDao.insertOrUpdateStats(stats.toEntity())
        } catch (e: Exception) {
            throw Exception("Failed to save user stats: ${e.message}")
        }
    }
    
    override suspend fun updateStats(
        totalDays: Int,
        currentStreak: Int,
        bestStreak: Int,
        healthScore: Int
    ) {
        try {
            userProfileDao.updateStats(totalDays, currentStreak, bestStreak, healthScore, System.currentTimeMillis())
        } catch (e: Exception) {
            throw Exception("Failed to update stats: ${e.message}")
        }
    }
    
    override suspend fun updateStreak(streak: Int) {
        try {
            userProfileDao.updateStreak(streak)
        } catch (e: Exception) {
            throw Exception("Failed to update streak: ${e.message}")
        }
    }
    
    override suspend fun updateHealthScore(score: Int) {
        try {
            userProfileDao.updateHealthScore(score)
        } catch (e: Exception) {
            throw Exception("Failed to update health score: ${e.message}")
        }
    }

    // ==================== INITIALIZATION ====================
    
    override suspend fun initializeDefaultData() {
        try {
            // Initialize default profile if not exists
            if (userProfileDao.getUserProfileOnce() == null) {
                userProfileDao.insertOrUpdateProfile(UserProfile.default().toEntity())
            }
            
            // Initialize default goals if not exists
            if (userProfileDao.getUserGoalsOnce() == null) {
                userProfileDao.insertOrUpdateGoals(UserGoals.default().toEntity())
            }
            
            // Initialize default stats if not exists
            if (userProfileDao.getUserStatsOnce() == null) {
                userProfileDao.insertOrUpdateStats(UserStats.default().toEntity())
            }
        } catch (e: Exception) {
            throw Exception("Failed to initialize default data: ${e.message}")
        }
    }
}
