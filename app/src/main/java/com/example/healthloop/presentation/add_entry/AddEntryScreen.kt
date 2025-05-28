package com.example.healthloop.presentation.add_entry

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEntryScreen() {
    var waterIntake by remember { mutableStateOf("") }
    var sleepHours by remember { mutableStateOf("") }
    var stepCount by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }

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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "Add Health Entry",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        Text(
            text = "Track your daily health metrics",
            fontSize = 14.sp,
            color = Color.Black.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Water Intake
        HealthInputCard(
            title = "Water Intake",
            icon = Icons.Default.Face,
            value = waterIntake,
            onValueChange = { waterIntake = it },
            placeholder = "Enter glasses (e.g., 8)",
            keyboardType = KeyboardType.Number,
            unit = "glasses"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Sleep Hours
        HealthInputCard(
            title = "Sleep Hours",
            icon = Icons.Default.Face,
            value = sleepHours,
            onValueChange = { sleepHours = it },
            placeholder = "Enter hours (e.g., 7.5)",
            keyboardType = KeyboardType.Decimal,
            unit = "hours"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Step Count
        HealthInputCard(
            title = "Step Count",
            icon = Icons.Default.ArrowForward,
            value = stepCount,
            onValueChange = { stepCount = it },
            placeholder = "Enter steps (e.g., 10000)",
            keyboardType = KeyboardType.Number,
            unit = "steps"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Mood Selection
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
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "Mood",
                        tint = Color.Black.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "How are you feeling?",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }

                // Mood Grid - Fixed implementation
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // First row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (i in 0..2) {
                            if (i < moods.size) {
                                val mood = moods[i]
                                MoodItem(
                                    emoji = mood.first,
                                    label = mood.second,
                                    isSelected = selectedMood == mood.first,
                                    onClick = {
                                        selectedMood = if (selectedMood == mood.first) "" else mood.first
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // Second row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (i in 3..5) {
                            if (i < moods.size) {
                                val mood = moods[i]
                                MoodItem(
                                    emoji = mood.first,
                                    label = mood.second,
                                    isSelected = selectedMood == mood.first,
                                    onClick = {
                                        selectedMood = if (selectedMood == mood.first) "" else mood.first
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Weight
        HealthInputCard(
            title = "Weight",
            icon = Icons.Default.Warning,
            value = weight,
            onValueChange = { weight = it },
            placeholder = "Enter weight (e.g., 70.5)",
            keyboardType = KeyboardType.Decimal,
            unit = "kg"
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Save Button
        Button(
            onClick = {
                // Handle save logic here
                // You can add validation and save the data
            },
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
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.Black.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        text = placeholder,
                        color = Color.Black.copy(alpha = 0.5f),
                        fontSize = 13.sp
                    )
                },
                trailingIcon = {
                    Text(
                        text = unit,
                        fontSize = 12.sp,
                        color = Color.Black.copy(alpha = 0.6f),
                        modifier = Modifier.padding(end = 6.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black.copy(alpha = 0.3f),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
            )
        }
    }
}

@Composable
fun MoodItem(
    emoji: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .background(
                if (isSelected) Color.Black.copy(alpha = 0.1f)
                else Color.Transparent
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color.Black else Color.Black.copy(alpha = 0.2f),
                shape = RoundedCornerShape(6.dp)
            )
            .padding(8.dp)
    ) {
        Text(
            text = emoji,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color.Black.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddEntryScreenPreview() {
    AddEntryScreen()
}