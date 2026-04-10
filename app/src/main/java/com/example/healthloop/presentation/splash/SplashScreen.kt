package com.example.healthloop.presentation.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthloop.R
import com.example.healthloop.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen() {
    // Animation states
    var startAnimation by remember { mutableStateOf(false) }

    // Heartbeat animation
    val heartbeatScale by animateFloatAsState(
        targetValue = if (startAnimation) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "heartbeat_animation"
    )

    // App name animation
    val appNameAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1200, delayMillis = 400),
        label = "app_name_animation"
    )
    
    // Slide up animation
    val slideUp by animateFloatAsState(
        targetValue = if (startAnimation) 0f else 50f,
        animationSpec = tween(1000, delayMillis = 400, easing = EaseOutCubic),
        label = "slide_up_animation"
    )

    // Bottom text animation
    val bottomTextAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1200, delayMillis = 800),
        label = "bottom_text_animation"
    )

    // Start animations when composable is first composed
    LaunchedEffect(Unit) {
        delay(200)
        startAnimation = true
    }

    // Gradient background matching the design system
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            WarmBeigeLight,
            SurfaceLight,
            WarmBeige.copy(alpha = 0.3f)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush),
        contentAlignment = Alignment.Center
    ) {
        // Decorative circles in background
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-100).dp, y = (-200).dp)
                .background(
                    PrimaryOrange.copy(alpha = 0.15f),
                    CircleShape
                )
        )
        
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = 120.dp, y = 280.dp)
                .background(
                    SoftGreen.copy(alpha = 0.2f),
                    CircleShape
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxHeight()
        ) {
            // Icon container with shadow and background
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .shadow(16.dp, CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                PrimaryOrange,
                                PrimaryOrangeLight
                            )
                        ),
                        shape = CircleShape
                    )
                    .scale(heartbeatScale),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.heartbeat),
                    contentDescription = "Heartbeat Icon",
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App Name with Animation
            Text(
                text = "HealthLoop",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = DeepBlack,
                modifier = Modifier
                    .graphicsLayer(
                        alpha = appNameAlpha,
                        translationY = slideUp
                    ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle with accent color
            Text(
                text = "Track Your Health, Transform Your Life",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
                modifier = Modifier
                    .graphicsLayer(
                        alpha = appNameAlpha,
                        translationY = slideUp
                    ),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Accent line
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(4.dp)
                    .graphicsLayer(alpha = appNameAlpha)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(PrimaryOrange, SoftGreen)
                        ),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }

        // Bottom Text
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
                .graphicsLayer(alpha = bottomTextAlpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Taking care of your health",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Text(
                text = "one step at a time ✨",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = PrimaryOrange,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen()
}