package com.junaidjamshid.healthloop.domain.usecase.assistant

import com.junaidjamshid.healthloop.domain.model.HealthEntry
import com.junaidjamshid.healthloop.domain.model.UserGoals
import com.junaidjamshid.healthloop.domain.model.UserProfile
import com.junaidjamshid.healthloop.domain.repository.AIAssistantRepository
import javax.inject.Inject

class GetAIResponseUseCase @Inject constructor(
    private val repository: AIAssistantRepository
) {
    suspend operator fun invoke(
        userMessage: String,
        userProfile: UserProfile?,
        userGoals: UserGoals?,
        recentEntries: List<HealthEntry>,
        conversationHistory: List<Pair<String, String>>
    ): Result<String> {
        return repository.getAIResponse(
            userMessage = userMessage,
            userProfile = userProfile,
            userGoals = userGoals,
            recentEntries = recentEntries,
            conversationHistory = conversationHistory
        )
    }
}
