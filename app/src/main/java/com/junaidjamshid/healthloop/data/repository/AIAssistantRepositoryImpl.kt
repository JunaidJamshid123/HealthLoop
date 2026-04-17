package com.junaidjamshid.healthloop.data.repository

import com.junaidjamshid.healthloop.BuildConfig
import com.junaidjamshid.healthloop.data.remote.OpenAIApiService
import com.junaidjamshid.healthloop.data.remote.OpenAIChatRequest
import com.junaidjamshid.healthloop.data.remote.OpenAIMessage
import com.junaidjamshid.healthloop.domain.model.HealthEntry
import com.junaidjamshid.healthloop.domain.model.UserGoals
import com.junaidjamshid.healthloop.domain.model.UserProfile
import com.junaidjamshid.healthloop.domain.repository.AIAssistantRepository
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class AIAssistantRepositoryImpl @Inject constructor(
    private val openAIApiService: OpenAIApiService
) : AIAssistantRepository {

    companion object {
        private val API_KEY = BuildConfig.OPENAI_API_KEY
        private const val MODEL = "gpt-4o-mini"
    }

    override suspend fun getAIResponse(
        userMessage: String,
        userProfile: UserProfile?,
        userGoals: UserGoals?,
        recentEntries: List<HealthEntry>,
        conversationHistory: List<Pair<String, String>>
    ): Result<String> {
        return try {
            val systemPrompt = buildSystemPrompt(userProfile, userGoals, recentEntries)

            val messages = mutableListOf<OpenAIMessage>()

            // System message
            messages.add(OpenAIMessage(role = "system", content = systemPrompt))

            // Conversation history
            conversationHistory.takeLast(5).forEach { (userMsg, aiMsg) ->
                messages.add(OpenAIMessage(role = "user", content = userMsg))
                messages.add(OpenAIMessage(role = "assistant", content = aiMsg))
            }

            // Current user message
            messages.add(OpenAIMessage(role = "user", content = userMessage))

            val request = OpenAIChatRequest(
                model = MODEL,
                messages = messages
            )

            val response = openAIApiService.createChatCompletion(
                authorization = "Bearer $API_KEY",
                request = request
            )

            val aiResponse = response.choices
                ?.firstOrNull()
                ?.message
                ?.content

            if (aiResponse != null) {
                Result.success(aiResponse)
            } else {
                val errorMessage = response.error?.message ?: "No response generated"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildSystemPrompt(
        userProfile: UserProfile?,
        userGoals: UserGoals?,
        recentEntries: List<HealthEntry>
    ): String {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        
        val profileSection = userProfile?.let {
            """
## USER PROFILE:
- Name: ${it.name}
- Age: ${it.age} years old
- Weight: ${it.weight} kg
- Height: ${it.height} cm
- BMI: ${String.format("%.1f", it.bmi)} (${it.bmiCategory})
- Member since: ${dateFormat.format(it.memberSince)}
            """.trimIndent()
        } ?: "## USER PROFILE: Not available"

        val goalsSection = userGoals?.let {
            """
## USER HEALTH GOALS:
- Daily Water Goal: ${it.waterGoal} glasses
- Sleep Goal: ${it.sleepGoal} hours
- Daily Steps Goal: ${String.format("%,d", it.stepsGoal)} steps
- Daily Calories Goal: ${String.format("%,d", it.caloriesGoal)} kcal
- Exercise Goal: ${it.exerciseGoal} minutes/day
- Target Weight: ${it.weightGoal} kg
            """.trimIndent()
        } ?: "## USER HEALTH GOALS: Using default goals (8 glasses water, 8h sleep, 10k steps)"

        val entriesSection = if (recentEntries.isNotEmpty()) {
            val entriesText = recentEntries.take(14).joinToString("\n") { entry ->
                """
  - Date: ${dateFormat.format(entry.date)}
    • Water: ${entry.waterIntake} glasses
    • Sleep: ${entry.sleepHours} hours
    • Steps: ${String.format("%,d", entry.stepCount)}
    • Mood: ${entry.mood}
    • Weight: ${entry.weight} kg
    • Calories: ${entry.calories} kcal
    • Exercise: ${entry.exerciseMinutes} min
                """.trimIndent()
            }

            // Calculate averages
            val avgWater = recentEntries.map { it.waterIntake }.filter { it > 0 }.average().takeIf { !it.isNaN() } ?: 0.0
            val avgSleep = recentEntries.map { it.sleepHours.toDouble() }.filter { it > 0 }.average().takeIf { !it.isNaN() } ?: 0.0
            val avgSteps = recentEntries.map { it.stepCount }.filter { it > 0 }.average().takeIf { !it.isNaN() } ?: 0.0
            val avgExercise = recentEntries.map { it.exerciseMinutes }.filter { it > 0 }.average().takeIf { !it.isNaN() } ?: 0.0
            val avgCalories = recentEntries.map { it.calories }.filter { it > 0 }.average().takeIf { !it.isNaN() } ?: 0.0

            // Mood distribution
            val moodCounts = recentEntries.filter { it.mood.isNotBlank() }.groupingBy { it.mood }.eachCount()
            val moodDistribution = moodCounts.entries.joinToString(", ") { "${it.key}: ${it.value}" }

            """
## RECENT HEALTH ENTRIES (Last ${recentEntries.size} days):
$entriesText

## CALCULATED AVERAGES:
- Average Water Intake: ${String.format("%.1f", avgWater)} glasses/day
- Average Sleep: ${String.format("%.1f", avgSleep)} hours/night
- Average Steps: ${String.format("%.0f", avgSteps)} steps/day
- Average Exercise: ${String.format("%.0f", avgExercise)} minutes/day
- Average Calories: ${String.format("%.0f", avgCalories)} kcal/day

## MOOD DISTRIBUTION: $moodDistribution

## GOAL ACHIEVEMENT:
- Water Goal Achievement: ${String.format("%.0f", (avgWater / (userGoals?.waterGoal ?: 8)) * 100)}%
- Sleep Goal Achievement: ${String.format("%.0f", (avgSleep / (userGoals?.sleepGoal ?: 8f)) * 100)}%
- Steps Goal Achievement: ${String.format("%.0f", (avgSteps / (userGoals?.stepsGoal ?: 10000)) * 100)}%
- Exercise Goal Achievement: ${String.format("%.0f", (avgExercise / (userGoals?.exerciseGoal ?: 30)) * 100)}%
            """.trimIndent()
        } else {
            "## RECENT HEALTH ENTRIES: No entries recorded yet. User is new or hasn't logged any data."
        }

        return """
# HEALTH ASSISTANT SYSTEM PROMPT

You are HealthLoop AI, a friendly and knowledgeable personal health assistant. Your role is to help users understand their health data, provide personalized recommendations, and support their wellness journey.

## YOUR CAPABILITIES:
1. Analyze user's health metrics and trends
2. Provide personalized health advice based on their actual data
3. Suggest improvements to help them reach their goals
4. Answer health-related questions with accurate information
5. Motivate and encourage healthy behaviors
6. Identify patterns in their health data

## GUIDELINES:
- Always be supportive, positive, and encouraging
- Reference the user's actual data when giving advice
- Provide specific, actionable recommendations
- Be conversational but professional
- Acknowledge their progress and achievements
- If you don't have enough data, politely mention it
- Never provide medical diagnoses - suggest consulting a doctor for medical concerns
- Use the user's name (${userProfile?.name ?: "friend"}) occasionally to personalize responses
- Keep responses concise but informative (aim for 150-300 words unless detailed explanation needed)

$profileSection

$goalsSection

$entriesSection
        """.trimIndent()
    }
}
