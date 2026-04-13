package com.example.healthloop.presentation.profile

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.healthloop.R
import com.example.healthloop.ui.theme.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    var showEditGoalsDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showImagePickerDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    
    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { 
            viewModel.onEvent(ProfileEvent.UpdateProfilePicture(it, context))
        }
    }
    
    // Show snackbar on save success
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            snackbarHostState.showSnackbar("Saved successfully!")
            viewModel.onEvent(ProfileEvent.ClearSaveSuccess)
        }
    }
    
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onEvent(ProfileEvent.ClearError)
        }
    }
    
    // Format member since date
    val memberSince = remember(uiState.userProfile.memberSince) {
        val sdf = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        sdf.format(Date(uiState.userProfile.memberSince))
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            WarmBeigeLight,
                            SurfaceLight,
                            WarmBeige.copy(alpha = 0.3f)
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = PrimaryOrange
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                        .padding(top = 16.dp, bottom = 100.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Profile",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepBlack
                        )
                        
                        // Settings icon
                        Surface(
                            shape = CircleShape,
                            color = PrimaryOrange.copy(alpha = 0.15f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            IconButton(onClick = { }) {
                                Icon(
                                    imageVector = Icons.Outlined.Tune,
                                    contentDescription = "Settings",
                                    tint = PrimaryOrange,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Profile Card
                    ProfileCard(
                        name = uiState.userProfile.name,
                        email = uiState.userProfile.email,
                        age = uiState.userProfile.age,
                        memberSince = memberSince,
                        profilePictureBitmap = uiState.profilePictureBitmap,
                        isPro = uiState.userProfile.isPro,
                        onEditClick = { showEditProfileDialog = true },
                        onImageClick = { showImagePickerDialog = true }
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Stats Overview
                    StatsOverviewSection(
                        totalDays = uiState.userStats.totalDays,
                        currentStreak = uiState.userStats.currentStreak,
                        bestStreak = uiState.userStats.bestStreak,
                        healthScore = uiState.userStats.healthScore
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Daily Goals Section
                    GoalsSection(
                        waterGoal = uiState.userGoals.waterGoal,
                        sleepGoal = uiState.userGoals.sleepGoal,
                        stepsGoal = uiState.userGoals.stepsGoal,
                        caloriesGoal = uiState.userGoals.caloriesGoal,
                        exerciseGoal = uiState.userGoals.exerciseGoal,
                        weightGoal = uiState.userGoals.weightGoal,
                        todayEntry = uiState.todayEntry,
                        onEditClick = { showEditGoalsDialog = true }
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Body Stats
                    BodyStatsSection(
                        weight = uiState.userProfile.weight,
                        height = uiState.userProfile.height,
                        bmi = uiState.userProfile.bmi,
                        onEditClick = { showEditProfileDialog = true }
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Settings Section
                    SettingsSection(
                        notificationsEnabled = uiState.notificationsEnabled,
                        onNotificationsChange = { viewModel.onEvent(ProfileEvent.UpdateSetting("notifications", it)) },
                        darkModeEnabled = uiState.darkModeEnabled,
                        onDarkModeChange = { viewModel.onEvent(ProfileEvent.UpdateSetting("dark_mode", it)) },
                        reminderEnabled = uiState.reminderEnabled,
                        onReminderChange = { viewModel.onEvent(ProfileEvent.UpdateSetting("reminders", it)) },
                        weeklyReportEnabled = uiState.weeklyReportEnabled,
                        onWeeklyReportChange = { viewModel.onEvent(ProfileEvent.UpdateSetting("weekly_reports", it)) },
                        soundEnabled = uiState.soundEnabled,
                        onSoundChange = { viewModel.onEvent(ProfileEvent.UpdateSetting("sound", it)) }
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Quick Actions
                    QuickActionsSection(
                        onExportClick = { showExportDialog = true }
                    )
                }
            }
            
            // Loading overlay when saving
            if (uiState.isSaving) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable(enabled = false) { },
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.padding(24.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = PrimaryOrange,
                                strokeWidth = 2.dp
                            )
                            Text("Saving...", fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
    
    // Edit Goals Dialog
    if (showEditGoalsDialog) {
        EditGoalsDialog(
            waterGoal = uiState.userGoals.waterGoal,
            sleepGoal = uiState.userGoals.sleepGoal,
            stepsGoal = uiState.userGoals.stepsGoal,
            caloriesGoal = uiState.userGoals.caloriesGoal,
            exerciseGoal = uiState.userGoals.exerciseGoal,
            weightGoal = uiState.userGoals.weightGoal,
            onDismiss = { showEditGoalsDialog = false },
            onSave = { water, sleep, steps, calories, exercise, weight ->
                viewModel.onEvent(ProfileEvent.UpdateGoals(
                    waterGoal = water,
                    sleepGoal = sleep,
                    stepsGoal = steps,
                    caloriesGoal = calories,
                    exerciseGoal = exercise,
                    weightGoal = weight
                ))
                showEditGoalsDialog = false
            }
        )
    }
    
    // Edit Profile Dialog
    if (showEditProfileDialog) {
        EditProfileDialog(
            name = uiState.userProfile.name,
            email = uiState.userProfile.email,
            age = uiState.userProfile.age,
            weight = uiState.userProfile.weight,
            height = uiState.userProfile.height,
            profilePictureBitmap = uiState.profilePictureBitmap,
            onDismiss = { showEditProfileDialog = false },
            onSave = { name, email, age, weight, height ->
                viewModel.onEvent(ProfileEvent.UpdateProfile(
                    name = name,
                    email = email,
                    age = age,
                    weight = weight,
                    height = height
                ))
                showEditProfileDialog = false
            },
            onChangePhoto = { imagePickerLauncher.launch("image/*") }
        )
    }
    
    // Image Picker Dialog
    if (showImagePickerDialog) {
        ImagePickerOptionsDialog(
            onDismiss = { showImagePickerDialog = false },
            onGalleryClick = {
                imagePickerLauncher.launch("image/*")
                showImagePickerDialog = false
            },
            onRemoveClick = {
                viewModel.onEvent(ProfileEvent.RemoveProfilePicture)
                showImagePickerDialog = false
            }
        )
    }

    // Export Format Dialog
    if (showExportDialog) {
        ExportFormatDialog(
            onDismiss = { showExportDialog = false },
            onExport = { format ->
                viewModel.onEvent(ProfileEvent.ExportData(context, format))
                showExportDialog = false
            }
        )
    }
}

// ==================== EXPORT FORMAT DIALOG ====================

@Composable
private fun ExportFormatDialog(
    onDismiss: () -> Unit,
    onExport: (String) -> Unit
) {
    var selectedFormat by remember { mutableStateOf("csv") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.FileDownload,
                    contentDescription = "Export",
                    tint = PrimaryOrange,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    "Export Health Data",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = DeepBlack
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Choose export format:",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                ExportFormatOption(
                    icon = Icons.Outlined.TableChart,
                    title = "CSV (.csv)",
                    subtitle = "Spreadsheet-compatible format",
                    selected = selectedFormat == "csv",
                    onClick = { selectedFormat = "csv" }
                )
                ExportFormatOption(
                    icon = Icons.Outlined.PictureAsPdf,
                    title = "PDF (.pdf)",
                    subtitle = "Formatted document with tables",
                    selected = selectedFormat == "pdf",
                    onClick = { selectedFormat = "pdf" }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onExport(selectedFormat) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Outlined.FileDownload,
                    contentDescription = "Export",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Export", fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
private fun ExportFormatOption(
    icon: ImageVector,
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        if (selected) PrimaryOrange else Color(0xFFE8E8E8),
        label = "border"
    )
    val bgColor by animateColorAsState(
        if (selected) PrimaryOrange.copy(alpha = 0.08f) else Color.White,
        label = "bg"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(1.5.dp, borderColor, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (selected) PrimaryOrange.copy(alpha = 0.15f) else WarmBeige,
                        RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (selected) PrimaryOrange else DeepBlack,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = DeepBlack)
                Text(subtitle, fontSize = 11.sp, color = TextSecondary)
            }
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = PrimaryOrange,
                    unselectedColor = Color(0xFFCCCCCC)
                )
            )
        }
    }
}

// ==================== PROFILE CARD ====================
@Composable
private fun ProfileCard(
    name: String,
    email: String,
    age: Int,
    memberSince: String,
    profilePictureBitmap: Bitmap?,
    isPro: Boolean,
    onEditClick: () -> Unit,
    onImageClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = PrimaryOrange.copy(alpha = 0.1f),
                spotColor = PrimaryOrange.copy(alpha = 0.15f)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Decorative gradient background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                PrimaryOrange.copy(alpha = 0.3f),
                                SoftGreen.copy(alpha = 0.3f)
                            )
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar with camera overlay
                    Box(
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .border(3.dp, Color.White, CircleShape)
                                .clickable { onImageClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            if (profilePictureBitmap != null) {
                                Image(
                                    bitmap = profilePictureBitmap.asImageBitmap(),
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(PrimaryOrange, SoftGreen)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = name.take(2).uppercase(),
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepBlack
                                    )
                                }
                            }
                        }
                        
                        // Camera icon overlay
                        Surface(
                            shape = CircleShape,
                            color = PrimaryOrange,
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { onImageClick() }
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CameraAlt,
                                    contentDescription = "Change Photo",
                                    tint = DeepBlack,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepBlack
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Email,
                                contentDescription = "Email",
                                tint = TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = email,
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (isPro) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = SoftGreen.copy(alpha = 0.4f)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Star,
                                            contentDescription = "Pro Member",
                                            tint = SoftGreenDark,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Pro Member",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = SoftGreenDark
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "Since $memberSince",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                    
                    // Edit Button
                    Surface(
                        shape = CircleShape,
                        color = PrimaryOrange.copy(alpha = 0.2f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        IconButton(onClick = onEditClick) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "Edit Profile",
                                tint = PrimaryOrangeDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==================== IMAGE PICKER DIALOG ====================
@Composable
private fun ImagePickerOptionsDialog(
    onDismiss: () -> Unit,
    onGalleryClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = "Profile Photo",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Choose from Gallery
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onGalleryClick() },
                    shape = RoundedCornerShape(12.dp),
                    color = SoftGreen.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PhotoLibrary,
                            contentDescription = "Choose from gallery",
                            tint = SoftGreenDark,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Choose from Gallery",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = DeepBlack
                        )
                    }
                }
                
                // Remove Photo
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onRemoveClick() },
                    shape = RoundedCornerShape(12.dp),
                    color = CoralPink.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Remove photo",
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Remove Photo",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFD32F2F)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary, fontWeight = FontWeight.Medium)
            }
        }
    )
}

// ==================== STATS OVERVIEW ====================
@Composable
private fun StatsOverviewSection(
    totalDays: Int,
    currentStreak: Int,
    bestStreak: Int,
    healthScore: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatMiniCard(
            value = totalDays.toString(),
            label = "Total Days",
            icon = Icons.Outlined.CalendarMonth,
            color = SkyBlue,
            modifier = Modifier.weight(1f)
        )
        StatMiniCard(
            value = currentStreak.toString(),
            label = "Streak",
            icon = Icons.Default.LocalFireDepartment,
            color = PrimaryOrange,
            modifier = Modifier.weight(1f)
        )
        StatMiniCard(
            value = bestStreak.toString(),
            label = "Best",
            icon = Icons.Outlined.EmojiEvents,
            color = SoftGreen,
            modifier = Modifier.weight(1f)
        )
        StatMiniCard(
            value = healthScore.toString(),
            label = "Score",
            icon = Icons.Outlined.Favorite,
            color = CoralPink,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatMiniCard(
    value: String,
    label: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = color.copy(alpha = 0.1f),
                spotColor = color.copy(alpha = 0.15f)
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DeepBlack
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = TextSecondary
            )
        }
    }
}

// ==================== GOALS SECTION ====================
@Composable
private fun GoalsSection(
    waterGoal: Int,
    sleepGoal: Float,
    stepsGoal: Int,
    caloriesGoal: Int,
    exerciseGoal: Int,
    weightGoal: Float,
    todayEntry: com.example.healthloop.domain.model.HealthEntry?,
    onEditClick: () -> Unit
) {
    // Compute actual progress from today's health entry
    val waterProgress = if (waterGoal > 0 && todayEntry != null) (todayEntry.waterIntake.toFloat() / waterGoal).coerceIn(0f, 1f) else 0f
    val sleepProgress = if (sleepGoal > 0 && todayEntry != null) (todayEntry.sleepHours / sleepGoal).coerceIn(0f, 1f) else 0f
    val stepsProgress = if (stepsGoal > 0 && todayEntry != null) (todayEntry.stepCount.toFloat() / stepsGoal).coerceIn(0f, 1f) else 0f
    val caloriesProgress = if (caloriesGoal > 0 && todayEntry != null) (todayEntry.calories.toFloat() / caloriesGoal).coerceIn(0f, 1f) else 0f
    val exerciseProgress = if (exerciseGoal > 0 && todayEntry != null) (todayEntry.exerciseMinutes.toFloat() / exerciseGoal).coerceIn(0f, 1f) else 0f
    val weightProgress = if (weightGoal > 0 && todayEntry != null && todayEntry.weight > 0) {
        val diff = kotlin.math.abs(todayEntry.weight - weightGoal)
        (1f - (diff / weightGoal)).coerceIn(0f, 1f)
    } else 0f
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = PrimaryOrange.copy(alpha = 0.08f),
                spotColor = PrimaryOrange.copy(alpha = 0.1f)
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Flag,
                        contentDescription = "Daily Goals",
                        tint = PrimaryOrange,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Daily Goals",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepBlack
                    )
                }
                TextButton(onClick = onEditClick) {
                    Text(
                        text = "Edit",
                        fontSize = 13.sp,
                        color = PrimaryOrange,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Goals Grid - 2 columns
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GoalItem(
                        icon = R.drawable.waterr,
                        label = "Water",
                        value = if (todayEntry != null) "${todayEntry.waterIntake}/$waterGoal glasses" else "$waterGoal glasses",
                        progress = waterProgress,
                        color = SkyBlue
                    )
                    GoalItem(
                        icon = R.drawable.walkk,
                        label = "Steps",
                        value = if (todayEntry != null) "${todayEntry.stepCount}/${stepsGoal / 1000}K" else "${stepsGoal / 1000}K steps",
                        progress = stepsProgress,
                        color = MintGreen
                    )
                    GoalItem(
                        icon = R.drawable.excercisee,
                        label = "Exercise",
                        value = if (todayEntry != null) "${todayEntry.exerciseMinutes}/$exerciseGoal min" else "$exerciseGoal min",
                        progress = exerciseProgress,
                        color = CoralPink
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GoalItem(
                        icon = R.drawable.sleepingg,
                        label = "Sleep",
                        value = if (todayEntry != null) "${todayEntry.sleepHours.toInt()}/${sleepGoal.toInt()} hrs" else "${sleepGoal.toInt()} hours",
                        progress = sleepProgress,
                        color = SoftGreen
                    )
                    GoalItem(
                        icon = R.drawable.calaroiess,
                        label = "Calories",
                        value = if (todayEntry != null) "${todayEntry.calories}/$caloriesGoal" else "$caloriesGoal kcal",
                        progress = caloriesProgress,
                        color = PrimaryOrange
                    )
                    GoalItem(
                        icon = R.drawable.weightt,
                        label = "Weight",
                        value = if (todayEntry != null && todayEntry.weight > 0) "${todayEntry.weight.toInt()}/${weightGoal.toInt()} kg" else "${weightGoal.toInt()} kg",
                        progress = weightProgress,
                        color = SkyBlue
                    )
                }
            }
        }
    }
}

@Composable
private fun GoalItem(
    icon: Int,
    label: String,
    value: String,
    progress: Float,
    color: Color
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000),
        label = "goalProgress"
    )
    
    val progressColor = when {
        progress >= 0.8f -> MintGreen
        progress >= 0.5f -> PrimaryOrange
        progress > 0f -> CoralPink
        else -> color
    }
    
    Card(
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = label,
                modifier = Modifier.size(28.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    if (progress > 0f) {
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = progressColor
                        )
                    }
                }
                Text(
                    text = value,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepBlack
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(color.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .fillMaxHeight()
                            .background(
                                if (progress > 0f) progressColor else color,
                                RoundedCornerShape(2.dp)
                            )
                    )
                }
            }
        }
    }
}

// ==================== BODY STATS ====================
@Composable
private fun BodyStatsSection(
    weight: Float,
    height: Int,
    bmi: Float,
    onEditClick: () -> Unit = {}
) {
    val bmiCategory = when {
        bmi == 0f -> "Not Set" to TextSecondary
        bmi < 18.5f -> "Underweight" to CoralPink
        bmi < 25f -> "Normal" to MintGreen
        bmi < 30f -> "Overweight" to PrimaryOrange
        else -> "Obese" to Color(0xFFE57373)
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEditClick() }
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = SkyBlue.copy(alpha = 0.08f),
                spotColor = SkyBlue.copy(alpha = 0.1f)
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.MonitorWeight,
                    contentDescription = "Body Stats",
                    tint = SkyBlue,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Body Stats",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepBlack
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit Body Stats",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BodyStatItem(
                    value = if (weight > 0) "${weight.toInt()}" else "0",
                    unit = "kg",
                    label = "Weight"
                )
                
                // Divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(50.dp)
                        .background(BorderColor)
                )
                
                BodyStatItem(
                    value = if (height > 0) "$height" else "0",
                    unit = "cm",
                    label = "Height"
                )
                
                // Divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(50.dp)
                        .background(BorderColor)
                )
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (bmi > 0) String.format("%.1f", bmi) else "0.0",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepBlack
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = bmiCategory.second.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = bmiCategory.first,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = bmiCategory.second,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = "BMI",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun BodyStatItem(
    value: String,
    unit: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DeepBlack
            )
            Text(
                text = unit,
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 2.dp, start = 2.dp)
            )
        }
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextSecondary
        )
    }
}

// ==================== SETTINGS SECTION ====================
@Composable
private fun SettingsSection(
    notificationsEnabled: Boolean,
    onNotificationsChange: (Boolean) -> Unit,
    darkModeEnabled: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    reminderEnabled: Boolean,
    onReminderChange: (Boolean) -> Unit,
    weeklyReportEnabled: Boolean,
    onWeeklyReportChange: (Boolean) -> Unit,
    soundEnabled: Boolean,
    onSoundChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Gray.copy(alpha = 0.08f),
                spotColor = Color.Gray.copy(alpha = 0.1f)
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Settings",
                    tint = TextSecondary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Settings",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepBlack
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            SettingToggleItem(
                icon = Icons.Outlined.Notifications,
                title = "Push Notifications",
                subtitle = "Get daily reminders",
                isEnabled = notificationsEnabled,
                onToggle = onNotificationsChange
            )
            
            SettingsDivider()
            
            SettingToggleItem(
                icon = Icons.Outlined.DarkMode,
                title = "Dark Mode",
                subtitle = "Switch to dark theme",
                isEnabled = darkModeEnabled,
                onToggle = onDarkModeChange
            )
            
            SettingsDivider()
            
            SettingToggleItem(
                icon = Icons.Outlined.Alarm,
                title = "Daily Reminders",
                subtitle = "Log your health data",
                isEnabled = reminderEnabled,
                onToggle = onReminderChange
            )
            
            SettingsDivider()
            
            SettingToggleItem(
                icon = Icons.Outlined.Assessment,
                title = "Weekly Reports",
                subtitle = "Get weekly summaries",
                isEnabled = weeklyReportEnabled,
                onToggle = onWeeklyReportChange
            )
            
            SettingsDivider()
            
            SettingToggleItem(
                icon = Icons.Outlined.VolumeUp,
                title = "Sound Effects",
                subtitle = "Play sounds on actions",
                isEnabled = soundEnabled,
                onToggle = onSoundChange
            )
        }
    }
}

@Composable
private fun SettingToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(WarmBeige, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = DeepBlack,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = DeepBlack
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextSecondary
            )
        }
        
        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = SoftGreen,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = WarmBeigeDark
            )
        )
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 52.dp),
        thickness = 0.5.dp,
        color = BorderColor
    )
}

// ==================== QUICK ACTIONS ====================
@Composable
private fun QuickActionsSection(onExportClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = MintGreen.copy(alpha = 0.08f),
                spotColor = MintGreen.copy(alpha = 0.1f)
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.TouchApp,
                    contentDescription = "Quick Actions",
                    tint = MintGreen,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Quick Actions",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepBlack
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            QuickActionItem(
                icon = Icons.Outlined.FileDownload,
                title = "Export Data",
                subtitle = "Download your health data",
                onClick = onExportClick
            )
            
            SettingsDivider()
            
            QuickActionItem(
                icon = Icons.Outlined.Share,
                title = "Share Progress",
                subtitle = "Share with friends & family"
            )
            
            SettingsDivider()
            
            QuickActionItem(
                icon = Icons.Outlined.Backup,
                title = "Backup & Sync",
                subtitle = "Sync across devices"
            )
            
            SettingsDivider()
            
            QuickActionItem(
                icon = Icons.Outlined.HealthAndSafety,
                title = "Connect Health Apps",
                subtitle = "Google Fit, Apple Health"
            )
        }
    }
}

@Composable
private fun QuickActionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(WarmBeige, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = DeepBlack,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = DeepBlack
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextSecondary
            )
        }
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Navigate",
            tint = TextSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}



// ==================== EDIT GOALS DIALOG ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditGoalsDialog(
    waterGoal: Int,
    sleepGoal: Float,
    stepsGoal: Int,
    caloriesGoal: Int,
    exerciseGoal: Int,
    weightGoal: Float,
    onDismiss: () -> Unit,
    onSave: (Int, Float, Int, Int, Int, Float) -> Unit
) {
    var editWater by remember { mutableStateOf(waterGoal.toString()) }
    var editSleep by remember { mutableStateOf(sleepGoal.toString()) }
    var editSteps by remember { mutableStateOf(stepsGoal.toString()) }
    var editCalories by remember { mutableStateOf(caloriesGoal.toString()) }
    var editExercise by remember { mutableStateOf(exerciseGoal.toString()) }
    var editWeight by remember { mutableStateOf(weightGoal.toString()) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardSurface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Flag,
                    contentDescription = "Edit Daily Goals",
                    tint = PrimaryOrange
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Edit Daily Goals",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GoalTextField(
                    value = editWater,
                    onValueChange = { editWater = it },
                    label = "Water (glasses)",
                    icon = R.drawable.waterr
                )
                GoalTextField(
                    value = editSleep,
                    onValueChange = { editSleep = it },
                    label = "Sleep (hours)",
                    icon = R.drawable.sleepingg
                )
                GoalTextField(
                    value = editSteps,
                    onValueChange = { editSteps = it },
                    label = "Steps",
                    icon = R.drawable.walkk
                )
                GoalTextField(
                    value = editCalories,
                    onValueChange = { editCalories = it },
                    label = "Calories (kcal)",
                    icon = R.drawable.calaroiess
                )
                GoalTextField(
                    value = editExercise,
                    onValueChange = { editExercise = it },
                    label = "Exercise (minutes)",
                    icon = R.drawable.excercisee
                )
                GoalTextField(
                    value = editWeight,
                    onValueChange = { editWeight = it },
                    label = "Target Weight (kg)",
                    icon = R.drawable.weightt
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        editWater.toIntOrNull() ?: waterGoal,
                        editSleep.toFloatOrNull() ?: sleepGoal,
                        editSteps.toIntOrNull() ?: stepsGoal,
                        editCalories.toIntOrNull() ?: caloriesGoal,
                        editExercise.toIntOrNull() ?: exerciseGoal,
                        editWeight.toFloatOrNull() ?: weightGoal
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save", color = DeepBlack, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
private fun GoalTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: Int
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp) },
        leadingIcon = {
            Image(
                painter = painterResource(id = icon),
                contentDescription = label,
                modifier = Modifier.size(24.dp)
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryOrange,
            unfocusedBorderColor = BorderColor,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        ),
        singleLine = true
    )
}

// ==================== EDIT PROFILE DIALOG ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileDialog(
    name: String,
    email: String,
    age: Int,
    weight: Float,
    height: Int,
    profilePictureBitmap: Bitmap?,
    onDismiss: () -> Unit,
    onSave: (String, String, Int, Float, Int) -> Unit,
    onChangePhoto: () -> Unit
) {
    var editName by remember { mutableStateOf(name) }
    var editEmail by remember { mutableStateOf(email) }
    var editAge by remember { mutableStateOf(if (age > 0) age.toString() else "") }
    var editWeight by remember { mutableStateOf(if (weight > 0) weight.toString() else "") }
    var editHeight by remember { mutableStateOf(if (height > 0) height.toString() else "") }
    
    // Validation errors
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var ageError by remember { mutableStateOf<String?>(null) }
    var weightError by remember { mutableStateOf<String?>(null) }
    var heightError by remember { mutableStateOf<String?>(null) }
    
    fun validate(): Boolean {
        var valid = true
        nameError = if (editName.isBlank()) { valid = false; "Name is required" } else null
        emailError = if (editEmail.isBlank()) { valid = false; "Email is required" } 
            else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(editEmail).matches()) { valid = false; "Invalid email" } 
            else null
        val ageVal = editAge.toIntOrNull()
        ageError = if (ageVal == null || ageVal < 1 || ageVal > 150) { valid = false; "Age must be 1-150" } else null
        val weightVal = editWeight.toFloatOrNull()
        weightError = if (weightVal == null || weightVal < 20 || weightVal > 500) { valid = false; "Weight must be 20-500 kg" } else null
        val heightVal = editHeight.toIntOrNull()
        heightError = if (heightVal == null || heightVal < 50 || heightVal > 300) { valid = false; "Height must be 50-300 cm" } else null
        return valid
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Edit Profile",
                    tint = PrimaryOrange
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Edit Profile",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile photo section
                Box(
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .border(2.dp, PrimaryOrange, CircleShape)
                            .clickable { onChangePhoto() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (profilePictureBitmap != null) {
                            Image(
                                bitmap = profilePictureBitmap.asImageBitmap(),
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(PrimaryOrange, SoftGreen)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = name.take(2).uppercase(),
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepBlack
                                )
                            }
                        }
                    }
                    
                    Surface(
                        shape = CircleShape,
                        color = PrimaryOrange,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { onChangePhoto() }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CameraAlt,
                                contentDescription = "Change Photo",
                                tint = DeepBlack,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                
                TextButton(onClick = onChangePhoto) {
                    Text(
                        text = "Change Photo",
                        color = PrimaryOrange,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                ProfileTextField(
                    value = editName,
                    onValueChange = { editName = it; nameError = null },
                    label = "Full Name",
                    icon = Icons.Outlined.Person,
                    errorText = nameError
                )
                ProfileTextField(
                    value = editEmail,
                    onValueChange = { editEmail = it; emailError = null },
                    label = "Email",
                    icon = Icons.Outlined.Email,
                    errorText = emailError
                )
                ProfileTextField(
                    value = editAge,
                    onValueChange = { editAge = it; ageError = null },
                    label = "Age",
                    icon = Icons.Outlined.Cake,
                    keyboardType = KeyboardType.Number,
                    errorText = ageError
                )
                ProfileTextField(
                    value = editWeight,
                    onValueChange = { editWeight = it; weightError = null },
                    label = "Weight (kg)",
                    icon = Icons.Outlined.MonitorWeight,
                    keyboardType = KeyboardType.Decimal,
                    errorText = weightError
                )
                ProfileTextField(
                    value = editHeight,
                    onValueChange = { editHeight = it; heightError = null },
                    label = "Height (cm)",
                    icon = Icons.Outlined.Height,
                    keyboardType = KeyboardType.Number,
                    errorText = heightError
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (validate()) {
                        onSave(
                            editName,
                            editEmail,
                            editAge.toIntOrNull() ?: age,
                            editWeight.toFloatOrNull() ?: weight,
                            editHeight.toIntOrNull() ?: height
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Changes", color = DeepBlack, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary, fontWeight = FontWeight.Medium)
            }
        }
    )
}

@Composable
private fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    errorText: String? = null
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, fontSize = 12.sp) },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (errorText != null) Color(0xFFD32F2F) else TextSecondary,
                    modifier = Modifier.size(22.dp)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (errorText != null) Color(0xFFD32F2F) else SkyBlue,
                unfocusedBorderColor = if (errorText != null) Color(0xFFD32F2F) else BorderColor,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            isError = errorText != null
        )
        if (errorText != null) {
            Text(
                text = errorText,
                color = Color(0xFFD32F2F),
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 16.dp, top = 2.dp)
            )
        }
    }
}
