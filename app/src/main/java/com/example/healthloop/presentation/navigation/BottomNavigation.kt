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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import kotlinx.coroutines.launch

// Drawer menu items
sealed class DrawerMenuItem(
    val title: String,
    val icon: ImageVector,
    val route: String? = null
) {
    object Home : DrawerMenuItem("Home", Icons.Outlined.Home, "dashboard")
    object History : DrawerMenuItem("History", Icons.Outlined.History, "history")
    object Insights : DrawerMenuItem("Insights", Icons.Outlined.Insights, "analysis")
    object AIAssistant : DrawerMenuItem("AI Assistant", Icons.Outlined.SmartToy, "assistant")
    object Profile : DrawerMenuItem("Profile", Icons.Outlined.Person, "profile")
    object About : DrawerMenuItem("About", Icons.Outlined.Info, "about")
    object RateApp : DrawerMenuItem("Rate App", Icons.Outlined.Star)
    object Share : DrawerMenuItem("Share App", Icons.Outlined.Share)
}

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
    
    // Drawer state
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val items = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.History,
        BottomNavItem.Analysis,
        BottomNavItem.Assistant,
        BottomNavItem.Profile
    )
    
    val drawerItems = listOf(
        DrawerMenuItem.Home,
        DrawerMenuItem.History,
        DrawerMenuItem.Insights,
        DrawerMenuItem.AIAssistant,
        DrawerMenuItem.Profile,
        DrawerMenuItem.About,
        DrawerMenuItem.RateApp,
        DrawerMenuItem.Share
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                items = drawerItems,
                currentRoute = currentRoute,
                onItemClick = { item ->
                    scope.launch { drawerState.close() }
                    item.route?.let { route ->
                        if (currentRoute != route) {
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    }
                },
                darkTheme = darkTheme,
                onToggleDarkTheme = onToggleDarkTheme
            )
        },
        gesturesEnabled = true
    ) {
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
                        DashboardScreen(
                            navController = navController,
                            onMenuClick = { scope.launch { drawerState.open() } }
                        )
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
                    composable("about") {
                        AboutScreen(
                            onBackClick = { navController.popBackStack() }
                        )
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
    }
    
    // Add Entry Dialog
    if (showAddEntryDialog) {
        AddEntryBottomSheet(
            onDismiss = { showAddEntryDialog = false }
        )
    }
}

// ==================== DRAWER CONTENT ====================
@Composable
fun DrawerContent(
    items: List<DrawerMenuItem>,
    currentRoute: String?,
    onItemClick: (DrawerMenuItem) -> Unit,
    darkTheme: Boolean,
    onToggleDarkTheme: (Boolean) -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(300.dp),
        drawerContainerColor = SurfaceLight,
        drawerContentColor = DeepBlack
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        // App Logo and Name
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(PrimaryOrange, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.heartbeat),
                        contentDescription = "Logo",
                        modifier = Modifier.size(28.dp),
                        tint = DeepBlack
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "HealthLoop",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepBlack
                    )
                    Text(
                        text = "Track Your Health",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }
        
        Divider(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            color = BorderColor
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Navigation Items
        items.forEachIndexed { index, item ->
            val isSelected = item.route == currentRoute
            
            // Add divider before About (index 5)
            if (index == 5) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    color = BorderColor
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (isSelected) DeepBlack else TextSecondary
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) DeepBlack else TextSecondary
                    )
                },
                selected = isSelected,
                onClick = { onItemClick(item) },
                modifier = Modifier.padding(horizontal = 12.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = SoftGreen.copy(alpha = 0.3f),
                    unselectedContainerColor = Color.Transparent
                )
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Version Info
        Text(
            text = "Version 1.0.0",
            fontSize = 12.sp,
            color = TextSecondary.copy(alpha = 0.6f),
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ==================== ABOUT SCREEN ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = DeepBlack
                )
            }
            Text(
                text = "About",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = DeepBlack
            )
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // App Logo and Name
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(PrimaryOrange, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.heartbeat),
                        contentDescription = "Logo",
                        modifier = Modifier.size(60.dp),
                        tint = DeepBlack
                    )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "HealthLoop",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = DeepBlack
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Track Your Health, Transform Your Life",
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Version 1.0.0",
                fontSize = 12.sp,
                color = TextSecondary.copy(alpha = 0.7f)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // App Info Section
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = CardSurface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "About the App",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepBlack
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "HealthLoop is your personal health companion that helps you track your daily wellness activities including mood, water intake, sleep, steps, weight, calories, and exercise. Get insights into your health patterns and stay motivated with our AI-powered assistant.",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    lineHeight = 22.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Features Section
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = CardSurface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Key Features",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepBlack
                )
                Spacer(modifier = Modifier.height(12.dp))
                FeatureItem("Track daily mood and emotions")
                FeatureItem("Monitor water intake")
                FeatureItem("Log sleep hours")
                FeatureItem("Count daily steps")
                FeatureItem("Track weight changes")
                FeatureItem("Monitor calories and exercise")
                FeatureItem("AI-powered health insights")
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Copyright
        Text(
            text = "© 2026 HealthLoop. All rights reserved.",
            fontSize = 12.sp,
            color = TextSecondary.copy(alpha = 0.6f),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FeatureItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.CheckCircle,
            contentDescription = null,
            tint = SoftGreenDark,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = TextSecondary
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
