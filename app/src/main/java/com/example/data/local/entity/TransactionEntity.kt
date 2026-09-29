package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val type: String, // "SPIN", "AD_WATCH", "MINING", "REFERRAL_BONUS", "WELCOME_BONUS", "WITHDRAWAL", "VIP_PURCHASE"
    val title: String,
    val coins: Int, // positive for earn, negative for withdrawal
    val bdtAmount: Double,
    val status: String = "COMPLETED", // "COMPLETED", "PENDING", "REJECTED"
    val paymentMethod: String? = null, // "bKash", "Nagad", "Rocket"
    val accountNumber: String? = null,
    val trxId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
