package com.example.healthloop.presentation.analysis

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.healthloop.R
import com.example.healthloop.presentation.components.LineChartView
import com.example.healthloop.presentation.components.BarChartView
import com.example.healthloop.presentation.components.PieChartView
import com.example.healthloop.presentation.analysis.HealthMetric
import com.example.healthloop.presentation.analysis.TimeRange
import com.example.healthloop.presentation.analysis.TrendDirection
import com.example.healthloop.presentation.model.HealthEntryUiModel
import com.example.healthloop.presentation.model.UiState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.MaterialTheme
import com.example.healthloop.ui.theme.*

@Composable
fun AnalysisScreen(
    viewModel: AnalysisViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val selectedMetric by viewModel.selectedMetric.collectAsState()
    val periods = listOf("7 Days" to TimeRange.WEEK, "30 Days" to TimeRange.MONTH)
    var selectedPeriod by remember { mutableStateOf(periods[0]) }

    LaunchedEffect(selectedPeriod) {
        viewModel.setTimeRange(selectedPeriod.second)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 100.dp)
    ) {
        AnalysisHeader()
        Spacer(modifier = Modifier.height(16.dp))
        
        when (val currentState = state) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryOrange)
                }
            }
            is UiState.Error -> {
                ErrorCard(message = currentState.message)
            }
            is UiState.Success -> {
                val data = currentState.data
                
                // Overview Stats Card
                OverviewStatsCard(
                    totalDays = data.totalDaysLogged,
                    entriesCount = data.entries.size,
                    timeRange = selectedPeriod.first
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                PeriodSelector(
                    selectedPeriod = selectedPeriod.first,
                    periods = periods.map { it.first },
                    onPeriodSelected = { period ->
                        selectedPeriod = periods.first { it.first == period }
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                MetricSelector(selectedMetric = selectedMetric, onMetricSelected = viewModel::setMetric)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Dynamic Summary Stats
                SummaryStatsSection(data)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Progress vs Goals
                if (data.entries.isNotEmpty()) {
                    GoalsProgressSection(data)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Charts
                ChartsSection(data.entries, data.metric)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Mood Distribution
                if (data.moodDistribution.isNotEmpty()) {
                    MoodAnalysisSection(data.moodDistribution)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun AnalysisHeader() {
    Column {
        Text(
            text = "Health Analysis",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = DeepBlack
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Track your progress and trends",
            fontSize = 14.sp,
            color = TextSecondary
        )
    }
}

@Composable
fun OverviewStatsCard(
    totalDays: Int,
    entriesCount: Int,
    timeRange: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PrimaryOrange.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OverviewStatItem(
                value = totalDays.toString(),
                label = "Total Days",
                icon = R.drawable.history
            )
            OverviewStatItem(
                value = entriesCount.toString(),
                label = "Entries ($timeRange)",
                icon = R.drawable.analysis
            )
            OverviewStatItem(
                value = if (entriesCount > 0) "${(entriesCount * 100 / 7).coerceAtMost(100)}%" else "0%",
                label = "Consistency",
                icon = R.drawable.heartbeat
            )
        }
    }
}

@Composable
private fun OverviewStatItem(
    value: String,
    label: String,
    icon: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 20.sp,
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

@Composable
fun ErrorCard(message: String) {
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
                text = "Failed to load data",
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

@Composable
fun PeriodSelector(
    selectedPeriod: String,
    periods: List<String>,
    onPeriodSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Time Period",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = DeepBlack,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                periods.forEach { period ->
                    PeriodChip(
                        text = period,
                        isSelected = selectedPeriod == period,
                        onClick = { onPeriodSelected(period) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun PeriodChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .background(if (isSelected) PrimaryOrange else WarmBeige.copy(alpha = 0.5f))
            .padding(vertical = 12.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isSelected) DeepBlack else TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MetricSelector(selectedMetric: HealthMetric, onMetricSelected: (HealthMetric) -> Unit) {
    val metrics = listOf(
        Triple(HealthMetric.WATER, "Water", R.drawable.water),
        Triple(HealthMetric.SLEEP, "Sleep", R.drawable.sleeping),
        Triple(HealthMetric.STEPS, "Steps", R.drawable.walk),
        Triple(HealthMetric.WEIGHT, "Weight", R.drawable.weight)
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        metrics.forEach { (metric, label, icon) ->
            MetricChip(
                icon = icon,
                label = label,
                isSelected = selectedMetric == metric,
                onClick = { onMetricSelected(metric) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MetricChip(
    icon: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryOrange.copy(alpha = 0.2f) else CardSurface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = label,
                modifier = Modifier.size(20.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) DeepBlack else TextSecondary
            )
        }
    }
}

@Composable
fun SummaryStatsSection(stats: AnalysisUiState) {
    Column {
        Text(
            text = "Average Summary",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = DeepBlack,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                StatCard(
                    title = "Water",
                    value = formatValue(stats.waterStats.average, "glasses"),
                    unit = "avg/day",
                    min = stats.waterStats.min,
                    max = stats.waterStats.max,
                    icon = R.drawable.water,
                    trend = stats.waterTrend,
                    color = SkyBlue,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Sleep",
                    value = formatSleepValue(stats.sleepStats.average),
                    unit = "avg/night",
                    min = stats.sleepStats.min,
                    max = stats.sleepStats.max,
                    icon = R.drawable.sleeping,
                    trend = stats.sleepTrend,
                    color = SoftGreen,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                StatCard(
                    title = "Steps",
                    value = formatStepsValue(stats.stepsStats.average),
                    unit = "avg/day",
                    min = stats.stepsStats.min,
                    max = stats.stepsStats.max,
                    icon = R.drawable.walk,
                    trend = stats.stepsTrend,
                    color = PrimaryOrange,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Weight",
                    value = if (stats.weightStats.average > 0) String.format("%.1f", stats.weightStats.average) else "--",
                    unit = "kg",
                    min = stats.weightStats.min,
                    max = stats.weightStats.max,
                    icon = R.drawable.weight,
                    trend = stats.weightTrend,
                    color = CoralPink,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

private fun formatValue(value: Float, unit: String): String {
    return if (value > 0) String.format("%.1f", value) else "--"
}

private fun formatSleepValue(hours: Float): String {
    if (hours <= 0) return "--"
    val wholeHours = hours.toInt()
    val minutes = ((hours - wholeHours) * 60).toInt()
    return if (minutes > 0) "${wholeHours}h ${minutes}m" else "${wholeHours}h"
}

private fun formatStepsValue(steps: Float): String {
    return if (steps > 0) String.format("%,.0f", steps) else "--"
}

@Composable
fun StatCard(
    title: String,
    value: String,
    unit: String,
    min: Float,
    max: Float,
    icon: Int,
    trend: TrendDirection,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = title,
                        modifier = Modifier.size(18.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = DeepBlack
                    )
                }
                TrendIndicator(trend = trend)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DeepBlack
            )
            Text(
                text = unit,
                fontSize = 11.sp,
                color = TextSecondary
            )
            if (min > 0 || max > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Range: ${formatRange(min)} - ${formatRange(max)}",
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

private fun formatRange(value: Float): String {
    return if (value >= 1000) String.format("%,.0f", value)
    else if (value == value.toInt().toFloat()) value.toInt().toString()
    else String.format("%.1f", value)
}

@Composable
private fun TrendIndicator(trend: TrendDirection) {
    val (icon, color) = when (trend) {
        TrendDirection.UP -> Icons.Default.TrendingUp to MintGreen
        TrendDirection.DOWN -> Icons.Default.TrendingDown to CoralPink
        TrendDirection.STABLE -> Icons.Default.Remove to TextSecondary
    }
    Icon(
        imageVector = icon,
        contentDescription = "Trend",
        tint = color,
        modifier = Modifier.size(16.dp)
    )
}

@Composable
fun GoalsProgressSection(data: AnalysisUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Goals Progress",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DeepBlack,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            GoalProgressItem(
                label = "Water",
                current = data.waterStats.average,
                goal = data.userGoals.waterGoal.toFloat(),
                unit = "glasses",
                color = SkyBlue
            )
            Spacer(modifier = Modifier.height(12.dp))
            GoalProgressItem(
                label = "Sleep",
                current = data.sleepStats.average,
                goal = data.userGoals.sleepGoal,
                unit = "hours",
                color = SoftGreen
            )
            Spacer(modifier = Modifier.height(12.dp))
            GoalProgressItem(
                label = "Steps",
                current = data.stepsStats.average,
                goal = data.userGoals.stepsGoal.toFloat(),
                unit = "steps",
                color = PrimaryOrange
            )
            Spacer(modifier = Modifier.height(12.dp))
            GoalProgressItem(
                label = "Exercise",
                current = data.exerciseStats.average,
                goal = data.userGoals.exerciseGoal.toFloat(),
                unit = "min",
                color = MintGreen
            )
        }
    }
}

@Composable
private fun GoalProgressItem(
    label: String,
    current: Float,
    goal: Float,
    unit: String,
    color: Color
) {
    val progress = if (goal > 0) (current / goal).coerceIn(0f, 1f) else 0f
    val percentage = (progress * 100).toInt()
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = DeepBlack
            )
            Text(
                text = "${formatRange(current)} / ${formatRange(goal)} $unit ($percentage%)",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}

@Composable
fun MoodAnalysisSection(moodDistribution: Map<String, Int>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = WarmBeigeLight),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Mood Analysis",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DeepBlack,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            val total = moodDistribution.values.sum()
            val sortedMoods = moodDistribution.entries.sortedByDescending { it.value }
            
            sortedMoods.take(5).forEach { (mood, count) ->
                val percentage = if (total > 0) (count * 100 / total) else 0
                MoodDistributionItem(
                    mood = mood,
                    count = count,
                    percentage = percentage
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            if (sortedMoods.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Most frequent mood: ${sortedMoods.first().key} ${getMoodEmoji(sortedMoods.first().key)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = DeepBlack,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun MoodDistributionItem(
    mood: String,
    count: Int,
    percentage: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${getMoodEmoji(mood)} $mood",
            fontSize = 13.sp,
            color = DeepBlack,
            modifier = Modifier.width(100.dp)
        )
        LinearProgressIndicator(
            progress = { percentage / 100f },
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = getMoodColor(mood),
            trackColor = getMoodColor(mood).copy(alpha = 0.2f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$count ($percentage%)",
            fontSize = 11.sp,
            color = TextSecondary,
            modifier = Modifier.width(60.dp),
            textAlign = TextAlign.End
        )
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

private fun getMoodColor(mood: String): Color {
    return when (mood.lowercase()) {
        "happy" -> Color(0xFF81C784)
        "sad" -> Color(0xFF64B5F6)
        "angry" -> Color(0xFFE57373)
        "anxious" -> Color(0xFFBA68C8)
        "excited" -> Color(0xFFFFB74D)
        "tired" -> Color(0xFFA1887F)
        "calm" -> Color(0xFF4DB6AC)
        "grateful" -> Color(0xFFFFD54F)
        "confused" -> Color(0xFFF06292)
        else -> Color(0xFF90A4AE)
    }
}

@Composable
fun ChartsSection(entries: List<HealthEntryUiModel>, metric: HealthMetric) {
    if (entries.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            shape = RoundedCornerShape(16.dp)
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
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No data available",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepBlack
                )
                Text(
                    text = "Start logging your health data to see trends",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
        return
    }
    
    Column {
        Text(
            text = "Trends & Charts",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = DeepBlack,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ChartCard(title = "${metric.name.lowercase().replaceFirstChar { it.uppercase() }} Trend") {
                val chartData = when (metric) {
                    HealthMetric.WATER -> entries.mapIndexed { idx, it -> idx.toFloat() to it.waterIntake.toFloat() }
                    HealthMetric.SLEEP -> entries.mapIndexed { idx, it -> idx.toFloat() to it.sleepHours }
                    HealthMetric.STEPS -> entries.mapIndexed { idx, it -> idx.toFloat() to it.stepCount.toFloat() }
                    HealthMetric.WEIGHT -> entries.mapIndexed { idx, it -> idx.toFloat() to it.weight }
                }
                LineChartView(
                    data = chartData,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    label = metric.name.lowercase().replaceFirstChar { it.uppercase() }
                )
            }
            
            ChartCard(title = "Water Intake Distribution") {
                val barData = entries.mapIndexed { idx, it -> idx.toFloat() to it.waterIntake.toFloat() }
                BarChartView(
                    data = barData,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    label = "Water Intake"
                )
            }
        }
    }
}

@Composable
fun ChartCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = DeepBlack,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            content()
        }
    }
}