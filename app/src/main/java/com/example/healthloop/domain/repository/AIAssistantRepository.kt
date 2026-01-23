package com.example.healthloop.domain.repository

import com.example.healthloop.domain.model.HealthEntry
import com.example.healthloop.domain.model.UserGoals
import com.example.healthloop.domain.model.UserProfile

interface AIAssistantRepository {
    suspend fun getAIResponse(
        userMessage: String,
        userProfile: UserProfile?,
        userGoals: UserGoals?,
        recentEntries: List<HealthEntry>,
        conversationHistory: List<Pair<String, String>>
    ): Result<String>
}
