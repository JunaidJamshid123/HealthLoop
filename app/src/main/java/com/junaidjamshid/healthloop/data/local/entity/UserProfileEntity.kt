package com.junaidjamshid.healthloop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1, // Single user profile, always use id = 1
    val name: String,
    val email: String,
    val age: Int,
    val weight: Float,
    val height: Int,
    val profilePictureBase64: String? = null, // Store image as Base64 string
    val memberSince: Long = System.currentTimeMillis(), // Timestamp
    val isPro: Boolean = false
)
