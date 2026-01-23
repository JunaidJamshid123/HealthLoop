package com.example.healthloop.presentation.profile

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthloop.data.local.util.ImageUtils
import com.example.healthloop.domain.model.UserGoals
import com.example.healthloop.domain.model.UserProfile
import com.example.healthloop.domain.model.UserStats
import com.example.healthloop.domain.usecase.profile.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = true,
    val userProfile: UserProfile = UserProfile.default(),
    val userGoals: UserGoals = UserGoals.default(),
    val userStats: UserStats = UserStats.default(),
    val profilePictureBitmap: Bitmap? = null,
    val error: String? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)

sealed class ProfileEvent {
    data class UpdateProfile(
        val name: String,
        val email: String,
        val age: Int,
        val weight: Float,
        val height: Int
    ) : ProfileEvent()
    
    data class UpdateProfilePicture(val uri: Uri, val context: Context) : ProfileEvent()
    object RemoveProfilePicture : ProfileEvent()
    
    data class UpdateGoals(
        val waterGoal: Int,
        val sleepGoal: Float,
        val stepsGoal: Int,
        val caloriesGoal: Int,
        val exerciseGoal: Int,
        val weightGoal: Float
    ) : ProfileEvent()
    
    data class UpdateStats(
        val totalDays: Int,
        val currentStreak: Int,
        val bestStreak: Int,
        val healthScore: Int
    ) : ProfileEvent()
    
    object ClearError : ProfileEvent()
    object ClearSaveSuccess : ProfileEvent()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val saveUserProfileUseCase: SaveUserProfileUseCase,
    private val updateProfileInfoUseCase: UpdateProfileInfoUseCase,
    private val updateProfilePictureUseCase: UpdateProfilePictureUseCase,
    private val getUserGoalsUseCase: GetUserGoalsUseCase,
    private val saveUserGoalsUseCase: SaveUserGoalsUseCase,
    private val updateUserGoalsUseCase: UpdateUserGoalsUseCase,
    private val getUserStatsUseCase: GetUserStatsUseCase,
    private val updateUserStatsUseCase: UpdateUserStatsUseCase,
    private val initializeUserDataUseCase: InitializeUserDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        initializeData()
        observeUserData()
    }

    private fun initializeData() {
        viewModelScope.launch {
            try {
                initializeUserDataUseCase()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to initialize data: ${e.message}") }
            }
        }
    }

    private fun observeUserData() {
        // Observe user profile
        viewModelScope.launch {
            getUserProfileUseCase().collect { profile ->
                profile?.let { userProfile ->
                    val bitmap = userProfile.profilePictureBase64?.let { 
                        ImageUtils.base64ToBitmap(it) 
                    }
                    _uiState.update { state ->
                        state.copy(
                            userProfile = userProfile,
                            profilePictureBitmap = bitmap,
                            isLoading = false
                        )
                    }
                }
            }
        }

        // Observe user goals
        viewModelScope.launch {
            getUserGoalsUseCase().collect { goals ->
                goals?.let { userGoals ->
                    _uiState.update { it.copy(userGoals = userGoals) }
                }
            }
        }

        // Observe user stats
        viewModelScope.launch {
            getUserStatsUseCase().collect { stats ->
                stats?.let { userStats ->
                    _uiState.update { it.copy(userStats = userStats) }
                }
            }
        }
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.UpdateProfile -> updateProfile(event)
            is ProfileEvent.UpdateProfilePicture -> updateProfilePicture(event.uri, event.context)
            is ProfileEvent.RemoveProfilePicture -> removeProfilePicture()
            is ProfileEvent.UpdateGoals -> updateGoals(event)
            is ProfileEvent.UpdateStats -> updateStats(event)
            is ProfileEvent.ClearError -> _uiState.update { it.copy(error = null) }
            is ProfileEvent.ClearSaveSuccess -> _uiState.update { it.copy(saveSuccess = false) }
        }
    }

    private fun updateProfile(event: ProfileEvent.UpdateProfile) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                // Check if profile exists
                val existingProfile = getUserProfileUseCase.getOnce()
                
                if (existingProfile != null) {
                    // Update existing profile
                    updateProfileInfoUseCase(
                        name = event.name,
                        email = event.email,
                        age = event.age,
                        weight = event.weight,
                        height = event.height
                    )
                } else {
                    // Create new profile
                    val newProfile = UserProfile(
                        name = event.name,
                        email = event.email,
                        age = event.age,
                        weight = event.weight,
                        height = event.height
                    )
                    saveUserProfileUseCase(newProfile)
                }
                
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isSaving = false, 
                        error = "Failed to update profile: ${e.message}"
                    ) 
                }
            }
        }
    }

    private fun updateProfilePicture(uri: Uri, context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                // Convert URI to Base64
                val base64Image = ImageUtils.uriToBase64(context, uri)
                
                if (base64Image != null) {
                    // Check if profile exists
                    val existingProfile = getUserProfileUseCase.getOnce()
                    
                    if (existingProfile != null) {
                        // Update profile picture
                        updateProfilePictureUseCase(base64Image)
                    } else {
                        // Create profile with picture
                        val newProfile = UserProfile.default().copy(
                            profilePictureBase64 = base64Image
                        )
                        saveUserProfileUseCase(newProfile)
                    }
                    
                    // Update bitmap in UI state
                    val bitmap = ImageUtils.base64ToBitmap(base64Image)
                    _uiState.update { 
                        it.copy(
                            profilePictureBitmap = bitmap,
                            isSaving = false, 
                            saveSuccess = true
                        ) 
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isSaving = false, 
                            error = "Failed to process image"
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isSaving = false, 
                        error = "Failed to update profile picture: ${e.message}"
                    ) 
                }
            }
        }
    }

    private fun removeProfilePicture() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                updateProfilePictureUseCase(null)
                _uiState.update { 
                    it.copy(
                        profilePictureBitmap = null,
                        isSaving = false, 
                        saveSuccess = true
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isSaving = false, 
                        error = "Failed to remove profile picture: ${e.message}"
                    ) 
                }
            }
        }
    }

    private fun updateGoals(event: ProfileEvent.UpdateGoals) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                // Check if goals exist
                val existingGoals = getUserGoalsUseCase.getOnce()
                
                if (existingGoals != null) {
                    // Update existing goals
                    updateUserGoalsUseCase(
                        waterGoal = event.waterGoal,
                        sleepGoal = event.sleepGoal,
                        stepsGoal = event.stepsGoal,
                        caloriesGoal = event.caloriesGoal,
                        exerciseGoal = event.exerciseGoal,
                        weightGoal = event.weightGoal
                    )
                } else {
                    // Create new goals
                    val newGoals = UserGoals(
                        waterGoal = event.waterGoal,
                        sleepGoal = event.sleepGoal,
                        stepsGoal = event.stepsGoal,
                        caloriesGoal = event.caloriesGoal,
                        exerciseGoal = event.exerciseGoal,
                        weightGoal = event.weightGoal
                    )
                    saveUserGoalsUseCase(newGoals)
                }
                
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isSaving = false, 
                        error = "Failed to update goals: ${e.message}"
                    ) 
                }
            }
        }
    }

    private fun updateStats(event: ProfileEvent.UpdateStats) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                updateUserStatsUseCase(
                    totalDays = event.totalDays,
                    currentStreak = event.currentStreak,
                    bestStreak = event.bestStreak,
                    healthScore = event.healthScore
                )
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isSaving = false, 
                        error = "Failed to update stats: ${e.message}"
                    ) 
                }
            }
        }
    }
}
