package com.example.healthloop.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthloop.domain.usecase.GetHealthEntriesUseCase
import com.example.healthloop.presentation.model.HealthEntryUiModel
import com.example.healthloop.presentation.model.TodayHealthDataUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getHealthEntriesUseCase: GetHealthEntriesUseCase
) : ViewModel() {

    // State for today's health data (most recent entry)
    private val _todayHealthData = MutableStateFlow<TodayHealthDataUiModel?>(null)
    val todayHealthData: StateFlow<TodayHealthDataUiModel?> = _todayHealthData.asStateFlow()

    // State for recent health entries (excluding today's entry)
    private val _recentHealthEntries = MutableStateFlow<List<HealthEntryUiModel>>(emptyList())
    val recentHealthEntries: StateFlow<List<HealthEntryUiModel>> = _recentHealthEntries.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadDashboardData()
    }

    /**
     * Load dashboard data from database
     */
    private fun loadDashboardData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                getHealthEntriesUseCase()
                    .catch { e ->
                        _errorMessage.value = "Failed to load health data: ${e.message}"
                        _todayHealthData.value = null
                        _recentHealthEntries.value = emptyList()
                    }
                    .collect { healthEntries ->
                        processHealthEntries(healthEntries)
                    }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load health data: ${e.message}"
                _todayHealthData.value = null
                _recentHealthEntries.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Process health entries and update UI states
     */
    private fun processHealthEntries(healthEntries: List<com.example.healthloop.domain.model.HealthEntry>) {
        if (healthEntries.isNotEmpty()) {
            // Sort entries by date (most recent first)
            val sortedEntries = healthEntries.sortedByDescending { it.date }

            // Get today's entry (most recent entry)
            val todayEntry = sortedEntries.firstOrNull()

            // Convert today's entry to TodayHealthDataUiModel
            todayEntry?.let { entry ->
                _todayHealthData.value = TodayHealthDataUiModel(
                    waterIntake = entry.waterIntake,
                    targetWater = 8, // Default target, you can make this configurable
                    sleepHours = entry.sleepHours,
                    targetSleep = 8f, // Default target, you can make this configurable
                    stepCount = entry.stepCount,
                    targetSteps = 10000, // Default target, you can make this configurable
                    mood = entry.mood,
                    weight = entry.weight
                )
            }

            // Get recent entries (excluding today's entry)
            val recentEntries = if (sortedEntries.size > 1) {
                sortedEntries.drop(1).take(5) // Take next 5 entries after today
            } else {
                emptyList()
            }

            // Convert to UI models
            _recentHealthEntries.value = recentEntries.map { entry ->
                HealthEntryUiModel(
                    id = entry.id,
                    date = formatDate(entry.date.time),
                    waterIntake = entry.waterIntake,
                    sleepHours = entry.sleepHours,
                    stepCount = entry.stepCount,
                    mood = entry.mood,
                    weight = entry.weight
                )
            }
        } else {
            // No entries found, set empty states
            _todayHealthData.value = null
            _recentHealthEntries.value = emptyList()
        }
    }

    /**
     * Refresh dashboard data
     */
    fun refreshData() {
        loadDashboardData()
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Format date for display in recent entries
     */
    private fun formatDate(date: Long): String {
        return try {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            dateFormat.format(Date(date))
        } catch (e: Exception) {
            "Unknown date"
        }
    }

    /**
     * Check if the given date is today
     */
    private fun isToday(timestamp: Long): Boolean {
        return try {
            val calendar = Calendar.getInstance()
            val today = Date()

            calendar.time = Date(timestamp)
            val entryDay = calendar.get(Calendar.DAY_OF_YEAR)
            val entryYear = calendar.get(Calendar.YEAR)

            calendar.time = today
            val todayDay = calendar.get(Calendar.DAY_OF_YEAR)
            val todayYear = calendar.get(Calendar.YEAR)

            entryDay == todayDay && entryYear == todayYear
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get progress percentage for a given metric
     */
    fun getWaterProgress(): Float {
        val today = _todayHealthData.value ?: return 0f
        return try {
            (today.waterIntake.toFloat() / today.targetWater).coerceIn(0f, 1f)
        } catch (e: Exception) {
            0f
        }
    }

    fun getSleepProgress(): Float {
        val today = _todayHealthData.value ?: return 0f
        return try {
            (today.sleepHours / today.targetSleep).coerceIn(0f, 1f)
        } catch (e: Exception) {
            0f
        }
    }

    fun getStepsProgress(): Float {
        val today = _todayHealthData.value ?: return 0f
        return try {
            (today.stepCount.toFloat() / today.targetSteps).coerceIn(0f, 1f)
        } catch (e: Exception) {
            0f
        }
    }

    /**
     * Get formatted display strings
     */
    fun getWaterDisplay(): String {
        val today = _todayHealthData.value ?: return "0 / 8 glasses"
        return try {
            "${today.waterIntake} / ${today.targetWater} glasses"
        } catch (e: Exception) {
            "0 / 8 glasses"
        }
    }

    fun getSleepDisplay(): String {
        val today = _todayHealthData.value ?: return "0 / 8 hours"
        return try {
            "${today.sleepHours} / ${today.targetSleep} hours"
        } catch (e: Exception) {
            "0 / 8 hours"
        }
    }

    fun getStepsDisplay(): String {
        val today = _todayHealthData.value ?: return "0 / 10,000 steps"
        return try {
            "${formatNumber(today.stepCount)} / ${formatNumber(today.targetSteps)} steps"
        } catch (e: Exception) {
            "0 / 10,000 steps"
        }
    }

    /**
     * Format numbers with thousand separators
     */
    private fun formatNumber(number: Int): String {
        return try {
            String.format(Locale.getDefault(), "%,d", number)
        } catch (e: Exception) {
            number.toString()
        }
    }

    /**
     * Check if there's data for today
     */
    fun hasTodayData(): Boolean {
        return _todayHealthData.value != null
    }

    /**
     * Check if there are recent entries
     */
    fun hasRecentEntries(): Boolean {
        return _recentHealthEntries.value.isNotEmpty()
    }

    /**
     * Get summary stats for display
     */
    fun getTodayMood(): String {
        return _todayHealthData.value?.mood ?: "😐"
    }

    fun getTodayWeight(): String {
        return _todayHealthData.value?.let { "${it.weight} kg" } ?: "No data"
    }

    /**
     * Handle lifecycle events
     */
    override fun onCleared() {
        super.onCleared()
        // Clean up any resources if needed
    }
}