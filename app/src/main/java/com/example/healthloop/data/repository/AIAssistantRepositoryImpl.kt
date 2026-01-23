package com.example.healthloop.data.repository

import com.example.healthloop.BuildConfig
import com.example.healthloop.data.remote.Content
import com.example.healthloop.data.remote.GeminiApiService
import com.example.healthloop.data.remote.GeminiRequest
import com.example.healthloop.data.remote.Part
import com.example.healthloop.domain.model.HealthEntry
import com.example.healthloop.domain.model.UserGoals
import com.example.healthloop.domain.model.UserProfile
import com.example.healthloop.domain.repository.AIAssistantRepository
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class AIAssistantRepositoryImpl @Inject constructor(
    private val geminiApiService: GeminiApiService
) : AIAssistantRepository {

    companion object {
        // API key is loaded securely from local.properties via BuildConfig
        private val API_KEY = BuildConfig.GEMINI_API_KEY
        private const val MODEL = "gemini-2.0-flash"
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
            val conversationContext = buildConversationContext(conversationHistory)
            
            val fullPrompt = """
$systemPrompt

$conversationContext

User's Current Question: $userMessage

Please provide a helpful, personalized response based on the user's health data and question. Be conversational, supportive, and include specific recommendations based on their actual data when relevant. Use emojis sparingly to make the response friendly. Format important information clearly.
            """.trimIndent()

            val request = GeminiRequest(
                contents = listOf(
                    Content(
                        parts = listOf(Part(text = fullPrompt)),
                        role = "user"
                    )
                )
            )

            val response = geminiApiService.generateContent(
                model = MODEL,
                apiKey = API_KEY,
                request = request
            )

            val aiResponse = response.candidates
                ?.firstOrNull()
                ?.content
                ?.parts
                ?.firstOrNull()
                ?.text

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

    private fun buildConversationContext(conversationHistory: List<Pair<String, String>>): String {
        if (conversationHistory.isEmpty()) return ""
        
        val recentHistory = conversationHistory.takeLast(5) // Keep last 5 exchanges for context
        
        return """
## RECENT CONVERSATION CONTEXT:
${recentHistory.joinToString("\n") { (userMsg, aiMsg) ->
            """
User: $userMsg
Assistant: ${aiMsg.take(200)}${if (aiMsg.length > 200) "..." else ""}
            """.trimIndent()
        }}
        """.trimIndent()
    }
}
