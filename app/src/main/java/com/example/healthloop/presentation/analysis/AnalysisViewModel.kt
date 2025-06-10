package com.example.healthloop.presentation.analysis

import androidx.lifecycle.viewModelScope
import com.example.healthloop.presentation.model.HealthEntryUiModel
import com.example.healthloop.presentation.model.UiState
import com.example.healthloop.presentation.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AnalysisViewModel : BaseViewModel<AnalysisUiState>() {

    private val _entries = MutableStateFlow<List<HealthEntryUiModel>>(emptyList())
    val entries: StateFlow<List<HealthEntryUiModel>> = _entries

    private val _selectedMetric = MutableStateFlow(HealthMetric.WATER)
    val selectedMetric: StateFlow<HealthMetric> = _selectedMetric

    private val _selectedTimeRange = MutableStateFlow(TimeRange.WEEK)
    val selectedTimeRange: StateFlow<TimeRange> = _selectedTimeRange

    // Date formatter for converting Date to String
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    init {
        loadAnalysisData()
    }

    fun setMetric(metric: HealthMetric) {
        _selectedMetric.value = metric
        loadAnalysisData()
    }

    fun setTimeRange(range: TimeRange) {
        _selectedTimeRange.value = range
        loadAnalysisData()
    }

    private fun loadAnalysisData() {
        viewModelScope.launch {
            updateState(UiState.Loading)

            // This would normally come from a use case in the domain layer
            // For now, we'll use mock data
            val daysToFetch = when (_selectedTimeRange.value) {
                TimeRange.WEEK -> 7
                TimeRange.MONTH -> 30
            }

            val mockEntries = generateMockEntries(daysToFetch)
            _entries.value = mockEntries

            // Calculate statistics
            val stats = calculateStatistics(mockEntries, _selectedMetric.value)

            updateState(UiState.Success(
                AnalysisUiState(
                    entries = mockEntries,
                    metric = _selectedMetric.value,
                    timeRange = _selectedTimeRange.value,
                    average = stats.average,
                    min = stats.min,
                    max = stats.max
                )
            ))
        }
    }

    private fun calculateStatistics(entries: List<HealthEntryUiModel>, metric: HealthMetric): Statistics {
        if (entries.isEmpty()) {
            return Statistics(0f, 0f, 0f)
        }

        val values = entries.map { entry ->
            when (metric) {
                HealthMetric.WATER -> entry.waterIntake.toFloat()
                HealthMetric.SLEEP -> entry.sleepHours
                HealthMetric.STEPS -> entry.stepCount.toFloat()
                HealthMetric.WEIGHT -> entry.weight
            }
        }

        return Statistics(
            average = values.average().toFloat(),  // Convert Double to Float here
            min = values.minOrNull() ?: 0f,
            max = values.maxOrNull() ?: 0f
        )
    }

    private fun generateMockEntries(count: Int): List<HealthEntryUiModel> {
        val entries = mutableListOf<HealthEntryUiModel>()
        val random = Random()

        for (i in 0 until count) {
            val date = getDateBefore(i)
            entries.add(
                HealthEntryUiModel(
                    id = i.toLong(),
                    date = dateFormatter.format(date), // Convert Date to String here
                    waterIntake = 5 + random.nextInt(4), // 5-8 glasses
                    sleepHours = 6f + random.nextFloat() * 3f, // 6-9 hours
                    stepCount = 5000 + random.nextInt(10000), // 5000-15000 steps
                    mood = listOf("😊", "😐", "😔", "😴", "😤")[random.nextInt(5)],
                    weight = 65f + random.nextFloat() * 10f // 65-75 kg
                )
            )
        }

        return entries.reversed() // Most recent first
    }

    private fun getDateBefore(days: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        return calendar.time
    }
}

enum class HealthMetric {
    WATER,
    SLEEP,
    STEPS,
    WEIGHT
}

enum class TimeRange {
    WEEK,
    MONTH
}

data class Statistics(
    val average: Float,
    val min: Float,
    val max: Float
)

data class AnalysisUiState(
    val entries: List<HealthEntryUiModel> = emptyList(),
    val metric: HealthMetric = HealthMetric.WATER,
    val timeRange: TimeRange = TimeRange.WEEK,
    val average: Float = 0f,
    val min: Float = 0f,
    val max: Float = 0f
)