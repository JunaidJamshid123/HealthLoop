package com.junaidjamshid.healthloop.presentation.add_entry

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.junaidjamshid.healthloop.presentation.model.UiState
import com.junaidjamshid.healthloop.util.NotificationManager
import androidx.compose.material3.MaterialTheme
import com.junaidjamshid.healthloop.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEntryScreen(viewModel: AddEntryViewModel = hiltViewModel()) {
    val waterIntake by viewModel.waterIntake.collectAsState()
    val sleepHours by viewModel.sleepHours.collectAsState()
    val stepCount by viewModel.stepCount.collectAsState()
    val selectedMood by viewModel.selectedMood.collectAsState()
    val weight by viewModel.weight.collectAsState()
    val calories by viewModel.calories.collectAsState()
    val exerciseMinutes by viewModel.exerciseMinutes.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()

    val context = LocalContext.current

    // Show toast and notification when save is successful
    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            val message = if (isEditing) "Entry updated successfully!" else "Entry saved successfully!"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            NotificationManager(context).showEntryAddedNotification()
            viewModel.clearSaveSuccess()
        }
    }

    // Show error toast if there's an error
    LaunchedEffect(uiState) {
        if (uiState is UiState.Error) {
            Toast.makeText(context, (uiState as UiState.Error).message, Toast.LENGTH_SHORT).show()
        }
    }

    val moods = listOf(
        "\uD83D\uDE0A" to "Happy",
        "\uD83D\uDE22" to "Sad",
        "\uD83D\uDE20" to "Angry",
        "\uD83D\uDE30" to "Anxious",
        "\uD83E\uDD29" to "Excited",
        "\uD83D\uDE34" to "Tired",
        "\uD83D\uDE0C" to "Calm",
        "\uD83C\uDF1F" to "Grateful"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 100.dp),
    ) {
        // Header
        Text(
            text = if (isEditing) "Edit Entry" else "New Entry",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Text(
            text = if (isEditing) "Update today's health data" else "Track your daily health",
            fontSize = 14.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Health Metrics Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Water Intake
                CompactHealthInput(
                    icon = Icons.Default.WaterDrop,
                    iconTint = SkyBlue,
                    title = "Water Intake",
                    value = waterIntake,
                    onValueChange = { viewModel.updateWaterIntake(it) },
                    unit = "glasses",
                    keyboardType = KeyboardType.Number
                )
                
                HorizontalDivider(color = BorderColor.copy(alpha = 0.5f))
                
                // Sleep Hours
                CompactHealthInput(
                    icon = Icons.Default.Bedtime,
                    iconTint = PrimaryOrange,
                    title = "Sleep Hours",
                    value = sleepHours,
                    onValueChange = { viewModel.updateSleepHours(it) },
                    unit = "hours",
                    keyboardType = KeyboardType.Decimal
                )
                
                HorizontalDivider(color = BorderColor.copy(alpha = 0.5f))
                
                // Step Count
                CompactHealthInput(
                    icon = Icons.Default.DirectionsWalk,
                    iconTint = SoftGreen,
                    title = "Step Count",
                    value = stepCount,
                    onValueChange = { viewModel.updateStepCount(it) },
                    unit = "steps",
                    keyboardType = KeyboardType.Number
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Mood Selection Section
        Text(
            text = "How are you feeling?",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Mood Grid - Using regular Rows instead of LazyVerticalGrid
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // First row of moods
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                moods.take(4).forEach { (emoji, description) ->
                    CompactMoodItem(
                        emoji = emoji,
                        description = description,
                        isSelected = selectedMood == emoji,
                        onSelect = { viewModel.updateMood(emoji) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            // Second row of moods
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                moods.drop(4).forEach { (emoji, description) ->
                    CompactMoodItem(
                        emoji = emoji,
                        description = description,
                        isSelected = selectedMood == emoji,
                        onSelect = { viewModel.updateMood(emoji) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Body & Fitness Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Weight
                CompactHealthInput(
                    icon = Icons.Default.MonitorWeight,
                    iconTint = CoralPink,
                    title = "Weight",
                    value = weight,
                    onValueChange = { viewModel.updateWeight(it) },
                    unit = "kg",
                    keyboardType = KeyboardType.Decimal
                )
                
                HorizontalDivider(color = BorderColor.copy(alpha = 0.5f))
                
                // Calories
                CompactHealthInput(
                    icon = Icons.Default.LocalFireDepartment,
                    iconTint = PrimaryOrange,
                    title = "Calories",
                    value = calories,
                    onValueChange = { viewModel.updateCalories(it) },
                    unit = "kcal",
                    keyboardType = KeyboardType.Number
                )
                
                HorizontalDivider(color = BorderColor.copy(alpha = 0.5f))
                
                // Exercise Minutes
                CompactHealthInput(
                    icon = Icons.Default.FitnessCenter,
                    iconTint = MintGreen,
                    title = "Exercise",
                    value = exerciseMinutes,
                    onValueChange = { viewModel.updateExerciseMinutes(it) },
                    unit = "mins",
                    keyboardType = KeyboardType.Number
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Save Button
        Button(
            onClick = { viewModel.saveEntry() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryOrange
            ),
            shape = RoundedCornerShape(16.dp),
            enabled = uiState !is UiState.Loading
        ) {
            if (uiState is UiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = DeepBlack
                )
            } else {
                Text(
                    text = if (isEditing) "Update Entry" else "Save Entry",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepBlack
                )
            }
        }
    }
}

@Composable
fun CompactHealthInput(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    unit: String,
    keyboardType: KeyboardType
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon with colored background
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Title
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        
        // Input field
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("0", color = TextSecondary) },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.width(100.dp),
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Medium
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryOrange,
                unfocusedBorderColor = BorderColor,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(10.dp),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Unit
        Text(
            text = unit,
            fontSize = 13.sp,
            color = TextSecondary,
            modifier = Modifier.width(50.dp)
        )
    }
}

@Composable
fun CompactMoodItem(
    emoji: String,
    description: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onSelect),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryOrange.copy(alpha = 0.2f) else CardSurface
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, PrimaryOrange) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            Text(
                text = emoji,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 10.sp,
                color = if (isSelected) DeepBlack else TextSecondary,
                textAlign = TextAlign.Center,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddEntryScreenPreview() {
    AddEntryScreen()
}
