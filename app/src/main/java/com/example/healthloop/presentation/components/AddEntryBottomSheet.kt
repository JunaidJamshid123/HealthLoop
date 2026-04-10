package com.example.healthloop.presentation.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.healthloop.R
import com.example.healthloop.presentation.add_entry.AddEntryViewModel
import com.example.healthloop.presentation.model.UiState
import com.example.healthloop.ui.theme.*
import com.example.healthloop.util.NotificationManager

data class MoodOption(
    val icon: Int,
    val label: String,
    val backgroundColor: Color
)

@Composable
fun AddEntryBottomSheet(
    onDismiss: () -> Unit,
    viewModel: AddEntryViewModel = hiltViewModel()
) {
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

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            val message = if (isEditing) "Entry updated successfully!" else "Entry saved successfully!"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            NotificationManager(context).showEntryAddedNotification()
            viewModel.clearSaveSuccess()
            onDismiss()
        }
    }

    val moods = listOf(
        MoodOption(R.drawable.happy, "Happy", Color(0xFFC8E6C9)),
        MoodOption(R.drawable.sad, "Sad", Color(0xFFBBDEFB)),
        MoodOption(R.drawable.angry, "Angry", Color(0xFFFFCDD2)),
        MoodOption(R.drawable.anxious, "Anxious", Color(0xFFE1BEE7)),
        MoodOption(R.drawable.exited, "Excited", Color(0xFFFFE0B2)),
        MoodOption(R.drawable.tired, "Tired", Color(0xFFD7CCC8)),
        MoodOption(R.drawable.calm, "Calm", Color(0xFFB2DFDB)),
        MoodOption(R.drawable.grateful, "Grateful", Color(0xFFFFF9C4)),
        MoodOption(R.drawable.confused, "Confused", Color(0xFFF8BBD0)),
        MoodOption(R.drawable.smile, "Content", Color(0xFFDCEDC8))
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isEditing) "Edit Entry" else "New Entry",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepBlack
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isEditing) "Update today's health data" else "Track your daily health",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = WarmBeige.copy(alpha = 0.6f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = DeepBlack,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Input fields - Basic Health
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        EntryField(
                            icon = R.drawable.waterr,
                            iconTint = null,
                            title = "Water Intake",
                            value = waterIntake,
                            onValueChange = { viewModel.updateWaterIntake(it) },
                            unit = "glasses",
                            keyboardType = KeyboardType.Number,
                            backgroundColor = SkyBlue.copy(alpha = 0.12f)
                        )

                        EntryField(
                            icon = R.drawable.sleepingg,
                            iconTint = null,
                            title = "Sleep Hours",
                            value = sleepHours,
                            onValueChange = { viewModel.updateSleepHours(it) },
                            unit = "hours",
                            keyboardType = KeyboardType.Decimal,
                            backgroundColor = SoftGreen.copy(alpha = 0.2f)
                        )

                        EntryField(
                            icon = R.drawable.walkk,
                            iconTint = null,
                            title = "Step Count",
                            value = stepCount,
                            onValueChange = { viewModel.updateStepCount(it) },
                            unit = "steps",
                            keyboardType = KeyboardType.Number,
                            backgroundColor = PrimaryOrange.copy(alpha = 0.15f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Body & Fitness Section
                    Text(
                        text = "Body & Fitness",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepBlack
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        EntryField(
                            icon = R.drawable.weight,
                            iconTint = null,
                            title = "Weight",
                            value = weight,
                            onValueChange = { viewModel.updateWeight(it) },
                            unit = "kg",
                            keyboardType = KeyboardType.Decimal,
                            backgroundColor = CoralPink.copy(alpha = 0.15f)
                        )

                        EntryField(
                            icon = R.drawable.calaroiess,
                            iconTint = null,
                            title = "Calories",
                            value = calories,
                            onValueChange = { viewModel.updateCalories(it) },
                            unit = "kcal",
                            keyboardType = KeyboardType.Number,
                            backgroundColor = PrimaryOrange.copy(alpha = 0.12f)
                        )

                        EntryField(
                            icon = R.drawable.excercisee,
                            iconTint = null,
                            title = "Exercise",
                            value = exerciseMinutes,
                            onValueChange = { viewModel.updateExerciseMinutes(it) },
                            unit = "mins",
                            keyboardType = KeyboardType.Number,
                            backgroundColor = MintGreen.copy(alpha = 0.2f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mood Selection Header
                    Text(
                        text = "How are you feeling?",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepBlack
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Mood Grid - Using Rows instead of LazyVerticalGrid for scroll compatibility
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // First row - 5 moods
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            moods.take(5).forEach { mood ->
                                MoodItem(
                                    moodOption = mood,
                                    isSelected = selectedMood == mood.label,
                                    onClick = { viewModel.updateMood(mood.label) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        // Second row - 5 moods
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            moods.drop(5).forEach { mood ->
                                MoodItem(
                                    moodOption = mood,
                                    isSelected = selectedMood == mood.label,
                                    onClick = { viewModel.updateMood(mood.label) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Save Button - Fixed at bottom
                Button(
                    onClick = { viewModel.saveEntry() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryOrange
                    ),
                    shape = RoundedCornerShape(16.dp),
                    enabled = uiState !is UiState.Loading
                ) {
                    if (uiState is UiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = DeepBlack,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (isEditing) "Update Entry" else "Save Entry",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DeepBlack
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EntryField(
    icon: Int,
    iconTint: Color?,
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    unit: String,
    keyboardType: KeyboardType,
    backgroundColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (iconTint != null) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            } else {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = title,
                    modifier = Modifier.size(24.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Title and input
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 12.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                textStyle = TextStyle(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepBlack
                ),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isEmpty()) {
                            Text(
                                text = "0",
                                color = TextSecondary.copy(alpha = 0.4f),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }

        // Unit
        Text(
            text = unit,
            fontSize = 13.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun MoodItem(
    moodOption: MoodOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .background(
                color = if (isSelected) moodOption.backgroundColor else WarmBeige.copy(alpha = 0.4f),
                shape = RoundedCornerShape(14.dp)
            )
            .then(
                if (isSelected) Modifier.border(
                    width = 2.dp,
                    color = moodOption.backgroundColor.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(14.dp)
                ) else Modifier
            )
            .padding(vertical = 10.dp, horizontal = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = if (isSelected) Color.White.copy(alpha = 0.6f) else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = moodOption.icon),
                contentDescription = moodOption.label,
                modifier = Modifier.size(26.dp),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = moodOption.label,
            fontSize = 9.sp,
            color = if (isSelected) DeepBlack else TextSecondary,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            maxLines = 1
        )
    }
}
