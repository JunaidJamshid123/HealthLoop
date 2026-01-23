package com.example.healthloop.presentation.analysis

import androidx.lifecycle.viewModelScope
import com.example.healthloop.domain.usecase.GetHealthEntriesUseCase
import com.example.healthloop.domain.usecase.GetRecentEntriesUseCase
import com.example.healthloop.domain.usecase.GetTotalDaysLoggedUseCase
import com.example.healthloop.domain.usecase.profile.GetUserGoalsUseCase
import com.example.healthloop.domain.model.UserGoals
import com.example.healthloop.presentation.mapper.toUiModels
import com.example.healthloop.presentation.model.HealthEntryUiModel
import com.example.healthloop.presentation.model.UiState
import com.example.healthloop.presentation.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val getHealthEntriesUseCase: GetHealthEntriesUseCase,
    private val getUserGoalsUseCase: GetUserGoalsUseCase,
    private val getTotalDaysLoggedUseCase: GetTotalDaysLoggedUseCase
) : BaseViewModel<AnalysisUiState>() {

    private val _entries = MutableStateFlow<List<HealthEntryUiModel>>(emptyList())
    val entries: StateFlow<List<HealthEntryUiModel>> = _entries

    private val _selectedMetric = MutableStateFlow(HealthMetric.WATER)
    val selectedMetric: StateFlow<HealthMetric> = _selectedMetric

    private val _selectedTimeRange = MutableStateFlow(TimeRange.WEEK)
    val selectedTimeRange: StateFlow<TimeRange> = _selectedTimeRange

    // Date formatter for converting Date to String
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private val _state = MutableStateFlow<UiState<AnalysisUiState>>(UiState.Loading)
    val state: StateFlow<UiState<AnalysisUiState>> get() = _state

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
            _state.value = UiState.Loading
            val (startDate, endDate) = getDateRange(_selectedTimeRange.value)
            
            try {
                combine(
                    getHealthEntriesUseCase(startDate, endDate),
                    getUserGoalsUseCase(),
                    getTotalDaysLoggedUseCase()
                ) { entries, userGoals, totalDays ->
                    val uiEntries = entries.toUiModels().sortedBy { it.date }
                    _entries.value = uiEntries
                    
                    val goals = userGoals ?: UserGoals.default()
                    
                    // Calculate statistics for each metric
                    val waterStats = calculateStatistics(uiEntries, HealthMetric.WATER)
                    val sleepStats = calculateStatistics(uiEntries, HealthMetric.SLEEP)
                    val stepsStats = calculateStatistics(uiEntries, HealthMetric.STEPS)
                    val weightStats = calculateStatistics(uiEntries, HealthMetric.WEIGHT)
                    val caloriesStats = calculateCaloriesStats(uiEntries)
                    val exerciseStats = calculateExerciseStats(uiEntries)
                    
                    // Calculate mood distribution
                    val moodDistribution = calculateMoodDistribution(uiEntries)
                    
                    // Calculate trends
                    val waterTrend = calculateTrend(uiEntries.map { it.waterIntake.toFloat() })
                    val sleepTrend = calculateTrend(uiEntries.map { it.sleepHours })
                    val stepsTrend = calculateTrend(uiEntries.map { it.stepCount.toFloat() })
                    val weightTrend = calculateTrend(uiEntries.map { it.weight })
                    
                    // Current metric stats
                    val currentStats = calculateStatistics(uiEntries, _selectedMetric.value)
                    
                    AnalysisUiState(
                        entries = uiEntries,
                        metric = _selectedMetric.value,
                        timeRange = _selectedTimeRange.value,
                        average = currentStats.average,
                        min = currentStats.min,
                        max = currentStats.max,
                        waterStats = waterStats,
                        sleepStats = sleepStats,
                        stepsStats = stepsStats,
                        weightStats = weightStats,
                        caloriesStats = caloriesStats,
                        exerciseStats = exerciseStats,
                        userGoals = goals,
                        totalDaysLogged = totalDays,
                        moodDistribution = moodDistribution,
                        waterTrend = waterTrend,
                        sleepTrend = sleepTrend,
                        stepsTrend = stepsTrend,
                        weightTrend = weightTrend
                    )
                }
                .catch { e ->
                    _state.value = UiState.Error(e.message ?: "Failed to load analysis data")
                }
                .collect { state ->
                    _state.value = UiState.Success(state)
                }
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message ?: "Failed to load analysis data")
            }
        }
    }

    private fun getDateRange(range: TimeRange): Pair<Date, Date> {
        val end = Calendar.getInstance()
        val start = Calendar.getInstance()
        when (range) {
            TimeRange.WEEK -> start.add(Calendar.DAY_OF_YEAR, -6)
            TimeRange.MONTH -> start.add(Calendar.DAY_OF_YEAR, -29)
        }
        return Pair(start.time, end.time)
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
        }.filter { it > 0 }
        
        if (values.isEmpty()) {
            return Statistics(0f, 0f, 0f)
        }

        return Statistics(
            average = values.average().toFloat(),
            min = values.minOrNull() ?: 0f,
            max = values.maxOrNull() ?: 0f
        )
    }
    
    private fun calculateCaloriesStats(entries: List<HealthEntryUiModel>): Statistics {
        val values = entries.map { it.calories.toFloat() }.filter { it > 0 }
        if (values.isEmpty()) return Statistics(0f, 0f, 0f)
        return Statistics(
            average = values.average().toFloat(),
            min = values.minOrNull() ?: 0f,
            max = values.maxOrNull() ?: 0f
        )
    }
    
    private fun calculateExerciseStats(entries: List<HealthEntryUiModel>): Statistics {
        val values = entries.map { it.exerciseMinutes.toFloat() }.filter { it > 0 }
        if (values.isEmpty()) return Statistics(0f, 0f, 0f)
        return Statistics(
            average = values.average().toFloat(),
            min = values.minOrNull() ?: 0f,
            max = values.maxOrNull() ?: 0f
        )
    }
    
    private fun calculateMoodDistribution(entries: List<HealthEntryUiModel>): Map<String, Int> {
        return entries
            .filter { it.mood.isNotBlank() }
            .groupingBy { it.mood }
            .eachCount()
    }
    
    private fun calculateTrend(values: List<Float>): TrendDirection {
        if (values.size < 2) return TrendDirection.STABLE
        
        val validValues = values.filter { it > 0 }
        if (validValues.size < 2) return TrendDirection.STABLE
        
        val firstHalf = validValues.take(validValues.size / 2).average()
        val secondHalf = validValues.takeLast(validValues.size / 2).average()
        
        val difference = secondHalf - firstHalf
        val threshold = firstHalf * 0.05 // 5% threshold
        
        return when {
            difference > threshold -> TrendDirection.UP
            difference < -threshold -> TrendDirection.DOWN
            else -> TrendDirection.STABLE
        }
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

enum class TrendDirection {
    UP, DOWN, STABLE
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
    val max: Float = 0f,
    val waterStats: Statistics = Statistics(0f, 0f, 0f),
    val sleepStats: Statistics = Statistics(0f, 0f, 0f),
    val stepsStats: Statistics = Statistics(0f, 0f, 0f),
    val weightStats: Statistics = Statistics(0f, 0f, 0f),
    val caloriesStats: Statistics = Statistics(0f, 0f, 0f),
    val exerciseStats: Statistics = Statistics(0f, 0f, 0f),
    val userGoals: UserGoals = UserGoals.default(),
    val totalDaysLogged: Int = 0,
    val moodDistribution: Map<String, Int> = emptyMap(),
    val waterTrend: TrendDirection = TrendDirection.STABLE,
    val sleepTrend: TrendDirection = TrendDirection.STABLE,
    val stepsTrend: TrendDirection = TrendDirection.STABLE,
    val weightTrend: TrendDirection = TrendDirection.STABLE
)