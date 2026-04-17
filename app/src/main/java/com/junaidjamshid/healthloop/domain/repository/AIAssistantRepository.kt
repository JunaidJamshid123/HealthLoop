package com.junaidjamshid.healthloop.domain.repository

import com.junaidjamshid.healthloop.domain.model.HealthEntry
import com.junaidjamshid.healthloop.domain.model.UserGoals
import com.junaidjamshid.healthloop.domain.model.UserProfile

interface AIAssistantRepository {
    suspend fun getAIResponse(
        userMessage: String,
        userProfile: UserProfile?,
        userGoals: UserGoals?,
        recentEntries: List<HealthEntry>,
        conversationHistory: List<Pair<String, String>>
    ): Result<String>
}
