package com.example.healthloop.presentation.add_entry

import androidx.lifecycle.viewModelScope
import com.example.healthloop.presentation.model.HealthEntryUiModel
import com.example.healthloop.presentation.model.UiState
import com.example.healthloop.presentation.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class AddEntryViewModel : BaseViewModel<AddEntryUiState>() {

    private val _waterIntake = MutableStateFlow("")
    val waterIntake: StateFlow<String> = _waterIntake

    private val _sleepHours = MutableStateFlow("")
    val sleepHours: StateFlow<String> = _sleepHours

    private val _stepCount = MutableStateFlow("")
    val stepCount: StateFlow<String> = _stepCount

    private val _selectedMood = MutableStateFlow("")
    val selectedMood: StateFlow<String> = _selectedMood

    private val _weight = MutableStateFlow("")
    val weight: StateFlow<String> = _weight

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess

    init {
        updateState(UiState.Success(AddEntryUiState()))
    }

    fun updateWaterIntake(value: String) {
        _waterIntake.value = value
    }

    fun updateSleepHours(value: String) {
        _sleepHours.value = value
    }

    fun updateStepCount(value: String) {
        _stepCount.value = value
    }

    fun updateMood(value: String) {
        _selectedMood.value = value
    }

    fun updateWeight(value: String) {
        _weight.value = value
    }

    fun saveEntry() {
        viewModelScope.launch {
            try {
                // Validate inputs
                val waterIntakeValue = _waterIntake.value.toIntOrNull() ?: 0
                val sleepHoursValue = _sleepHours.value.toFloatOrNull() ?: 0f
                val stepCountValue = _stepCount.value.toIntOrNull() ?: 0
                val weightValue = _weight.value.toFloatOrNull() ?: 0f

                if (_selectedMood.value.isEmpty()) {
                    updateState(UiState.Error("Please select your mood"))
                    return@launch
                }

                // Create health entry
                val healthEntry = HealthEntryUiModel(
                    date = Date(),
                    waterIntake = waterIntakeValue,
                    sleepHours = sleepHoursValue,
                    stepCount = stepCountValue,
                    mood = _selectedMood.value,
                    weight = weightValue
                )

                // In a real app, this would save to repository
                // For now, just simulate success
                _saveSuccess.value = true
                
                // Reset form
                resetForm()
                
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
    }
}

data class AddEntryUiState(val isSaved: Boolean = false)