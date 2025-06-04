package com.example.healthloop.presentation.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthloop.presentation.model.UiState
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(viewModel: HistoryViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedDateRange by viewModel.selectedDateRange.collectAsState()
    
    val filterOptions = listOf(
        DateRange.ALL_TIME to "All Time", 
        DateRange.THIS_WEEK to "This Week", 
        DateRange.THIS_MONTH to "This Month", 
        DateRange.LAST_30_DAYS to "Last 30 Days", 
        DateRange.LAST_90_DAYS to "Last 90 Days"
    )
    
    var showFilterMenu by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header and Filter Section
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            HistoryHeader()

            Spacer(modifier = Modifier.height(16.dp))

            // Filter dropdown
            Box {
                OutlinedButton(
                    onClick = { showFilterMenu = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = filterOptions.find { it.first == selectedDateRange }?.second ?: "All Time",
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Filter",
                            tint = Color.Black
                        )
                    }
                }

                DropdownMenu(
                    expanded = showFilterMenu,
                    onDismissRequest = { showFilterMenu = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    filterOptions.forEach { (range, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                viewModel.setDateRange(range)
                                showFilterMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Results count and state handling
            when (uiState) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Success -> {
                    val data = (uiState as UiState.Success<HistoryUiState>).data
                    Text(
                        text = "${data.entries.size} entries found",
                        fontSize = 12.sp,
                        color = Color.Black.copy(alpha = 0.6f)
                    )
                    
                    // History List
                    if (data.entries.isEmpty()) {
                        EmptyHistoryState()
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(data.entries) { entry ->
                                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                                
                                HistoryEntryCard(
                                    HistoryEntry(
                                        id = entry.id.toString(),
                                        date = dateFormat.format(entry.date),
                                        dayOfWeek = dayFormat.format(entry.date),
                                        waterIntake = entry.waterIntake,
                                        sleepHours = entry.sleepHours,
                                        stepCount = entry.stepCount,
                                        mood = entry.mood,
                                        weight = entry.weight,
                                        isComplete = true
                                    )
                                )
                            }

                            // Bottom spacing
                            item {
                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    Text(
                        text = (uiState as UiState.Error).message,
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "History",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "Track your progress over time",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun EmptyHistoryState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = "No History",
            modifier = Modifier.size(64.dp),
            tint = Color.Gray.copy(alpha = 0.5f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No entries found",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Start tracking your health by adding your first entry in the Add Entry tab.",
            fontSize = 14.sp,
            color = Color.Black.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun HistoryEntryCard(entry: HistoryEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Date and Mood Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = entry.date,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Text(
                        text = entry.dayOfWeek,
                        fontSize = 12.sp,
                        color = Color.Black.copy(alpha = 0.6f)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = entry.mood,
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (entry.isComplete) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Complete Entry",
                            tint = Color.Green,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Health Metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Water
                HistoryMetricItem(
                    icon = Icons.Default.WaterDrop,
                    value = "${entry.waterIntake}",
                    unit = "glasses",
                    modifier = Modifier.weight(1f)
                )

                // Sleep
                HistoryMetricItem(
                    icon = Icons.Default.Bedtime,
                    value = String.format("%.1f", entry.sleepHours),
                    unit = "hours",
                    modifier = Modifier.weight(1f)
                )

                // Steps
                HistoryMetricItem(
                    icon = Icons.Default.DirectionsWalk,
                    value = "${entry.stepCount}",
                    unit = "steps",
                    modifier = Modifier.weight(1f)
                )

                // Weight
                HistoryMetricItem(
                    icon = Icons.Default.FitnessCenter,
                    value = String.format("%.1f", entry.weight),
                    unit = "kg",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun HistoryMetricItem(
    icon: ImageVector,
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Black.copy(alpha = 0.7f),
            modifier = Modifier.size(16.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        
        Text(
            text = unit,
            fontSize = 10.sp,
            color = Color.Gray,
        )
    }
}

// Data Classes
data class HistoryEntry(
    val id: String,
    val date: String,
    val dayOfWeek: String,
    val waterIntake: Int,
    val sleepHours: Float,
    val stepCount: Int,
    val mood: String,
    val weight: Float,
    val isComplete: Boolean
)

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    HistoryScreen()
}