package com.example.healthloop.presentation.history

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
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen() {
    var selectedFilter by remember { mutableStateOf("All Time") }
    var showFilterMenu by remember { mutableStateOf(false) }

    val filterOptions = listOf("All Time", "This Week", "This Month", "Last 30 Days", "Last 90 Days")

    // Sample historical data - replace with actual data from database
    val historyEntries = generateSampleHistoryData()

    // Filter entries based on selected filter
    val filteredEntries = filterEntriesByPeriod(historyEntries, selectedFilter)

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

            FilterSection(
                selectedFilter = selectedFilter,
                filterOptions = filterOptions,
                showFilterMenu = showFilterMenu,
                onFilterSelected = { selectedFilter = it },
                onToggleFilterMenu = { showFilterMenu = !showFilterMenu }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Results count
            Text(
                text = "${filteredEntries.size} entries found",
                fontSize = 12.sp,
                color = Color.Black.copy(alpha = 0.6f)
            )
        }

        // History List
        if (filteredEntries.isEmpty()) {
            EmptyHistoryState()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredEntries) { entry ->
                    HistoryEntryCard(entry)
                }

                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(20.dp))
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
            text = "Health History",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "View your past health entries",
            fontSize = 14.sp,
            color = Color.Black.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSection(
    selectedFilter: String,
    filterOptions: List<String>,
    showFilterMenu: Boolean,
    onFilterSelected: (String) -> Unit,
    onToggleFilterMenu: () -> Unit
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
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Filter",
                    tint = Color.Black.copy(alpha = 0.8f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Filter by Period",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }

            ExposedDropdownMenuBox(
                expanded = showFilterMenu,
                onExpandedChange = { onToggleFilterMenu() }
            ) {
                OutlinedTextField(
                    value = selectedFilter,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = if (showFilterMenu) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Dropdown",
                            tint = Color.Black.copy(alpha = 0.6f)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black.copy(alpha = 0.3f),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                )

                ExposedDropdownMenu(
                    expanded = showFilterMenu,
                    onDismissRequest = { onToggleFilterMenu() },
                    modifier = Modifier.background(Color.White)
                ) {
                    filterOptions.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = option,
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                            },
                            onClick = {
                                onFilterSelected(option)
                                onToggleFilterMenu()
                            }
                        )
                    }
                }
            }
        }
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
                            contentDescription = "Complete",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Incomplete",
                            tint = Color(0xFFFF9800),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Health Metrics Grid
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HistoryMetricItem(
                        icon = Icons.Default.Face,
                        label = "Water",
                        value = "${entry.waterIntake}",
                        unit = "glasses",
                        modifier = Modifier.weight(1f)
                    )

                    HistoryMetricItem(
                        icon = Icons.Default.Face,
                        label = "Sleep",
                        value = "${entry.sleepHours}",
                        unit = "hours",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HistoryMetricItem(
                        icon = Icons.Default.ArrowForward,
                        label = "Steps",
                        value = "${entry.stepCount}",
                        unit = "steps",
                        modifier = Modifier.weight(1f)
                    )

                    HistoryMetricItem(
                        icon = Icons.Default.Warning,
                        label = "Weight",
                        value = "${entry.weight}",
                        unit = "kg",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Completion Status
            if (!entry.isComplete) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Incomplete entry - some data missing",
                    fontSize = 10.sp,
                    color = Color(0xFFFF9800),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun HistoryMetricItem(
    icon: ImageVector,
    label: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(
                Color.Black.copy(alpha = 0.05f),
                RoundedCornerShape(6.dp)
            )
            .padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.Black.copy(alpha = 0.6f),
            modifier = Modifier.size(14.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = "$label ($unit)",
                fontSize = 9.sp,
                color = Color.Black.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun EmptyHistoryState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "No History",
                tint = Color.Black.copy(alpha = 0.3f),
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No Health Entries Found",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
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
}

// Helper Functions
fun generateSampleHistoryData(): List<HistoryEntry> {
    val entries = mutableListOf<HistoryEntry>()
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())

    val moods = listOf("😊", "😐", "😔", "😴", "😤", "🤒")

    // Generate 30 days of sample data
    for (i in 0..29) {
        calendar.time = Date()
        calendar.add(Calendar.DAY_OF_YEAR, -i)

        val date = dateFormat.format(calendar.time)
        val dayOfWeek = dayFormat.format(calendar.time)

        entries.add(
            HistoryEntry(
                id = i.toString(),
                date = date,
                dayOfWeek = dayOfWeek,
                waterIntake = (4..10).random(),
                sleepHours = (5.5f + Math.random().toFloat() * (9.0f - 5.5f)) * (9.0f - 5.5f) + 5.5f,
                stepCount = (3000..15000).random(),
                mood = moods.random(),
                weight = (5.5f + Math.random().toFloat() * (9.0f - 5.5f)) * (75.0f - 65.0f) + 65.0f,
                isComplete = (0..10).random() > 2 // 80% complete entries
            )
        )
    }

    return entries
}

fun filterEntriesByPeriod(entries: List<HistoryEntry>, filter: String): List<HistoryEntry> {
    val calendar = Calendar.getInstance()
    val currentDate = calendar.time

    return when (filter) {
        "This Week" -> {
            calendar.add(Calendar.DAY_OF_YEAR, -7)
            entries.filter { entry ->
                // Simple filter - in real app, parse entry.date and compare
                entries.indexOf(entry) < 7
            }
        }
        "This Month" -> {
            entries.filter { entries.indexOf(it) < 30 }
        }
        "Last 30 Days" -> {
            entries.filter { entries.indexOf(it) < 30 }
        }
        "Last 90 Days" -> {
            entries.filter { entries.indexOf(it) < 90 }
        }
        else -> entries // "All Time"
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