package com.example.healthloop.presentation.add_entry

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthloop.presentation.model.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEntryScreen(viewModel: AddEntryViewModel = viewModel()) {
    val waterIntake by viewModel.waterIntake.collectAsState()
    val sleepHours by viewModel.sleepHours.collectAsState()
    val stepCount by viewModel.stepCount.collectAsState()
    val selectedMood by viewModel.selectedMood.collectAsState()
    val weight by viewModel.weight.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current

    // Show toast when save is successful
    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            Toast.makeText(context, "Entry saved successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    // Show error toast if there's an error
    LaunchedEffect(uiState) {
        if (uiState is UiState.Error) {
            Toast.makeText(context, (uiState as UiState.Error).message, Toast.LENGTH_SHORT).show()
        }
    }

    val moods = listOf(
        "😊" to "Happy",
        "😐" to "Neutral",
        "😔" to "Sad",
        "😴" to "Tired",
        "😤" to "Stressed",
        "🤒" to "Sick"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
        Text(
            text = "Add Entry",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Water Intake Input
        HealthInputCard(
            title = "Water Intake",
            icon = Icons.Default.WaterDrop,
            value = waterIntake,
            onValueChange = { viewModel.updateWaterIntake(it) },
            placeholder = "Enter glasses",
            keyboardType = KeyboardType.Number,
            unit = "glasses"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sleep Hours Input
        HealthInputCard(
            title = "Sleep Hours",
            icon = Icons.Default.Bedtime,
            value = sleepHours,
            onValueChange = { viewModel.updateSleepHours(it) },
            placeholder = "Enter hours",
            keyboardType = KeyboardType.Decimal,
            unit = "hours"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Step Count Input
        HealthInputCard(
            title = "Step Count",
            icon = Icons.Default.DirectionsWalk,
            value = stepCount,
            onValueChange = { viewModel.updateStepCount(it) },
            placeholder = "Enter steps",
            keyboardType = KeyboardType.Number,
            unit = "steps"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mood Selection
        Text(
            text = "How are you feeling today?",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Mood Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.height(160.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(moods.size) { index ->
                val (emoji, description) = moods[index]
                MoodItem(
                    emoji = emoji,
                    description = description,
                    isSelected = selectedMood == emoji,
                    onSelect = { viewModel.updateMood(emoji) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Weight Input
        HealthInputCard(
            title = "Weight",
            icon = Icons.Default.FitnessCenter,
            value = weight,
            onValueChange = { viewModel.updateWeight(it) },
            placeholder = "Enter weight",
            keyboardType = KeyboardType.Decimal,
            unit = "kg"
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Save Button
        Button(
            onClick = { viewModel.saveEntry() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = "Save Entry",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthInputCard(
    title: String,
    icon: ImageVector,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType,
    unit: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = {
                        Text(
                            text = placeholder,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                    )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = unit,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.width(50.dp)
            )
        }
    }
}

@Composable
fun MoodItem(
    emoji: String,
    description: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color.Black else Color.Gray.copy(alpha = 0.3f),
                shape = RoundedCornerShape(10.dp)
            )
            .background(if (isSelected) Color.Black.copy(alpha = 0.05f) else Color.White)
            .clickable { onSelect() }
            .padding(vertical = 12.dp, horizontal = 8.dp)
    ) {
        Text(
            text = emoji,
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = description,
            fontSize = 12.sp,
            color = if (isSelected) Color.Black else Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddEntryScreenPreview() {
    AddEntryScreen()
}