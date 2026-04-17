package com.junaidjamshid.healthloop.di

import com.junaidjamshid.healthloop.domain.repository.HealthRepository
import com.junaidjamshid.healthloop.domain.repository.UserProfileRepository
import com.junaidjamshid.healthloop.domain.usecase.AddHealthEntryUseCase
import com.junaidjamshid.healthloop.domain.usecase.UpdateHealthEntryUseCase
import com.junaidjamshid.healthloop.domain.usecase.GetHealthEntriesUseCase
import com.junaidjamshid.healthloop.domain.usecase.GetTodayEntryUseCase
import com.junaidjamshid.healthloop.domain.usecase.GetRecentEntriesUseCase
import com.junaidjamshid.healthloop.domain.usecase.GetLatestEntryUseCase
import com.junaidjamshid.healthloop.domain.usecase.GetTotalDaysLoggedUseCase
import com.junaidjamshid.healthloop.domain.usecase.profile.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    
    // ==================== HEALTH ENTRY USE CASES ====================
    
    @Provides
    @Singleton
    fun provideAddHealthEntryUseCase(
        repository: HealthRepository
    ): AddHealthEntryUseCase {
        return AddHealthEntryUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideUpdateHealthEntryUseCase(
        repository: HealthRepository
    ): UpdateHealthEntryUseCase {
        return UpdateHealthEntryUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetHealthEntriesUseCase(
        repository: HealthRepository
    ): GetHealthEntriesUseCase {
        return GetHealthEntriesUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideGetTodayEntryUseCase(
        repository: HealthRepository
    ): GetTodayEntryUseCase {
        return GetTodayEntryUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideGetRecentEntriesUseCase(
        repository: HealthRepository
    ): GetRecentEntriesUseCase {
        return GetRecentEntriesUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideGetLatestEntryUseCase(
        repository: HealthRepository
    ): GetLatestEntryUseCase {
        return GetLatestEntryUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideGetTotalDaysLoggedUseCase(
        repository: HealthRepository
    ): GetTotalDaysLoggedUseCase {
        return GetTotalDaysLoggedUseCase(repository)
    }
    
    // ==================== PROFILE USE CASES ====================
    
    @Provides
    @Singleton
    fun provideGetUserProfileUseCase(
        repository: UserProfileRepository
    ): GetUserProfileUseCase {
        return GetUserProfileUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideSaveUserProfileUseCase(
        repository: UserProfileRepository
    ): SaveUserProfileUseCase {
        return SaveUserProfileUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideUpdateProfileInfoUseCase(
        repository: UserProfileRepository
    ): UpdateProfileInfoUseCase {
        return UpdateProfileInfoUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideUpdateProfilePictureUseCase(
        repository: UserProfileRepository
    ): UpdateProfilePictureUseCase {
        return UpdateProfilePictureUseCase(repository)
    }
    
    // ==================== GOALS USE CASES ====================
    
    @Provides
    @Singleton
    fun provideGetUserGoalsUseCase(
        repository: UserProfileRepository
    ): GetUserGoalsUseCase {
        return GetUserGoalsUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideSaveUserGoalsUseCase(
        repository: UserProfileRepository
    ): SaveUserGoalsUseCase {
        return SaveUserGoalsUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideUpdateUserGoalsUseCase(
        repository: UserProfileRepository
    ): UpdateUserGoalsUseCase {
        return UpdateUserGoalsUseCase(repository)
    }
    
    // ==================== STATS USE CASES ====================
    
    @Provides
    @Singleton
    fun provideGetUserStatsUseCase(
        repository: UserProfileRepository
    ): GetUserStatsUseCase {
        return GetUserStatsUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideUpdateUserStatsUseCase(
        repository: UserProfileRepository
    ): UpdateUserStatsUseCase {
        return UpdateUserStatsUseCase(repository)
    }
    
    // ==================== INITIALIZATION USE CASE ====================
    
    @Provides
    @Singleton
    fun provideInitializeUserDataUseCase(
        repository: UserProfileRepository
    ): InitializeUserDataUseCase {
        return InitializeUserDataUseCase(repository)
    }
} 