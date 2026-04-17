package com.junaidjamshid.healthloop.presentation.starter

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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.junaidjamshid.healthloop.R
import com.junaidjamshid.healthloop.ui.theme.*
import kotlinx.coroutines.delay


@Composable
fun StarterScreen(
    onGetStarted: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val isCompact = screenHeight < 680.dp
    val isTablet = screenWidth > 600.dp

    // Responsive sizing
    val horizontalPadding = if (isTablet) 48.dp else 24.dp
    val imageWidthFraction = if (isTablet) 0.6f else 0.88f
    val titleSize = if (isCompact) 24.sp else if (isTablet) 34.sp else 28.sp
    val subtitleSize = if (isCompact) 13.sp else if (isTablet) 17.sp else 15.sp
    val buttonHeight = if (isCompact) 50.dp else 56.dp
    val iconBaseSize = if (isCompact) 40.dp else if (isTablet) 56.dp else 48.dp
    val topPadding = if (isCompact) 24.dp else 48.dp
    val bottomPadding = if (isCompact) 28.dp else 48.dp

    // Animation states
    var startAnimation by remember { mutableStateOf(false) }

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

    val iconsAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1000, delayMillis = 400),
        label = "icons_alpha"
    )

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

    // Subtle pulse for the glow
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    LaunchedEffect(Unit) {
        delay(100)
        startAnimation = true
    }

    // Background gradient matching the warm palette
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            WarmBeigeLight,
            SurfaceLight,
            WarmBeigeLight
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .systemBarsPadding()
    ) {
        // Decorative blurred orb - top left
        Box(
            modifier = Modifier
                .size(220.dp)
                .offset(x = (-60).dp, y = 120.dp)
                .alpha(0.18f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            SoftGreen.copy(alpha = 0.6f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
                .blur(80.dp)
        )

        // Decorative blurred orb - top right
        Box(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.TopEnd)
                .offset(x = 50.dp, y = 80.dp)
                .alpha(0.15f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            PrimaryOrange.copy(alpha = 0.6f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
                .blur(70.dp)
        )

        // Decorative blurred orb - bottom center
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomCenter)
                .offset(y = 60.dp)
                .alpha(0.12f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            SkyBlue.copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
                .blur(60.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding)
                .padding(top = topPadding, bottom = bottomPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Main illustration area with floating icons
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Large warm glow behind the image to mask white background
                Box(
                    modifier = Modifier
                        .fillMaxWidth(imageWidthFraction + 0.1f)
                        .aspectRatio(0.9f)
                        .alpha(glowPulse)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    WarmBeige.copy(alpha = 0.7f),
                                    PrimaryOrangeLight.copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Warm tinted background circle behind image to cover white
                Box(
                    modifier = Modifier
                        .fillMaxWidth(imageWidthFraction)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    WarmBeigeLight,
                                    WarmBeigeLight.copy(alpha = 0.95f),
                                    SurfaceLight.copy(alpha = 0.8f)
                                )
                            )
                        )
                )

                // Main image with color tinting to blend white areas
                Image(
                    painter = painterResource(id = R.drawable.starterr2),
                    contentDescription = "Meditation illustration",
                    modifier = Modifier
                        .fillMaxWidth(imageWidthFraction)
                        .aspectRatio(0.95f)
                        .graphicsLayer {
                            alpha = imageAlpha
                            scaleX = imageScale
                            scaleY = imageScale
                            translationY = floatAnimation1 * 0.3f
                        },
                    contentScale = ContentScale.Fit,

                )

                // Floating Health Icons
                FloatingHealthIcon(
                    iconText = "💧",
                    backgroundColor = SkyBlue.copy(alpha = 0.85f),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = 12.dp, y = 50.dp)
                        .graphicsLayer {
                            alpha = iconsAlpha
                            translationY = floatAnimation1
                            rotationZ = rotateAnimation
                        },
                    size = (iconBaseSize.value * 1.08f).dp
                )

                FloatingHealthIcon(
                    iconText = "😴",
                    backgroundColor = SoftGreenLight.copy(alpha = 0.85f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-8).dp, y = 70.dp)
                        .graphicsLayer {
                            alpha = iconsAlpha
                            translationY = floatAnimation2
                            rotationZ = -rotateAnimation
                        },
                    size = iconBaseSize
                )

                FloatingHealthIcon(
                    iconText = "🚶",
                    backgroundColor = MintGreen.copy(alpha = 0.85f),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = (-8).dp, y = 24.dp)
                        .graphicsLayer {
                            alpha = iconsAlpha
                            translationY = floatAnimation3
                            rotationZ = rotateAnimation * 0.5f
                        },
                    size = (iconBaseSize.value * 0.92f).dp
                )

                FloatingHealthIcon(
                    iconText = "🏋️",
                    backgroundColor = CoralPink.copy(alpha = 0.85f),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = 8.dp, y = (-24).dp)
                        .graphicsLayer {
                            alpha = iconsAlpha
                            translationY = floatAnimation1 * -1
                            rotationZ = -rotateAnimation * 0.5f
                        },
                    size = (iconBaseSize.value * 0.88f).dp
                )

                FloatingHealthIcon(
                    iconText = "🔥",
                    backgroundColor = PrimaryOrangeLight.copy(alpha = 0.85f),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = 28.dp, y = (-44).dp)
                        .graphicsLayer {
                            alpha = iconsAlpha
                            translationY = floatAnimation2 * -1
                            rotationZ = rotateAnimation
                        },
                    size = (iconBaseSize.value * 0.85f).dp
                )

                FloatingHealthIcon(
                    iconText = "😊",
                    backgroundColor = Color(0xFFFFE0B2).copy(alpha = 0.9f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-24).dp, y = (-36).dp)
                        .graphicsLayer {
                            alpha = iconsAlpha
                            translationY = floatAnimation3 * -1
                            rotationZ = -rotateAnimation
                        },
                    size = (iconBaseSize.value * 0.88f).dp
                )
            }

            Spacer(modifier = Modifier.height(if (isCompact) 8.dp else 20.dp))

            // Bottom content section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (isTablet) Modifier.fillMaxWidth(0.7f) else Modifier
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Track Your Health",
                    fontSize = titleSize,
                    fontWeight = FontWeight.Bold,
                    color = DeepBlack,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = titleAlpha
                            translationY = titleSlide
                        }
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Transform Your Life",
                    fontSize = titleSize,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryOrangeDark,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = titleAlpha
                            translationY = titleSlide
                        }
                )

                Spacer(modifier = Modifier.height(if (isCompact) 10.dp else 16.dp))

                Text(
                    text = "Track your mood, sleep, and daily habits.\nStart your wellness journey today.",
                    fontSize = subtitleSize,
                    fontWeight = FontWeight.Normal,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = (subtitleSize.value * 1.6f).sp,
                    modifier = Modifier
                        .alpha(subtitleAlpha)
                        .padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(if (isCompact) 20.dp else 32.dp))

                // Get Started Button with gradient
                Button(
                    onClick = onGetStarted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(buttonHeight)
                        .graphicsLayer {
                            alpha = buttonAlpha
                            translationY = buttonSlide
                        }
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(28.dp),
                            ambientColor = PrimaryOrange.copy(alpha = 0.3f),
                            spotColor = PrimaryOrange.copy(alpha = 0.25f)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(28.dp),
                    contentPadding = PaddingValues(0.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 4.dp
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        PrimaryOrange,
                                        PrimaryOrangeLight,
                                        PrimaryOrange
                                    )
                                ),
                                shape = RoundedCornerShape(28.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Get Started",
                            fontSize = if (isCompact) 15.sp else 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DeepBlack
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun FloatingHealthIcon(
    iconText: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = 12.dp,
                shape = CircleShape,
                ambientColor = backgroundColor.copy(alpha = 0.4f),
                spotColor = backgroundColor.copy(alpha = 0.3f)
            )
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        backgroundColor,
                        backgroundColor.copy(alpha = 0.8f)
                    )
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = iconText,
            fontSize = (size.value * 0.45f).sp,
            textAlign = TextAlign.Center
        )
    }
}
