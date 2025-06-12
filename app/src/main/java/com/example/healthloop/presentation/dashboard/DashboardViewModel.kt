package com.example.healthloop.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthloop.domain.usecase.GetHealthEntriesUseCase
import com.example.healthloop.presentation.model.HealthEntryUiModel
import com.example.healthloop.presentation.model.TodayHealthDataUiModel
import com.example.healthloop.presentation.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class DashboardUiState(
    val today: TodayHealthDataUiModel? = null,
    val recent: List<HealthEntryUiModel> = emptyList()
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getHealthEntriesUseCase: GetHealthEntriesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<DashboardUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<DashboardUiState>> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            getHealthEntriesUseCase()
                .catch { e ->
                    _uiState.value = UiState.Error(e.message ?: "Failed to load dashboard data")
                }
                .collect { entries ->
                    if (entries.isEmpty()) {
                        _uiState.value = UiState.Success(DashboardUiState())
                    } else {
                        val sorted = entries.sortedByDescending { it.date }
                        val today = sorted.firstOrNull()?.let { entry ->
                            TodayHealthDataUiModel(
                                waterIntake = entry.waterIntake,
                                targetWater = 8,
                                sleepHours = entry.sleepHours,
                                targetSleep = 8f,
                                stepCount = entry.stepCount,
                                targetSteps = 10000,
                                mood = entry.mood,
                                weight = entry.weight
                            )
                        }
                        val recent = if (sorted.size > 1) sorted.drop(1).take(5).map {
                            HealthEntryUiModel(
                                id = it.id,
                                date = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it.date),
                                waterIntake = it.waterIntake,
                                sleepHours = it.sleepHours,
                                stepCount = it.stepCount,
                                mood = it.mood,
                                weight = it.weight
                            )
                        } else emptyList()
                        _uiState.value = UiState.Success(DashboardUiState(today, recent))
                    }
                }
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
        // Implementation needed
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
     * Handle lifecycle events
     */
    override fun onCleared() {
        super.onCleared()
        // Clean up any resources if needed
    }
}