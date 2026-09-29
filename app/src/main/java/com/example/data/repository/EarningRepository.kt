package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.ReferralEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.VipRequestEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlin.random.Random

class EarningRepository(private val database: AppDatabase) {

    private val userDao = database.userDao()
    private val transactionDao = database.transactionDao()
    private val vipRequestDao = database.vipRequestDao()
    private val referralDao = database.referralDao()

    fun getUserStream(userId: Long): Flow<UserEntity?> = userDao.getUserById(userId)

    fun getTransactionsStream(userId: Long): Flow<List<TransactionEntity>> =
        transactionDao.getTransactionsByUserId(userId)

    fun getWithdrawalsStream(userId: Long): Flow<List<TransactionEntity>> =
        transactionDao.getWithdrawalsByUserId(userId)

    fun getAllUsersStream(): Flow<List<UserEntity>> = userDao.getAllUsers()

    fun getAllWithdrawalsStream(): Flow<List<TransactionEntity>> = transactionDao.getAllWithdrawals()

    fun getAllVipRequestsStream(): Flow<List<VipRequestEntity>> = vipRequestDao.getAllVipRequests()

    fun getVipRequestsStream(userId: Long): Flow<List<VipRequestEntity>> =
        vipRequestDao.getRequestsByUserId(userId)

    fun getReferralsStream(referralCode: String): Flow<List<ReferralEntity>> =
        referralDao.getReferralsByCode(referralCode)

    fun getAllReferralsStream(): Flow<List<ReferralEntity>> =
        referralDao.getAllReferrals()

    suspend fun registerUser(
        phone: String,
        password: String,
        fullName: String,
        email: String,
        address: String,
        referredByCode: String?
    ): Result<UserEntity> = withContext(Dispatchers.IO) {
        val cleanPhone = phone.trim()
        val existing = userDao.getUserByPhone(cleanPhone)
        if (existing != null) {
            return@withContext Result.failure(Exception("এই ফোন নাম্বার দিয়ে ইতিমধ্যে একাউন্ট রয়েছে!"))
        }

        val randomIdSuffix = (10000 + Random.nextInt(90000)).toString()
        val userIdCode = "TI-$randomIdSuffix"
        val referralCode = "TK" + (1000 + Random.nextInt(9000)).toString()

        val newUser = UserEntity(
            phone = cleanPhone,
            password = password.trim(),
            fullName = fullName.trim(),
            email = email.trim(),
            address = address.trim(),
            userIdCode = userIdCode,
            referralCode = referralCode,
            referredBy = referredByCode?.trim()?.takeIf { it.isNotEmpty() },
            coins = 100 // ১০০ কয়েন ওয়েলকাম বোনাস
        )

        val insertedId = userDao.insertUser(newUser)
        val savedUser = newUser.copy(id = insertedId)

        // Record Welcome Bonus transaction
        transactionDao.insertTransaction(
            TransactionEntity(
                userId = insertedId,
                type = "WELCOME_BONUS",
                title = "ওয়েলকাম সাইনআপ বোনাস",
                coins = 100,
                bdtAmount = 10.0,
                status = "COMPLETED"
            )
        )

        // Handle referral link if present
        if (!referredByCode.isNullOrBlank()) {
            val cleanCode = referredByCode.trim().uppercase()
            val referrer = userDao.getUserByReferralCode(cleanCode)
            if (referrer != null && referrer.id != insertedId) {
                referralDao.insertReferral(
                    ReferralEntity(
                        referrerCode = cleanCode,
                        referredUserId = insertedId,
                        referredUserName = fullName.trim(),
                        totalCommissionCoins = 0
                    )
                )
                // Award 20 coins referral signup bonus to referrer
                awardReferralCommission(referrer.id, 20, "বন্ধু যোগদানের বোনাস (${fullName.trim()})")
            }
        }

        Result.success(savedUser)
    }

    suspend fun loginUser(phone: String, password: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        val cleanPhone = phone.trim()
        val cleanPass = password.trim()
        val user = userDao.getUserByPhone(cleanPhone)
            ?: return@withContext Result.failure(Exception("এই নাম্বারে কোনো অ্যাকাউন্ট খুঁজে পাওয়া যায়নি!"))

        if (user.password != cleanPass) {
            return@withContext Result.failure(Exception("ভুল পাসওয়ার্ড! আবার চেষ্টা করুন।"))
        }

        if (user.isBanned) {
            return@withContext Result.failure(Exception("আপনার অ্যাকাউন্টটি এডমিন কর্তৃক সাময়িকভাবে ব্যান্ড (Banned) করা হয়েছে। সহায়তার জন্য WhatsApp-এ যোগাযোগ করুন।"))
        }

        Result.success(user)
    }

    suspend fun updateProfile(
        userId: Long,
        fullName: String,
        email: String,
        address: String,
        avatarIndex: Int
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByIdSync(userId)
            ?: return@withContext Result.failure(Exception("ইউজার পাওয়া যায়নি"))

        val updated = user.copy(
            fullName = fullName.trim(),
            email = email.trim(),
            address = address.trim(),
            avatarIndex = avatarIndex
        )
        userDao.updateUser(updated)
        Result.success(Unit)
    }

    suspend fun claimDailySpin(userId: Long, wonCoins: Int): Result<Int> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByIdSync(userId)
            ?: return@withContext Result.failure(Exception("ইউজার পাওয়া যায়নি"))

        val now = System.currentTimeMillis()
        val timeSinceLast = now - user.lastDailySpin
        val cooldownMs = 24 * 60 * 60 * 1000L // 24 hours

        if (user.lastDailySpin > 0 && timeSinceLast < cooldownMs) {
            val remainingHours = ((cooldownMs - timeSinceLast) / (1000 * 60 * 60)) + 1
            return@withContext Result.failure(Exception("আজকের স্পিন সম্পন্ন হয়েছে! আর $remainingHours ঘন্টা পর আবার স্পিন করতে পারবেন।"))
        }

        val bonusMultiplier = if (user.isVip) 1.5 else 1.0
        val finalCoins = (wonCoins * bonusMultiplier).toInt()
        val newBalance = user.coins + finalCoins

        userDao.updateUser(user.copy(coins = newBalance, lastDailySpin = now))

        transactionDao.insertTransaction(
            TransactionEntity(
                userId = userId,
                type = "SPIN",
                title = if (user.isVip) "দৈনিক স্পিন বোনাস (VIP x1.5)" else "দৈনিক লাকি স্পিন বোনাস",
                coins = finalCoins,
                bdtAmount = finalCoins / 10.0,
                status = "COMPLETED"
            )
        )

        // Give 10% commission to referrer
        checkAndGiveReferralCommission(user, finalCoins, "স্পিন থেকে রেফারেল কমিশন")

        Result.success(finalCoins)
    }

    suspend fun claimVideoAdReward(userId: Long): Result<Int> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByIdSync(userId)
            ?: return@withContext Result.failure(Exception("ইউজার পাওয়া যায়নি"))

        val now = System.currentTimeMillis()
        val isSameDay = isSameDay(user.lastAdDate, now)
        val currentCount = if (isSameDay) user.adsWatchedToday else 0

        if (!user.isVip && currentCount >= 50) {
            return@withContext Result.failure(Exception("আজকের ফ্রি লিমিট (৫০ টি অ্যাড) শেষ হয়েছে! আনলিমিটেড অ্যাড দেখতে ভিআইপি প্ল্যান সক্রিয় করুন।"))
        }

        val rewardCoins = 2
        val newBalance = user.coins + rewardCoins
        val newCount = currentCount + 1

        userDao.updateUser(
            user.copy(
                coins = newBalance,
                adsWatchedToday = newCount,
                lastAdDate = now
            )
        )

        transactionDao.insertTransaction(
            TransactionEntity(
                userId = userId,
                type = "AD_WATCH",
                title = if (user.isVip) "ভিআইপি ভিডিও অ্যাড রিওয়ার্ড (আনলিমিটেড)" else "ভিডিও বিজ্ঞাপন দেখার পুরস্কার ($newCount/৫০)",
                coins = rewardCoins,
                bdtAmount = rewardCoins / 10.0,
                status = "COMPLETED"
            )
        )

        // Give 10% commission to referrer
        checkAndGiveReferralCommission(user, rewardCoins, "ভিডিও বিজ্ঞাপন থেকে রেফারেল কমিশন")

        Result.success(rewardCoins)
    }

    private fun isSameDay(time1: Long, time2: Long): Boolean {
        if (time1 == 0L || time2 == 0L) return false
        val cal1 = java.util.Calendar.getInstance().apply { timeInMillis = time1 }
        val cal2 = java.util.Calendar.getInstance().apply { timeInMillis = time2 }
        return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
               cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR)
    }

    suspend fun startMining(userId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByIdSync(userId)
            ?: return@withContext Result.failure(Exception("ইউজার পাওয়া যায়নি"))

        if (!user.isVip) {
            return@withContext Result.failure(Exception("মাইনিং শুধুমাত্র ভিআইপি সদস্যদের জন্য! অনুগ্রহ করে ভিআইপি প্ল্যান সক্রিয় করুন।"))
        }

        val now = System.currentTimeMillis()
        if (user.miningStartTime > 0 && !user.miningClaimed) {
            val elapsed = now - user.miningStartTime
            if (elapsed < user.miningDurationMs) {
                return@withContext Result.failure(Exception("মাইনিং ইতিমধ্যে চলমান রয়েছে!"))
            }
        }

        userDao.updateUser(
            user.copy(
                miningStartTime = now,
                miningClaimed = false
            )
        )
        Result.success(Unit)
    }

    suspend fun claimMining(userId: Long): Result<Int> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByIdSync(userId)
            ?: return@withContext Result.failure(Exception("ইউজার পাওয়া যায়নি"))

        if (!user.isVip) {
            return@withContext Result.failure(Exception("শুধুমাত্র ভিআইপি প্ল্যানে মাইনিং পাওয়া যায়"))
        }

        if (user.miningStartTime == 0L || user.miningClaimed) {
            return@withContext Result.failure(Exception("কোনো সক্রিয় মাইনিং সেশন নেই! শুরু করতে 'স্টার্ট মাইনিং' চাপুন।"))
        }

        val now = System.currentTimeMillis()
        val elapsed = now - user.miningStartTime

        // Check 1 hour (3600000 ms)
        if (elapsed < user.miningDurationMs) {
            val remainingMin = ((user.miningDurationMs - elapsed) / (1000 * 60)) + 1
            return@withContext Result.failure(Exception("১ ঘন্টা পূর্ণ হতে আরও $remainingMin মিনিট বাকি আছে!"))
        }

        val miningCoins = 25 // প্রতি ঘন্টায় ২৫ কয়েন
        val newBalance = user.coins + miningCoins

        userDao.updateUser(
            user.copy(
                coins = newBalance,
                miningStartTime = 0L,
                miningClaimed = true
            )
        )

        transactionDao.insertTransaction(
            TransactionEntity(
                userId = userId,
                type = "MINING",
                title = "১ ঘন্টা ভিআইপি মাইনিং রিওয়ার্ড",
                coins = miningCoins,
                bdtAmount = miningCoins / 10.0,
                status = "COMPLETED"
            )
        )

        // Give 10% commission to referrer
        checkAndGiveReferralCommission(user, miningCoins, "মাইনিং থেকে রেফারেল কমিশন")

        Result.success(miningCoins)
    }

    suspend fun submitVipRequest(
        userId: Long,
        method: String,
        senderNumber: String,
        trxId: String,
        screenshotNote: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByIdSync(userId)
            ?: return@withContext Result.failure(Exception("ইউজার পাওয়া যায়নি"))

        val existingPending = vipRequestDao.getPendingRequestByUserId(userId)
        if (existingPending != null) {
            return@withContext Result.failure(Exception("আপনার একটি ভিআইপি রিকোয়েস্ট ইতিমধ্যে পেন্ডিং অবস্থায় আছে। এডমিন ভেরিফাই না করা পর্যন্ত নতুন রিকোয়েস্ট পাঠানো যাবে না।"))
        }

        if (senderNumber.trim().length < 11) {
            return@withContext Result.failure(Exception("সঠিক ১১ ডিজিটের ফোন নাম্বার দিন"))
        }

        if (trxId.trim().length < 6) {
            return@withContext Result.failure(Exception("সঠিক ট্রানজেকশন আইডি (TrxID) দিন"))
        }

        vipRequestDao.insertVipRequest(
            VipRequestEntity(
                userId = userId,
                userName = user.fullName,
                userPhone = user.phone,
                method = method,
                senderNumber = senderNumber.trim(),
                trxId = trxId.trim().uppercase(),
                amount = 599.0,
                screenshotNote = screenshotNote.trim(),
                status = "PENDING"
            )
        )

        Result.success(Unit)
    }

    suspend fun requestWithdrawal(
        userId: Long,
        method: String,
        accountNumber: String,
        coinsToWithdraw: Int
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByIdSync(userId)
            ?: return@withContext Result.failure(Exception("ইউজার পাওয়া যায়নি"))

        if (coinsToWithdraw < 1000) {
            return@withContext Result.failure(Exception("সর্বনিম্ন উইথড্র ১০০০ কয়েন (১০০ টাকা)"))
        }

        if (coinsToWithdraw > user.coins) {
            return@withContext Result.failure(Exception("পর্যাপ্ত কয়েন ব্যালেন্স নেই! আপনার ব্যালেন্স: ${user.coins} কয়েন"))
        }

        if (accountNumber.trim().length < 11) {
            return@withContext Result.failure(Exception("সঠিক ১১ ডিজিটের অ্যাকাউন্ট নাম্বার দিন"))
        }

        val bdtAmount = coinsToWithdraw / 10.0 // 1000 coins = 100 BDT
        val newBalance = user.coins - coinsToWithdraw

        userDao.updateCoins(userId, newBalance)

        transactionDao.insertTransaction(
            TransactionEntity(
                userId = userId,
                type = "WITHDRAWAL",
                title = "$method ক্যাশ আউট রিকোয়েস্ট",
                coins = -coinsToWithdraw,
                bdtAmount = bdtAmount,
                status = "PENDING",
                paymentMethod = method,
                accountNumber = accountNumber.trim(),
                trxId = "WTH-" + (100000 + Random.nextInt(900000))
            )
        )

        Result.success(Unit)
    }

    // Admin Operations
    suspend fun approveVipRequest(requestId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        val req = vipRequestDao.getRequestById(requestId)
            ?: return@withContext Result.failure(Exception("রিকোয়েস্ট পাওয়া যায়নি"))

        vipRequestDao.updateRequestStatus(requestId, "APPROVED")
        userDao.updateVipStatus(req.userId, true)

        transactionDao.insertTransaction(
            TransactionEntity(
                userId = req.userId,
                type = "VIP_PURCHASE",
                title = "ভিআইপি মেম্বারশিপ অ্যাক্টিভ (৳৫৯৯)",
                coins = 0,
                bdtAmount = 599.0,
                status = "COMPLETED",
                paymentMethod = req.method,
                trxId = req.trxId
            )
        )
        Result.success(Unit)
    }

    suspend fun rejectVipRequest(requestId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        val req = vipRequestDao.getRequestById(requestId)
            ?: return@withContext Result.failure(Exception("রিকোয়েস্ট পাওয়া যায়নি"))

        vipRequestDao.updateRequestStatus(requestId, "REJECTED")
        Result.success(Unit)
    }

    suspend fun setVipStatusForUser(userId: Long, isVip: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        userDao.updateVipStatus(userId, isVip)
        Result.success(Unit)
    }

    suspend fun toggleBanUser(userId: Long, isBanned: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        userDao.updateBanStatus(userId, isBanned)
        Result.success(Unit)
    }

    suspend fun adjustUserCoins(userId: Long, newCoins: Int): Result<Unit> = withContext(Dispatchers.IO) {
        userDao.updateCoins(userId, maxOf(0, newCoins))
        Result.success(Unit)
    }

    suspend fun deleteUser(userId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByIdSync(userId)
            ?: return@withContext Result.failure(Exception("ইউজার খুঁজে পাওয়া যায়নি"))

        userDao.deleteUserById(userId)
        userDao.deleteTransactionsByUserId(userId)
        userDao.deleteVipRequestsByUserId(userId)
        referralDao.deleteReferralsForUser(user.referralCode, userId)
        Result.success(Unit)
    }

    suspend fun updateWithdrawalStatus(transactionId: Long, status: String): Result<Unit> = withContext(Dispatchers.IO) {
        val tx = transactionDao.getTransactionById(transactionId)
            ?: return@withContext Result.failure(Exception("উইথড্র রিকোয়েস্ট পাওয়া যায়নি"))

        if (tx.status == "PENDING" && status == "REJECTED") {
            // Refund coins
            val user = userDao.getUserByIdSync(tx.userId)
            if (user != null) {
                val refundCoins = kotlin.math.abs(tx.coins)
                userDao.updateCoins(user.id, user.coins + refundCoins)
            }
        }
        transactionDao.updateTransactionStatus(transactionId, status)
        Result.success(Unit)
    }

    private suspend fun checkAndGiveReferralCommission(user: UserEntity, earnedCoins: Int, reason: String) {
        if (!user.referredBy.isNullOrBlank()) {
            val referrer = userDao.getUserByReferralCode(user.referredBy)
            if (referrer != null && referrer.id != user.id) {
                // 10% commission (minimum 1 coin if earned > 0)
                val commission = maxOf(1, (earnedCoins * 0.10).toInt())
                awardReferralCommission(referrer.id, commission, "$reason (${user.fullName})")
                referralDao.addCommission(user.referredBy, user.id, commission)
            }
        }
    }

    private suspend fun awardReferralCommission(referrerId: Long, coins: Int, title: String) {
        val referrer = userDao.getUserByIdSync(referrerId) ?: return
        val newBalance = referrer.coins + coins
        userDao.updateCoins(referrerId, newBalance)
        transactionDao.insertTransaction(
            TransactionEntity(
                userId = referrerId,
                type = "REFERRAL_BONUS",
                title = title,
                coins = coins,
                bdtAmount = coins / 10.0,
                status = "COMPLETED"
            )
        )
    }
}
