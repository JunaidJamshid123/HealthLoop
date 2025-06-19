package com.example.healthloop.presentation.analysis

import androidx.lifecycle.viewModelScope
import com.example.healthloop.domain.usecase.GetHealthEntriesUseCase
import com.example.healthloop.presentation.mapper.toUiModels
import com.example.healthloop.presentation.model.HealthEntryUiModel
import com.example.healthloop.presentation.model.UiState
import com.example.healthloop.presentation.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val getHealthEntriesUseCase: GetHealthEntriesUseCase
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
            getHealthEntriesUseCase(startDate, endDate)
                .catch { e ->
                    _state.value = UiState.Error(e.message ?: "Failed to load analysis data")
                }
                .collectLatest { entries ->
                    val uiEntries = entries.toUiModels().sortedBy { it.date }
                    _entries.value = uiEntries
                    val stats = calculateStatistics(uiEntries, _selectedMetric.value)
                    _state.value = UiState.Success(
                        AnalysisUiState(
                            entries = uiEntries,
                            metric = _selectedMetric.value,
                            timeRange = _selectedTimeRange.value,
                            average = stats.average,
                            min = stats.min,
                            max = stats.max
                        )
                    )
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
        }

        return Statistics(
            average = values.average().toFloat(),  // Convert Double to Float here
            min = values.minOrNull() ?: 0f,
            max = values.maxOrNull() ?: 0f
        )
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