package com.example.healthloop.presentation.dashboard

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.healthloop.presentation.model.HealthEntryUiModel
import com.example.healthloop.presentation.model.TodayHealthDataUiModel
import com.example.healthloop.presentation.model.UiState
import java.text.SimpleDateFormat
import java.util.*
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.PlaceholderHighlight
import androidx.navigation.NavController
import com.google.accompanist.placeholder.material.shimmer

// Remove the old Material 2 imports that are causing issues
// import androidx.compose.material.DismissDirection
// import androidx.compose.material.DismissValue
// import androidx.compose.material.SwipeToDismiss
// import androidx.compose.material.rememberDismissState
// import androidx.compose.material.rememberScaffoldState
// import androidx.compose.material.SnackbarHostState

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    navController: NavController? = null // Pass NavController from MainScreenWithBottomNav
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentDate = remember { java.text.SimpleDateFormat("EEEE, MMM dd", Locale.getDefault()).format(Date()) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            DashboardHeader(currentDate)
            Spacer(modifier = Modifier.height(20.dp))
            when (uiState) {
                is UiState.Loading -> {
                    ShimmerDashboard()
                }
                is UiState.Error -> {
                    Text(
                        text = (uiState as UiState.Error).message,
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp)
                    )
                }
                is UiState.Success -> {
                    val data = (uiState as UiState.Success<DashboardUiState>).data
                    if (data.today == null) {
                        Text(
                            text = "No health data recorded for today.",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                            color = Color.Gray
                        )
                    } else {
                        TodaySummaryCard(data.today)
                        Spacer(modifier = Modifier.height(16.dp))
                        QuickStatsGrid(data.today)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    if (data.recent.isNotEmpty()) {
                        RecentEntriesSection(data.recent)
                    } else if (data.today != null) {
                        Text(
                            text = "No recent health entries found.",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                            color = Color.Gray
                        )
                    }
                }
            }
        }
        // FAB for quick add entry
        FloatingActionButton(
            onClick = {
                navController?.navigate("add_entry")
            },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Entry")
        }
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
fun TodaySummaryCard(data: TodayHealthDataUiModel) {
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
fun QuickStatsGrid(data: TodayHealthDataUiModel) {
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
                    icon = Icons.Default.WaterDrop,
                    modifier = Modifier.weight(1f)
                )

                ProgressCard(
                    title = "Sleep",
                    current = data.sleepHours.toInt(),
                    target = data.targetSleep.toInt(),
                    unit = "hours",
                    icon = Icons.Default.Bedtime,
                    modifier = Modifier.weight(1f)
                )
            }

            // Steps Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                ProgressCard(
                    title = "Steps",
                    current = data.stepCount,
                    target = data.targetSteps,
                    unit = "steps",
                    icon = Icons.Default.DirectionsWalk,
                    modifier = Modifier.weight(1f)
                )

                // Empty spacer to balance the row
                Spacer(modifier = Modifier.weight(1f))
            }
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
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$current / $target $unit",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun RecentEntriesSection(entries: List<HealthEntryUiModel>) {
    Column {
        Text(
            text = "Recent Entries",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (entries.isEmpty()) {
            Text(
                text = "No recent entries.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                entries.forEach { entry ->
                    RecentEntryItem(entry = entry)
                }
            }
        }
    }
}

@Composable
fun RecentEntryItem(entry: HealthEntryUiModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(entry.date),
                fontSize = 12.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Better layout for recent entry data
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Water: ${entry.waterIntake} glasses",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "Sleep: ${entry.sleepHours} hours",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Steps: ${entry.stepCount}",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "Mood: ${entry.mood}",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Weight: ${entry.weight} kg",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun ShimmerDashboard() {
    Column {
        // Shimmer TodaySummaryCard
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .placeholder(
                    visible = true,
                    highlight = PlaceholderHighlight.shimmer()
                ),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            shape = RoundedCornerShape(12.dp)
        ) {}
        Spacer(modifier = Modifier.height(16.dp))
        // Shimmer QuickStatsGrid
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            repeat(2) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .placeholder(
                            visible = true,
                            highlight = PlaceholderHighlight.shimmer()
                        ),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {}
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Shimmer RecentEntriesSection
        repeat(2) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(vertical = 4.dp)
                    .placeholder(
                        visible = true,
                        highlight = PlaceholderHighlight.shimmer()
                    ),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                shape = RoundedCornerShape(8.dp)
            ) {}
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    // Create mock data for preview
    val mockTodayData = TodayHealthDataUiModel(
        waterIntake = 6,
        targetWater = 8,
        sleepHours = 7.5f,
        targetSleep = 8f,
        stepCount = 8500,
        targetSteps = 10000,
        mood = "😊",
        weight = 75.5f
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        DashboardHeader("Monday, Jun 10")
        Spacer(modifier = Modifier.height(20.dp))
        TodaySummaryCard(mockTodayData)
        Spacer(modifier = Modifier.height(16.dp))
        QuickStatsGrid(mockTodayData)
    }
}