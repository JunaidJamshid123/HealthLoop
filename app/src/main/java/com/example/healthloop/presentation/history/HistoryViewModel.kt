package com.example.healthloop.presentation.history

import androidx.lifecycle.viewModelScope
import com.example.healthloop.presentation.model.HealthEntryUiModel
import com.example.healthloop.presentation.model.UiState
import com.example.healthloop.presentation.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class HistoryViewModel : BaseViewModel<HistoryUiState>() {

    private val _entries = MutableStateFlow<List<HealthEntryUiModel>>(emptyList())
    val entries: StateFlow<List<HealthEntryUiModel>> = _entries

    private val _selectedDateRange = MutableStateFlow(DateRange.LAST_WEEK)
    val selectedDateRange: StateFlow<DateRange> = _selectedDateRange

    init {
        loadHistoryData()
    }

    fun setDateRange(range: DateRange) {
        _selectedDateRange.value = range
        loadHistoryData()
    }

    private fun loadHistoryData() {
        viewModelScope.launch {
            updateState(UiState.Loading)
            
            // This would normally come from a use case in the domain layer
            // For now, we'll use mock data
            val mockEntries = generateMockEntries(30)
            
            // Filter based on selected date range
            val filteredEntries = when (_selectedDateRange.value) {
                DateRange.LAST_WEEK -> mockEntries.take(7)
                DateRange.LAST_MONTH -> mockEntries.take(30)
                DateRange.LAST_3_MONTHS -> mockEntries
            }
            
            _entries.value = filteredEntries
            
            updateState(UiState.Success(
                HistoryUiState(
                    entries = filteredEntries,
                    dateRange = _selectedDateRange.value
                )
            ))
        }
    }

    private fun generateMockEntries(count: Int): List<HealthEntryUiModel> {
        val entries = mutableListOf<HealthEntryUiModel>()
        val random = Random()
        
        for (i in 0 until count) {
            val date = getDateBefore(i)
            entries.add(
                HealthEntryUiModel(
                    id = i.toLong(),
                    date = date,
                    waterIntake = 5 + random.nextInt(4), // 5-8 glasses
                    sleepHours = 6f + random.nextFloat() * 3f, // 6-9 hours
                    stepCount = 5000 + random.nextInt(10000), // 5000-15000 steps
                    mood = listOf("😊", "😐", "😔", "😴", "😤")[random.nextInt(5)],
                    weight = 65f + random.nextFloat() * 10f // 65-75 kg
                )
            )
        }
        
        return entries
    }

    private fun getDateBefore(days: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        return calendar.time
    }
}

enum class DateRange {
    LAST_WEEK,
    LAST_MONTH,
    LAST_3_MONTHS
}

data class HistoryUiState(
    val entries: List<HealthEntryUiModel> = emptyList(),
    val dateRange: DateRange = DateRange.LAST_WEEK
)