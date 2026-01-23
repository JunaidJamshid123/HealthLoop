package com.example.healthloop.presentation.history

import androidx.lifecycle.viewModelScope
import com.example.healthloop.domain.usecase.GetHealthEntriesUseCase
import com.example.healthloop.domain.usecase.GetRecentEntriesUseCase
import com.example.healthloop.domain.usecase.profile.GetUserGoalsUseCase
import com.example.healthloop.domain.model.UserGoals
import com.example.healthloop.presentation.model.HealthEntryUiModel
import com.example.healthloop.presentation.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class HistoryUiState(
    val entries: List<HealthEntryUiModel> = emptyList(),
    val selectedDateEntry: HealthEntryUiModel? = null,
    val dateRange: DateRange = DateRange.ALL_TIME,
    val selectedDate: Calendar = Calendar.getInstance(),
    val userGoals: UserGoals = UserGoals.default(),
    val weeklyMoodData: List<Pair<String, String>> = emptyList(), // day to mood
    val mostCommonMood: String = ""
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getHealthEntriesUseCase: GetHealthEntriesUseCase,
    private val getRecentEntriesUseCase: GetRecentEntriesUseCase,
    private val getUserGoalsUseCase: GetUserGoalsUseCase
) : androidx.lifecycle.ViewModel() {
    private val _uiState = MutableStateFlow<UiState<HistoryUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<HistoryUiState>> = _uiState.asStateFlow()

    private val _selectedDateRange = MutableStateFlow(DateRange.ALL_TIME)
    val selectedDateRange: StateFlow<DateRange> = _selectedDateRange.asStateFlow()
    
    private val _selectedDate = MutableStateFlow(Calendar.getInstance())
    val selectedDate: StateFlow<Calendar> = _selectedDate.asStateFlow()

    init {
        loadHistoryData()
    }

    fun setDateRange(range: DateRange) {
        _selectedDateRange.value = range
        loadHistoryData()
    }
    
    fun setSelectedDate(calendar: Calendar) {
        _selectedDate.value = calendar
        loadHistoryData()
    }

    private fun loadHistoryData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            try {
                combine(
                    getHealthEntriesUseCase(),
                    getRecentEntriesUseCase(7),
                    getUserGoalsUseCase()
                ) { allEntries, recentEntries, userGoals ->
                    
                    // Map all entries to UI models
                    val uiEntries = allEntries.map {
                        HealthEntryUiModel(
                            id = it.id,
                            date = it.date,
                            waterIntake = it.waterIntake,
                            sleepHours = it.sleepHours,
                            stepCount = it.stepCount,
                            mood = it.mood,
                            weight = it.weight,
                            calories = it.calories,
                            exerciseMinutes = it.exerciseMinutes
                        )
                    }
                    
                    // Find entry for selected date
                    val selectedDateEntry = findEntryForDate(_selectedDate.value, uiEntries)
                    
                    // Calculate weekly mood data
                    val weeklyMoodData = calculateWeeklyMoodData(recentEntries.map {
                        HealthEntryUiModel(
                            id = it.id,
                            date = it.date,
                            waterIntake = it.waterIntake,
                            sleepHours = it.sleepHours,
                            stepCount = it.stepCount,
                            mood = it.mood,
                            weight = it.weight,
                            calories = it.calories,
                            exerciseMinutes = it.exerciseMinutes
                        )
                    })
                    
                    // Find most common mood
                    val mostCommonMood = findMostCommonMood(recentEntries.map { it.mood })
                    
                    HistoryUiState(
                        entries = uiEntries,
                        selectedDateEntry = selectedDateEntry,
                        dateRange = _selectedDateRange.value,
                        selectedDate = _selectedDate.value,
                        userGoals = userGoals ?: UserGoals.default(),
                        weeklyMoodData = weeklyMoodData,
                        mostCommonMood = mostCommonMood
                    )
                }
                .catch { e ->
                    _uiState.value = UiState.Error(e.message ?: "Failed to load history data")
                }
                .collect { state ->
                    _uiState.value = UiState.Success(state)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to load history data")
            }
        }
    }
    
    private fun findEntryForDate(calendar: Calendar, entries: List<HealthEntryUiModel>): HealthEntryUiModel? {
        val targetStart = calendar.clone() as Calendar
        targetStart.set(Calendar.HOUR_OF_DAY, 0)
        targetStart.set(Calendar.MINUTE, 0)
        targetStart.set(Calendar.SECOND, 0)
        targetStart.set(Calendar.MILLISECOND, 0)
        
        val targetEnd = calendar.clone() as Calendar
        targetEnd.set(Calendar.HOUR_OF_DAY, 23)
        targetEnd.set(Calendar.MINUTE, 59)
        targetEnd.set(Calendar.SECOND, 59)
        targetEnd.set(Calendar.MILLISECOND, 999)
        
        return entries.find { entry ->
            val entryTime = entry.date.time
            entryTime in targetStart.timeInMillis..targetEnd.timeInMillis
        }
    }
    
    private fun calculateWeeklyMoodData(entries: List<HealthEntryUiModel>): List<Pair<String, String>> {
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val calendar = Calendar.getInstance()
        
        return (0..6).map { daysAgo ->
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
            val dayName = dayFormat.format(calendar.time)
            
            val entryForDay = findEntryForDate(calendar.clone() as Calendar, entries)
            dayName to (entryForDay?.mood ?: "")
        }.reversed()
    }
    
    private fun findMostCommonMood(moods: List<String>): String {
        val filteredMoods = moods.filter { it.isNotBlank() }
        if (filteredMoods.isEmpty()) return ""
        
        return filteredMoods
            .groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key ?: ""
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