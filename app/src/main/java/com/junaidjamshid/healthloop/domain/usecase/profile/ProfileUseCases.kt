package com.junaidjamshid.healthloop.domain.usecase.profile

import com.junaidjamshid.healthloop.domain.model.UserGoals
import com.junaidjamshid.healthloop.domain.model.UserProfile
import com.junaidjamshid.healthloop.domain.model.UserStats
import com.junaidjamshid.healthloop.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {
    operator fun invoke(): Flow<UserProfile?> = repository.getUserProfile()
    
    suspend fun getOnce(): UserProfile? = repository.getUserProfileOnce()
}

class SaveUserProfileUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {
    suspend operator fun invoke(profile: UserProfile) = repository.saveUserProfile(profile)
}

class UpdateProfileInfoUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        age: Int,
        weight: Float,
        height: Int
    ) = repository.updateProfileInfo(name, email, age, weight, height)
}

class UpdateProfilePictureUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {
    suspend operator fun invoke(base64Image: String?) = repository.updateProfilePicture(base64Image)
}

class GetUserGoalsUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {
    operator fun invoke(): Flow<UserGoals?> = repository.getUserGoals()
    
    suspend fun getOnce(): UserGoals? = repository.getUserGoalsOnce()
}

class SaveUserGoalsUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {
    suspend operator fun invoke(goals: UserGoals) = repository.saveUserGoals(goals)
}

class UpdateUserGoalsUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {
    suspend operator fun invoke(
        waterGoal: Int,
        sleepGoal: Float,
        stepsGoal: Int,
        caloriesGoal: Int,
        exerciseGoal: Int,
        weightGoal: Float
    ) = repository.updateGoals(waterGoal, sleepGoal, stepsGoal, caloriesGoal, exerciseGoal, weightGoal)
}

class GetUserStatsUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {
    operator fun invoke(): Flow<UserStats?> = repository.getUserStats()
    
    suspend fun getOnce(): UserStats? = repository.getUserStatsOnce()
}

class UpdateUserStatsUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {
    suspend operator fun invoke(
        totalDays: Int,
        currentStreak: Int,
        bestStreak: Int,
        healthScore: Int
    ) = repository.updateStats(totalDays, currentStreak, bestStreak, healthScore)
}

class InitializeUserDataUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {
    suspend operator fun invoke() = repository.initializeDefaultData()
}
