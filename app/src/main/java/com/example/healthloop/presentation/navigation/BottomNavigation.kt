package com.example.healthloop.presentation.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.healthloop.presentation.dashboard.DashboardScreen
import com.example.healthloop.presentation.history.HistoryScreen
import com.example.healthloop.presentation.insights.InsightsScreen
import com.example.healthloop.presentation.profile.ProfileScreen
import com.example.healthloop.presentation.assistant.AIAssistantScreen
import com.example.healthloop.presentation.components.AddEntryBottomSheet
import com.example.healthloop.R
import com.example.healthloop.ui.theme.*

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val drawableResId: Int
) {
    object Dashboard : BottomNavItem("dashboard", "Home", R.drawable.home)
    object History : BottomNavItem("history", "History", R.drawable.history)
    object Analysis : BottomNavItem("analysis", "Insights", R.drawable.analysis)
    object Assistant : BottomNavItem("assistant", "AI Help", R.drawable.bot)
    object Profile : BottomNavItem("profile", "Profile", R.drawable.user)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenWithBottomNav(
    darkTheme: Boolean,
    onToggleDarkTheme: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val haptic = LocalHapticFeedback.current
    var showAddEntryDialog by remember { mutableStateOf(false) }

    val items = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.History,
        BottomNavItem.Analysis,
        BottomNavItem.Assistant,
        BottomNavItem.Profile
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceLight),
            containerColor = SurfaceLight,
            bottomBar = {
                BottomNavigationBar(
                    items = items,
                    currentRoute = currentRoute,
                    onItemSelected = { item ->
                        if (currentRoute != item.route) {
                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = BottomNavItem.Dashboard.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(BottomNavItem.Dashboard.route) {
                    DashboardScreen(navController = navController)
                }
                composable(BottomNavItem.History.route) {
                    HistoryScreen()
                }
                composable(BottomNavItem.Analysis.route) {
                    InsightsScreen()
                }
                composable(BottomNavItem.Assistant.route) {
                    AIAssistantScreen()
                }
                composable(BottomNavItem.Profile.route) {
                    ProfileScreen()
                }
            }
        }
        
        // Floating Action Button - Bottom Right
        FloatingActionButton(
            onClick = { showAddEntryDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 100.dp)
                .size(56.dp),
            shape = CircleShape,
            containerColor = PrimaryOrange,
            contentColor = DeepBlack,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Entry",
                modifier = Modifier.size(26.dp)
            )
        }
    }
    
    // Add Entry Dialog
    if (showAddEntryDialog) {
        AddEntryBottomSheet(
            onDismiss = { showAddEntryDialog = false }
        )
    }
}

@Composable
fun ComingSoonScreen(title: String, icon: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        color = WarmBeige.copy(alpha = 0.5f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = title,
                    modifier = Modifier.size(48.dp),
                    tint = DeepBlack
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = title,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = DeepBlack
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Coming Soon",
                fontSize = 16.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "We're working on this feature",
                fontSize = 14.sp,
                color = TextSecondary.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    currentRoute: String?,
    onItemSelected: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp),
            shape = RoundedCornerShape(34.dp),
            color = WarmBeige.copy(alpha = 0.6f),
            tonalElevation = 0.dp,
            shadowElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val selected = currentRoute == item.route
                    
                    NavItem(
                        item = item,
                        selected = selected,
                        onClick = { onItemSelected(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NavItem(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.15f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )
    
    val iconTint by animateColorAsState(
        targetValue = if (selected) DeepBlack else BottomNavUnselected,
        animationSpec = tween(durationMillis = 200),
        label = "iconTint"
    )
    
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) SoftGreen.copy(alpha = 0.5f) else Color.Transparent,
        animationSpec = tween(durationMillis = 200),
        label = "bgColor"
    )
    
    Box(
        modifier = Modifier
            .size(48.dp)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            )
            .background(
                color = backgroundColor,
                shape = CircleShape
            )
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = item.drawableResId),
            contentDescription = item.title,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
    }
}
