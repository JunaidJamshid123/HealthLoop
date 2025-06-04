package com.example.healthloop.presentation.dashboard

import androidx.lifecycle.viewModelScope
import com.example.healthloop.presentation.model.HealthEntryUiModel
import com.example.healthloop.presentation.model.TodayHealthDataUiModel
import com.example.healthloop.presentation.model.UiState
import com.example.healthloop.presentation.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DashboardViewModel : BaseViewModel<DashboardUiState>() {
    
    private val _todayData = MutableStateFlow<TodayHealthDataUiModel?>(null)
    val todayData: StateFlow<TodayHealthDataUiModel?> = _todayData
    
    private val _recentEntries = MutableStateFlow<List<HealthEntryUiModel>>(emptyList())
    val recentEntries: StateFlow<List<HealthEntryUiModel>> = _recentEntries
    
    init {
        loadDashboardData()
    }
    
    private fun loadDashboardData() {
        viewModelScope.launch {
            // This would normally come from a use case in the domain layer
            // For now, we'll use mock data
            val mockTodayData = TodayHealthDataUiModel(
                waterIntake = 6,
                targetWater = 8,
                sleepHours = 7.5f,
                targetSleep = 8f,
                stepCount = 8500,
                targetSteps = 10000,
                mood = "😊",
                weight = 70.5f
            )
            
            val mockRecentEntries = listOf(
                HealthEntryUiModel(
                    id = 2,
                    date = getDateBefore(1),
                    waterIntake = 7,
                    sleepHours = 7.0f,
                    stepCount = 9200,
                    mood = "😐",
                    weight = 69.8f
                ),
                HealthEntryUiModel(
                    id = 3,
                    date = getDateBefore(2),
                    waterIntake = 8,
                    sleepHours = 8.5f,
                    stepCount = 12000,
                    mood = "😊",
                    weight = 70.2f
                ),
                HealthEntryUiModel(
                    id = 4,
                    date = getDateBefore(3),
                    waterIntake = 5,
                    sleepHours = 6.0f,
                    stepCount = 6500,
                    mood = "😔",
                    weight = 70.0f
                )
            )
            
            _todayData.value = mockTodayData
            _recentEntries.value = mockRecentEntries
            
            updateState(UiState.Success(
                DashboardUiState(
                    todayEntry = mockTodayData,
                    recentEntries = mockRecentEntries
                )
            ))
        }
    }
    
    private fun getDateBefore(days: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        return calendar.time
    }
}

data class DashboardUiState(
    val todayEntry: TodayHealthDataUiModel,
    val recentEntries: List<HealthEntryUiModel>
)