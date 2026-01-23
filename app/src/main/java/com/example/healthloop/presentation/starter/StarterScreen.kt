package com.example.healthloop.presentation.starter

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthloop.R
import com.example.healthloop.ui.theme.*
import kotlinx.coroutines.delay


@Composable
fun StarterScreen(
    onGetStarted: () -> Unit
) {
    // Animation states
    var startAnimation by remember { mutableStateOf(false) }

    // Image animation
    val imageAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(800, easing = EaseOutCubic),
        label = "image_alpha"
    )
    
    val imageScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "image_scale"
    )

    // Text animations
    val titleAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(800, delayMillis = 300),
        label = "title_alpha"
    )
    
    val titleSlide by animateFloatAsState(
        targetValue = if (startAnimation) 0f else 30f,
        animationSpec = tween(800, delayMillis = 300, easing = EaseOutCubic),
        label = "title_slide"
    )

    val subtitleAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(800, delayMillis = 500),
        label = "subtitle_alpha"
    )

    // Button animation
    val buttonAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(800, delayMillis = 700),
        label = "button_alpha"
    )
    
    val buttonSlide by animateFloatAsState(
        targetValue = if (startAnimation) 0f else 50f,
        animationSpec = tween(800, delayMillis = 700, easing = EaseOutCubic),
        label = "button_slide"
    )

    // Icons animation
    val iconsAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1000, delayMillis = 400),
        label = "icons_alpha"
    )

    // Floating animations for icons
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    
    val floatAnimation1 by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_anim1"
    )
    
    val floatAnimation2 by infiniteTransition.animateFloat(
        initialValue = 5f,
        targetValue = -5f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_anim2"
    )
    
    val floatAnimation3 by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_anim3"
    )

    val rotateAnimation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotate_anim"
    )

    LaunchedEffect(Unit) {
        delay(100)
        startAnimation = true
    }

    // Background gradient
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            SurfaceLight,
            WarmBeigeLight,
            SurfaceLight
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        // Decorative circles in background
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = (-50).dp, y = 180.dp)
                .alpha(0.25f)
                .background(
                    color = SoftGreen.copy(alpha = 0.5f),
                    shape = CircleShape
                )
                .blur(60.dp)
        )
        
        Box(
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = 100.dp)
                .alpha(0.2f)
                .background(
                    color = PrimaryOrange.copy(alpha = 0.5f),
                    shape = CircleShape
                )
                .blur(50.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
                .padding(top = 50.dp, bottom = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Main illustration with floating icons
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Soft glow behind image
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .offset(y = floatAnimation1.dp)
                        .alpha(0.3f)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    PrimaryOrange.copy(alpha = 0.3f),
                                    WarmBeige.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )
                
                // Main image
                Image(
                    painter = painterResource(id = R.drawable.starter2),
                    contentDescription = "Meditation illustration",
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .aspectRatio(0.95f)
                        .graphicsLayer {
                            alpha = imageAlpha
                            scaleX = imageScale
                            scaleY = imageScale
                            translationY = floatAnimation1 * 0.3f
                        },
                    contentScale = ContentScale.Fit
                )
                
                // Floating Health Icons around the image
                // Water icon - Top Left
                FloatingHealthIcon(
                    iconRes = R.drawable.water,
                    backgroundColor = SkyBlue.copy(alpha = 0.9f),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = 20.dp, y = 60.dp)
                        .graphicsLayer {
                            alpha = iconsAlpha
                            translationY = floatAnimation1
                            rotationZ = rotateAnimation
                        },
                    size = 52.dp
                )
                
                // Sleep icon - Top Right
                FloatingHealthIcon(
                    iconRes = R.drawable.sleeping,
                    backgroundColor = SoftGreen.copy(alpha = 0.9f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-15).dp, y = 80.dp)
                        .graphicsLayer {
                            alpha = iconsAlpha
                            translationY = floatAnimation2
                            rotationZ = -rotateAnimation
                        },
                    size = 48.dp
                )
                
                // Steps/Walk icon - Middle Left
                FloatingHealthIcon(
                    iconRes = R.drawable.walk,
                    backgroundColor = MintGreen.copy(alpha = 0.9f),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = (-5).dp, y = 30.dp)
                        .graphicsLayer {
                            alpha = iconsAlpha
                            translationY = floatAnimation3
                            rotationZ = rotateAnimation * 0.5f
                        },
                    size = 46.dp
                )
                
                // Exercise icon - Middle Right
                FloatingHealthIcon(
                    iconRes = R.drawable.excercise,
                    backgroundColor = CoralPink.copy(alpha = 0.9f),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = 5.dp, y = (-20).dp)
                        .graphicsLayer {
                            alpha = iconsAlpha
                            translationY = floatAnimation1 * -1
                            rotationZ = -rotateAnimation * 0.5f
                        },
                    size = 44.dp
                )
                
                // Calories icon - Bottom Left
                FloatingHealthIcon(
                    iconRes = R.drawable.calaroies,
                    backgroundColor = PrimaryOrange.copy(alpha = 0.9f),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = 35.dp, y = (-50).dp)
                        .graphicsLayer {
                            alpha = iconsAlpha
                            translationY = floatAnimation2 * -1
                            rotationZ = rotateAnimation
                        },
                    size = 42.dp
                )
                
                // Heart/Mood icon - Bottom Right
                FloatingHealthIcon(
                    iconRes = R.drawable.happy,
                    backgroundColor = Color(0xFFFFE0B2).copy(alpha = 0.95f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-30).dp, y = (-40).dp)
                        .graphicsLayer {
                            alpha = iconsAlpha
                            translationY = floatAnimation3 * -1
                            rotationZ = -rotateAnimation
                        },
                    size = 44.dp
                )
            }

            // Content section
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = "Balance Your Mind",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepBlack,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = titleAlpha
                            translationY = titleSlide
                        }
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "& Body",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryOrange,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = titleAlpha
                            translationY = titleSlide
                        }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Subtitle
                Text(
                    text = "Track your mood, sleep, and daily habits.\nStart your wellness journey today.",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp,
                    modifier = Modifier
                        .alpha(subtitleAlpha)
                        .padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Get Started Button
                Button(
                    onClick = onGetStarted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .graphicsLayer {
                            alpha = buttonAlpha
                            translationY = buttonSlide
                        },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryOrange
                    ),
                    shape = RoundedCornerShape(28.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    Text(
                        text = "Get Started",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepBlack
                    )
                }
            }
        }
    }
}

@Composable
private fun FloatingHealthIcon(
    iconRes: Int,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                ambientColor = backgroundColor.copy(alpha = 0.3f),
                spotColor = backgroundColor.copy(alpha = 0.2f)
            )
            .background(backgroundColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(size * 0.55f),
            contentScale = ContentScale.Fit
        )
    }
}
