package com.junaidjamshid.healthloop.domain.model

data class UserStats(
    val id: Int = 1,
    val totalDays: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val healthScore: Int = 0,
    val lastActiveDate: Long = System.currentTimeMillis()
) {
    companion object {
        fun default() = UserStats()
    }
}
