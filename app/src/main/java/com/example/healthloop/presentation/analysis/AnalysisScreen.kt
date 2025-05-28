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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnalysisScreen() {
    var selectedPeriod by remember { mutableStateOf("7 Days") }
    val periods = listOf("7 Days", "30 Days", "3 Months")

    // Sample data for charts - replace with actual data later
    val weeklyStats = WeeklyStats(
        averageWater = 6.8f,
        averageSleep = 7.2f,
        averageSteps = 8500,
        averageWeight = 70.2f,
        waterTrend = 12f, // +12% from last period
        sleepTrend = -5f, // -5% from last period
        stepsTrend = 8f,  // +8% from last period
        weightTrend = -2f // -2% from last period
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Header
        AnalysisHeader()

        Spacer(modifier = Modifier.height(16.dp))

        // Period Selection
        PeriodSelector(
            selectedPeriod = selectedPeriod,
            periods = periods,
            onPeriodSelected = { selectedPeriod = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Summary Stats
        SummaryStatsSection(weeklyStats)

        Spacer(modifier = Modifier.height(16.dp))

        // Charts Section
        ChartsSection(selectedPeriod)

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun AnalysisHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
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
            .background(
                if (isSelected) Color.Black else Color.Transparent
            )
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
fun SummaryStatsSection(stats: WeeklyStats) {
    Column {
        Text(
            text = "Average Summary",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StatCard(
                    title = "Water",
                    value = "${stats.averageWater}",
                    unit = "glasses",
                    trend = stats.waterTrend,
                    icon = Icons.Default.Face,
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Sleep",
                    value = "${stats.averageSleep}",
                    unit = "hours",
                    trend = stats.sleepTrend,
                    icon = Icons.Default.Face,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StatCard(
                    title = "Steps",
                    value = "${stats.averageSteps}",
                    unit = "avg",
                    trend = stats.stepsTrend,
                    icon = Icons.Default.ArrowForward,
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Weight",
                    value = "${stats.averageWeight}",
                    unit = "kg",
                    trend = stats.weightTrend,
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
    trend: Float,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val trendColor = if (trend >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
    val trendIcon = if (trend >= 0) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
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

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = trendIcon,
                    contentDescription = "Trend",
                    tint = trendColor,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = "${if (trend >= 0) "+" else ""}${trend.toInt()}%",
                    fontSize = 10.sp,
                    color = trendColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ChartsSection(selectedPeriod: String) {
    Column {
        Text(
            text = "Trends & Charts",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Water Intake Chart
            ChartCard(
                title = "Water Intake",
                subtitle = "Daily glasses consumed",
                chartType = "Line Chart",
                icon = Icons.Default.Face
            )

            // Sleep Hours Chart
            ChartCard(
                title = "Sleep Hours",
                subtitle = "Daily sleep duration",
                chartType = "Bar Chart",
                icon = Icons.Default.Face
            )

            // Steps Chart
            ChartCard(
                title = "Step Count",
                subtitle = "Daily activity level",
                chartType = "Area Chart",
                icon = Icons.Default.ArrowForward
            )

            // Weight Chart
            ChartCard(
                title = "Weight Tracking",
                subtitle = "Weight changes over time",
                chartType = "Line Chart",
                icon = Icons.Default.Warning
            )

            // Mood Chart
            ChartCard(
                title = "Mood Analysis",
                subtitle = "Emotional wellness trends",
                chartType = "Pie Chart",
                icon = Icons.Default.Face
            )
        }
    }
}

@Composable
fun ChartCard(
    title: String,
    subtitle: String,
    chartType: String,
    icon: ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.Black.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = Color.Black.copy(alpha = 0.6f)
                    )
                }
                Text(
                    text = chartType,
                    fontSize = 10.sp,
                    color = Color.Black.copy(alpha = 0.5f),
                    modifier = Modifier
                        .background(
                            Color.Black.copy(alpha = 0.1f),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            // Placeholder for actual chart - replace with real chart library
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        Color.Black.copy(alpha = 0.05f),
                        RoundedCornerShape(8.dp)
                    )
                    .border(
                        1.dp,
                        Color.Black.copy(alpha = 0.1f),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Chart",
                        tint = Color.Black.copy(alpha = 0.3f),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$chartType will appear here",
                        fontSize = 10.sp,
                        color = Color.Black.copy(alpha = 0.4f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// Data Classes
data class WeeklyStats(
    val averageWater: Float,
    val averageSleep: Float,
    val averageSteps: Int,
    val averageWeight: Float,
    val waterTrend: Float,
    val sleepTrend: Float,
    val stepsTrend: Float,
    val weightTrend: Float
)

@Preview(showBackground = true)
@Composable
fun AnalysisScreenPreview() {
    AnalysisScreen()
}