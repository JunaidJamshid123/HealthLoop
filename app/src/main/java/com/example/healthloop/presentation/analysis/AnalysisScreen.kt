package com.example.healthloop.presentation.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.healthloop.presentation.components.LineChartView
import com.example.healthloop.presentation.components.BarChartView
import com.example.healthloop.presentation.components.PieChartView
import com.example.healthloop.presentation.analysis.HealthMetric
import com.example.healthloop.presentation.analysis.TimeRange
import com.example.healthloop.presentation.model.HealthEntryUiModel
import com.example.healthloop.presentation.model.UiState
import androidx.compose.ui.graphics.vector.ImageVector

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
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        AnalysisHeader()
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
        if (state is UiState.Success) {
            val data = (state as UiState.Success<AnalysisUiState>).data
            SummaryStatsSection(data)
            Spacer(modifier = Modifier.height(16.dp))
            ChartsSection(data.entries, data.metric)
        } else if (state is UiState.Loading) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun AnalysisHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Health Analysis",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Track your progress and trends",
            fontSize = 14.sp,
            color = Color.Black.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Time Period",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
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
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .background(if (isSelected) Color.Black else Color.Transparent)
            .border(
                width = 1.dp,
                color = Color.Black.copy(alpha = 0.3f),
                shape = RoundedCornerShape(6.dp)
            )
            .padding(vertical = 8.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color.White else Color.Black,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MetricSelector(selectedMetric: HealthMetric, onMetricSelected: (HealthMetric) -> Unit) {
    val metrics = listOf(
        HealthMetric.WATER to "Water",
        HealthMetric.SLEEP to "Sleep",
        HealthMetric.STEPS to "Steps",
        HealthMetric.WEIGHT to "Weight"
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        metrics.forEach { (metric, label) ->
            FilterChip(
                selected = selectedMetric == metric,
                onClick = { onMetricSelected(metric) },
                label = { Text(label) },
                modifier = Modifier.padding(horizontal = 4.dp)
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
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                StatCard(
                    title = "Water",
                    value = "${stats.average}",
                    unit = "glasses",
                    min = stats.min,
                    max = stats.max,
                    icon = Icons.Default.Face,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Sleep",
                    value = "${stats.average}",
                    unit = "hours",
                    min = stats.min,
                    max = stats.max,
                    icon = Icons.Default.Face,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                StatCard(
                    title = "Steps",
                    value = "${stats.average}",
                    unit = "avg",
                    min = stats.min,
                    max = stats.max,
                    icon = Icons.Default.ArrowForward,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Weight",
                    value = "${stats.average}",
                    unit = "kg",
                    min = stats.min,
                    max = stats.max,
                    icon = Icons.Default.Warning,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    unit: String,
    min: Float,
    max: Float,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 6.dp)) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.Black.copy(alpha = 0.8f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = unit,
                fontSize = 10.sp,
                color = Color.Black.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Min: $min, Max: $max",
                fontSize = 10.sp,
                color = Color.Black.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun ChartsSection(entries: List<HealthEntryUiModel>, metric: HealthMetric) {
    Column {
        Text(
            text = "Trends & Charts",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
            ChartCard(title = "${metric.name.lowercase().replaceFirstChar { it.uppercase() }} Trend (Line Chart)") {
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
                        .height(220.dp),
                    label = metric.name.lowercase().replaceFirstChar { it.uppercase() }
                )
            }
            if (entries.isNotEmpty()) {
                ChartCard(title = "Water Intake (Bar Chart)") {
                    val barData = entries.mapIndexed { idx, it -> idx.toFloat() to it.waterIntake.toFloat() }
                    BarChartView(
                        data = barData,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        label = "Water Intake"
                    )
                }
            }
            val moodMap = entries.groupingBy { it.mood }.eachCount().mapValues { it.value.toFloat() }
            if (moodMap.isNotEmpty()) {
                ChartCard(title = "Mood Distribution (Pie Chart)") {
                    PieChartView(
                        data = moodMap,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        label = "Mood"
                    )
                }
            }
        }
    }
}

@Composable
fun ChartCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            content()
        }
    }
}