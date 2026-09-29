package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.model.Screen
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.DailySpinScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.MiningScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ReferralScreen
import com.example.ui.screens.VipPlanScreen
import com.example.ui.screens.WatchAdsScreen
import com.example.ui.screens.WithdrawScreen
import com.example.ui.viewmodel.AppViewModel

@Composable
fun MainApp(
    viewModel: AppViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Crossfade(targetState = state.currentScreen, label = "screen_transition") { screen ->
            when (screen) {
                Screen.AUTH -> {
                    AuthScreen(
                        state = state,
                        onLogin = { phone, pass -> viewModel.login(phone, pass) },
                        onSignup = { phone, pass, name, email, addr, ref ->
                            viewModel.signup(phone, pass, name, email, addr, ref)
                        },
                        onDismissMessage = { viewModel.clearMessages() }
                    )
                }

                Screen.DASHBOARD -> {
                    DashboardScreen(
                        state = state,
                        onNavigateToSpin = { viewModel.navigateTo(Screen.SPIN) },
                        onNavigateToAds = { viewModel.navigateTo(Screen.ADS) },
                        onNavigateToMining = { viewModel.navigateTo(Screen.MINING) },
                        onNavigateToReferral = { viewModel.navigateTo(Screen.REFERRAL) },
                        onNavigateToVip = { viewModel.navigateTo(Screen.VIP) },
                        onNavigateToWithdraw = { viewModel.navigateTo(Screen.WITHDRAW) },
                        onNavigateToHistory = { viewModel.navigateTo(Screen.HISTORY) },
                        onNavigateToProfile = { viewModel.navigateTo(Screen.PROFILE) },
                        onNavigateToAdmin = { viewModel.navigateTo(Screen.ADMIN) },
                        onDismissMessage = { viewModel.clearMessages() }
                    )
                }

                Screen.SPIN -> {
                    DailySpinScreen(
                        state = state,
                        onBack = { viewModel.navigateBack() },
                        onSpinClaim = { coins, onResult -> viewModel.spinWheel(coins, onResult) },
                        onDismissMessage = { viewModel.clearMessages() }
                    )
                }

                Screen.ADS -> {
                    WatchAdsScreen(
                        state = state,
                        onBack = { viewModel.navigateBack() },
                        onStartAd = { viewModel.startWatchingAd() },
                        onClaimReward = { viewModel.claimAdReward() },
                        onCloseAd = { viewModel.closeAd() },
                        onDismissMessage = { viewModel.clearMessages() }
                    )
                }

                Screen.MINING -> {
                    MiningScreen(
                        state = state,
                        onBack = { viewModel.navigateBack() },
                        onStartMining = { viewModel.startMining() },
                        onClaimMining = { viewModel.claimMining() },
                        onNavigateToVip = { viewModel.navigateTo(Screen.VIP) },
                        onDismissMessage = { viewModel.clearMessages() }
                    )
                }

                Screen.VIP -> {
                    VipPlanScreen(
                        state = state,
                        onBack = { viewModel.navigateBack() },
                        onSubmitVipRequest = { method, sender, trx, note ->
                            viewModel.submitVipPurchase(method, sender, trx, note)
                        },
                        onInstantTestApprove = { userId ->
                            viewModel.adminSetUserVip(userId, true)
                        },
                        onDismissMessage = { viewModel.clearMessages() }
                    )
                }

                Screen.WITHDRAW -> {
                    WithdrawScreen(
                        state = state,
                        onBack = { viewModel.navigateBack() },
                        onSubmitWithdrawal = { method, account, coins ->
                            viewModel.submitWithdrawal(method, account, coins)
                        },
                        onDismissMessage = { viewModel.clearMessages() }
                    )
                }

                Screen.REFERRAL -> {
                    ReferralScreen(
                        state = state,
                        onBack = { viewModel.navigateBack() },
                        onDismissMessage = { viewModel.clearMessages() }
                    )
                }

                Screen.PROFILE -> {
                    ProfileScreen(
                        state = state,
                        onBack = { viewModel.navigateBack() },
                        onUpdateProfile = { name, email, addr, avatar ->
                            viewModel.updateProfile(name, email, addr, avatar)
                        },
                        onLogout = { viewModel.logout() },
                        onNavigateToAdmin = { viewModel.navigateTo(Screen.ADMIN) },
                        onDismissMessage = { viewModel.clearMessages() }
                    )
                }

                Screen.HISTORY -> {
                    HistoryScreen(
                        state = state,
                        onBack = { viewModel.navigateBack() }
                    )
                }

                Screen.ADMIN -> {
                    AdminScreen(
                        state = state,
                        onBack = { viewModel.navigateBack() },
                        onApproveVip = { reqId -> viewModel.adminApproveVip(reqId) },
                        onRejectVip = { reqId -> viewModel.adminRejectVip(reqId) },
                        onSetUserVip = { userId, isVip -> viewModel.adminSetUserVip(userId, isVip) },
                        onToggleBanUser = { userId, isBanned -> viewModel.adminToggleBanUser(userId, isBanned) },
                        onAdjustCoins = { userId, newCoins -> viewModel.adminAdjustUserCoins(userId, newCoins) },
                        onDeleteUser = { userId -> viewModel.adminDeleteUser(userId) },
                        onUpdateWithdrawalStatus = { txId, status ->
                            viewModel.adminUpdateWithdrawalStatus(txId, status)
                        },
                        onDismissMessage = { viewModel.clearMessages() }
                    )
                }
            }
        }
    }
}
