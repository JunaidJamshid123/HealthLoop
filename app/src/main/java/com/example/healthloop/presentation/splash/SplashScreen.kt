package com.example.healthloop.presentation.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthloop.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen() {
    // Animation states
    var startAnimation by remember { mutableStateOf(false) }

    // Heartbeat animation
    val heartbeatScale by animateFloatAsState(
        targetValue = if (startAnimation) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "heartbeat_animation"
    )

    // App name animation
    val appNameAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1500, delayMillis = 500),
        label = "app_name_animation"
    )

    // Bottom text animation
    val bottomTextAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1500, delayMillis = 1000),
        label = "bottom_text_animation"
    )

    // Start animations when composable is first composed
    LaunchedEffect(Unit) {
        delay(300)
        startAnimation = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxHeight()
        ) {
            // Heartbeat Image from drawable
            Image(
                painter = painterResource(id = R.drawable.heartbeat),
                contentDescription = "Heartbeat Icon",
                modifier = Modifier
                    .size(80.dp)
                    .scale(heartbeatScale)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // App Name with Animation
            Text(
                text = "HealthLoop",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .graphicsLayer(alpha = appNameAlpha),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "Your Health, Your Journey",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier
                    .graphicsLayer(alpha = appNameAlpha),
                textAlign = TextAlign.Center
            )
        }

        // Bottom Text
        Text(
            text = "Taking care of your health, one step at a time",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .padding(horizontal = 32.dp)
                .graphicsLayer(alpha = bottomTextAlpha),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen()
}