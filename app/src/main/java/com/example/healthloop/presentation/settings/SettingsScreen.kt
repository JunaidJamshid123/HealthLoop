package com.example.healthloop.presentation.settings

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
fun SettingsScreen() {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    var googleFitSync by remember { mutableStateOf(false) }
    var selectedReminderTime by remember { mutableStateOf("9:00 AM") }
    var selectedWeightUnit by remember { mutableStateOf("kg") }
    var selectedWaterUnit by remember { mutableStateOf("glasses") }

    var showReminderTimePicker by remember { mutableStateOf(false) }
    var showWeightUnitPicker by remember { mutableStateOf(false) }
    var showWaterUnitPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Header
        SettingsHeader()

        Spacer(modifier = Modifier.height(20.dp))

        // Notifications Section
        SettingsSection(title = "Notifications") {
            SettingsToggleItem(
                icon = Icons.Default.Notifications,
                title = "Daily Reminders",
                subtitle = "Get reminded to log your health data",
                isChecked = notificationsEnabled,
                onToggle = { notificationsEnabled = it }
            )

            if (notificationsEnabled) {
                SettingsClickableItem(
                    icon = Icons.Default.Warning,
                    title = "Reminder Time",
                    subtitle = selectedReminderTime,
                    onClick = { showReminderTimePicker = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Units & Preferences Section
        SettingsSection(title = "Units & Preferences") {
            SettingsClickableItem(
                icon = Icons.Default.Menu,
                title = "Weight Unit",
                subtitle = selectedWeightUnit,
                onClick = { showWeightUnitPicker = true }
            )

            SettingsClickableItem(
                icon = Icons.Default.ShoppingCart,
                title = "Water Unit",
                subtitle = selectedWaterUnit,
                onClick = { showWaterUnitPicker = true }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sync & Backup Section
        SettingsSection(title = "Sync & Backup") {
            SettingsToggleItem(
                icon = Icons.Default.Refresh,
                title = "Google Fit Sync",
                subtitle = "Sync step count with Google Fit",
                isChecked = googleFitSync,
                onToggle = { googleFitSync = it }
            )

            SettingsClickableItem(
                icon = Icons.Default.Warning,
                title = "Backup Data",
                subtitle = "Backup your health data to cloud",
                onClick = { /* Handle backup */ }
            )

            SettingsClickableItem(
                icon = Icons.Default.Home,
                title = "Export Data",
                subtitle = "Export data as CSV or PDF",
                onClick = { /* Handle export */ }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Appearance Section
        SettingsSection(title = "Appearance") {
            SettingsToggleItem(
                icon = Icons.Default.Home,
                title = "Dark Mode",
                subtitle = "Switch to dark theme",
                isChecked = darkModeEnabled,
                onToggle = { darkModeEnabled = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Account Section
        SettingsSection(title = "Account") {
            SettingsClickableItem(
                icon = Icons.Default.Person,
                title = "Profile",
                subtitle = "Manage your profile information",
                onClick = { /* Handle profile */ }
            )

            SettingsClickableItem(
                icon = Icons.Default.Warning,
                title = "Privacy & Security",
                subtitle = "Manage your privacy settings",
                onClick = { /* Handle privacy */ }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Support Section
        SettingsSection(title = "Support") {
            SettingsClickableItem(
                icon = Icons.Default.LocationOn,
                title = "Help & FAQ",
                subtitle = "Get help and find answers",
                onClick = { /* Handle help */ }
            )

            SettingsClickableItem(
                icon = Icons.Default.DateRange,
                title = "Send Feedback",
                subtitle = "Share your thoughts with us",
                onClick = { /* Handle feedback */ }
            )

            SettingsClickableItem(
                icon = Icons.Default.Info,
                title = "About",
                subtitle = "App version and information",
                onClick = { /* Handle about */ }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Danger Zone
        SettingsSection(title = "Data Management") {
            SettingsClickableItem(
                icon = Icons.Default.Delete,
                title = "Clear All Data",
                subtitle = "Permanently delete all health data",
                onClick = { /* Handle clear data */ },
                isDestructive = true
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // App Version
        Text(
            text = "HealthLoop v1.0.0",
            fontSize = 12.sp,
            color = Color.Black.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))
    }

    // Time Picker Dialog
    if (showReminderTimePicker) {
        TimePickerDialog(
            selectedTime = selectedReminderTime,
            onTimeSelected = { selectedReminderTime = it },
            onDismiss = { showReminderTimePicker = false }
        )
    }

    // Weight Unit Picker Dialog
    if (showWeightUnitPicker) {
        UnitPickerDialog(
            title = "Weight Unit",
            options = listOf("kg", "lbs"),
            selectedOption = selectedWeightUnit,
            onOptionSelected = { selectedWeightUnit = it },
            onDismiss = { showWeightUnitPicker = false }
        )
    }

    // Water Unit Picker Dialog
    if (showWaterUnitPicker) {
        UnitPickerDialog(
            title = "Water Unit",
            options = listOf("glasses", "ml", "liters"),
            selectedOption = selectedWaterUnit,
            onOptionSelected = { selectedWaterUnit = it },
            onDismiss = { showWaterUnitPicker = false }
        )
    }
}

@Composable
fun SettingsHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Customize your health tracking experience",
            fontSize = 14.sp,
            color = Color.Black.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier.padding(4.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(!isChecked) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color.Black.copy(alpha = 0.8f),
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
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

        Switch(
            checked = isChecked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color.Black,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.Black.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
fun SettingsClickableItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isDestructive) Color(0xFFF44336) else Color.Black.copy(alpha = 0.8f),
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDestructive) Color(0xFFF44336) else Color.Black
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = if (isDestructive) Color(0xFFF44336).copy(alpha = 0.7f) else Color.Black.copy(alpha = 0.6f)
            )
        }

        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Navigate",
            tint = Color.Black.copy(alpha = 0.4f),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun TimePickerDialog(
    selectedTime: String,
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val timeOptions = listOf(
        "6:00 AM", "7:00 AM", "8:00 AM", "9:00 AM", "10:00 AM",
        "11:00 AM", "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM",
        "4:00 PM", "5:00 PM", "6:00 PM", "7:00 PM", "8:00 PM",
        "9:00 PM", "10:00 PM"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Select Reminder Time",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        },
        text = {
            Column {
                timeOptions.forEach { time ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onTimeSelected(time)
                                onDismiss()
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedTime == time,
                            onClick = {
                                onTimeSelected(time)
                                onDismiss()
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color.Black,
                                unselectedColor = Color.Black.copy(alpha = 0.3f)
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = time,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Cancel",
                    color = Color.Black
                )
            }
        },
        containerColor = Color.White
    )
}

@Composable
fun UnitPickerDialog(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        },
        text = {
            Column {
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onOptionSelected(option)
                                onDismiss()
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedOption == option,
                            onClick = {
                                onOptionSelected(option)
                                onDismiss()
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color.Black,
                                unselectedColor = Color.Black.copy(alpha = 0.3f)
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = option,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Cancel",
                    color = Color.Black
                )
            }
        },
        containerColor = Color.White
    )
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}