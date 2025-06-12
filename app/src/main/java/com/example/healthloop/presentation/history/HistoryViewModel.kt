package com.example.healthloop.presentation.history

import androidx.lifecycle.viewModelScope
import com.example.healthloop.domain.usecase.GetHealthEntriesUseCase
import com.example.healthloop.presentation.model.HealthEntryUiModel
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

data class HistoryUiState(
    val entries: List<HealthEntryUiModel> = emptyList(),
    val dateRange: DateRange = DateRange.ALL_TIME
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getHealthEntriesUseCase: GetHealthEntriesUseCase
) : androidx.lifecycle.ViewModel() {
    private val _uiState = MutableStateFlow<UiState<HistoryUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<HistoryUiState>> = _uiState.asStateFlow()

    private val _selectedDateRange = MutableStateFlow(DateRange.ALL_TIME)
    val selectedDateRange: StateFlow<DateRange> = _selectedDateRange.asStateFlow()

    init {
        loadHistoryData()
    }

    fun setDateRange(range: DateRange) {
        _selectedDateRange.value = range
        loadHistoryData()
    }

    private fun loadHistoryData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val flow = when (_selectedDateRange.value) {
                DateRange.ALL_TIME -> getHealthEntriesUseCase()
                else -> {
                    val (startDate, endDate) = getDateRangeForFilter(_selectedDateRange.value)
                    getHealthEntriesUseCase(startDate, endDate)
                }
            }
            flow
                .catch { e ->
                    _uiState.value = UiState.Error(e.message ?: "Failed to load history data")
                }
                .collect { entries ->
                    val uiEntries = entries.map {
                        HealthEntryUiModel(
                            id = it.id,
                            date = try {
                                SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it.date)
                            } catch (e: Exception) { "Unknown" },
                            waterIntake = it.waterIntake,
                            sleepHours = it.sleepHours,
                            stepCount = it.stepCount,
                            mood = it.mood,
                            weight = it.weight
                        )
                    }
                    _uiState.value = UiState.Success(
                        HistoryUiState(
                            entries = uiEntries,
                            dateRange = _selectedDateRange.value
                        )
                    )
                }
        }
    }

    private fun getDateRangeForFilter(dateRange: DateRange): Pair<Date, Date> {
        val endDate = Calendar.getInstance().time
        val startDate = Calendar.getInstance()
        when (dateRange) {
            DateRange.THIS_WEEK -> startDate.add(Calendar.DAY_OF_YEAR, -7)
            DateRange.THIS_MONTH -> startDate.add(Calendar.MONTH, -1)
            DateRange.LAST_30_DAYS -> startDate.add(Calendar.DAY_OF_YEAR, -30)
            DateRange.LAST_90_DAYS -> startDate.add(Calendar.DAY_OF_YEAR, -90)
            else -> {}
        }
        return Pair(startDate.time, endDate)
    }
}

enum class DateRange {
    ALL_TIME,
    THIS_WEEK,
    THIS_MONTH,
    LAST_30_DAYS,
    LAST_90_DAYS
}