package com.example.healthloop.presentation.history

import androidx.lifecycle.viewModelScope
import com.example.healthloop.domain.usecase.GetHealthEntriesUseCase
import com.example.healthloop.presentation.mapper.toUiModels
import com.example.healthloop.presentation.model.HealthEntryUiModel
import com.example.healthloop.presentation.model.UiState
import com.example.healthloop.presentation.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getHealthEntriesUseCase: GetHealthEntriesUseCase
) : BaseViewModel<HistoryUiState>() {

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
            try {
                updateState(UiState.Loading)
                
                val flow = when (_selectedDateRange.value) {
                    DateRange.ALL_TIME -> {
                        getHealthEntriesUseCase()
                    }
                    else -> {
                        val (startDate, endDate) = getDateRangeForFilter(_selectedDateRange.value)
                        getHealthEntriesUseCase(startDate, endDate)
                    }
                }

                flow
                    .catch { e ->
                        updateState(UiState.Error(e.message ?: "Failed to load history data"))
                    }
                    .collect { entries ->
                        val uiEntries = entries.toUiModels()
                        updateState(UiState.Success(
                            HistoryUiState(
                                entries = uiEntries,
                                dateRange = _selectedDateRange.value
                            )
                        ))
                    }
            } catch (e: Exception) {
                updateState(UiState.Error(e.message ?: "Failed to load history data"))
            }
        }
    }

    private fun getDateRangeForFilter(dateRange: DateRange): Pair<Date, Date> {
        val endDate = Calendar.getInstance().time
        val startDate = Calendar.getInstance()
        
        when (dateRange) {
            DateRange.THIS_WEEK -> {
                startDate.add(Calendar.DAY_OF_YEAR, -7)
            }
            DateRange.THIS_MONTH -> {
                startDate.add(Calendar.MONTH, -1)
            }
            DateRange.LAST_30_DAYS -> {
                startDate.add(Calendar.DAY_OF_YEAR, -30)
            }
            DateRange.LAST_90_DAYS -> {
                startDate.add(Calendar.DAY_OF_YEAR, -90)
            }
            else -> {
                // ALL_TIME - no need to adjust startDate
            }
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

data class HistoryUiState(
    val entries: List<HealthEntryUiModel> = emptyList(),
    val dateRange: DateRange = DateRange.ALL_TIME
)