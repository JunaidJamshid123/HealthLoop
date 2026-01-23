package com.example.healthloop.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthloop.domain.model.HealthEntry
import com.example.healthloop.domain.model.UserGoals
import com.example.healthloop.domain.model.UserProfile
import com.example.healthloop.domain.usecase.GetTodayEntryUseCase
import com.example.healthloop.domain.usecase.GetRecentEntriesUseCase
import com.example.healthloop.domain.usecase.GetTotalDaysLoggedUseCase
import com.example.healthloop.domain.usecase.profile.GetUserGoalsUseCase
import com.example.healthloop.domain.usecase.profile.GetUserProfileUseCase
import com.example.healthloop.presentation.model.HealthEntryUiModel
import com.example.healthloop.presentation.model.TodayHealthDataUiModel
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

data class DashboardUiState(
    val today: TodayHealthDataUiModel = TodayHealthDataUiModel.empty(),
    val recent: List<HealthEntryUiModel> = emptyList(),
    val streakDays: Int = 0,
    val totalDaysLogged: Int = 0,
    val currentDate: String = "",
    val weeklyData: WeeklyData = WeeklyData(),
    val userName: String = "User",
    val profilePictureBase64: String? = null,
    val todayEntryId: Long? = null // Track today's entry ID for editing
)

data class WeeklyData(
    val waterData: List<Pair<String, Int>> = emptyList(),
    val sleepData: List<Pair<String, Float>> = emptyList(),
    val stepsData: List<Pair<String, Int>> = emptyList(),
    val caloriesData: List<Pair<String, Int>> = emptyList(),
    val exerciseData: List<Pair<String, Int>> = emptyList(),
    val moodData: List<Pair<String, String>> = emptyList(),
    val weightData: List<Pair<String, Float>> = emptyList(),
    val avgWater: Float = 0f,
    val avgSleep: Float = 0f,
    val totalSteps: Int = 0,
    val avgCalories: Int = 0,
    val totalExercise: Int = 0
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getTodayEntryUseCase: GetTodayEntryUseCase,
    private val getRecentEntriesUseCase: GetRecentEntriesUseCase,
    private val getTotalDaysLoggedUseCase: GetTotalDaysLoggedUseCase,
    private val getUserGoalsUseCase: GetUserGoalsUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<UiState<DashboardUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<DashboardUiState>> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            try {
                // Combine all the flows to get complete dashboard data
                combine(
                    getTodayEntryUseCase(),
                    getRecentEntriesUseCase(7),
                    getTotalDaysLoggedUseCase(),
                    getUserGoalsUseCase(),
                    getUserProfileUseCase()
                ) { todayEntry, recentEntries, totalDays, userGoals, userProfile ->
                    createDashboardState(todayEntry, recentEntries, totalDays, userGoals, userProfile)
                }
                .catch { e ->
                    _uiState.value = UiState.Error(e.message ?: "Failed to load dashboard data")
                }
                .collect { state ->
                    _uiState.value = UiState.Success(state)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to load dashboard data")
            }
        }
    }
    
    private fun createDashboardState(
        todayEntry: HealthEntry?,
        recentEntries: List<HealthEntry>,
        totalDays: Int,
        userGoals: UserGoals?,
        userProfile: UserProfile?
    ): DashboardUiState {
        val goals = userGoals ?: UserGoals.default()
        val profile = userProfile ?: UserProfile.default()
        
        // Create today's data
        val todayData = if (todayEntry != null) {
            TodayHealthDataUiModel(
                waterIntake = todayEntry.waterIntake,
                targetWater = goals.waterGoal,
                sleepHours = todayEntry.sleepHours,
                targetSleep = goals.sleepGoal,
                stepCount = todayEntry.stepCount,
                targetSteps = goals.stepsGoal,
                mood = todayEntry.mood,
                weight = todayEntry.weight,
                targetWeight = goals.weightGoal,
                calories = todayEntry.calories,
                targetCalories = goals.caloriesGoal,
                exerciseMinutes = todayEntry.exerciseMinutes,
                targetExercise = goals.exerciseGoal,
                hasEntry = true
            )
        } else {
            TodayHealthDataUiModel(
                targetWater = goals.waterGoal,
                targetSleep = goals.sleepGoal,
                targetSteps = goals.stepsGoal,
                targetWeight = goals.weightGoal,
                targetCalories = goals.caloriesGoal,
                targetExercise = goals.exerciseGoal,
                hasEntry = false
            )
        }
        
        // Create recent entries UI models (excluding today)
        val recentUiModels = recentEntries
            .filterNot { isToday(it.date.time) }
            .take(5)
            .map { entry ->
                HealthEntryUiModel(
                    id = entry.id,
                    date = entry.date,
                    waterIntake = entry.waterIntake,
                    sleepHours = entry.sleepHours,
                    stepCount = entry.stepCount,
                    mood = entry.mood,
                    weight = entry.weight,
                    calories = entry.calories,
                    exerciseMinutes = entry.exerciseMinutes
                )
            }
        
        // Calculate streak
        val streak = calculateStreak(recentEntries)
        
        // Create weekly data for charts
        val weeklyData = createWeeklyData(recentEntries)
        
        // Format current date
        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        
        return DashboardUiState(
            today = todayData,
            recent = recentUiModels,
            streakDays = streak,
            totalDaysLogged = totalDays,
            currentDate = currentDate,
            weeklyData = weeklyData,
            userName = profile.name,
            profilePictureBase64 = profile.profilePictureBase64,
            todayEntryId = todayEntry?.id
        )
    }
    
    private fun createWeeklyData(entries: List<HealthEntry>): WeeklyData {
        val calendar = Calendar.getInstance()
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        
        // Create a map for the last 7 days
        val last7Days = (0..6).map { daysAgo ->
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
            val dayName = dayFormat.format(calendar.time)
            val dayStart = calendar.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            val dayEnd = calendar.apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis
            
            Triple(dayName, dayStart, dayEnd)
        }.reversed()
        
        val waterData = mutableListOf<Pair<String, Int>>()
        val sleepData = mutableListOf<Pair<String, Float>>()
        val stepsData = mutableListOf<Pair<String, Int>>()
        val caloriesData = mutableListOf<Pair<String, Int>>()
        val exerciseData = mutableListOf<Pair<String, Int>>()
        val moodData = mutableListOf<Pair<String, String>>()
        val weightData = mutableListOf<Pair<String, Float>>()
        
        var totalWater = 0
        var totalSleep = 0f
        var totalSteps = 0
        var totalCalories = 0
        var totalExercise = 0
        var daysWithData = 0
        
        for ((dayName, dayStart, dayEnd) in last7Days) {
            val entryForDay = entries.find { entry ->
                val entryTime = entry.date.time
                entryTime in dayStart..dayEnd
            }
            
            if (entryForDay != null) {
                waterData.add(dayName to entryForDay.waterIntake)
                sleepData.add(dayName to entryForDay.sleepHours)
                stepsData.add(dayName to entryForDay.stepCount)
                caloriesData.add(dayName to entryForDay.calories)
                exerciseData.add(dayName to entryForDay.exerciseMinutes)
                moodData.add(dayName to entryForDay.mood)
                weightData.add(dayName to entryForDay.weight)
                
                totalWater += entryForDay.waterIntake
                totalSleep += entryForDay.sleepHours
                totalSteps += entryForDay.stepCount
                totalCalories += entryForDay.calories
                totalExercise += entryForDay.exerciseMinutes
                daysWithData++
            } else {
                waterData.add(dayName to 0)
                sleepData.add(dayName to 0f)
                stepsData.add(dayName to 0)
                caloriesData.add(dayName to 0)
                exerciseData.add(dayName to 0)
                moodData.add(dayName to "")
                weightData.add(dayName to 0f)
            }
        }
        
        val avgWater = if (daysWithData > 0) totalWater.toFloat() / daysWithData else 0f
        val avgSleep = if (daysWithData > 0) totalSleep / daysWithData else 0f
        val avgCalories = if (daysWithData > 0) totalCalories / daysWithData else 0
        
        return WeeklyData(
            waterData = waterData,
            sleepData = sleepData,
            stepsData = stepsData,
            caloriesData = caloriesData,
            exerciseData = exerciseData,
            moodData = moodData,
            weightData = weightData,
            avgWater = avgWater,
            avgSleep = avgSleep,
            totalSteps = totalSteps,
            avgCalories = avgCalories,
            totalExercise = totalExercise
        )
    }
    
    private fun calculateStreak(entries: List<HealthEntry>): Int {
        if (entries.isEmpty()) return 0
        
        val calendar = Calendar.getInstance()
        val sortedEntries = entries.sortedByDescending { it.date }
        
        var streak = 0
        var expectedDate = calendar.time
        
        for (entry in sortedEntries) {
            val entryCalendar = Calendar.getInstance().apply { time = entry.date }
            val expectedCalendar = Calendar.getInstance().apply { time = expectedDate }
            
            // Check if entry is on the expected date (same day)
            if (entryCalendar.get(Calendar.YEAR) == expectedCalendar.get(Calendar.YEAR) &&
                entryCalendar.get(Calendar.DAY_OF_YEAR) == expectedCalendar.get(Calendar.DAY_OF_YEAR)
            ) {
                streak++
                // Move expected date to previous day
                expectedCalendar.add(Calendar.DAY_OF_YEAR, -1)
                expectedDate = expectedCalendar.time
            } else if (entryCalendar.before(expectedCalendar)) {
                // Entry is before expected date, check if it's the previous day
                expectedCalendar.add(Calendar.DAY_OF_YEAR, -1)
                if (entryCalendar.get(Calendar.YEAR) == expectedCalendar.get(Calendar.YEAR) &&
                    entryCalendar.get(Calendar.DAY_OF_YEAR) == expectedCalendar.get(Calendar.DAY_OF_YEAR)
                ) {
                    streak++
                    expectedCalendar.add(Calendar.DAY_OF_YEAR, -1)
                    expectedDate = expectedCalendar.time
                } else {
                    // Gap in streak, stop counting
                    break
                }
            }
        }
        
        return streak
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
        // Reset to loading state and reload
        loadDashboardData()
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