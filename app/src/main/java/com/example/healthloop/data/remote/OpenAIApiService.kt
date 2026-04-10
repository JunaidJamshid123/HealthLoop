package com.example.healthloop.data.remote

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenAIApiService {

    @POST("v1/chat/completions")
    suspend fun createChatCompletion(
        @Header("Authorization") authorization: String,
        @Body request: OpenAIChatRequest
    ): OpenAIChatResponse
}

// Request Models
data class OpenAIChatRequest(
    val model: String = "gpt-4o-mini",
    val messages: List<OpenAIMessage>,
    val temperature: Float = 0.7f,
    val max_tokens: Int = 2048,
    val top_p: Float = 0.95f
)

data class OpenAIMessage(
    val role: String,  // "system", "user", or "assistant"
    val content: String
)

// Response Models
data class OpenAIChatResponse(
    val id: String? = null,
    val choices: List<OpenAIChoice>? = null,
    val error: OpenAIError? = null
)

data class OpenAIChoice(
    val index: Int? = null,
    val message: OpenAIMessage? = null,
    val finish_reason: String? = null
)

data class OpenAIError(
    val message: String? = null,
    val type: String? = null,
    val code: String? = null
)
