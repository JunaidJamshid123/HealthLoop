package com.example.healthloop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.healthloop.presentation.splash.SplashScreen
import com.example.healthloop.presentation.navigation.MainScreenWithBottomNav
import com.example.healthloop.ui.theme.HealthLoopTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HealthLoopTheme {
                var showSplash by remember { mutableStateOf(true) }

                // Show splash screen for 3 seconds
                LaunchedEffect(Unit) {
                    delay(3000)
                    showSplash = false
                }

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),
                    color = Color.White
                ) {
                    if (showSplash) {
                        SplashScreen()
                    } else {
                        MainScreenWithBottomNav()
                    }
                }
            }
        }
    }
}