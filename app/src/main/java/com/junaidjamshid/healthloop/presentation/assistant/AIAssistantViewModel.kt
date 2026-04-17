package com.junaidjamshid.healthloop.presentation.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.junaidjamshid.healthloop.domain.model.HealthEntry
import com.junaidjamshid.healthloop.domain.model.UserGoals
import com.junaidjamshid.healthloop.domain.model.UserProfile
import com.junaidjamshid.healthloop.domain.usecase.GetRecentEntriesUseCase
import com.junaidjamshid.healthloop.domain.usecase.assistant.GetAIResponseUseCase
import com.junaidjamshid.healthloop.domain.usecase.profile.GetUserGoalsUseCase
import com.junaidjamshid.healthloop.domain.usecase.profile.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val isError: Boolean = false
)

data class AIAssistantUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isTyping: Boolean = false,
    val userProfile: UserProfile? = null,
    val userGoals: UserGoals? = null,
    val recentEntries: List<HealthEntry> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class AIAssistantViewModel @Inject constructor(
    private val getAIResponseUseCase: GetAIResponseUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getUserGoalsUseCase: GetUserGoalsUseCase,
    private val getRecentEntriesUseCase: GetRecentEntriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AIAssistantUiState())
    val uiState: StateFlow<AIAssistantUiState> = _uiState.asStateFlow()

    // Store conversation history for context
    private val conversationHistory = mutableListOf<Pair<String, String>>()

    init {
        loadUserData()
        sendInitialGreeting()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            try {
                // Load user profile
                getUserProfileUseCase().collect { profile ->
                    _uiState.value = _uiState.value.copy(userProfile = profile)
                }
            } catch (e: Exception) {
                // Profile not found, use default
            }
        }

        viewModelScope.launch {
            try {
                // Load user goals
                getUserGoalsUseCase().collect { goals ->
                    _uiState.value = _uiState.value.copy(userGoals = goals)
                }
            } catch (e: Exception) {
                // Goals not found, use default
            }
        }

        viewModelScope.launch {
            try {
                // Load recent entries (last 14 days)
                getRecentEntriesUseCase(14).collect { entries ->
                    _uiState.value = _uiState.value.copy(recentEntries = entries)
                }
            } catch (e: Exception) {
                // No entries found
            }
        }
    }

    private fun sendInitialGreeting() {
        val userName = _uiState.value.userProfile?.name ?: "there"
        val greeting = ChatMessage(
            content = "Hello $userName! 👋 I'm your AI Health Assistant powered by Google Gemini.\n\n" +
                    "I have access to all your health data and can provide personalized advice based on:\n\n" +
                    "• Your health entries & trends\n" +
                    "• Your goals & progress\n" +
                    "• Your profile information\n\n" +
                    "Ask me anything about your health! For example:\n" +
                    "• \"How am I doing with my water intake?\"\n" +
                    "• \"Give me tips to improve my sleep\"\n" +
                    "• \"Analyze my health trends\"\n" +
                    "• \"What should I focus on this week?\"\n\n" +
                    "How can I help you today? 😊",
            isFromUser = false
        )
        _uiState.value = _uiState.value.copy(messages = listOf(greeting))
    }

    fun sendMessage(userMessage: String) {
        if (userMessage.isBlank()) return

        viewModelScope.launch {
            // Add user message
            val userChatMessage = ChatMessage(
                content = userMessage.trim(),
                isFromUser = true
            )
            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages + userChatMessage,
                isTyping = true,
                errorMessage = null
            )

            // Get fresh data
            val currentProfile = try {
                getUserProfileUseCase().first()
            } catch (e: Exception) {
                _uiState.value.userProfile
            }

            val currentGoals = try {
                getUserGoalsUseCase().first()
            } catch (e: Exception) {
                _uiState.value.userGoals
            }

            val currentEntries = try {
                getRecentEntriesUseCase(14).first()
            } catch (e: Exception) {
                _uiState.value.recentEntries
            }

            // Call AI API
            val result = getAIResponseUseCase(
                userMessage = userMessage,
                userProfile = currentProfile,
                userGoals = currentGoals,
                recentEntries = currentEntries,
                conversationHistory = conversationHistory.toList()
            )

            result.fold(
                onSuccess = { aiResponse ->
                    // Store in conversation history
                    conversationHistory.add(userMessage to aiResponse)
                    
                    // Keep history manageable
                    if (conversationHistory.size > 10) {
                        conversationHistory.removeAt(0)
                    }

                    val aiChatMessage = ChatMessage(
                        content = aiResponse,
                        isFromUser = false
                    )
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + aiChatMessage,
                        isTyping = false
                    )
                },
                onFailure = { error ->
                    val errorMessage = ChatMessage(
                        content = "I'm sorry, I encountered an error: ${error.message ?: "Unknown error"}. Please try again.",
                        isFromUser = false,
                        isError = true
                    )
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + errorMessage,
                        isTyping = false,
                        errorMessage = error.message
                    )
                }
            )
        }
    }

    fun clearChat() {
        conversationHistory.clear()
        sendInitialGreeting()
    }
}
