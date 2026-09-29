package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vip_requests")
data class VipRequestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val userName: String,
    val userPhone: String,
    val method: String, // "bKash", "Nagad"
    val senderNumber: String,
    val trxId: String,
    val amount: Double = 599.0,
    val screenshotNote: String = "",
    val status: String = "PENDING", // "PENDING", "APPROVED", "REJECTED"
    val timestamp: Long = System.currentTimeMillis()
)
