package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.UserEntity
import com.example.data.repository.EarningRepository
import com.example.ui.model.EarningUiState
import com.example.ui.model.Screen
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = EarningRepository(AppDatabase.getDatabase(application))
    private val prefs = application.getSharedPreferences("taka_income_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(EarningUiState())
    val uiState: StateFlow<EarningUiState> = _uiState.asStateFlow()

    private var navigationStack = mutableListOf<Screen>()
    private var miningTimerJob: Job? = null
    private var adTimerJob: Job? = null

    init {
        checkAutoLogin()
        observeAdminData()
    }

    private fun checkAutoLogin() {
        val savedUserId = prefs.getLong("logged_in_user_id", -1L)
        if (savedUserId != -1L) {
            viewModelScope.launch {
                repository.getUserStream(savedUserId).collect { user ->
                    if (user != null) {
                        if (user.isBanned) {
                            prefs.edit().remove("logged_in_user_id").apply()
                            _uiState.update {
                                it.copy(
                                    currentUser = null,
                                    currentScreen = Screen.AUTH,
                                    errorMessage = "আপনার একাউন্টটি এডমিন কর্তৃক সাময়িকভাবে ব্যান্ড (Banned) করা হয়েছে।"
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(
                                    currentUser = user,
                                    currentScreen = if (it.currentScreen == Screen.AUTH) Screen.DASHBOARD else it.currentScreen
                                )
                            }
                            observeUserData(user.id, user.referralCode)
                            updateMiningState(user)
                        }
                    } else {
                        prefs.edit().remove("logged_in_user_id").apply()
                        _uiState.update { it.copy(currentUser = null, currentScreen = Screen.AUTH) }
                    }
                }
            }
        }
    }

    private fun observeUserData(userId: Long, referralCode: String) {
        viewModelScope.launch {
            repository.getTransactionsStream(userId).collect { txs ->
                _uiState.update { it.copy(userTransactions = txs) }
            }
        }
        viewModelScope.launch {
            repository.getWithdrawalsStream(userId).collect { wths ->
                _uiState.update { it.copy(userWithdrawals = wths) }
            }
        }
        viewModelScope.launch {
            repository.getVipRequestsStream(userId).collect { reqs ->
                _uiState.update { it.copy(userVipRequests = reqs) }
            }
        }
        viewModelScope.launch {
            repository.getReferralsStream(referralCode).collect { refs ->
                _uiState.update { it.copy(userReferrals = refs) }
            }
        }
    }

    private fun observeAdminData() {
        viewModelScope.launch {
            repository.getAllUsersStream().collect { users ->
                _uiState.update { it.copy(allUsers = users) }
            }
        }
        viewModelScope.launch {
            repository.getAllWithdrawalsStream().collect { wths ->
                _uiState.update { it.copy(allWithdrawals = wths) }
            }
        }
        viewModelScope.launch {
            repository.getAllVipRequestsStream().collect { reqs ->
                _uiState.update { it.copy(allVipRequests = reqs) }
            }
        }
        viewModelScope.launch {
            repository.getAllReferralsStream().collect { refs ->
                _uiState.update { it.copy(allReferrals = refs) }
            }
        }
    }

    fun navigateTo(screen: Screen) {
        if (_uiState.value.currentScreen != screen) {
            navigationStack.add(_uiState.value.currentScreen)
            _uiState.update { it.copy(currentScreen = screen, errorMessage = null, successMessage = null) }
        }
    }

    fun navigateBack(): Boolean {
        if (navigationStack.isNotEmpty()) {
            val previous = navigationStack.removeAt(navigationStack.size - 1)
            _uiState.update { it.copy(currentScreen = previous, errorMessage = null, successMessage = null) }
            return true
        }
        return false
    }

    fun toggleAuthMode() {
        _uiState.update { it.copy(isLoginMode = !it.isLoginMode, errorMessage = null) }
    }

    fun login(phone: String, pass: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.loginUser(phone, pass)
            result.onSuccess { user ->
                prefs.edit().putLong("logged_in_user_id", user.id).apply()
                _uiState.update {
                    it.copy(
                        currentUser = user,
                        currentScreen = Screen.DASHBOARD,
                        isLoading = false,
                        successMessage = "স্বাগতম ${user.fullName}!"
                    )
                }
                observeUserData(user.id, user.referralCode)
                updateMiningState(user)
            }.onFailure { err ->
                _uiState.update { it.copy(isLoading = false, errorMessage = err.message ?: "লগইন ব্যর্থ হয়েছে") }
            }
        }
    }

    fun signup(phone: String, pass: String, name: String, email: String, address: String, refCode: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.registerUser(phone, pass, name, email, address, refCode)
            result.onSuccess { user ->
                prefs.edit().putLong("logged_in_user_id", user.id).apply()
                _uiState.update {
                    it.copy(
                        currentUser = user,
                        currentScreen = Screen.DASHBOARD,
                        isLoading = false,
                        successMessage = "অ্যাকাউন্ট সফলভাবে তৈরি হয়েছে! ১০০ কয়েন ওয়েলকাম বোনাস।"
                    )
                }
                observeUserData(user.id, user.referralCode)
                updateMiningState(user)
            }.onFailure { err ->
                _uiState.update { it.copy(isLoading = false, errorMessage = err.message ?: "নিবন্ধন ব্যর্থ হয়েছে") }
            }
        }
    }

    fun logout() {
        prefs.edit().remove("logged_in_user_id").apply()
        navigationStack.clear()
        miningTimerJob?.cancel()
        adTimerJob?.cancel()
        _uiState.update {
            it.copy(
                currentUser = null,
                currentScreen = Screen.AUTH,
                isLoginMode = true,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun updateProfile(name: String, email: String, address: String, avatarIndex: Int) {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val res = repository.updateProfile(user.id, name, email, address, avatarIndex)
            res.onSuccess {
                _uiState.update { it.copy(isLoading = false, successMessage = "প্রোফাইল আপডেট সফল হয়েছে") }
            }.onFailure { err ->
                _uiState.update { it.copy(isLoading = false, errorMessage = err.message) }
            }
        }
    }

    // Daily Spin
    fun spinWheel(awardedCoins: Int, onResult: (Int) -> Unit) {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            val res = repository.claimDailySpin(user.id, awardedCoins)
            res.onSuccess { coins ->
                onResult(coins)
                _uiState.update { it.copy(successMessage = "অভিনন্দন! আপনি $coins কয়েন জিতেছেন!") }
            }.onFailure { err ->
                _uiState.update { it.copy(errorMessage = err.message) }
            }
        }
    }

    // Video Ads
    fun startWatchingAd() {
        val user = _uiState.value.currentUser ?: return
        adTimerJob?.cancel()
        _uiState.update {
            it.copy(
                isAdPlaying = true,
                adSecondsRemaining = 15,
                canClaimAdReward = false,
                currentAdIndex = (it.currentAdIndex + 1) % 5
            )
        }
        adTimerJob = viewModelScope.launch {
            for (i in 14 downTo 0) {
                delay(1000L)
                _uiState.update { it.copy(adSecondsRemaining = i) }
            }
            // Add reward immediately as soon as video ad finishes
            val res = repository.claimVideoAdReward(user.id)
            res.onSuccess { coins ->
                _uiState.update {
                    it.copy(
                        canClaimAdReward = true,
                        successMessage = "অভিনন্দন! ভিডিও দেখা শেষ এবং সাথে সাথে $coins কয়েন যোগ হয়েছে।"
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        canClaimAdReward = true,
                        errorMessage = err.message
                    )
                }
            }
        }
    }

    fun claimAdReward() {
        adTimerJob?.cancel()
        _uiState.update {
            it.copy(
                isAdPlaying = false,
                canClaimAdReward = false
            )
        }
    }

    fun closeAd() {
        adTimerJob?.cancel()
        _uiState.update { it.copy(isAdPlaying = false, canClaimAdReward = false) }
    }

    // VIP Mining
    private fun updateMiningState(user: UserEntity) {
        miningTimerJob?.cancel()
        if (user.miningStartTime > 0 && !user.miningClaimed) {
            miningTimerJob = viewModelScope.launch {
                while (true) {
                    val now = System.currentTimeMillis()
                    val elapsed = now - user.miningStartTime
                    val remaining = maxOf(0L, (user.miningDurationMs - elapsed) / 1000L)
                    val isDone = remaining == 0L
                    _uiState.update {
                        it.copy(
                            isMiningActive = !isDone,
                            isMiningCompleted = isDone,
                            miningRemainingSeconds = remaining
                        )
                    }
                    if (isDone) break
                    delay(1000L)
                }
            }
        } else {
            _uiState.update {
                it.copy(
                    isMiningActive = false,
                    isMiningCompleted = false,
                    miningRemainingSeconds = 0L
                )
            }
        }
    }

    fun startMining() {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            val res = repository.startMining(user.id)
            res.onSuccess {
                _uiState.update { it.copy(successMessage = "মাইনিং চালু হয়েছে! ১ ঘন্টা পর ২৫ কয়েন পাবেন।") }
            }.onFailure { err ->
                _uiState.update { it.copy(errorMessage = err.message) }
            }
        }
    }

    fun claimMining() {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            val res = repository.claimMining(user.id)
            res.onSuccess { coins ->
                _uiState.update {
                    it.copy(
                        isMiningActive = false,
                        isMiningCompleted = false,
                        successMessage = "অভিনন্দন! মাইনিং থেকে $coins কয়েন আপনার ব্যালেন্সে যোগ হয়েছে।"
                    )
                }
            }.onFailure { err ->
                _uiState.update { it.copy(errorMessage = err.message) }
            }
        }
    }

    // Fast-forward test mining for demonstration/testing convenience
    fun fastForwardMiningForTesting() {
        val user = _uiState.value.currentUser ?: return
        if (user.miningStartTime > 0 && !user.miningClaimed) {
            viewModelScope.launch {
                // Set start time to 61 minutes ago
                val simulatedStart = System.currentTimeMillis() - 3700000L
                AppDatabase.getDatabase(getApplication()).userDao().updateUser(
                    user.copy(miningStartTime = simulatedStart)
                )
                _uiState.update { it.copy(isMiningActive = false, isMiningCompleted = true, miningRemainingSeconds = 0L) }
            }
        }
    }

    // VIP Request
    fun submitVipPurchase(method: String, senderNumber: String, trxId: String, note: String) {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val res = repository.submitVipRequest(user.id, method, senderNumber, trxId, note)
            res.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "ভিআইপি রিকোয়েস্ট সফলভাবে সাবমিট হয়েছে! ভেরিফিকেশনের পর একটিভ হবে।"
                    )
                }
            }.onFailure { err ->
                _uiState.update { it.copy(isLoading = false, errorMessage = err.message) }
            }
        }
    }

    // Withdraw Request
    fun submitWithdrawal(method: String, accountNumber: String, coins: Int) {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val res = repository.requestWithdrawal(user.id, method, accountNumber, coins)
            res.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "উইথড্র রিকোয়েস্ট জমা হয়েছে! শীঘ্রই আপনার অ্যাকাউন্টে টাকা পৌঁছে যাবে।"
                    )
                }
            }.onFailure { err ->
                _uiState.update { it.copy(isLoading = false, errorMessage = err.message) }
            }
        }
    }

    // Admin Controls
    fun verifyAdminSecretCode(code: String): Boolean {
        if (code.trim() == "08520852") {
            navigateTo(Screen.ADMIN)
            _uiState.update { it.copy(successMessage = "সিক্রেট কোড ভেরিফাইড! এডমিন প্যানেলে স্বাগতম।", errorMessage = null) }
            return true
        } else {
            _uiState.update { it.copy(errorMessage = "ভুল সিক্রেট কোড! এডমিন এক্সেস প্রত্যাখ্যাত।") }
            return false
        }
    }

    fun adminApproveVip(requestId: Long) {
        viewModelScope.launch {
            val res = repository.approveVipRequest(requestId)
            res.onSuccess {
                _uiState.update { it.copy(successMessage = "ভিআইপি রিকোয়েস্ট অ্যাপ্রুভ করা হয়েছে এবং মেম্বারশিপ সক্রিয় করা হয়েছে!") }
            }.onFailure { err ->
                _uiState.update { it.copy(errorMessage = err.message) }
            }
        }
    }

    fun adminRejectVip(requestId: Long) {
        viewModelScope.launch {
            val res = repository.rejectVipRequest(requestId)
            res.onSuccess {
                _uiState.update { it.copy(successMessage = "ভিআইপি রিকোয়েস্ট বাতিল করা হয়েছে!") }
            }.onFailure { err ->
                _uiState.update { it.copy(errorMessage = err.message) }
            }
        }
    }

    fun adminToggleBanUser(userId: Long, isBanned: Boolean) {
        viewModelScope.launch {
            val res = repository.toggleBanUser(userId, isBanned)
            res.onSuccess {
                val actionName = if (isBanned) "ব্যান্ড (Banned)" else "আনব্যান্ড (Active)"
                _uiState.update { it.copy(successMessage = "ইউজার সফলভাবে $actionName করা হয়েছে") }
            }.onFailure { err ->
                _uiState.update { it.copy(errorMessage = err.message) }
            }
        }
    }

    fun adminAdjustUserCoins(userId: Long, newCoins: Int) {
        viewModelScope.launch {
            val res = repository.adjustUserCoins(userId, newCoins)
            res.onSuccess {
                _uiState.update { it.copy(successMessage = "ইউজারের কয়েন ব্যালেন্স $newCoins কয়েনে আপডেট হয়েছে") }
            }.onFailure { err ->
                _uiState.update { it.copy(errorMessage = err.message) }
            }
        }
    }

    fun adminDeleteUser(userId: Long) {
        viewModelScope.launch {
            val res = repository.deleteUser(userId)
            res.onSuccess {
                _uiState.update { it.copy(successMessage = "ইউজার অ্যাকাউন্ট স্থায়ীভাবে ডিলিট করা হয়েছে") }
            }.onFailure { err ->
                _uiState.update { it.copy(errorMessage = err.message) }
            }
        }
    }

    fun adminSetUserVip(userId: Long, isVip: Boolean) {
        viewModelScope.launch {
            repository.setVipStatusForUser(userId, isVip)
            _uiState.update { it.copy(successMessage = "ইউজারের ভিআইপি স্ট্যাটাস পরিবর্তন হয়েছে") }
        }
    }

    fun adminUpdateWithdrawalStatus(txId: Long, status: String) {
        viewModelScope.launch {
            repository.updateWithdrawalStatus(txId, status)
            val msg = if (status == "REJECTED") "উইথড্র বাতিল করা হয়েছে এবং কয়েন ইউজারের ব্যালেন্সে ফেরত দেওয়া হয়েছে।" else "উইথড্র স্ট্যাটাস আপডেট করা হয়েছে ($status)"
            _uiState.update { it.copy(successMessage = msg) }
        }
    }

    // Helper for testing referral system
    fun simulateReferral(referrerCode: String) {
        viewModelScope.launch {
            val randomSuffix = (1000 + kotlin.random.Random.nextInt(9000)).toString()
            val dummyPhone = "018$randomSuffix"
            val dummyName = "বন্ধু #$randomSuffix"
            val res = repository.registerUser(
                phone = dummyPhone,
                password = "123",
                fullName = dummyName,
                email = "friend$randomSuffix@test.com",
                address = "ঢাকা",
                referredByCode = referrerCode
            )
            res.onSuccess {
                _uiState.update { it.copy(successMessage = "নতুন বন্ধু $dummyName সফলভাবে যোগ হয়েছে! রেফারেল বোনাস যোগ হয়েছে।") }
            }.onFailure { err ->
                _uiState.update { it.copy(errorMessage = err.message) }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
