package com.example.ui.model

import com.example.data.local.entity.ReferralEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.VipRequestEntity

enum class Screen {
    AUTH,
    DASHBOARD,
    SPIN,
    ADS,
    MINING,
    VIP,
    WITHDRAW,
    HISTORY,
    REFERRAL,
    PROFILE,
    ADMIN
}

data class EarningUiState(
    val currentScreen: Screen = Screen.AUTH,
    val currentUser: UserEntity? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,

    // Auth screen mode
    val isLoginMode: Boolean = true,

    // Lists
    val userTransactions: List<TransactionEntity> = emptyList(),
    val userWithdrawals: List<TransactionEntity> = emptyList(),
    val userVipRequests: List<VipRequestEntity> = emptyList(),
    val userReferrals: List<ReferralEntity> = emptyList(),

    // Admin view
    val allUsers: List<UserEntity> = emptyList(),
    val allWithdrawals: List<TransactionEntity> = emptyList(),
    val allVipRequests: List<VipRequestEntity> = emptyList(),
    val allReferrals: List<ReferralEntity> = emptyList(),

    // Video Ad Watch State
    val isAdPlaying: Boolean = false,
    val adSecondsRemaining: Int = 15,
    val currentAdIndex: Int = 0,
    val canClaimAdReward: Boolean = false,

    // Mining State
    val miningRemainingSeconds: Long = 0L,
    val isMiningActive: Boolean = false,
    val isMiningCompleted: Boolean = false
)
