package com.example.healthloop.presentation.profile

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthloop.data.local.util.ImageUtils
import com.example.healthloop.domain.model.UserGoals
import com.example.healthloop.domain.model.UserProfile
import com.example.healthloop.domain.model.UserStats
import com.example.healthloop.domain.model.HealthEntry
import com.example.healthloop.domain.usecase.GetHealthEntriesUseCase
import com.example.healthloop.domain.usecase.profile.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = true,
    val userProfile: UserProfile = UserProfile.default(),
    val userGoals: UserGoals = UserGoals.default(),
    val userStats: UserStats = UserStats.default(),
    val profilePictureBitmap: Bitmap? = null,
    val error: String? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)

sealed class ProfileEvent {
    data class UpdateProfile(
        val name: String,
        val email: String,
        val age: Int,
        val weight: Float,
        val height: Int
    ) : ProfileEvent()
    
    data class UpdateProfilePicture(val uri: Uri, val context: Context) : ProfileEvent()
    object RemoveProfilePicture : ProfileEvent()
    
    data class UpdateGoals(
        val waterGoal: Int,
        val sleepGoal: Float,
        val stepsGoal: Int,
        val caloriesGoal: Int,
        val exerciseGoal: Int,
        val weightGoal: Float
    ) : ProfileEvent()
    
    data class UpdateStats(
        val totalDays: Int,
        val currentStreak: Int,
        val bestStreak: Int,
        val healthScore: Int
    ) : ProfileEvent()
    
    object ClearError : ProfileEvent()
    object ClearSaveSuccess : ProfileEvent()
    data class ExportData(val context: Context, val format: String) : ProfileEvent()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val saveUserProfileUseCase: SaveUserProfileUseCase,
    private val updateProfileInfoUseCase: UpdateProfileInfoUseCase,
    private val updateProfilePictureUseCase: UpdateProfilePictureUseCase,
    private val getUserGoalsUseCase: GetUserGoalsUseCase,
    private val saveUserGoalsUseCase: SaveUserGoalsUseCase,
    private val updateUserGoalsUseCase: UpdateUserGoalsUseCase,
    private val getUserStatsUseCase: GetUserStatsUseCase,
    private val updateUserStatsUseCase: UpdateUserStatsUseCase,
    private val initializeUserDataUseCase: InitializeUserDataUseCase,
    private val getHealthEntriesUseCase: GetHealthEntriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        initializeData()
        observeUserData()
    }

    private fun initializeData() {
        viewModelScope.launch {
            try {
                initializeUserDataUseCase()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to initialize data: ${e.message}") }
            }
        }
    }

    private fun observeUserData() {
        // Observe user profile
        viewModelScope.launch {
            getUserProfileUseCase().collect { profile ->
                profile?.let { userProfile ->
                    val bitmap = userProfile.profilePictureBase64?.let { 
                        ImageUtils.base64ToBitmap(it) 
                    }
                    _uiState.update { state ->
                        state.copy(
                            userProfile = userProfile,
                            profilePictureBitmap = bitmap,
                            isLoading = false
                        )
                    }
                }
            }
        }

        // Observe user goals
        viewModelScope.launch {
            getUserGoalsUseCase().collect { goals ->
                goals?.let { userGoals ->
                    _uiState.update { it.copy(userGoals = userGoals) }
                }
            }
        }

        // Observe user stats
        viewModelScope.launch {
            getUserStatsUseCase().collect { stats ->
                stats?.let { userStats ->
                    _uiState.update { it.copy(userStats = userStats) }
                }
            }
        }
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.UpdateProfile -> updateProfile(event)
            is ProfileEvent.UpdateProfilePicture -> updateProfilePicture(event.uri, event.context)
            is ProfileEvent.RemoveProfilePicture -> removeProfilePicture()
            is ProfileEvent.UpdateGoals -> updateGoals(event)
            is ProfileEvent.UpdateStats -> updateStats(event)
            is ProfileEvent.ClearError -> _uiState.update { it.copy(error = null) }
            is ProfileEvent.ClearSaveSuccess -> _uiState.update { it.copy(saveSuccess = false) }
            is ProfileEvent.ExportData -> exportData(event.context, event.format)
        }
    }

    private fun updateProfile(event: ProfileEvent.UpdateProfile) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                // Check if profile exists
                val existingProfile = getUserProfileUseCase.getOnce()
                
                if (existingProfile != null) {
                    // Update existing profile
                    updateProfileInfoUseCase(
                        name = event.name,
                        email = event.email,
                        age = event.age,
                        weight = event.weight,
                        height = event.height
                    )
                } else {
                    // Create new profile
                    val newProfile = UserProfile(
                        name = event.name,
                        email = event.email,
                        age = event.age,
                        weight = event.weight,
                        height = event.height
                    )
                    saveUserProfileUseCase(newProfile)
                }
                
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isSaving = false, 
                        error = "Failed to update profile: ${e.message}"
                    ) 
                }
            }
        }
    }

    private fun updateProfilePicture(uri: Uri, context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                // Convert URI to Base64
                val base64Image = ImageUtils.uriToBase64(context, uri)
                
                if (base64Image != null) {
                    // Check if profile exists
                    val existingProfile = getUserProfileUseCase.getOnce()
                    
                    if (existingProfile != null) {
                        // Update profile picture
                        updateProfilePictureUseCase(base64Image)
                    } else {
                        // Create profile with picture
                        val newProfile = UserProfile.default().copy(
                            profilePictureBase64 = base64Image
                        )
                        saveUserProfileUseCase(newProfile)
                    }
                    
                    // Update bitmap in UI state
                    val bitmap = ImageUtils.base64ToBitmap(base64Image)
                    _uiState.update { 
                        it.copy(
                            profilePictureBitmap = bitmap,
                            isSaving = false, 
                            saveSuccess = true
                        ) 
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isSaving = false, 
                            error = "Failed to process image"
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isSaving = false, 
                        error = "Failed to update profile picture: ${e.message}"
                    ) 
                }
            }
        }
    }

    private fun removeProfilePicture() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                updateProfilePictureUseCase(null)
                _uiState.update { 
                    it.copy(
                        profilePictureBitmap = null,
                        isSaving = false, 
                        saveSuccess = true
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isSaving = false, 
                        error = "Failed to remove profile picture: ${e.message}"
                    ) 
                }
            }
        }
    }

    private fun updateGoals(event: ProfileEvent.UpdateGoals) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                // Check if goals exist
                val existingGoals = getUserGoalsUseCase.getOnce()
                
                if (existingGoals != null) {
                    // Update existing goals
                    updateUserGoalsUseCase(
                        waterGoal = event.waterGoal,
                        sleepGoal = event.sleepGoal,
                        stepsGoal = event.stepsGoal,
                        caloriesGoal = event.caloriesGoal,
                        exerciseGoal = event.exerciseGoal,
                        weightGoal = event.weightGoal
                    )
                } else {
                    // Create new goals
                    val newGoals = UserGoals(
                        waterGoal = event.waterGoal,
                        sleepGoal = event.sleepGoal,
                        stepsGoal = event.stepsGoal,
                        caloriesGoal = event.caloriesGoal,
                        exerciseGoal = event.exerciseGoal,
                        weightGoal = event.weightGoal
                    )
                    saveUserGoalsUseCase(newGoals)
                }
                
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isSaving = false, 
                        error = "Failed to update goals: ${e.message}"
                    ) 
                }
            }
        }
    }

    private fun updateStats(event: ProfileEvent.UpdateStats) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                updateUserStatsUseCase(
                    totalDays = event.totalDays,
                    currentStreak = event.currentStreak,
                    bestStreak = event.bestStreak,
                    healthScore = event.healthScore
                )
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isSaving = false, 
                        error = "Failed to update stats: ${e.message}"
                    ) 
                }
            }
        }
    }

    private fun exportData(context: Context, format: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                val entries = getHealthEntriesUseCase().first()
                if (entries.isEmpty()) {
                    _uiState.update { it.copy(isSaving = false, error = "No health data to export") }
                    return@launch
                }

                val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                val timestamp = dateFormat.format(java.util.Date())
                val sorted = entries.sortedBy { it.date }

                val profile = _uiState.value.userProfile
                val goals = _uiState.value.userGoals
                val stats = _uiState.value.userStats

                when (format) {
                    "csv" -> exportCsv(context, sorted, dateFormat, timestamp)
                    "pdf" -> exportPdf(context, sorted, dateFormat, timestamp, profile, goals, stats)
                }

                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSaving = false, error = "Failed to export data: ${e.message}")
                }
            }
        }
    }

    private fun exportCsv(
        context: Context,
        entries: List<HealthEntry>,
        dateFormat: java.text.SimpleDateFormat,
        timestamp: String
    ) {
        val fileName = "HealthLoop_Export_$timestamp.csv"
        val sb = StringBuilder()
        sb.appendLine("Date,Water Intake (glasses),Sleep (hours),Steps,Mood,Weight (kg),Calories,Exercise (min)")
        for (entry in entries) {
            sb.appendLine(
                "${dateFormat.format(entry.date)},${entry.waterIntake},${entry.sleepHours},${entry.stepCount},${entry.mood},${entry.weight},${entry.calories},${entry.exerciseMinutes}"
            )
        }
        val csvContent = sb.toString()
        saveToDownloads(context, fileName, "text/csv", csvContent.toByteArray())
        android.widget.Toast.makeText(context, "Exported to Downloads/$fileName", android.widget.Toast.LENGTH_LONG).show()
    }

    private fun exportPdf(
        context: Context,
        entries: List<HealthEntry>,
        dateFormat: java.text.SimpleDateFormat,
        timestamp: String,
        profile: UserProfile,
        goals: UserGoals,
        stats: UserStats
    ) {
        val fileName = "HealthLoop_Report_$timestamp.pdf"
        val pw = 595
        val ph = 842
        val m = 40f
        val contentW = pw - m * 2

        val document = android.graphics.pdf.PdfDocument()
        val pages = mutableListOf<android.graphics.pdf.PdfDocument.Page>()

        // Paints
        val orange = android.graphics.Color.parseColor("#FFC067")
        val deepBlack = android.graphics.Color.parseColor("#0D0D14")
        val gray = android.graphics.Color.parseColor("#6B6B7B")
        val lightBg = android.graphics.Color.parseColor("#FFF8EE")
        val green = android.graphics.Color.parseColor("#4CAF50")
        val red = android.graphics.Color.parseColor("#F44336")
        val blue = android.graphics.Color.parseColor("#2196F3")
        val white = android.graphics.Color.WHITE

        fun makePaint(size: Float, color: Int, bold: Boolean = false) = android.graphics.Paint().apply {
            textSize = size; this.color = color; isFakeBoldText = bold; isAntiAlias = true
        }
        val titleP = makePaint(22f, deepBlack, true)
        val h1P = makePaint(16f, deepBlack, true)
        val h2P = makePaint(13f, deepBlack, true)
        val bodyP = makePaint(10f, android.graphics.Color.parseColor("#333333"))
        val smallP = makePaint(9f, gray)
        val boldBodyP = makePaint(10f, deepBlack, true)
        val whiteP = makePaint(10f, white, true)
        val bgPaint = android.graphics.Paint().apply { color = orange; isAntiAlias = true }
        val lightBgPaint = android.graphics.Paint().apply { this.color = lightBg; isAntiAlias = true }
        val linePaint = android.graphics.Paint().apply { this.color = android.graphics.Color.parseColor("#E0E0E0"); strokeWidth = 0.5f }
        val greenPaint = android.graphics.Paint().apply { this.color = green; isAntiAlias = true; style = android.graphics.Paint.Style.FILL }
        val bluePaint = android.graphics.Paint().apply { this.color = blue; isAntiAlias = true; style = android.graphics.Paint.Style.FILL }
        val orangeFillPaint = android.graphics.Paint().apply { this.color = orange; isAntiAlias = true; style = android.graphics.Paint.Style.FILL }
        val grayBarPaint = android.graphics.Paint().apply { this.color = android.graphics.Color.parseColor("#E8E8E8"); isAntiAlias = true }

        var currentPage: android.graphics.pdf.PdfDocument.Page? = null
        var canvas: android.graphics.Canvas? = null
        var y = 0f
        var pageNum = 0

        fun newPage() {
            currentPage?.let { document.finishPage(it) }
            pageNum++
            val info = android.graphics.pdf.PdfDocument.PageInfo.Builder(pw, ph, pageNum).create()
            currentPage = document.startPage(info)
            canvas = currentPage!!.canvas
            y = m
        }

        fun ensureSpace(needed: Float) {
            if (y + needed > ph - m) newPage()
        }

        fun drawLine() {
            canvas?.drawLine(m, y, m + contentW, y, linePaint)
            y += 8f
        }

        fun drawSpacer(h: Float = 16f) { y += h }

        // ======== CALCULATIONS ========
        val avgWater = entries.map { it.waterIntake }.average()
        val avgSleep = entries.map { it.sleepHours.toDouble() }.average()
        val avgSteps = entries.map { it.stepCount }.average()
        val avgCalories = entries.map { it.calories }.average()
        val avgExercise = entries.map { it.exerciseMinutes }.average()
        val avgWeight = entries.map { it.weight.toDouble() }.average()

        val maxSteps = entries.maxOf { it.stepCount }
        val minSteps = entries.minOf { it.stepCount }
        val maxSleep = entries.maxOf { it.sleepHours }
        val minSleep = entries.minOf { it.sleepHours }
        val maxWeight = entries.maxOf { it.weight }
        val minWeight = entries.minOf { it.weight }

        val waterPct = if (goals.waterGoal > 0) (avgWater / goals.waterGoal * 100) else 0.0
        val sleepPct = if (goals.sleepGoal > 0) (avgSleep / goals.sleepGoal * 100) else 0.0
        val stepsPct = if (goals.stepsGoal > 0) (avgSteps / goals.stepsGoal * 100) else 0.0
        val calPct = if (goals.caloriesGoal > 0) (avgCalories / goals.caloriesGoal * 100) else 0.0
        val exPct = if (goals.exerciseGoal > 0) (avgExercise / goals.exerciseGoal * 100) else 0.0

        val moodCounts = entries.groupingBy { it.mood }.eachCount().toList().sortedByDescending { it.second }
        val topMood = moodCounts.firstOrNull()?.first ?: "N/A"

        // Trend: compare last half vs first half
        val half = entries.size / 2
        val firstHalf = entries.take(maxOf(half, 1))
        val secondHalf = entries.takeLast(maxOf(half, 1))
        fun trend(first: Double, second: Double): String = when {
            second > first * 1.05 -> "↑ Improving"
            second < first * 0.95 -> "↓ Declining"
            else -> "→ Stable"
        }
        val stepsTrend = trend(firstHalf.map { it.stepCount.toDouble() }.average(), secondHalf.map { it.stepCount.toDouble() }.average())
        val sleepTrend = trend(firstHalf.map { it.sleepHours.toDouble() }.average(), secondHalf.map { it.sleepHours.toDouble() }.average())
        val waterTrend = trend(firstHalf.map { it.waterIntake.toDouble() }.average(), secondHalf.map { it.waterIntake.toDouble() }.average())
        val weightFirst = firstHalf.map { it.weight.toDouble() }.average()
        val weightSecond = secondHalf.map { it.weight.toDouble() }.average()
        val weightTrend = if (weightSecond < weightFirst * 0.98) "↓ Losing" else if (weightSecond > weightFirst * 1.02) "↑ Gaining" else "→ Stable"

        // ======== PAGE 1: COVER + PROFILE + HEALTH SCORE ========
        newPage()

        // Orange header bar
        canvas?.drawRect(0f, 0f, pw.toFloat(), 80f, bgPaint)
        canvas?.drawText("HealthLoop", m, 35f, makePaint(24f, white, true))
        canvas?.drawText("Complete Health Report", m, 58f, makePaint(14f, white))
        canvas?.drawText(timestamp, pw - m - 80f, 35f, makePaint(11f, white))
        y = 100f

        // Profile section
        canvas?.drawText("Personal Profile", m, y + 16f, h1P)
        y += 30f
        drawLine()

        val profData = listOf(
            "Name" to profile.name,
            "Email" to profile.email,
            "Age" to "${profile.age} years",
            "Height" to "${profile.height} cm",
            "Weight" to "${"%.1f".format(profile.weight)} kg",
            "BMI" to "${"%.1f".format(profile.bmi)} (${profile.bmiCategory})"
        )
        for ((label, value) in profData) {
            canvas?.drawText(label, m, y + 12f, boldBodyP)
            canvas?.drawText(value, m + 120f, y + 12f, bodyP)
            y += 18f
        }
        drawSpacer(12f)

        // Health Score Card
        canvas?.drawRoundRect(m, y, m + contentW, y + 70f, 12f, 12f, lightBgPaint)
        canvas?.drawText("Overall Health Score", m + 16f, y + 22f, h2P)
        val scoreText = "${stats.healthScore}/100"
        val scoreColor = when {
            stats.healthScore >= 80 -> green
            stats.healthScore >= 50 -> orange
            else -> red
        }
        canvas?.drawText(scoreText, m + 16f, y + 48f, makePaint(28f, scoreColor, true))
        // Score bar
        val barX = m + 160f
        val barW = contentW - 176f
        val barH = 14f
        canvas?.drawRoundRect(barX, y + 36f, barX + barW, y + 36f + barH, 7f, 7f, grayBarPaint)
        val filledW = barW * (stats.healthScore / 100f)
        val scoreFillPaint = android.graphics.Paint().apply { color = scoreColor; isAntiAlias = true }
        canvas?.drawRoundRect(barX, y + 36f, barX + filledW, y + 36f + barH, 7f, 7f, scoreFillPaint)
        canvas?.drawText("Streak: ${stats.currentStreak} days  •  Best: ${stats.bestStreak} days  •  Total logged: ${stats.totalDays} days", barX, y + 62f, smallP)
        y += 82f

        drawSpacer()
        drawLine()
        drawSpacer(6f)

        // ======== SUMMARY STATISTICS ========
        canvas?.drawText("Summary Statistics", m, y + 16f, h1P)
        y += 30f

        val summaryItems = listOf(
            Triple("Water Intake", "${"%.1f".format(avgWater)} glasses/day", "Range: ${entries.minOf { it.waterIntake }}–${entries.maxOf { it.waterIntake }}"),
            Triple("Sleep", "${"%.1f".format(avgSleep)} hours/day", "Range: ${"%.1f".format(minSleep)}–${"%.1f".format(maxSleep)} hr"),
            Triple("Steps", "${"%.0f".format(avgSteps)} steps/day", "Range: $minSteps–$maxSteps"),
            Triple("Calories", "${"%.0f".format(avgCalories)} cal/day", "Range: ${entries.minOf { it.calories }}–${entries.maxOf { it.calories }}"),
            Triple("Exercise", "${"%.0f".format(avgExercise)} min/day", "Range: ${entries.minOf { it.exerciseMinutes }}–${entries.maxOf { it.exerciseMinutes }} min"),
            Triple("Weight", "${"%.1f".format(avgWeight)} kg avg", "Range: ${"%.1f".format(minWeight)}–${"%.1f".format(maxWeight)} kg")
        )

        for ((i, item) in summaryItems.withIndex()) {
            ensureSpace(22f)
            if (i % 2 == 0) canvas?.drawRect(m, y, m + contentW, y + 20f, lightBgPaint)
            canvas?.drawText(item.first, m + 6f, y + 14f, boldBodyP)
            canvas?.drawText(item.second, m + 140f, y + 14f, bodyP)
            canvas?.drawText(item.third, m + 320f, y + 14f, smallP)
            y += 20f
        }

        drawSpacer()
        drawLine()
        drawSpacer(6f)

        // ======== GOAL ACHIEVEMENT ========
        ensureSpace(180f)
        canvas?.drawText("Goal Achievement", m, y + 16f, h1P)
        y += 30f

        data class GoalItem(val label: String, val avg: Double, val goal: Double, val unit: String, val pct: Double)
        val goalItems = listOf(
            GoalItem("Water", avgWater, goals.waterGoal.toDouble(), "glasses", waterPct),
            GoalItem("Sleep", avgSleep, goals.sleepGoal.toDouble(), "hours", sleepPct),
            GoalItem("Steps", avgSteps, goals.stepsGoal.toDouble(), "steps", stepsPct),
            GoalItem("Calories", avgCalories, goals.caloriesGoal.toDouble(), "cal", calPct),
            GoalItem("Exercise", avgExercise, goals.exerciseGoal.toDouble(), "min", exPct)
        )

        for (gi in goalItems) {
            ensureSpace(36f)
            canvas?.drawText(gi.label, m, y + 12f, boldBodyP)
            canvas?.drawText("${"%.1f".format(gi.avg)} / ${"%.0f".format(gi.goal)} ${gi.unit}", m + 90f, y + 12f, bodyP)

            // Progress bar
            val pBarX = m + 260f
            val pBarW = contentW - 330f
            canvas?.drawRoundRect(pBarX, y + 2f, pBarX + pBarW, y + 16f, 8f, 8f, grayBarPaint)
            val pFill = (pBarW * minOf(gi.pct / 100.0, 1.0)).toFloat()
            val pColor = if (gi.pct >= 80) green else if (gi.pct >= 50) orange else red
            val pPaint = android.graphics.Paint().apply { color = pColor; isAntiAlias = true }
            canvas?.drawRoundRect(pBarX, y + 2f, pBarX + pFill, y + 16f, 8f, 8f, pPaint)

            // Percentage
            canvas?.drawText("${"%.0f".format(gi.pct)}%", m + contentW - 40f, y + 12f, makePaint(10f, pColor, true))
            y += 26f
        }

        drawSpacer()
        drawLine()
        drawSpacer(6f)

        // ======== TRENDS ========
        ensureSpace(100f)
        canvas?.drawText("Trend Analysis", m, y + 16f, h1P)
        canvas?.drawText("(comparing first half vs second half of data)", m + 130f, y + 16f, smallP)
        y += 30f

        val trends = listOf(
            "Steps" to stepsTrend,
            "Sleep" to sleepTrend,
            "Water Intake" to waterTrend,
            "Weight" to weightTrend
        )
        for ((label, trend) in trends) {
            ensureSpace(20f)
            val tColor = when {
                trend.contains("Improving") || trend.contains("Losing") -> green
                trend.contains("Declining") || trend.contains("Gaining") -> red
                else -> gray
            }
            canvas?.drawText(label, m + 6f, y + 12f, boldBodyP)
            canvas?.drawText(trend, m + 140f, y + 12f, makePaint(10f, tColor, true))
            y += 20f
        }

        drawSpacer()
        drawLine()
        drawSpacer(6f)

        // ======== MOOD DISTRIBUTION ========
        ensureSpace(110f)
        canvas?.drawText("Mood Distribution", m, y + 16f, h1P)
        y += 30f

        val moodColors = listOf(
            android.graphics.Color.parseColor("#FFC067"),
            android.graphics.Color.parseColor("#D3FC74"),
            android.graphics.Color.parseColor("#90CAF9"),
            android.graphics.Color.parseColor("#CE93D8"),
            android.graphics.Color.parseColor("#EF9A9A"),
            android.graphics.Color.parseColor("#FFCC80")
        )
        val total = entries.size.toFloat()
        for ((i, mc) in moodCounts.take(6).withIndex()) {
            ensureSpace(22f)
            val pct = mc.second / total * 100f
            canvas?.drawText(mc.first, m + 6f, y + 14f, boldBodyP)
            canvas?.drawText("${mc.second}x (${"%.0f".format(pct)}%)", m + 100f, y + 14f, bodyP)
            // Mini bar
            val bX = m + 200f
            val bW = contentW - 210f
            canvas?.drawRoundRect(bX, y + 2f, bX + bW, y + 16f, 8f, 8f, grayBarPaint)
            val fW = bW * (pct / 100f)
            val mPaint = android.graphics.Paint().apply { color = moodColors[i % moodColors.size]; isAntiAlias = true }
            canvas?.drawRoundRect(bX, y + 2f, bX + fW, y + 16f, 8f, 8f, mPaint)
            y += 22f
        }
        canvas?.drawText("Most frequent mood: $topMood", m + 6f, y + 14f, smallP)
        y += 20f

        drawSpacer()
        drawLine()
        drawSpacer(6f)

        // ======== CHARTS — STEPS BAR CHART ========
        ensureSpace(180f)
        canvas?.drawText("Steps Over Time", m, y + 16f, h1P)
        y += 30f

        val chartH = 120f
        val chartEntries = entries.takeLast(14) // last 14 entries for readability
        if (chartEntries.isNotEmpty()) {
            val maxVal = chartEntries.maxOf { it.stepCount }.toFloat().coerceAtLeast(1f)
            val barW2 = (contentW - 40f) / chartEntries.size
            // Y axis labels
            canvas?.drawText("${"%.0f".format(maxVal)}", m, y + 10f, smallP)
            canvas?.drawText("0", m, y + chartH, smallP)
            val chartX = m + 35f
            // Goal line
            if (goals.stepsGoal > 0 && goals.stepsGoal <= maxVal) {
                val goalY = y + chartH - (goals.stepsGoal / maxVal * chartH)
                val dashPaint = android.graphics.Paint().apply {
                    color = red; strokeWidth = 1f; isAntiAlias = true
                    pathEffect = android.graphics.DashPathEffect(floatArrayOf(4f, 4f), 0f)
                }
                canvas?.drawLine(chartX, goalY, chartX + barW2 * chartEntries.size, goalY, dashPaint)
                canvas?.drawText("Goal", chartX + barW2 * chartEntries.size + 2f, goalY + 4f, makePaint(7f, red))
            }
            for ((i, e) in chartEntries.withIndex()) {
                val bH = (e.stepCount / maxVal) * chartH
                val bx = chartX + i * barW2 + 2f
                val cPaint = if (e.stepCount >= goals.stepsGoal) greenPaint else bluePaint
                canvas?.drawRoundRect(bx, y + chartH - bH, bx + barW2 - 4f, y + chartH, 3f, 3f, cPaint)
            }
            // X labels (show some dates)
            for (i in chartEntries.indices step maxOf(1, chartEntries.size / 5)) {
                val lx = chartX + i * barW2
                canvas?.drawText(dateFormat.format(chartEntries[i].date).substring(5), lx, y + chartH + 12f, makePaint(7f, gray))
            }
            y += chartH + 22f
        }

        drawSpacer()
        drawLine()
        drawSpacer(6f)

        // ======== SLEEP LINE CHART ========
        ensureSpace(180f)
        canvas?.drawText("Sleep Over Time", m, y + 16f, h1P)
        y += 30f

        if (chartEntries.isNotEmpty()) {
            val maxSl = chartEntries.maxOf { it.sleepHours }.coerceAtLeast(1f)
            val chartX = m + 35f
            val slotW = (contentW - 40f) / maxOf(chartEntries.size - 1, 1)
            // Y axis
            canvas?.drawText("${"%.0f".format(maxSl)} hr", m - 5f, y + 10f, smallP)
            canvas?.drawText("0", m + 10f, y + chartH, smallP)
            // Goal line
            if (goals.sleepGoal > 0 && goals.sleepGoal <= maxSl) {
                val goalY = y + chartH - (goals.sleepGoal / maxSl * chartH)
                val dashPaint = android.graphics.Paint().apply {
                    color = green; strokeWidth = 1f; isAntiAlias = true
                    pathEffect = android.graphics.DashPathEffect(floatArrayOf(4f, 4f), 0f)
                }
                canvas?.drawLine(chartX, goalY, chartX + slotW * (chartEntries.size - 1), goalY, dashPaint)
                canvas?.drawText("Goal", chartX + slotW * (chartEntries.size - 1) + 2f, goalY + 4f, makePaint(7f, green))
            }
            // Lines + dots
            val lineP = android.graphics.Paint().apply { color = blue; strokeWidth = 2f; isAntiAlias = true; style = android.graphics.Paint.Style.STROKE }
            val dotP = android.graphics.Paint().apply { color = blue; isAntiAlias = true }
            for (i in 0 until chartEntries.size - 1) {
                val x1 = chartX + i * slotW
                val y1 = y + chartH - (chartEntries[i].sleepHours / maxSl * chartH)
                val x2 = chartX + (i + 1) * slotW
                val y2 = y + chartH - (chartEntries[i + 1].sleepHours / maxSl * chartH)
                canvas?.drawLine(x1, y1, x2, y2, lineP)
                canvas?.drawCircle(x1, y1, 3f, dotP)
            }
            // Last dot
            val lastX = chartX + (chartEntries.size - 1) * slotW
            val lastY = y + chartH - (chartEntries.last().sleepHours / maxSl * chartH)
            canvas?.drawCircle(lastX, lastY, 3f, dotP)
            // X labels
            for (i in chartEntries.indices step maxOf(1, chartEntries.size / 5)) {
                val lx = chartX + i * slotW
                canvas?.drawText(dateFormat.format(chartEntries[i].date).substring(5), lx, y + chartH + 12f, makePaint(7f, gray))
            }
            y += chartH + 22f
        }

        drawSpacer()
        drawLine()
        drawSpacer(6f)

        // ======== WEIGHT TREND CHART ========
        ensureSpace(180f)
        canvas?.drawText("Weight Trend", m, y + 16f, h1P)
        y += 30f

        if (chartEntries.isNotEmpty()) {
            val wMax = chartEntries.maxOf { it.weight }.coerceAtLeast(1f)
            val wMin = chartEntries.minOf { it.weight }
            val range = (wMax - wMin).coerceAtLeast(1f)
            val chartX = m + 45f
            val slotW = (contentW - 50f) / maxOf(chartEntries.size - 1, 1)
            canvas?.drawText("${"%.0f".format(wMax)} kg", m - 5f, y + 10f, smallP)
            canvas?.drawText("${"%.0f".format(wMin)} kg", m - 5f, y + chartH, smallP)
            // Goal line
            if (goals.weightGoal in wMin..wMax) {
                val goalY = y + chartH - ((goals.weightGoal - wMin) / range * chartH)
                val dashPaint = android.graphics.Paint().apply {
                    color = green; strokeWidth = 1f; isAntiAlias = true
                    pathEffect = android.graphics.DashPathEffect(floatArrayOf(4f, 4f), 0f)
                }
                canvas?.drawLine(chartX, goalY, chartX + slotW * (chartEntries.size - 1), goalY, dashPaint)
                canvas?.drawText("Goal ${"%.0f".format(goals.weightGoal)}kg", chartX + slotW * (chartEntries.size - 1) + 2f, goalY + 4f, makePaint(7f, green))
            }
            val lineP = android.graphics.Paint().apply { color = android.graphics.Color.parseColor("#FF7043"); strokeWidth = 2f; isAntiAlias = true; style = android.graphics.Paint.Style.STROKE }
            val dotP = android.graphics.Paint().apply { color = android.graphics.Color.parseColor("#FF7043"); isAntiAlias = true }
            // Fill area
            val path = android.graphics.Path()
            for (i in chartEntries.indices) {
                val cx = chartX + i * slotW
                val cy = y + chartH - ((chartEntries[i].weight - wMin) / range * chartH)
                if (i == 0) path.moveTo(cx, cy) else path.lineTo(cx, cy)
            }
            val areaPath = android.graphics.Path(path)
            areaPath.lineTo(chartX + (chartEntries.size - 1) * slotW, y + chartH)
            areaPath.lineTo(chartX, y + chartH)
            areaPath.close()
            val areaPaint = android.graphics.Paint().apply { color = android.graphics.Color.parseColor("#30FF7043"); isAntiAlias = true }
            canvas?.drawPath(areaPath, areaPaint)
            canvas?.drawPath(path, lineP)
            for (i in chartEntries.indices) {
                val cx = chartX + i * slotW
                val cy = y + chartH - ((chartEntries[i].weight - wMin) / range * chartH)
                canvas?.drawCircle(cx, cy, 3f, dotP)
            }
            for (i in chartEntries.indices step maxOf(1, chartEntries.size / 5)) {
                val lx = chartX + i * slotW
                canvas?.drawText(dateFormat.format(chartEntries[i].date).substring(5), lx, y + chartH + 12f, makePaint(7f, gray))
            }
            y += chartH + 22f
        }

        drawSpacer()
        drawLine()
        drawSpacer(6f)

        // ======== HEALTH ASSESSMENT ========
        ensureSpace(140f)
        canvas?.drawText("Health Assessment", m, y + 16f, h1P)
        y += 30f

        val assessments = mutableListOf<Pair<String, String>>()
        if (avgSleep >= goals.sleepGoal) assessments.add("✓ Sleep" to "You're meeting your sleep goal. Great rest habits!")
        else assessments.add("✗ Sleep" to "Avg ${"%.1f".format(avgSleep)}h is below your ${goals.sleepGoal}h goal. Try a consistent schedule.")

        if (avgSteps >= goals.stepsGoal) assessments.add("✓ Activity" to "Step count is on target. Keep moving!")
        else assessments.add("✗ Activity" to "Avg ${"%.0f".format(avgSteps)} steps is below your ${goals.stepsGoal} goal. Add short walks.")

        if (avgWater >= goals.waterGoal) assessments.add("✓ Hydration" to "Great hydration habits!")
        else assessments.add("✗ Hydration" to "Avg ${"%.1f".format(avgWater)} glasses is below your ${goals.waterGoal} goal. Set reminders.")

        if (profile.bmi in 18.5f..24.9f) assessments.add("✓ BMI" to "Your BMI ${"%.1f".format(profile.bmi)} is in the normal range.")
        else assessments.add("⚠ BMI" to "Your BMI ${"%.1f".format(profile.bmi)} (${profile.bmiCategory}). Consult a professional if needed.")

        for ((title, desc) in assessments) {
            ensureSpace(32f)
            val isGood = title.startsWith("✓")
            canvas?.drawText(title, m + 6f, y + 12f, makePaint(10f, if (isGood) green else red, true))
            canvas?.drawText(desc, m + 80f, y + 12f, bodyP)
            y += 22f
        }

        drawSpacer()
        drawLine()
        drawSpacer(6f)

        // ======== DATA TABLE (all entries) ========
        ensureSpace(50f)
        canvas?.drawText("Complete Data Log", m, y + 16f, h1P)
        canvas?.drawText("${entries.size} entries", m + 160f, y + 16f, smallP)
        y += 30f

        val colW = floatArrayOf(68f, 50f, 48f, 52f, 56f, 52f, 56f, 52f)
        val colH = arrayOf("Date", "Water", "Sleep", "Steps", "Mood", "Weight", "Cal", "Exercise")
        val tableW = colW.sum()

        fun drawTableHeader() {
            canvas?.drawRect(m, y, m + tableW, y + 20f, bgPaint)
            var cx = m
            for (i in colH.indices) {
                canvas?.drawText(colH[i], cx + 3f, y + 14f, whiteP)
                cx += colW[i]
            }
            y += 20f
        }

        drawTableHeader()

        for ((idx, entry) in entries.withIndex()) {
            if (y + 18f > ph - m) {
                newPage()
                drawTableHeader()
            }
            if (idx % 2 == 1) canvas?.drawRect(m, y, m + tableW, y + 18f, lightBgPaint)
            canvas?.drawLine(m, y, m + tableW, y, linePaint)
            val vals = arrayOf(
                dateFormat.format(entry.date),
                "${entry.waterIntake} gl",
                "${"%.1f".format(entry.sleepHours)} hr",
                "${entry.stepCount}",
                entry.mood,
                "${"%.1f".format(entry.weight)} kg",
                "${entry.calories}",
                "${entry.exerciseMinutes} m"
            )
            var cx = m
            for (j in vals.indices) {
                canvas?.drawText(vals[j], cx + 3f, y + 13f, makePaint(8f, android.graphics.Color.parseColor("#333333")))
                cx += colW[j]
            }
            y += 18f
        }
        canvas?.drawLine(m, y, m + tableW, y, linePaint)

        // ======== FOOTER on last page ========
        drawSpacer(20f)
        ensureSpace(30f)
        canvas?.drawLine(m, y, m + contentW, y, linePaint)
        y += 14f
        canvas?.drawText("Generated by HealthLoop on $timestamp  •  This report is for personal reference only.", m, y + 10f, smallP)

        currentPage?.let { document.finishPage(it) }

        val bytes = java.io.ByteArrayOutputStream().use { bos ->
            document.writeTo(bos)
            document.close()
            bos.toByteArray()
        }

        saveToDownloads(context, fileName, "application/pdf", bytes)
        android.widget.Toast.makeText(context, "Report saved to Downloads/$fileName", android.widget.Toast.LENGTH_LONG).show()
    }

    private fun saveToDownloads(context: Context, fileName: String, mimeType: String, data: ByteArray) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            val contentValues = android.content.ContentValues().apply {
                put(android.provider.MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(android.provider.MediaStore.Downloads.MIME_TYPE, mimeType)
                put(android.provider.MediaStore.Downloads.IS_PENDING, 1)
            }
            val resolver = context.contentResolver
            val uri = resolver.insert(
                android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                contentValues
            )
            uri?.let {
                resolver.openOutputStream(it)?.use { os -> os.write(data) }
                contentValues.clear()
                contentValues.put(android.provider.MediaStore.Downloads.IS_PENDING, 0)
                resolver.update(it, contentValues, null, null)
            }
        } else {
            @Suppress("DEPRECATION")
            val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(
                android.os.Environment.DIRECTORY_DOWNLOADS
            )
            val file = java.io.File(downloadsDir, fileName)
            file.writeBytes(data)
        }
    }
}
