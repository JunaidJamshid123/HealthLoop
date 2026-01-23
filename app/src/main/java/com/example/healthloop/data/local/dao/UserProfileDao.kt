package com.example.healthloop.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.healthloop.data.local.entity.UserGoalsEntity
import com.example.healthloop.data.local.entity.UserProfileEntity
import com.example.healthloop.data.local.entity.UserStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    
    // ==================== USER PROFILE ====================
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)
    
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfileEntity?>
    
    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileOnce(): UserProfileEntity?
    
    @Query("UPDATE user_profile SET name = :name, email = :email, age = :age, weight = :weight, height = :height WHERE id = 1")
    suspend fun updateProfileInfo(name: String, email: String, age: Int, weight: Float, height: Int)
    
    @Query("UPDATE user_profile SET profilePictureBase64 = :base64Image WHERE id = 1")
    suspend fun updateProfilePicture(base64Image: String?)
    
    @Query("DELETE FROM user_profile")
    suspend fun deleteProfile()
    
    // ==================== USER GOALS ====================
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateGoals(goals: UserGoalsEntity)
    
    @Query("SELECT * FROM user_goals WHERE id = 1")
    fun getUserGoals(): Flow<UserGoalsEntity?>
    
    @Query("SELECT * FROM user_goals WHERE id = 1")
    suspend fun getUserGoalsOnce(): UserGoalsEntity?
    
    @Query("""
        UPDATE user_goals SET 
        waterGoal = :water, 
        sleepGoal = :sleep, 
        stepsGoal = :steps, 
        caloriesGoal = :calories, 
        exerciseGoal = :exercise, 
        weightGoal = :weight 
        WHERE id = 1
    """)
    suspend fun updateGoals(
        water: Int,
        sleep: Float,
        steps: Int,
        calories: Int,
        exercise: Int,
        weight: Float
    )
    
    // ==================== USER STATS ====================
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStats(stats: UserStatsEntity)
    
    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun getUserStats(): Flow<UserStatsEntity?>
    
    @Query("SELECT * FROM user_stats WHERE id = 1")
    suspend fun getUserStatsOnce(): UserStatsEntity?
    
    @Query("""
        UPDATE user_stats SET 
        totalDays = :totalDays, 
        currentStreak = :currentStreak, 
        bestStreak = :bestStreak, 
        healthScore = :healthScore,
        lastActiveDate = :lastActiveDate
        WHERE id = 1
    """)
    suspend fun updateStats(
        totalDays: Int,
        currentStreak: Int,
        bestStreak: Int,
        healthScore: Int,
        lastActiveDate: Long
    )
    
    @Query("UPDATE user_stats SET currentStreak = :streak WHERE id = 1")
    suspend fun updateStreak(streak: Int)
    
    @Query("UPDATE user_stats SET healthScore = :score WHERE id = 1")
    suspend fun updateHealthScore(score: Int)
}
