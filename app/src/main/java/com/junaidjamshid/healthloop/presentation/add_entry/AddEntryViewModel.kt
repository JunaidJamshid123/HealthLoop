package com.junaidjamshid.healthloop.presentation.add_entry

import androidx.lifecycle.viewModelScope
import com.junaidjamshid.healthloop.domain.usecase.AddHealthEntryUseCase
import com.junaidjamshid.healthloop.domain.usecase.UpdateHealthEntryUseCase
import com.junaidjamshid.healthloop.domain.usecase.GetTodayEntryUseCase
import com.junaidjamshid.healthloop.presentation.mapper.toDomain
import com.junaidjamshid.healthloop.presentation.model.HealthEntryUiModel
import com.junaidjamshid.healthloop.presentation.model.UiState
import com.junaidjamshid.healthloop.presentation.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddEntryViewModel @Inject constructor(
    private val addHealthEntryUseCase: AddHealthEntryUseCase,
    private val updateHealthEntryUseCase: UpdateHealthEntryUseCase,
    private val getTodayEntryUseCase: GetTodayEntryUseCase
) : BaseViewModel<AddEntryUiState>() {

    private val _waterIntake = MutableStateFlow("")
    val waterIntake: StateFlow<String> = _waterIntake.asStateFlow()

    private val _sleepHours = MutableStateFlow("")
    val sleepHours: StateFlow<String> = _sleepHours.asStateFlow()

    private val _stepCount = MutableStateFlow("")
    val stepCount: StateFlow<String> = _stepCount.asStateFlow()

    private val _selectedMood = MutableStateFlow("")
    val selectedMood: StateFlow<String> = _selectedMood.asStateFlow()

    private val _weight = MutableStateFlow("")
    val weight: StateFlow<String> = _weight.asStateFlow()

    private val _calories = MutableStateFlow("")
    val calories: StateFlow<String> = _calories.asStateFlow()

    private val _exerciseMinutes = MutableStateFlow("")
    val exerciseMinutes: StateFlow<String> = _exerciseMinutes.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()
    
    // Track if we're editing an existing entry
    private var existingEntryId: Long? = null
    private var existingEntryDate: Date? = null
    
    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    init {
        updateState(UiState.Success(AddEntryUiState()))
        loadTodayEntryIfExists()
    }
    
    private fun loadTodayEntryIfExists() {
        viewModelScope.launch {
            try {
                val todayEntry = getTodayEntryUseCase.getOnce()
                if (todayEntry != null) {
                    // Pre-fill the form with today's entry
                    existingEntryId = todayEntry.id
                    existingEntryDate = todayEntry.date
                    _isEditing.value = true
                    
                    _waterIntake.value = todayEntry.waterIntake.toString()
                    _sleepHours.value = todayEntry.sleepHours.toString()
                    _stepCount.value = todayEntry.stepCount.toString()
                    _selectedMood.value = todayEntry.mood
                    _weight.value = if (todayEntry.weight > 0) todayEntry.weight.toString() else ""
                    _calories.value = if (todayEntry.calories > 0) todayEntry.calories.toString() else ""
                    _exerciseMinutes.value = if (todayEntry.exerciseMinutes > 0) todayEntry.exerciseMinutes.toString() else ""
                }
            } catch (e: Exception) {
                // If we can't load today's entry, just start fresh
            }
        }
    }

    fun updateWaterIntake(value: String) {
        if (value.isEmpty() || value.toIntOrNull() != null) {
            _waterIntake.value = value
        }
    }

    fun updateSleepHours(value: String) {
        if (value.isEmpty() || value.toFloatOrNull() != null) {
            _sleepHours.value = value
        }
    }

    fun updateStepCount(value: String) {
        if (value.isEmpty() || value.toIntOrNull() != null) {
            _stepCount.value = value
        }
    }

    fun updateMood(value: String) {
        _selectedMood.value = value
    }

    fun updateWeight(value: String) {
        if (value.isEmpty() || value.toFloatOrNull() != null) {
            _weight.value = value
        }
    }

    fun updateCalories(value: String) {
        if (value.isEmpty() || value.toIntOrNull() != null) {
            _calories.value = value
        }
    }

    fun updateExerciseMinutes(value: String) {
        if (value.isEmpty() || value.toIntOrNull() != null) {
            _exerciseMinutes.value = value
        }
    }

    fun saveEntry() {
        viewModelScope.launch {
            try {
                updateState(UiState.Loading)

                // Validate inputs
                val waterIntakeValue = _waterIntake.value.toIntOrNull() ?: 0
                val sleepHoursValue = _sleepHours.value.toFloatOrNull() ?: 0f
                val stepCountValue = _stepCount.value.toIntOrNull() ?: 0
                val weightValue = _weight.value.toFloatOrNull() ?: 0f
                val caloriesValue = _calories.value.toIntOrNull() ?: 0
                val exerciseMinutesValue = _exerciseMinutes.value.toIntOrNull() ?: 0

                if (_selectedMood.value.isEmpty()) {
                    updateState(UiState.Error("Please select your mood"))
                    return@launch
                }

                // Create health entry
                val healthEntry = HealthEntryUiModel(
                    id = existingEntryId ?: 0,
                    date = existingEntryDate ?: Date(),
                    waterIntake = waterIntakeValue,
                    sleepHours = sleepHoursValue,
                    stepCount = stepCountValue,
                    mood = _selectedMood.value,
                    weight = weightValue,
                    calories = caloriesValue,
                    exerciseMinutes = exerciseMinutesValue
                )

                // Save or update to database
                if (existingEntryId != null) {
                    // Update existing entry
                    updateHealthEntryUseCase(healthEntry.toDomain())
                } else {
                    // Insert new entry
                    addHealthEntryUseCase(healthEntry.toDomain())
                }

                // Set success flag
                _saveSuccess.value = true

                // After saving, reload to get the new entry ID if it was new
                if (existingEntryId == null) {
                    loadTodayEntryIfExists()
                }

                updateState(UiState.Success(AddEntryUiState(isSaved = true)))
            } catch (e: Exception) {
                updateState(UiState.Error(e.message ?: "Failed to save entry"))
            }
        }
    }

    private fun resetForm() {
        _waterIntake.value = ""
        _sleepHours.value = ""
        _stepCount.value = ""
        _selectedMood.value = ""
        _weight.value = ""
        _calories.value = ""
        _exerciseMinutes.value = ""
        existingEntryId = null
        existingEntryDate = null
        _isEditing.value = false
    }

    /**
     * Format current date as string for UI display
     */
    private fun formatCurrentDate(): String {
        return try {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            dateFormat.format(Date())
        } catch (e: Exception) {
            "Today"
        }
    }

    /**
     * Clear the save success flag
     */
    fun clearSaveSuccess() {
        _saveSuccess.value = false
    }

    /**
     * Get current timestamp for domain layer
     */
    fun getCurrentTimestamp(): Long {
        return Date().time
    }
}

data class AddEntryUiState(val isSaved: Boolean = false)