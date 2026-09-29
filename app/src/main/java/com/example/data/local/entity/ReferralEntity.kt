package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "referrals")
data class ReferralEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val referrerCode: String,
    val referredUserId: Long,
    val referredUserName: String,
    val totalCommissionCoins: Int = 0,
    val joinedDate: Long = System.currentTimeMillis()
)
