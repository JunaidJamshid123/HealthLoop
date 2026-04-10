package com.example.healthloop.presentation.dashboard

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.healthloop.R
import com.example.healthloop.presentation.model.TodayHealthDataUiModel
import com.example.healthloop.presentation.model.UiState
import com.example.healthloop.ui.theme.*
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    navController: NavController? = null,
    onMenuClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf("Mood") }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    
    // Determine greeting based on time
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when {
            hour < 12 -> "Good morning"
            hour < 17 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    when (val state = uiState) {
        is UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryOrange)
            }
        }
        is UiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = state.message,
                        color = CoralPink,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.refreshData() },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                    ) {
                        Text("Retry")
                    }
                }
            }
        }
        is UiState.Success -> {
            val dashboardState = state.data
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SurfaceLight)
                    .verticalScroll(rememberScrollState())
                    .padding(top = 20.dp, bottom = 100.dp)
            ) {
                // Header Section with user profile data
                HomeHeader(
                    greeting = greeting,
                    userName = dashboardState.userName,
                    profilePictureBase64 = dashboardState.profilePictureBase64,
                    onMenuClick = onMenuClick,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Balance Text Section
                BalanceTextSection(
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Streak Counter Card
                StreakCard(
                    streakDays = dashboardState.streakDays,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Tab Chips - Scrollable for small screens
                TabChipsRow(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tab Content based on selection - now with real data
                when (selectedTab) {
                    "Mood" -> MoodHistoryCard(
                        moodData = dashboardState.weeklyData.moodData,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    "Water" -> WaterHistoryCard(
                        waterData = dashboardState.weeklyData.waterData,
                        avgWater = dashboardState.weeklyData.avgWater,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    "Sleep" -> SleepHistoryCard(
                        sleepData = dashboardState.weeklyData.sleepData,
                        avgSleep = dashboardState.weeklyData.avgSleep,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    "Steps" -> StepsHistoryCard(
                        stepsData = dashboardState.weeklyData.stepsData,
                        totalSteps = dashboardState.weeklyData.totalSteps,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    "Weight" -> WeightHistoryCard(
                        weightData = dashboardState.weeklyData.weightData,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    "Calories" -> CaloriesHistoryCard(
                        caloriesData = dashboardState.weeklyData.caloriesData,
                        avgCalories = dashboardState.weeklyData.avgCalories,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    "Exercise" -> ExerciseHistoryCard(
                        exerciseData = dashboardState.weeklyData.exerciseData,
                        totalExercise = dashboardState.weeklyData.totalExercise,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Today's Summary Card - now with real data
                TodaySummaryCard(
                    todayData = dashboardState.today,
                    currentDate = dashboardState.currentDate,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Weekly Stats Section - now with real data
                WeeklyStatsSection(
                    weeklyData = dashboardState.weeklyData,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
    }
}

// ==================== BALANCE TEXT SECTION ====================
@Composable
private fun BalanceTextSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = buildAnnotatedString {
                append("Track Your ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Health")
                }
                append("\nTransform Your ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Life")
                }
            },
            fontSize = 26.sp,
            fontWeight = FontWeight.Normal,
            color = DeepBlack,
            lineHeight = 32.sp
        )
    }
}

// ==================== STREAK CARD ====================
@Composable
private fun StreakCard(
    streakDays: Int,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
        label = "streakScale"
    )
    
    LaunchedEffect(Unit) {
        delay(200)
        isVisible = true
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryOrange),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "\uD83D\uDD25",
                fontSize = 28.sp
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$streakDays Day Streak!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepBlack
                )
                Text(
                    text = "Keep logging daily",
                    fontSize = 12.sp,
                    color = DeepBlack.copy(alpha = 0.7f)
                )
            }
        }
    }
}

// ==================== TODAY'S SUMMARY CARD ====================
@Composable
private fun TodaySummaryCard(
    todayData: TodayHealthDataUiModel,
    currentDate: String,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(300)
        isVisible = true
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
            initialOffsetY = { it / 4 },
            animationSpec = tween(500)
        )
    ) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
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
                        text = "Today's Summary",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepBlack
                    )
                    Text(
                        text = currentDate,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Summary Grid - 2 rows of 3 items with staggered animation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SummaryItem(
                        icon = R.drawable.waterr,
                        title = "Water",
                        value = "${todayData.waterIntake}/${todayData.targetWater}",
                        unit = "glasses",
                        progress = todayData.waterProgress,
                        color = SkyBlue,
                        modifier = Modifier.weight(1f),
                        delayMillis = 0
                    )
                    SummaryItem(
                        icon = R.drawable.sleepingg,
                        title = "Sleep",
                        value = String.format(Locale.US, "%.1f", todayData.sleepHours),
                        unit = "hours",
                        progress = todayData.sleepProgress,
                        color = SoftGreen,
                        modifier = Modifier.weight(1f),
                        delayMillis = 100
                    )
                    SummaryItem(
                        icon = R.drawable.walkk,
                        title = "Steps",
                        value = NumberFormat.getNumberInstance(Locale.US).format(todayData.stepCount),
                        unit = "steps",
                        progress = todayData.stepsProgress,
                        color = PrimaryOrange,
                        modifier = Modifier.weight(1f),
                        delayMillis = 200
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SummaryItem(
                        icon = R.drawable.weight,
                        title = "Weight",
                        value = if (todayData.weight > 0) String.format(Locale.US, "%.1f", todayData.weight) else "—",
                        unit = "kg",
                        progress = if (todayData.weight > 0) 1f else 0f,
                        color = CoralPink,
                        modifier = Modifier.weight(1f),
                        delayMillis = 300
                    )
                    SummaryItem(
                        icon = R.drawable.calaroiess,
                        title = "Calories",
                        value = NumberFormat.getNumberInstance(Locale.US).format(todayData.calories),
                        unit = "kcal",
                        progress = todayData.caloriesProgress,
                        color = PrimaryOrange,
                        modifier = Modifier.weight(1f),
                        delayMillis = 400
                    )
                    SummaryItem(
                        icon = R.drawable.excercisee,
                        title = "Exercise",
                        value = "${todayData.exerciseMinutes}",
                        unit = "mins",
                        progress = todayData.exerciseProgress,
                        color = MintGreen,
                        modifier = Modifier.weight(1f),
                        delayMillis = 500
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryItem(
    icon: Int,
    title: String,
    value: String,
    unit: String,
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier,
    delayMillis: Int = 0
) {
    var startAnimation by remember { mutableStateOf(false) }
    
    val animatedProgress by animateFloatAsState(
        targetValue = if (startAnimation) progress else 0f,
        animationSpec = tween(durationMillis = 1000, delayMillis = delayMillis),
        label = "progress"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 200f),
        label = "scale"
    )
    
    LaunchedEffect(Unit) {
        delay(delayMillis.toLong() + 100)
        startAnimation = true
    }
    
    Column(
        modifier = modifier
            .padding(4.dp)
            .scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.size(56.dp),
                color = color,
                trackColor = color.copy(alpha = 0.2f),
                strokeWidth = 5.dp,
                strokeCap = StrokeCap.Round
            )
            Image(
                painter = painterResource(id = icon),
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                contentScale = ContentScale.Fit
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = DeepBlack
        )
        Text(
            text = unit,
            fontSize = 11.sp,
            color = TextSecondary
        )
    }
}

// ==================== WEEKLY STATS SECTION ====================
@Composable
private fun WeeklyStatsSection(
    weeklyData: WeeklyData,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(600)
        isVisible = true
    }
    
    // Calculate trends
    val sleepTrend = if (weeklyData.avgSleep >= 7f) "+${String.format(Locale.US, "%.1f", weeklyData.avgSleep - 7f)}" else "${String.format(Locale.US, "%.1f", weeklyData.avgSleep - 7f)}"
    val stepsTrendPercent = if (weeklyData.totalSteps > 0) ((weeklyData.totalSteps.toFloat() / 70000) * 100).toInt() else 0
    val waterTrend = if (weeklyData.avgWater >= 6f) "+${String.format(Locale.US, "%.1f", weeklyData.avgWater - 6f)}" else "${String.format(Locale.US, "%.1f", weeklyData.avgWater - 6f)}"
    
    // Get first and last weight for trend
    val weightChange = run {
        val weights = weeklyData.weightData.filter { it.second > 0f }
        if (weights.size >= 2) {
            val diff = weights.last().second - weights.first().second
            if (diff < 0) "↓ ${String.format(Locale.US, "%.1f", -diff)} kg" else if (diff > 0) "↑ ${String.format(Locale.US, "%.1f", diff)} kg" else "No change"
        } else "—"
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
            initialOffsetY = { it / 3 },
            animationSpec = tween(500)
        )
    ) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = WarmBeigeLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "This Week",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepBlack
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // First row - 3 items
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WeeklyStatItem(
                        icon = R.drawable.sleepingg,
                        title = "Sleep",
                        value = String.format(Locale.US, "%.1f", weeklyData.avgSleep),
                        unit = "hrs",
                        trend = sleepTrend,
                        isPositive = weeklyData.avgSleep >= 7f,
                        modifier = Modifier.weight(1f)
                    )
                    WeeklyStatItem(
                        icon = R.drawable.walkk,
                        title = "Steps",
                        value = "${weeklyData.totalSteps / 1000}K",
                        unit = "steps",
                        trend = if (stepsTrendPercent >= 100) "On track" else "${stepsTrendPercent}%",
                        isPositive = stepsTrendPercent >= 70,
                        modifier = Modifier.weight(1f)
                    )
                    WeeklyStatItem(
                        icon = R.drawable.waterr,
                        title = "Water",
                        value = String.format(Locale.US, "%.1f", weeklyData.avgWater),
                        unit = "glasses",
                        trend = waterTrend,
                        isPositive = weeklyData.avgWater >= 6f,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Second row - 3 items
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WeeklyStatItem(
                        icon = R.drawable.weight,
                        title = "Weight",
                        value = weightChange,
                        unit = "",
                        trend = if (weightChange.startsWith("↓")) "Good" else "—",
                        isPositive = weightChange.startsWith("↓"),
                        modifier = Modifier.weight(1f)
                    )
                    WeeklyStatItem(
                        icon = R.drawable.calaroiess,
                        title = "Calories",
                        value = "${weeklyData.avgCalories / 1000}.${(weeklyData.avgCalories % 1000) / 100}K",
                        unit = "avg",
                        trend = if (weeklyData.avgCalories in 1500..2500) "On track" else "—",
                        isPositive = weeklyData.avgCalories in 1500..2500,
                        modifier = Modifier.weight(1f)
                    )
                    WeeklyStatItem(
                        icon = R.drawable.excercisee,
                        title = "Exercise",
                        value = String.format(Locale.US, "%.1f", weeklyData.totalExercise / 60f),
                        unit = "hrs",
                        trend = "+${weeklyData.totalExercise}m",
                        isPositive = weeklyData.totalExercise >= 150,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun WeeklyStatItem(
    icon: Int,
    title: String,
    value: String,
    unit: String,
    trend: String,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = Color.White.copy(alpha = 0.7f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = title,
                modifier = Modifier.size(22.dp),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = DeepBlack
        )
        Text(
            text = unit,
            fontSize = 10.sp,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isPositive) MintGreen.copy(alpha = 0.4f) else CoralPink.copy(alpha = 0.4f)
        ) {
            Text(
                text = trend,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = if (isPositive) Color(0xFF2E7D32) else Color(0xFFC62828),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

// ==================== TAB CONTENT CARDS ====================

@Composable
private fun WaterHistoryCard(
    waterData: List<Pair<String, Int>>,
    avgWater: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SkyBlue.copy(alpha = 0.12f)),
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
                    text = "Water Intake",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepBlack
                )
                Text(
                    text = "Last 7 days",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Bar chart for water
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                val displayData = if (waterData.isNotEmpty()) waterData else listOf(
                    "Mon" to 0, "Tue" to 0, "Wed" to 0, "Thu" to 0, 
                    "Fri" to 0, "Sat" to 0, "Sun" to 0
                )
                displayData.forEach { (day, glasses) ->
                    BarChartItem(
                        day = day,
                        value = glasses,
                        maxValue = 8,
                        color = SkyBlue
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Average: ${String.format(Locale.US, "%.1f", avgWater)} glasses/day",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SleepHistoryCard(
    sleepData: List<Pair<String, Float>>,
    avgSleep: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SoftGreen.copy(alpha = 0.15f)),
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
                    text = "Sleep Patterns",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepBlack
                )
                Text(
                    text = "Last 7 days",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                val displayData = if (sleepData.isNotEmpty()) sleepData else listOf(
                    "Mon" to 0f, "Tue" to 0f, "Wed" to 0f, "Thu" to 0f,
                    "Fri" to 0f, "Sat" to 0f, "Sun" to 0f
                )
                displayData.forEach { (day, hours) ->
                    SleepBarItem(
                        day = day,
                        hours = hours,
                        maxHours = 10f
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Average: ${String.format(Locale.US, "%.1f", avgSleep)} hours/night",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun StepsHistoryCard(
    stepsData: List<Pair<String, Int>>,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryOrange.copy(alpha = 0.12f)),
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
                    text = "Step Count",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepBlack
                )
                Text(
                    text = "Last 7 days",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                val displayData = if (stepsData.isNotEmpty()) stepsData else listOf(
                    "Mon" to 0, "Tue" to 0, "Wed" to 0, "Thu" to 0,
                    "Fri" to 0, "Sat" to 0, "Sun" to 0
                )
                displayData.forEach { (day, steps) ->
                    BarChartItem(
                        day = day,
                        value = steps / 1000,
                        maxValue = 12,
                        color = PrimaryOrange,
                        displayValue = if (steps > 0) "${steps / 1000}K" else "0"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Total: ${NumberFormat.getNumberInstance(Locale.US).format(totalSteps)} steps this week",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun WeightHistoryCard(
    weightData: List<Pair<String, Float>>,
    modifier: Modifier = Modifier
) {
    // Calculate weight change
    val weights = weightData.filter { it.second > 0f }
    val weightChange = if (weights.size >= 2) {
        val diff = weights.last().second - weights.first().second
        if (diff < 0) "↓ ${String.format(Locale.US, "%.1f", -diff)} kg this week" 
        else if (diff > 0) "↑ ${String.format(Locale.US, "%.1f", diff)} kg this week" 
        else "No change"
    } else "No data yet"
    
    val isPositive = weightChange.startsWith("↓")
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CoralPink.copy(alpha = 0.12f)),
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
                    text = "Weight Trend",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepBlack
                )
                Text(
                    text = "Last 7 days",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Simple weight display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val displayData = if (weightData.isNotEmpty()) weightData else listOf(
                    "Mon" to 0f, "Tue" to 0f, "Wed" to 0f, "Thu" to 0f,
                    "Fri" to 0f, "Sat" to 0f, "Sun" to 0f
                )
                displayData.forEach { (day, weight) ->
                    WeightDayItem(
                        day = day, 
                        weight = if (weight > 0) String.format(Locale.US, "%.1f", weight) else "—"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = weightChange,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isPositive) MintGreen else if (weightChange.startsWith("↑")) CoralPink else TextSecondary
                )
            }
        }
    }
}

@Composable
private fun WeightDayItem(day: String, weight: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = weight,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = DeepBlack
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = day,
            fontSize = 10.sp,
            color = TextSecondary
        )
    }
}

@Composable
private fun CaloriesHistoryCard(
    caloriesData: List<Pair<String, Int>>,
    avgCalories: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryOrange.copy(alpha = 0.1f)),
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
                    text = "Calorie Tracking",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepBlack
                )
                Text(
                    text = "Last 7 days",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                val displayData = if (caloriesData.isNotEmpty()) caloriesData else listOf(
                    "Mon" to 0, "Tue" to 0, "Wed" to 0, "Thu" to 0,
                    "Fri" to 0, "Sat" to 0, "Sun" to 0
                )
                displayData.forEach { (day, cal) ->
                    BarChartItem(
                        day = day,
                        value = cal / 100,
                        maxValue = 25,
                        color = PrimaryOrange,
                        displayValue = if (cal > 0) "${cal / 1000}.${(cal % 1000) / 100}K" else "0"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Average: ${NumberFormat.getNumberInstance(Locale.US).format(avgCalories)} kcal/day",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ExerciseHistoryCard(
    exerciseData: List<Pair<String, Int>>,
    totalExercise: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MintGreen.copy(alpha = 0.15f)),
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
                    text = "Exercise Log",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepBlack
                )
                Text(
                    text = "Last 7 days",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                val displayData = if (exerciseData.isNotEmpty()) exerciseData else listOf(
                    "Mon" to 0, "Tue" to 0, "Wed" to 0, "Thu" to 0,
                    "Fri" to 0, "Sat" to 0, "Sun" to 0
                )
                displayData.forEach { (day, mins) ->
                    BarChartItem(
                        day = day,
                        value = mins,
                        maxValue = 90,
                        color = MintGreen,
                        displayValue = "${mins}m"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Total: ${String.format(Locale.US, "%.2f", totalExercise / 60f)} hours this week",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun BarChartItem(
    day: String,
    value: Int,
    maxValue: Int,
    color: Color,
    displayValue: String? = null
) {
    val height = ((value.toFloat() / maxValue) * 80).coerceIn(8f, 80f)
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = displayValue ?: value.toString(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = DeepBlack
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(28.dp)
                .height(height.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = day,
            fontSize = 10.sp,
            color = TextSecondary
        )
    }
}

@Composable
private fun SleepBarItem(
    day: String,
    hours: Float,
    maxHours: Float
) {
    val height = ((hours / maxHours) * 80).coerceIn(8f, 80f)
    val color = when {
        hours >= 7.5f -> MintGreen
        hours >= 6f -> PrimaryOrange
        else -> CoralPink
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${hours}h",
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = DeepBlack
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(28.dp)
                .height(height.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = day,
            fontSize = 10.sp,
            color = TextSecondary
        )
    }
}

// ==================== EXISTING COMPONENTS ====================

@Composable
private fun HomeHeader(
    greeting: String,
    userName: String = "User",
    profilePictureBase64: String? = null,
    onMenuClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Decode profile picture from Base64
    val profileBitmap = remember(profilePictureBase64) {
        profilePictureBase64?.let {
            try {
                val decodedBytes = Base64.decode(it, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            } catch (e: Exception) {
                null
            }
        }
    }
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side - Menu and Greeting
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Menu Button - Left Side
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = WarmBeige.copy(alpha = 0.6f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Menu,
                    contentDescription = "Menu",
                    tint = DeepBlack,
                    modifier = Modifier.size(22.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = greeting,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = userName.ifEmpty { "User" },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepBlack
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "\u2B50",
                        fontSize = 14.sp
                    )
                }
            }
        }
        
        // Right side - Profile Avatar
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(WarmBeige)
        ) {
            if (profileBitmap != null) {
                Image(
                    bitmap = profileBitmap.asImageBitmap(),
                    contentDescription = "Profile",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(28.dp)
                        .align(Alignment.Center),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}
     

@Composable
private fun TabChipsRow(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        "Mood" to R.drawable.calmm,
        "Water" to R.drawable.waterr,
        "Sleep" to R.drawable.sleepingg,
        "Steps" to R.drawable.walkk,
        "Weight" to R.drawable.weight,
        "Calories" to R.drawable.calaroiess,
        "Exercise" to R.drawable.excercisee
    )

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(tabs) { (tab, icon) ->
            val isSelected = tab == selectedTab

            Surface(
                modifier = Modifier
                    .clickable { onTabSelected(tab) },
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) PrimaryOrange else WarmBeige
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = tab,
                        modifier = Modifier.size(18.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        text = tab,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        color = if (isSelected) DeepBlack else TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun MoodHistoryCard(
    moodData: List<Pair<String, String>>,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    // Reduced size to fit 7 days
    val moodIconSize = if (screenWidth < 360.dp) 36.dp else 40.dp
    val emojiSize = if (screenWidth < 360.dp) 20.dp else 24.dp

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WarmBeigeLight),
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
                    text = "Mood History",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepBlack
                )
                Text(
                    text = "Last 7 days",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mood icons for each day - responsive - show all 7 days
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val displayData = if (moodData.isNotEmpty()) moodData else listOf(
                    "Mon" to "", "Tue" to "", "Wed" to "", "Thu" to "", 
                    "Fri" to "", "Sat" to "", "Sun" to ""
                )
                displayData.forEach { (day, mood) ->
                    val (moodIcon, backgroundColor) = getMoodIconAndColor(mood)
                    MoodDayItem(
                        day = day,
                        moodIcon = moodIcon,
                        backgroundColor = backgroundColor,
                        iconSize = moodIconSize,
                        emojiSize = emojiSize
                    )
                }
            }
        }
    }
}

// Helper function to get mood icon and color
@Composable
private fun getMoodIconAndColor(mood: String): Pair<Int, Color> {
    if (mood.isBlank()) {
        return R.drawable.smile to Color(0xFFE8F5E9) // Default normal/neutral mood - light green
    }
    
    val normalizedMood = mood.lowercase().trim()
    
    return when {
       // normalizedMood.contains("happy") || normalizedMood == "😊" ->
       //     R.drawable.happy to Color(0xFFC8E6C9) // Light Green
        normalizedMood.contains("sad") || normalizedMood == "😢" -> 
            R.drawable.sad to Color(0xFFFFE0B2) // Light Orange
        normalizedMood.contains("angry") || normalizedMood == "😠" -> 
            R.drawable.angry to Color(0xFFFFCDD2) // Light Red
        normalizedMood.contains("anxious") || normalizedMood == "😰" -> 
            R.drawable.anxious to Color(0xFFBBDEFB) // Light Blue
        normalizedMood.contains("excited") || normalizedMood == "🤩" -> 
            R.drawable.exited to Color(0xFFFFF9C4) // Light Yellow (exited.png exists)
        normalizedMood.contains("tired") || normalizedMood == "😴" -> 
            R.drawable.tired to Color(0xFFE1BEE7) // Light Purple
        normalizedMood.contains("calm") || normalizedMood == "😌" -> 
            R.drawable.calm to Color(0xFFB2DFDB) // Light Teal
        normalizedMood.contains("grateful") || normalizedMood == "🌟" -> 
            R.drawable.grateful to Color(0xFFFFD54F) // Gold
        normalizedMood.contains("confused") || normalizedMood == "😕" -> 
            R.drawable.confused to Color(0xFFF8BBD0) // Light Pink
        normalizedMood.contains("normal") || normalizedMood.contains("neutral") -> 
            R.drawable.smile to Color(0xFFE8F5E9) // Light Green for normal
        else -> R.drawable.smile to Color(0xFFE8F5E9) // Default normal mood
    }
}

@Composable
private fun MoodDayItem(
    day: String,
    moodIcon: Int,
    backgroundColor: Color,
    iconSize: Dp,
    emojiSize: Dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(iconSize)
                .background(
                    color = backgroundColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = moodIcon),
                contentDescription = day,
                modifier = Modifier.size(emojiSize),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = day,
            fontSize = 12.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}
