package com.example.healthloop.presentation.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.healthloop.presentation.dashboard.DashboardScreen
import com.example.healthloop.presentation.history.HistoryScreen
import com.example.healthloop.presentation.add_entry.AddEntryScreen
import com.example.healthloop.presentation.analysis.AnalysisScreen
import com.example.healthloop.presentation.settings.SettingsScreen
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material3.MaterialTheme

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Dashboard : BottomNavItem("dashboard", "Home", Icons.Default.Home)
    object History : BottomNavItem("history", "History", Icons.Default.History)
    object AddEntry : BottomNavItem("add_entry", "Add", Icons.Default.Add)
    object Analysis : BottomNavItem("analysis", "Analysis", Icons.Default.BarChart)
    object Settings : BottomNavItem("settings", "Settings", Icons.Default.Settings)
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

    val items = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.History,
        BottomNavItem.AddEntry,
        BottomNavItem.Analysis,
        BottomNavItem.Settings
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .height(80.dp)
                    .navigationBarsPadding()
            ) {
                items.forEach { item ->
                    val selected = currentRoute == item.route
                    val iconColor by animateColorAsState(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    val iconScale by animateFloatAsState(if (selected) 1.2f else 1f)
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = iconColor,
                                modifier = Modifier.size(28.dp).graphicsLayer(scaleX = iconScale, scaleY = iconScale)
                            )
                        },
                        label = {
                            if (selected) {
                                Text(
                                    text = item.title,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        selected = selected,
                        onClick = {
                            if (!selected) {
                                haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId)
                                    launchSingleTop = true
                                }
                            }
                        },
                        alwaysShowLabel = false,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                        )
                    )
                }
            }
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
            composable(BottomNavItem.AddEntry.route) {
                AddEntryScreen()
            }
            composable(BottomNavItem.Analysis.route) {
                AnalysisScreen()
            }
            composable(BottomNavItem.Settings.route) {
                SettingsScreen(
                    darkTheme = darkTheme,
                    onToggleDarkTheme = onToggleDarkTheme
                )
            }
        }
    }
}