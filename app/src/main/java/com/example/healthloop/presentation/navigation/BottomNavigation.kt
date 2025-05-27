package com.example.healthloop.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.healthloop.presentation.dashboard.DashboardScreen
import com.example.healthloop.presentation.history.HistoryScreen
import com.example.healthloop.presentation.add_entry.AddEntryScreen
import com.example.healthloop.presentation.analysis.AnalysisScreen
import com.example.healthloop.presentation.settings.SettingsScreen

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Dashboard : BottomNavItem("dashboard", "Home", Icons.Default.Home)
    object History : BottomNavItem("history", "History", Icons.Default.Refresh)
    object AddEntry : BottomNavItem("add_entry", "Add Entry", Icons.Default.Add)
    object Analysis : BottomNavItem("analysis", "Analysis", Icons.Default.CheckCircle)
    object Settings : BottomNavItem("settings", "Settings", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenWithBottomNav() {
    var selectedItem by remember { mutableStateOf(0) }

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
                containerColor = Color.White,
                contentColor = Color.Black,
                modifier = Modifier
                    .height(80.dp)
                    .navigationBarsPadding()
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = if (selectedItem == index) Color.Black else Color.Gray
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                color = if (selectedItem == index) Color.Black else Color.Gray
                            )
                        },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = Color.Black,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color.Black.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = Color.White
        ) {
            when (selectedItem) {
                0 -> DashboardScreen()
                1 -> HistoryScreen()
                2 -> AddEntryScreen()
                3 -> AnalysisScreen()
                4 -> SettingsScreen()
            }
        }
    }
}