package com.junaidjamshid.healthloop.data.remote

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface GeminiApiService {
    
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String = "gemini-2.0-flash",
        @Header("x-goog-api-key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

// Request Models
data class GeminiRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = GenerationConfig()
)

data class Content(
    val parts: List<Part>,
    val role: String = "user"
)

data class Part(
    val text: String
)

data class GenerationConfig(
    val temperature: Float = 0.7f,
    val topK: Int = 40,
    val topP: Float = 0.95f,
    val maxOutputTokens: Int = 2048
)

// Response Models
data class GeminiResponse(
    val candidates: List<Candidate>? = null,
    val error: GeminiError? = null
)

data class Candidate(
    val content: ContentResponse? = null,
    val finishReason: String? = null
)

data class ContentResponse(
    val parts: List<PartResponse>? = null,
    val role: String? = null
)

data class PartResponse(
    val text: String? = null
)

data class GeminiError(
    val code: Int? = null,
    val message: String? = null,
    val status: String? = null
)
