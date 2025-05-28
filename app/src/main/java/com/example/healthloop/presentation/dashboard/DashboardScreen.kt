package com.example.healthloop.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen() {
    // Sample data - replace with actual data from your repository/database
    val currentDate = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault()).format(Date())
    val todayData = TodayHealthData(
        waterIntake = 6,
        targetWater = 8,
        sleepHours = 7.5f,
        targetSleep = 8f,
        stepCount = 8500,
        targetSteps = 10000,
        mood = "😊",
        weight = 70.5f
    )

    val recentEntries = listOf(
        HealthEntry("Yesterday", "😐", 7, 7.0f, 9200, 69.8f),
        HealthEntry("2 days ago", "😊", 8, 8.5f, 12000, 70.2f),
        HealthEntry("3 days ago", "😔", 5, 6.0f, 6500, 70.0f)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Header Section
        DashboardHeader(currentDate)

        Spacer(modifier = Modifier.height(20.dp))

        // Today's Summary
        TodaySummaryCard(todayData)

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Stats Grid
        QuickStatsGrid(todayData)

        Spacer(modifier = Modifier.height(16.dp))

        // Recent Entries
        RecentEntriesSection(recentEntries)

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun DashboardHeader(currentDate: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Dashboard",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = currentDate,
            fontSize = 14.sp,
            color = Color.Black.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TodaySummaryCard(data: TodayHealthData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Today",
                    tint = Color.Black.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Today's Summary",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }

            // Mood and Weight Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = data.mood,
                        fontSize = 32.sp
                    )
                    Text(
                        text = "Mood",
                        fontSize = 12.sp,
                        color = Color.Black.copy(alpha = 0.6f)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${data.weight} kg",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Text(
                        text = "Weight",
                        fontSize = 12.sp,
                        color = Color.Black.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun QuickStatsGrid(data: TodayHealthData) {
    Column {
        Text(
            text = "Today's Progress",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Water and Sleep Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                ProgressCard(
                    title = "Water",
                    current = data.waterIntake,
                    target = data.targetWater,
                    unit = "glasses",
                    icon = Icons.Default.Face,
                    modifier = Modifier.weight(1f)
                )

                ProgressCard(
                    title = "Sleep",
                    current = data.sleepHours.toInt(),
                    target = data.targetSleep.toInt(),
                    unit = "hours",
                    icon = Icons.Default.Face,
                    modifier = Modifier.weight(1f)
                )
            }

            // Steps (Full Width)
            ProgressCard(
                title = "Steps",
                current = data.stepCount,
                target = data.targetSteps,
                unit = "steps",
                icon = Icons.Default.ArrowForward,
                modifier = Modifier.fillMaxWidth(),
                isLarge = true
            )
        }
    }
}

@Composable
fun ProgressCard(
    title: String,
    current: Int,
    target: Int,
    unit: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    isLarge: Boolean = false
) {
    val progress = (current.toFloat() / target.toFloat()).coerceIn(0f, 1f)
    val progressPercentage = (progress * 100).toInt()

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
                modifier = Modifier.padding(bottom = 8.dp)
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

            if (isLarge) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "$current",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "of $target $unit",
                            fontSize = 10.sp,
                            color = Color.Black.copy(alpha = 0.6f)
                        )
                    }

                    Text(
                        text = "$progressPercentage%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
            } else {
                Text(
                    text = "$current",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "of $target $unit",
                    fontSize = 10.sp,
                    color = Color.Black.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.Black.copy(alpha = 0.1f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.Black)
                )
            }
        }
    }
}

@Composable
fun RecentEntriesSection(entries: List<HealthEntry>) {
    Column {
        Text(
            text = "Recent Entries",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            entries.forEach { entry ->
                RecentEntryCard(entry)
            }
        }
    }
}

@Composable
fun RecentEntryCard(entry: HealthEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = entry.date,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = "${entry.water} glasses • ${entry.sleep}h sleep • ${entry.steps} steps",
                    fontSize = 10.sp,
                    color = Color.Black.copy(alpha = 0.6f)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.mood,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${entry.weight}kg",
                    fontSize = 12.sp,
                    color = Color.Black.copy(alpha = 0.8f)
                )
            }
        }
    }
}

// Data Classes
data class TodayHealthData(
    val waterIntake: Int,
    val targetWater: Int,
    val sleepHours: Float,
    val targetSleep: Float,
    val stepCount: Int,
    val targetSteps: Int,
    val mood: String,
    val weight: Float
)

data class HealthEntry(
    val date: String,
    val mood: String,
    val water: Int,
    val sleep: Float,
    val steps: Int,
    val weight: Float
)

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    DashboardScreen()
}