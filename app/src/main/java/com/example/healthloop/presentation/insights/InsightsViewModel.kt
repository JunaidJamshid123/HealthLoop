package com.example.healthloop.presentation.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthloop.domain.model.HealthEntry
import com.example.healthloop.domain.model.UserGoals
import com.example.healthloop.domain.usecase.GetHealthEntriesUseCase
import com.example.healthloop.domain.usecase.GetRecentEntriesUseCase
import com.example.healthloop.domain.usecase.GetTotalDaysLoggedUseCase
import com.example.healthloop.domain.usecase.profile.GetUserGoalsUseCase
import com.example.healthloop.presentation.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class InsightsUiState(
    val healthScore: Int = 0,
    val healthScoreLabel: String = "No Data",
    val scoreChange: Int = 0,
    val avgSleep: Float = 0f,
    val avgSteps: Int = 0,
    val avgWater: Int = 0,
    val avgExercise: Int = 0,
    val weeklyTrendData: List<Float> = emptyList(),
    val weeklyLabels: List<String> = emptyList(),
    val moodDistribution: Map<String, Int> = emptyMap(),
    val sleepQuality: SleepQualityData = SleepQualityData(),
    val insights: List<InsightData> = emptyList(),
    val totalDaysLogged: Int = 0,
    val userGoals: UserGoals = UserGoals.default(),
    val selectedPeriod: TimePeriod = TimePeriod.WEEK
)

data class SleepQualityData(
    val avgHours: Float = 0f,
    val quality: String = "No Data",
    val qualityPercent: Int = 0,
    val bestDay: String = "",
    val worstDay: String = "",
    val consistency: Int = 0
)

data class InsightData(
    val icon: Int,
    val text: String,
    val isPositive: Boolean
)

enum class TimePeriod {
    WEEK, MONTH, YEAR
}

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val getHealthEntriesUseCase: GetHealthEntriesUseCase,
    private val getRecentEntriesUseCase: GetRecentEntriesUseCase,
    private val getUserGoalsUseCase: GetUserGoalsUseCase,
    private val getTotalDaysLoggedUseCase: GetTotalDaysLoggedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<InsightsUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<InsightsUiState>> = _uiState.asStateFlow()

    private val _selectedPeriod = MutableStateFlow(TimePeriod.WEEK)
    val selectedPeriod: StateFlow<TimePeriod> = _selectedPeriod.asStateFlow()

    init {
        loadInsightsData()
    }

    fun setTimePeriod(period: TimePeriod) {
        _selectedPeriod.value = period
        loadInsightsData()
    }

    private fun loadInsightsData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            try {
                val daysToFetch = when (_selectedPeriod.value) {
                    TimePeriod.WEEK -> 7
                    TimePeriod.MONTH -> 30
                    TimePeriod.YEAR -> 365
                }

                combine(
                    getRecentEntriesUseCase(daysToFetch),
                    getUserGoalsUseCase(),
                    getTotalDaysLoggedUseCase()
                ) { entries, userGoals, totalDays ->
                    createInsightsState(entries, userGoals, totalDays)
                }
                .catch { e ->
                    _uiState.value = UiState.Error(e.message ?: "Failed to load insights")
                }
                .collect { state ->
                    _uiState.value = UiState.Success(state)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to load insights")
            }
        }
    }

    private fun createInsightsState(
        entries: List<HealthEntry>,
        userGoals: UserGoals?,
        totalDays: Int
    ): InsightsUiState {
        val goals = userGoals ?: UserGoals.default()

        if (entries.isEmpty()) {
            return InsightsUiState(
                totalDaysLogged = totalDays,
                userGoals = goals,
                selectedPeriod = _selectedPeriod.value
            )
        }

        // Calculate averages
        val avgSleep = entries.map { it.sleepHours }.filter { it > 0 }.average().toFloat()
        val avgSteps = entries.map { it.stepCount }.filter { it > 0 }.average().toInt()
        val avgWater = entries.map { it.waterIntake }.filter { it > 0 }.average().toInt()
        val avgExercise = entries.map { it.exerciseMinutes }.filter { it > 0 }.average().toInt()

        // Calculate health score (0-100)
        val healthScore = calculateHealthScore(entries, goals)
        val healthScoreLabel = getHealthScoreLabel(healthScore)

        // Calculate score change (compare first half vs second half)
        val scoreChange = calculateScoreChange(entries, goals)

        // Weekly trend data (use health scores for each day)
        val weeklyTrendData = calculateWeeklyTrend(entries, goals)
        val weeklyLabels = getWeeklyLabels(entries)

        // Mood distribution
        val moodDistribution = entries
            .filter { it.mood.isNotBlank() }
            .groupingBy { it.mood }
            .eachCount()

        // Sleep quality analysis
        val sleepQuality = calculateSleepQuality(entries, goals)

        // Generate insights
        val insights = generateInsights(entries, goals)

        return InsightsUiState(
            healthScore = healthScore,
            healthScoreLabel = healthScoreLabel,
            scoreChange = scoreChange,
            avgSleep = avgSleep,
            avgSteps = avgSteps,
            avgWater = avgWater,
            avgExercise = avgExercise,
            weeklyTrendData = weeklyTrendData,
            weeklyLabels = weeklyLabels,
            moodDistribution = moodDistribution,
            sleepQuality = sleepQuality,
            insights = insights,
            totalDaysLogged = totalDays,
            userGoals = goals,
            selectedPeriod = _selectedPeriod.value
        )
    }

    private fun calculateHealthScore(entries: List<HealthEntry>, goals: UserGoals): Int {
        if (entries.isEmpty()) return 0

        var totalScore = 0f
        var components = 0

        // Water score (0-25 points)
        val avgWater = entries.map { it.waterIntake }.average()
        if (avgWater > 0) {
            val waterScore = minOf((avgWater / goals.waterGoal) * 25, 25.0)
            totalScore += waterScore.toFloat()
            components++
        }

        // Sleep score (0-25 points)
        val avgSleep = entries.map { it.sleepHours }.average()
        if (avgSleep > 0) {
            val sleepScore = minOf((avgSleep / goals.sleepGoal) * 25, 25.0)
            totalScore += sleepScore.toFloat()
            components++
        }

        // Steps score (0-25 points)
        val avgSteps = entries.map { it.stepCount }.average()
        if (avgSteps > 0) {
            val stepsScore = minOf((avgSteps / goals.stepsGoal) * 25, 25.0)
            totalScore += stepsScore.toFloat()
            components++
        }

        // Exercise score (0-25 points)
        val avgExercise = entries.map { it.exerciseMinutes }.average()
        if (avgExercise > 0) {
            val exerciseScore = minOf((avgExercise / goals.exerciseGoal) * 25, 25.0)
            totalScore += exerciseScore.toFloat()
            components++
        }

        // Normalize to 100 if not all components present
        return if (components > 0) {
            ((totalScore / components) * 4).toInt().coerceIn(0, 100)
        } else 0
    }

    private fun getHealthScoreLabel(score: Int): String {
        return when {
            score >= 90 -> "Excellent"
            score >= 75 -> "Good"
            score >= 60 -> "Fair"
            score >= 40 -> "Needs Work"
            score > 0 -> "Poor"
            else -> "No Data"
        }
    }

    private fun calculateScoreChange(entries: List<HealthEntry>, goals: UserGoals): Int {
        if (entries.size < 4) return 0

        val midpoint = entries.size / 2
        val firstHalf = entries.take(midpoint)
        val secondHalf = entries.takeLast(midpoint)

        val firstScore = calculateHealthScore(firstHalf, goals)
        val secondScore = calculateHealthScore(secondHalf, goals)

        return secondScore - firstScore
    }

    private fun calculateWeeklyTrend(entries: List<HealthEntry>, goals: UserGoals): List<Float> {
        if (entries.isEmpty()) return listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f)

        val calendar = Calendar.getInstance()
        val dayFormat = java.text.SimpleDateFormat("EEE", Locale.getDefault())

        // Get last 7 days
        return (6 downTo 0).map { daysAgo ->
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
            
            val dayStart = calendar.clone() as Calendar
            dayStart.set(Calendar.HOUR_OF_DAY, 0)
            dayStart.set(Calendar.MINUTE, 0)
            dayStart.set(Calendar.SECOND, 0)
            
            val dayEnd = calendar.clone() as Calendar
            dayEnd.set(Calendar.HOUR_OF_DAY, 23)
            dayEnd.set(Calendar.MINUTE, 59)
            dayEnd.set(Calendar.SECOND, 59)

            val dayEntry = entries.find { entry ->
                entry.date.time in dayStart.timeInMillis..dayEnd.timeInMillis
            }

            if (dayEntry != null) {
                calculateHealthScore(listOf(dayEntry), goals).toFloat()
            } else {
                0f
            }
        }
    }

    private fun getWeeklyLabels(entries: List<HealthEntry>): List<String> {
        val calendar = Calendar.getInstance()
        val dayFormat = java.text.SimpleDateFormat("EEE", Locale.getDefault())

        return (6 downTo 0).map { daysAgo ->
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
            dayFormat.format(calendar.time)
        }
    }

    private fun calculateSleepQuality(entries: List<HealthEntry>, goals: UserGoals): SleepQualityData {
        val sleepEntries = entries.filter { it.sleepHours > 0 }
        if (sleepEntries.isEmpty()) return SleepQualityData()

        val avgHours = sleepEntries.map { it.sleepHours }.average().toFloat()
        val qualityPercent = ((avgHours / goals.sleepGoal) * 100).toInt().coerceIn(0, 100)
        
        val quality = when {
            qualityPercent >= 95 -> "Excellent"
            qualityPercent >= 85 -> "Good"
            qualityPercent >= 70 -> "Fair"
            else -> "Needs Improvement"
        }

        val dayFormat = java.text.SimpleDateFormat("EEEE", Locale.getDefault())
        val bestEntry = sleepEntries.maxByOrNull { it.sleepHours }
        val worstEntry = sleepEntries.minByOrNull { it.sleepHours }

        // Calculate consistency (standard deviation based)
        val mean = avgHours
        val variance = sleepEntries.map { (it.sleepHours - mean) * (it.sleepHours - mean) }.average()
        val stdDev = kotlin.math.sqrt(variance)
        val consistency = (100 - (stdDev * 20)).toInt().coerceIn(0, 100)

        return SleepQualityData(
            avgHours = avgHours,
            quality = quality,
            qualityPercent = qualityPercent,
            bestDay = bestEntry?.let { dayFormat.format(it.date) } ?: "",
            worstDay = worstEntry?.let { dayFormat.format(it.date) } ?: "",
            consistency = consistency
        )
    }

    private fun generateInsights(entries: List<HealthEntry>, goals: UserGoals): List<InsightData> {
        val insights = mutableListOf<InsightData>()

        if (entries.isEmpty()) return insights

        // Sleep insight
        val avgSleep = entries.map { it.sleepHours }.filter { it > 0 }.average()
        if (avgSleep > 0) {
            val sleepGoalPercent = ((avgSleep / goals.sleepGoal) * 100).toInt()
            insights.add(
                InsightData(
                    icon = com.example.healthloop.R.drawable.sleeping,
                    text = if (sleepGoalPercent >= 90) 
                        "Great! You're averaging ${String.format("%.1f", avgSleep)}h sleep, meeting your goal"
                    else 
                        "Your average sleep is ${String.format("%.1f", avgSleep)}h, ${100 - sleepGoalPercent}% below goal",
                    isPositive = sleepGoalPercent >= 90
                )
            )
        }

        // Water insight
        val avgWater = entries.map { it.waterIntake }.filter { it > 0 }.average()
        if (avgWater > 0) {
            val waterGoalPercent = ((avgWater / goals.waterGoal) * 100).toInt()
            insights.add(
                InsightData(
                    icon = com.example.healthloop.R.drawable.water,
                    text = if (waterGoalPercent >= 80)
                        "Well hydrated! Averaging ${avgWater.toInt()} glasses daily"
                    else
                        "Water intake at ${avgWater.toInt()} glasses, try to reach ${goals.waterGoal}",
                    isPositive = waterGoalPercent >= 80
                )
            )
        }

        // Steps insight
        val avgSteps = entries.map { it.stepCount }.filter { it > 0 }.average()
        if (avgSteps > 0) {
            val stepsGoalPercent = ((avgSteps / goals.stepsGoal) * 100).toInt()
            insights.add(
                InsightData(
                    icon = com.example.healthloop.R.drawable.walk,
                    text = if (stepsGoalPercent >= 80)
                        "Active! Averaging ${String.format("%,.0f", avgSteps)} steps daily"
                    else
                        "Steps at ${String.format("%,.0f", avgSteps)}, ${100 - stepsGoalPercent}% below your ${String.format("%,d", goals.stepsGoal)} goal",
                    isPositive = stepsGoalPercent >= 80
                )
            )
        }

        // Mood insight
        val moodCounts = entries.filter { it.mood.isNotBlank() }.groupingBy { it.mood }.eachCount()
        if (moodCounts.isNotEmpty()) {
            val mostCommonMood = moodCounts.maxByOrNull { it.value }?.key ?: ""
            val moodPercent = ((moodCounts[mostCommonMood] ?: 0) * 100) / entries.size
            insights.add(
                InsightData(
                    icon = com.example.healthloop.R.drawable.happy,
                    text = "Your most common mood is $mostCommonMood ($moodPercent% of days)",
                    isPositive = mostCommonMood.lowercase() in listOf("happy", "excited", "calm", "grateful")
                )
            )
        }

        return insights.take(4)
    }
}
