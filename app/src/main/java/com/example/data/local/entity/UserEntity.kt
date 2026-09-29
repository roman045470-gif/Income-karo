package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val phone: String,
    val password: String,
    val fullName: String,
    val email: String,
    val address: String,
    val userIdCode: String,
    val referralCode: String,
    val referredBy: String? = null,
    val coins: Int = 100, // 100 welcome coins bonus
    val isVip: Boolean = false,
    val vipExpiry: Long? = null,
    val lastDailySpin: Long = 0L,
    val miningStartTime: Long = 0L, // 0 if not running
    val miningDurationMs: Long = 3600000L, // 1 hour in ms
    val miningClaimed: Boolean = true,
    val adsWatchedToday: Int = 0,
    val lastAdDate: Long = 0L,
    val avatarIndex: Int = 0,
    val isBanned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
