package com.example.healthloop.presentation.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.healthloop.R
import com.example.healthloop.presentation.model.UiState
import com.example.healthloop.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(viewModel: HistoryViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 100.dp)
    ) {
        // Header
        Text(
            text = "Today Your Activities",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = DeepBlack
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Calendar View
        FullCalendarView(
            currentMonth = currentMonth,
            selectedDate = selectedDate,
            onMonthChange = { currentMonth = it },
            onDateSelected = { viewModel.setSelectedDate(it) }
        )
        
        when (val state = uiState) {
            is UiState.Loading -> {
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryOrange)
                }
            }
            is UiState.Error -> {
                Spacer(modifier = Modifier.height(20.dp))
                ErrorStateCard(message = state.message)
            }
            is UiState.Success -> {
                val historyState = state.data
                val entry = historyState.selectedDateEntry
                
                if (entry == null) {
                    Spacer(modifier = Modifier.height(20.dp))
                    EmptyStateCard()
                } else {
                    // Animation states for staggered entrance
                    var showQuickStats by remember { mutableStateOf(false) }
                    var showHealthOverview by remember { mutableStateOf(false) }
                    var showMoodSection by remember { mutableStateOf(false) }
                    var showActivityStats by remember { mutableStateOf(false) }
                    var showExercise by remember { mutableStateOf(false) }
                    var showCaloriesWeight by remember { mutableStateOf(false) }
                    
                    LaunchedEffect(entry.id) {
                        showQuickStats = false
                        showHealthOverview = false
                        showMoodSection = false
                        showActivityStats = false
                        showExercise = false
                        showCaloriesWeight = false
                        
                        delay(100)
                        showQuickStats = true
                        delay(150)
                        showHealthOverview = true
                        delay(150)
                        showMoodSection = true
                        delay(150)
                        showActivityStats = true
                        delay(150)
                        showExercise = true
                        delay(150)
                        showCaloriesWeight = true
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Quick Stats Row - Sleep Time & Mood
                    AnimatedVisibility(
                        visible = showQuickStats,
                        enter = fadeIn(animationSpec = tween(400)) + slideInHorizontally(
                            initialOffsetX = { -it / 2 },
                            animationSpec = tween(400)
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            QuickStatCard(
                                icon = R.drawable.sleepingg,
                                title = "Sleep Time",
                                value = formatSleepTime(entry.sleepHours),
                                backgroundColor = SoftGreen.copy(alpha = 0.2f),
                                modifier = Modifier.weight(1f)
                            )
                            QuickStatCard(
                                icon = getMoodIcon(entry.mood),
                                title = "Mood",
                                value = entry.mood.ifEmpty { "Not set" },
                                backgroundColor = PrimaryOrange.copy(alpha = 0.2f),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Health Overview Section
                    AnimatedVisibility(
                        visible = showHealthOverview,
                        enter = fadeIn(animationSpec = tween(400)) + scaleIn(
                            initialScale = 0.9f,
                            animationSpec = tween(400)
                        )
                    ) {
                        HealthOverviewCard(
                            waterIntake = entry.waterIntake,
                            waterGoal = historyState.userGoals.waterGoal,
                            sleepHours = entry.sleepHours,
                            sleepGoal = historyState.userGoals.sleepGoal,
                            stepCount = entry.stepCount,
                            stepsGoal = historyState.userGoals.stepsGoal,
                            calories = entry.calories,
                            caloriesGoal = historyState.userGoals.caloriesGoal,
                            exerciseMinutes = entry.exerciseMinutes,
                            exerciseGoal = historyState.userGoals.exerciseGoal
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Mood Check-ins Section
                    AnimatedVisibility(
                        visible = showMoodSection,
                        enter = fadeIn(animationSpec = tween(400)) + slideInVertically(
                            initialOffsetY = { it / 4 },
                            animationSpec = tween(400)
                        )
                    ) {
                        MoodCheckInsSection(
                            currentMood = entry.mood,
                            weeklyMoodData = historyState.weeklyMoodData,
                            mostCommonMood = historyState.mostCommonMood
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Activity Stats Row - Steps & Water
                    AnimatedVisibility(
                        visible = showActivityStats,
                        enter = fadeIn(animationSpec = tween(400)) + slideInHorizontally(
                            initialOffsetX = { it / 2 },
                            animationSpec = tween(400)
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            QuickStatCard(
                                icon = R.drawable.walkk,
                                title = "Steps",
                                value = formatNumber(entry.stepCount),
                                backgroundColor = SkyBlue.copy(alpha = 0.2f),
                                modifier = Modifier.weight(1f)
                            )
                            QuickStatCard(
                                icon = R.drawable.waterr,
                                title = "Water Intake",
                                value = "${entry.waterIntake} glasses",
                                backgroundColor = SkyBlue.copy(alpha = 0.15f),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Exercise Card
                    AnimatedVisibility(
                        visible = showExercise,
                        enter = fadeIn(animationSpec = tween(400)) + scaleIn(
                            initialScale = 0.9f,
                            animationSpec = tween(400)
                        )
                    ) {
                        ExerciseCard(
                            exerciseMinutes = entry.exerciseMinutes,
                            exerciseGoal = historyState.userGoals.exerciseGoal,
                            calories = entry.calories
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Calories & Weight Row
                    AnimatedVisibility(
                        visible = showCaloriesWeight,
                        enter = fadeIn(animationSpec = tween(400)) + slideInVertically(
                            initialOffsetY = { it / 3 },
                            animationSpec = tween(400)
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CaloriesCard(
                                calories = entry.calories,
                                caloriesGoal = historyState.userGoals.caloriesGoal,
                                modifier = Modifier.weight(1f)
                            )
                            WeightCard(
                                weight = entry.weight,
                                weightGoal = historyState.userGoals.weightGoal,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Helper functions
private fun formatSleepTime(hours: Float): String {
    val wholeHours = hours.toInt()
    val minutes = ((hours - wholeHours) * 60).toInt()
    return if (minutes > 0) "${wholeHours}h ${minutes}m" else "${wholeHours}h"
}

private fun formatNumber(number: Int): String {
    return if (number >= 1000) {
        String.format("%,d", number)
    } else {
        number.toString()
    }
}

private fun getMoodIcon(mood: String): Int {
    return when (mood.lowercase()) {
        "happy" -> R.drawable.happy
        "sad" -> R.drawable.sad
        "angry" -> R.drawable.angry
        "anxious" -> R.drawable.anxious
        "excited" -> R.drawable.exited
        "tired" -> R.drawable.tired
        "calm" -> R.drawable.calm
        "grateful" -> R.drawable.grateful
        "confused" -> R.drawable.confused
        else -> R.drawable.happy
    }
}

// Error State Card
@Composable
private fun ErrorStateCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CoralPink.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Error",
                tint = CoralPink,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Something went wrong",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DeepBlack
            )
            Text(
                text = message,
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ==================== QUICK STAT CARD ====================
@Composable
private fun QuickStatCard(
    icon: Int,
    title: String,
    value: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = title,
                    modifier = Modifier.size(20.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DeepBlack
            )
        }
    }
}

// ==================== HEALTH OVERVIEW CARD ====================
@Composable
private fun HealthOverviewCard(
    waterIntake: Int,
    waterGoal: Int,
    sleepHours: Float,
    sleepGoal: Float,
    stepCount: Int,
    stepsGoal: Int,
    calories: Int,
    caloriesGoal: Int,
    exerciseMinutes: Int,
    exerciseGoal: Int
) {
    val waterProgress = if (waterGoal > 0) minOf(waterIntake.toFloat() / waterGoal, 1f) else 0f
    val sleepProgress = if (sleepGoal > 0) minOf(sleepHours / sleepGoal, 1f) else 0f
    val stepsProgress = if (stepsGoal > 0) minOf(stepCount.toFloat() / stepsGoal, 1f) else 0f
    val caloriesProgress = if (caloriesGoal > 0) minOf(calories.toFloat() / caloriesGoal, 1f) else 0f
    val exerciseProgress = if (exerciseGoal > 0) minOf(exerciseMinutes.toFloat() / exerciseGoal, 1f) else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Health Overview",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepBlack
                )
                Icon(
                    painter = painterResource(id = R.drawable.setting),
                    contentDescription = "Settings",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left side - Stats
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    OverviewStatRow(
                        color = SkyBlue,
                        label = "$waterGoal Glasses Goal",
                        subLabel = "Water: $waterIntake glasses",
                        progress = waterProgress
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OverviewStatRow(
                        color = SoftGreen,
                        label = "${formatSleepTime(sleepHours)} Achieved",
                        subLabel = "Sleep Goal: ${formatSleepTime(sleepGoal)}",
                        progress = sleepProgress
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OverviewStatRow(
                        color = PrimaryOrange,
                        label = "$stepCount Steps",
                        subLabel = "Goal: $stepsGoal",
                        progress = stepsProgress
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OverviewStatRow(
                        color = CoralPink,
                        label = "$calories kcal",
                        subLabel = "Goal: $caloriesGoal kcal",
                        progress = caloriesProgress
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OverviewStatRow(
                        color = MintGreen,
                        label = "$exerciseMinutes min",
                        subLabel = "Goal: $exerciseGoal min",
                        progress = exerciseProgress
                    )
                }

                // Right side - Donut Chart
                Box(
                    modifier = Modifier.size(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    DonutChart(
                        data = listOf(
                            DonutChartData(waterProgress * 0.20f, SkyBlue, R.drawable.waterr),
                            DonutChartData(sleepProgress * 0.20f, SoftGreen, R.drawable.sleepingg),
                            DonutChartData(stepsProgress * 0.20f, PrimaryOrange, R.drawable.walkk),
                            DonutChartData(caloriesProgress * 0.20f, CoralPink, R.drawable.calaroiess),
                            DonutChartData(exerciseProgress * 0.20f, MintGreen, R.drawable.excercisee)
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun OverviewStatRow(
    color: Color,
    label: String,
    subLabel: String,
    progress: Float
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = DeepBlack
            )
            Text(
                text = subLabel,
                fontSize = 11.sp,
                color = TextSecondary
            )
        }
    }
}

data class DonutChartData(
    val value: Float,
    val color: Color,
    val iconRes: Int = 0
)

@Composable
private fun DonutChart(
    data: List<DonutChartData>,
    modifier: Modifier = Modifier
) {
    // Calculate icon positions based on arc midpoints
    data class IconPosition(val x: Float, val y: Float, val iconRes: Int, val color: Color)
    val iconPositions = remember(data) {
        val positions = mutableListOf<IconPosition>()
        var startAngle = -90f
        val labelRadiusFraction = 0.92f

        data.forEach { segment ->
            val sweepAngle = segment.value * 360f
            if (sweepAngle > 0f && segment.iconRes != 0) {
                val midAngle = Math.toRadians((startAngle + sweepAngle / 2).toDouble())
                val xFraction = 0.5f + (labelRadiusFraction * 0.5f * kotlin.math.cos(midAngle)).toFloat()
                val yFraction = 0.5f + (labelRadiusFraction * 0.5f * kotlin.math.sin(midAngle)).toFloat()
                positions.add(IconPosition(xFraction, yFraction, segment.iconRes, segment.color))
            }
            startAngle += sweepAngle
        }
        positions
    }

    Box(
        modifier = modifier.size(160.dp),
        contentAlignment = Alignment.Center
    ) {
        // Draw arcs
        Canvas(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.Center)
        ) {
            val strokeWidth = 18.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val center = Offset(size.width / 2, size.height / 2)

            var startAngle = -90f

            data.forEach { segment ->
                val sweepAngle = segment.value * 360f
                if (sweepAngle > 0f) {
                    drawArc(
                        color = segment.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle - 3f,
                        useCenter = false,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
                startAngle += sweepAngle
            }
        }

        // Draw icons at calculated positions
        iconPositions.forEach { pos ->
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .offset(
                        x = ((pos.x - 0.5f) * 160).dp,
                        y = ((pos.y - 0.5f) * 160).dp
                    )
                    .background(pos.color.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = pos.iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

// ==================== MOOD CHECK-INS SECTION ====================
@Composable
private fun MoodCheckInsSection(
    currentMood: String,
    weeklyMoodData: List<Pair<String, String>>,
    mostCommonMood: String
) {
    val moodMap = mapOf(
        "angry" to Triple(R.drawable.angry, "Angry", Color(0xFFFFCDD2)),
        "sad" to Triple(R.drawable.sad, "Sad", Color(0xFFBBDEFB)),
        "anxious" to Triple(R.drawable.anxious, "Anxious", Color(0xFFE1BEE7)),
        "confused" to Triple(R.drawable.confused, "Confused", Color(0xFFF8BBD0)),
        "happy" to Triple(R.drawable.happy, "Happy", Color(0xFFC8E6C9)),
        "excited" to Triple(R.drawable.exited, "Excited", Color(0xFFFFE0B2)),
        "calm" to Triple(R.drawable.calm, "Calm", Color(0xFFB2DFDB))
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = WarmBeigeLight),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Mood Check-ins",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DeepBlack
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Weekly mood timeline - show each day's logged mood
            if (weeklyMoodData.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    weeklyMoodData.forEach { (day, mood) ->
                        val moodInfo = moodMap[mood.lowercase()]
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (moodInfo != null) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            color = moodInfo.third,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = moodInfo.first),
                                        contentDescription = moodInfo.second,
                                        modifier = Modifier.size(22.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                            } else {
                                // No mood logged for this day
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            color = Color(0xFFEEEEEE),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "—",
                                        fontSize = 14.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = day,
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = "No mood data this week",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Summary text
            if (mostCommonMood.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Most common mood this week: ",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = mostCommonMood,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepBlack
                    )
                    Text(
                        text = " ${getMoodEmoji(mostCommonMood)}",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

private fun getMoodEmoji(mood: String): String {
    return when (mood.lowercase()) {
        "happy" -> "😊"
        "sad" -> "😢"
        "angry" -> "😠"
        "anxious" -> "😰"
        "excited" -> "🤩"
        "tired" -> "😴"
        "calm" -> "😌"
        "grateful" -> "🌟"
        "confused" -> "😕"
        else -> "😊"
    }
}

// ==================== EXERCISE CARD ====================
@Composable
private fun ExerciseCard(
    exerciseMinutes: Int,
    exerciseGoal: Int,
    calories: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (exerciseGoal > 0) minOf(exerciseMinutes.toFloat() / exerciseGoal, 1f) else 0f
    val progressPercent = (progress * 100).toInt()
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MintGreen.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.excercisee),
                        contentDescription = "Exercise",
                        modifier = Modifier.size(24.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Exercise",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepBlack
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MintGreen.copy(alpha = 0.4f)
                ) {
                    Text(
                        text = "Goal: $exerciseGoal min",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ExerciseStatItem("Duration", "$exerciseMinutes min", MintGreen)
                ExerciseStatItem("Calories", "$calories kcal", PrimaryOrange)
                ExerciseStatItem("Progress", "$progressPercent%", SkyBlue)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Progress bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Daily Progress",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "$progressPercent%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (progressPercent >= 100) MintGreen else PrimaryOrange
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (progressPercent >= 100) MintGreen else PrimaryOrange,
                    trackColor = MintGreen.copy(alpha = 0.2f),
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
private fun ExerciseStatItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = DeepBlack
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextSecondary
        )
    }
}

// ==================== CALORIES CARD ====================
@Composable
private fun CaloriesCard(
    calories: Int,
    caloriesGoal: Int,
    modifier: Modifier = Modifier
) {
    val isOnTrack = calories <= caloriesGoal || caloriesGoal == 0
    val statusText = when {
        caloriesGoal == 0 -> "No goal set"
        calories <= caloriesGoal * 0.8 -> "Under goal"
        calories <= caloriesGoal -> "On track"
        else -> "Over goal"
    }
    val statusColor = when {
        caloriesGoal == 0 -> TextSecondary
        calories <= caloriesGoal -> Color(0xFF2E7D32)
        else -> CoralPink
    }
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = PrimaryOrange.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.calaroiess),
                contentDescription = "Calories",
                modifier = Modifier.size(28.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = formatNumber(calories),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DeepBlack
            )
            Text(
                text = "kcal",
                fontSize = 12.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isOnTrack) MintGreen.copy(alpha = 0.3f) else CoralPink.copy(alpha = 0.3f)
            ) {
                Text(
                    text = statusText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = statusColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

// ==================== WEIGHT CARD ====================
@Composable
private fun WeightCard(
    weight: Float,
    weightGoal: Float,
    modifier: Modifier = Modifier
) {
    val weightDiff = weight - weightGoal
    val statusText = when {
        weightGoal == 0f -> "No goal"
        kotlin.math.abs(weightDiff) < 0.5f -> "At goal!"
        weightDiff > 0 -> "↓ ${String.format("%.1f", weightDiff)} kg"
        else -> "↑ ${String.format("%.1f", -weightDiff)} kg"
    }
    val isPositive = weightGoal == 0f || weightDiff <= 0
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CoralPink.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.weightt),
                contentDescription = "Weight",
                modifier = Modifier.size(28.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (weight > 0) String.format("%.1f", weight) else "--",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DeepBlack
            )
            Text(
                text = "kg",
                fontSize = 12.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isPositive) MintGreen.copy(alpha = 0.3f) else PrimaryOrange.copy(alpha = 0.3f)
            ) {
                Text(
                    text = statusText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isPositive) Color(0xFF2E7D32) else PrimaryOrange,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
private fun FullCalendarView(
    currentMonth: Calendar,
    selectedDate: Calendar,
    onMonthChange: (Calendar) -> Unit,
    onDateSelected: (Calendar) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showMonthPicker by remember { mutableStateOf(false) }
    var showYearPicker by remember { mutableStateOf(false) }
    
    val today = remember { Calendar.getInstance() }
    val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
    
    val monthCalendar = remember(currentMonth) {
        (currentMonth.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, 1)
        }
    }
    val monthName = remember(currentMonth) {
        SimpleDateFormat("MMMM", Locale.getDefault()).format(currentMonth.time)
    }
    val year = currentMonth.get(Calendar.YEAR)
    val daysInMonth = monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1
    
    val currentWeekStart = remember(selectedDate) {
        val cal = selectedDate.clone() as Calendar
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        cal
    }
    
    val expandIconRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "expandRotation"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        colors = CardDefaults.cardColors(containerColor = WarmBeigeLight),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 14.dp)
        ) {
            // Header with Month/Year and navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        val newMonth = (currentMonth.clone() as Calendar).apply {
                            add(Calendar.MONTH, -1)
                        }
                        onMonthChange(newMonth)
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .background(WarmBeige.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Previous",
                        tint = DeepBlack,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { showMonthPicker = true }
                ) {
                    Text(
                        text = monthName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepBlack
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = year.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryOrange,
                        modifier = Modifier.clickable { showYearPicker = true }
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                IconButton(
                    onClick = {
                        val newMonth = (currentMonth.clone() as Calendar).apply {
                            add(Calendar.MONTH, 1)
                        }
                        onMonthChange(newMonth)
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .background(WarmBeige.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Next",
                        tint = DeepBlack,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Days of Week Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                daysOfWeek.forEach { day ->
                    Text(
                        text = day,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (isExpanded) {
                val totalCells = ((firstDayOfWeek + daysInMonth + 6) / 7) * 7
                val rows = totalCells / 7
                
                for (row in 0 until rows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (col in 0 until 7) {
                            val dayIndex = row * 7 + col - firstDayOfWeek + 1
                            
                            if (dayIndex in 1..daysInMonth) {
                                val dayCalendar = (currentMonth.clone() as Calendar).apply {
                                    set(Calendar.DAY_OF_MONTH, dayIndex)
                                }
                                val isToday = isSameDay(dayCalendar, today)
                                val isSelected = isSameDay(dayCalendar, selectedDate)
                                
                                CalendarDayItem(
                                    day = dayIndex,
                                    isToday = isToday,
                                    isSelected = isSelected,
                                    onClick = { onDateSelected(dayCalendar.clone() as Calendar) },
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                Box(modifier = Modifier.weight(1f).height(36.dp))
                            }
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (i in 0 until 7) {
                        val dayCalendar = (currentWeekStart.clone() as Calendar).apply {
                            add(Calendar.DAY_OF_MONTH, i)
                        }
                        val dayOfMonth = dayCalendar.get(Calendar.DAY_OF_MONTH)
                        val isToday = isSameDay(dayCalendar, today)
                        val isSelected = isSameDay(dayCalendar, selectedDate)
                        
                        CalendarDayItem(
                            day = dayOfMonth,
                            isToday = isToday,
                            isSelected = isSelected,
                            onClick = { onDateSelected(dayCalendar.clone() as Calendar) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { isExpanded = !isExpanded },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = TextSecondary,
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(expandIconRotation)
                )
            }
        }
    }
    
    // Month Picker Dialog
    if (showMonthPicker) {
        MonthPickerDialog(
            currentMonth = currentMonth.get(Calendar.MONTH),
            onMonthSelected = { month ->
                val newCal = (currentMonth.clone() as Calendar).apply {
                    set(Calendar.MONTH, month)
                }
                onMonthChange(newCal)
                showMonthPicker = false
            },
            onDismiss = { showMonthPicker = false }
        )
    }
    
    // Year Picker Dialog
    if (showYearPicker) {
        YearPickerDialog(
            currentYear = year,
            onYearSelected = { selectedYear ->
                val newCal = (currentMonth.clone() as Calendar).apply {
                    set(Calendar.YEAR, selectedYear)
                }
                onMonthChange(newCal)
                showYearPicker = false
            },
            onDismiss = { showYearPicker = false }
        )
    }
}

@Composable
private fun CalendarDayItem(
    day: Int,
    isToday: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(36.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = when {
                        isSelected -> PrimaryOrange
                        isToday -> SoftGreen.copy(alpha = 0.3f)
                        else -> Color.Transparent
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day.toString(),
                fontSize = 14.sp,
                fontWeight = if (isSelected || isToday) FontWeight.SemiBold else FontWeight.Normal,
                color = when {
                    isSelected -> DeepBlack
                    isToday -> DeepBlack
                    else -> DeepBlack
                }
            )
        }
    }
}

@Composable
private fun MonthPickerDialog(
    currentMonth: Int,
    onMonthSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val months = listOf(
        "January", "February", "March", "April",
        "May", "June", "July", "August",
        "September", "October", "November", "December"
    )
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Select Month",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepBlack
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                months.chunked(3).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        row.forEach { month ->
                            val monthIndex = months.indexOf(month)
                            val isSelected = monthIndex == currentMonth
                            
                            Surface(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clickable { onMonthSelected(monthIndex) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) PrimaryOrange else WarmBeige.copy(alpha = 0.5f)
                            ) {
                                Text(
                                    text = month.take(3),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = DeepBlack
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun YearPickerDialog(
    currentYear: Int,
    onYearSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val years = (currentYear - 5..currentYear + 5).toList()
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Select Year",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepBlack
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                years.chunked(3).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        row.forEach { year ->
                            val isSelected = year == currentYear
                            
                            Surface(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clickable { onYearSelected(year) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) PrimaryOrange else WarmBeige.copy(alpha = 0.5f)
                            ) {
                                Text(
                                    text = year.toString(),
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = DeepBlack
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyStateCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = WarmBeige.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.nodata),
                contentDescription = "No data",
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No health data yet",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DeepBlack
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Start tracking your health journey!",
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
