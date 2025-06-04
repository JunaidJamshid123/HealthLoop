package com.example.healthloop.presentation.settings

import androidx.lifecycle.viewModelScope
import com.example.healthloop.presentation.model.UiState
import com.example.healthloop.presentation.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel : BaseViewModel<SettingsUiState>() {

    private val _reminderEnabled = MutableStateFlow(true)
    val reminderEnabled: StateFlow<Boolean> = _reminderEnabled

    private val _reminderTime = MutableStateFlow("20:00")
    val reminderTime: StateFlow<String> = _reminderTime

    private val _useDarkTheme = MutableStateFlow(false)
    val useDarkTheme: StateFlow<Boolean> = _useDarkTheme

    private val _useMetricSystem = MutableStateFlow(true)
    val useMetricSystem: StateFlow<Boolean> = _useMetricSystem

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            // This would normally load from SharedPreferences or DataStore
            // For now, we'll use default values
            updateState(UiState.Success(
                SettingsUiState(
                    reminderEnabled = _reminderEnabled.value,
                    reminderTime = _reminderTime.value,
                    useDarkTheme = _useDarkTheme.value,
                    useMetricSystem = _useMetricSystem.value
                )
            ))
        }
    }

    fun toggleReminder(enabled: Boolean) {
        _reminderEnabled.value = enabled
        saveSettings()
    }

    fun setReminderTime(time: String) {
        _reminderTime.value = time
        saveSettings()
    }

    fun toggleDarkTheme(enabled: Boolean) {
        _useDarkTheme.value = enabled
        saveSettings()
    }

    fun toggleMetricSystem(useMetric: Boolean) {
        _useMetricSystem.value = useMetric
        saveSettings()
    }

    private fun saveSettings() {
        viewModelScope.launch {
            // This would normally save to SharedPreferences or DataStore
            // For now, just update the UI state
            updateState(UiState.Success(
                SettingsUiState(
                    reminderEnabled = _reminderEnabled.value,
                    reminderTime = _reminderTime.value,
                    useDarkTheme = _useDarkTheme.value,
                    useMetricSystem = _useMetricSystem.value
                )
            ))
        }
    }
}

data class SettingsUiState(
    val reminderEnabled: Boolean = true,
    val reminderTime: String = "20:00",
    val useDarkTheme: Boolean = false,
    val useMetricSystem: Boolean = true
)