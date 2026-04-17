package com.junaidjamshid.healthloop.presentation.assistant

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.junaidjamshid.healthloop.R
import com.junaidjamshid.healthloop.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIAssistantScreen(
    viewModel: AIAssistantViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll when new messages arrive
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            delay(100)
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Existing AI Assistant UI (blurred in background)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceLight)
                .blur(12.dp)
        ) {
            // Header
            AssistantHeader(
                userName = uiState.userProfile?.name,
                onClearChat = { viewModel.clearChat() }
            )
        
            // Chat Messages
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(uiState.messages, key = { it.id }) { message ->
                    ChatBubble(message = message)
                }
                
                // Typing indicator
                if (uiState.isTyping) {
                    item {
                        TypingIndicator()
                    }
                }
            }
            
            // Quick Suggestions
            if (uiState.messages.size <= 2) {
                QuickSuggestions(
                    onSuggestionClick = { suggestion ->
                        messageText = suggestion
                    }
                )
            }
            
            // Input Section
            ChatInputSection(
                messageText = messageText,
                onMessageChange = { messageText = it },
                isLoading = uiState.isTyping,
                onSendClick = {
                    if (messageText.isNotBlank() && !uiState.isTyping) {
                        viewModel.sendMessage(messageText)
                        messageText = ""
                    }
                }
            )
        }

        // Coming Soon Overlay
        ComingSoonOverlay()
    }
}

@Composable
private fun ComingSoonOverlay() {
    // Animated sparkle rotation
    val infiniteTransition = rememberInfiniteTransition(label = "sparkle")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DeepBlack.copy(alpha = 0.55f),
                        DeepBlack.copy(alpha = 0.75f),
                        DeepBlack.copy(alpha = 0.55f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Glowing icon container
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                PrimaryOrange.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(PrimaryOrange, SoftGreen)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = DeepBlack,
                        modifier = Modifier
                            .size(36.dp)
                            .graphicsLayer(
                                scaleX = pulse,
                                scaleY = pulse
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Coming Soon",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "AI Health Assistant is being\nprepared for you",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = WarmBeige.copy(alpha = 0.15f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = null,
                        tint = PrimaryOrange,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "We'll notify you when it's ready",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

// ==================== HEADER ====================
@Composable
private fun AssistantHeader(
    userName: String?,
        onClearChat: () -> Unit
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = SurfaceLight,
            shadowElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bot Avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(PrimaryOrange, SoftGreen)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bot),
                        contentDescription = "AI Assistant",
                        modifier = Modifier.size(32.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Health Assistant",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepBlack
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(MintGreen, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (userName != null) "Helping $userName" else "Powered by Gemini AI",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Clear Chat Button
                IconButton(
                    onClick = onClearChat,
                    modifier = Modifier
                        .size(40.dp)
                        .background(WarmBeige, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = "Clear Chat",
                        tint = DeepBlack,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    // ==================== CHAT BUBBLE ====================
    @Composable
    private fun ChatBubble(message: ChatMessage) {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = if (message.isFromUser) Alignment.End else Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!message.isFromUser) {
                    // Bot Avatar
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                if (message.isError) CoralPink.copy(alpha = 0.3f)
                                else SoftGreen.copy(alpha = 0.3f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.bot),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Surface(
                    modifier = Modifier.widthIn(max = 300.dp),
                    shape = RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (message.isFromUser) 20.dp else 4.dp,
                        bottomEnd = if (message.isFromUser) 4.dp else 20.dp
                    ),
                    color = when {
                        message.isError -> CoralPink.copy(alpha = 0.2f)
                        message.isFromUser -> PrimaryOrange
                        else -> CardSurface
                    }
                ) {
                    Text(
                        text = message.content,
                        modifier = Modifier.padding(14.dp),
                        fontSize = 14.sp,
                        color = DeepBlack,
                        lineHeight = 20.sp
                    )
                }

                if (message.isFromUser) {
                    Spacer(modifier = Modifier.width(8.dp))
                    // User Avatar
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SkyBlue.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = SkyBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Timestamp
            Text(
                text = timeFormat.format(Date(message.timestamp)),
                fontSize = 10.sp,
                color = TextSecondary,
                modifier = Modifier.padding(
                    start = if (message.isFromUser) 0.dp else 40.dp,
                    end = if (message.isFromUser) 40.dp else 0.dp,
                    top = 4.dp
                )
            )
        }
    }

    // ==================== TYPING INDICATOR ====================
    @Composable
    private fun TypingIndicator() {
        val infiniteTransition = rememberInfiniteTransition(label = "typing")

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(SoftGreen.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bot),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp),
                color = CardSurface
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(3) { index ->
                        val alpha by infiniteTransition.animateFloat(
                            initialValue = 0.3f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(600),
                                repeatMode = RepeatMode.Reverse,
                                initialStartOffset = StartOffset(index * 200)
                            ),
                            label = "dot$index"
                        )

                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    TextSecondary.copy(alpha = alpha),
                                    CircleShape
                                )
                        )
                    }
                }
            }
        }
    }

    // ==================== QUICK SUGGESTIONS ====================
    @Composable
    private fun QuickSuggestions(
        onSuggestionClick: (String) -> Unit
    ) {
        val suggestions = listOf(
            "� How am I doing with my health goals?",
            "💧 Analyze my water intake",
            "😴 Tips to improve my sleep",
            "🏃 What exercise should I do today?",
            "📈 Show my health trends",
            "🎯 What should I focus on this week?"
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Quick Questions",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 2 rows of suggestions
            suggestions.chunked(3).forEach { rowSuggestions ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowSuggestions.forEach { suggestion ->
                        SuggestionChip(
                            text = suggestion,
                            onClick = { onSuggestionClick(suggestion) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Fill empty space if needed
                    repeat(3 - rowSuggestions.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }

    @Composable
    private fun SuggestionChip(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        Surface(
            onClick = onClick,
            modifier = modifier,
            shape = RoundedCornerShape(12.dp),
            color = WarmBeige
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                fontSize = 11.sp,
                color = DeepBlack,
                textAlign = TextAlign.Center,
                maxLines = 2,
                lineHeight = 14.sp
            )
        }
    }

    // ==================== INPUT SECTION ====================
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ChatInputSection(
        messageText: String,
        onMessageChange: (String) -> Unit,
        isLoading: Boolean,
        onSendClick: () -> Unit
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = SurfaceLight,
            shadowElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .padding(bottom = 80.dp), // Space for bottom nav
                verticalAlignment = Alignment.Bottom
            ) {
                // Text Field
                OutlinedTextField(
                    value = messageText,
                    onValueChange = onMessageChange,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp, max = 120.dp),
                    enabled = !isLoading,
                    placeholder = {
                        Text(
                            if (isLoading) "AI is thinking..." else "Ask about your health data...",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryOrange,
                        unfocusedBorderColor = BorderColor,
                        focusedContainerColor = SurfaceLight,
                        unfocusedContainerColor = SurfaceLight,
                        disabledBorderColor = BorderColor.copy(alpha = 0.5f),
                        disabledContainerColor = SurfaceLight
                    ),
                    maxLines = 4,
                    trailingIcon = {
                        if (messageText.isNotBlank() && !isLoading) {
                            IconButton(onClick = { onMessageChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.width(10.dp))

                // Send Button
                FloatingActionButton(
                    onClick = onSendClick,
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    containerColor = when {
                        isLoading -> WarmBeigeDark
                        messageText.isNotBlank() -> PrimaryOrange
                        else -> WarmBeigeDark
                    },
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = TextSecondary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = if (messageText.isNotBlank()) DeepBlack else TextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }

