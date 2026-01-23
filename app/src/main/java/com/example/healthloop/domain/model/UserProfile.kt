package com.example.healthloop.domain.model

data class UserProfile(
    val id: Int = 1,
    val name: String,
    val email: String,
    val age: Int,
    val weight: Float,
    val height: Int,
    val profilePictureBase64: String? = null,
    val memberSince: Long = System.currentTimeMillis(),
    val isPro: Boolean = false
) {
    val bmi: Float
        get() {
            val heightInMeters = height / 100f
            return if (heightInMeters > 0 && weight > 0) weight / (heightInMeters * heightInMeters) else 0f
        }
    
    val bmiCategory: String
        get() = when {
            bmi == 0f -> "Not Set"
            bmi < 18.5f -> "Underweight"
            bmi < 25f -> "Normal"
            bmi < 30f -> "Overweight"
            else -> "Obese"
        }
    
    companion object {
        fun default() = UserProfile(
            name = "User",
            email = "user@email.com",
            age = 0,
            weight = 0f,
            height = 0
        )
    }
}
