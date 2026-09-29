package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ReferralEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReferralDao {
    @Query("SELECT * FROM referrals WHERE referrerCode = :code ORDER BY joinedDate DESC")
    fun getReferralsByCode(code: String): Flow<List<ReferralEntity>>

    @Query("SELECT * FROM referrals ORDER BY joinedDate DESC")
    fun getAllReferrals(): Flow<List<ReferralEntity>>

    @Query("SELECT COUNT(*) FROM referrals WHERE referrerCode = :code")
    suspend fun getReferralCountByCode(code: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReferral(referral: ReferralEntity): Long

    @Update
    suspend fun updateReferral(referral: ReferralEntity)

    @Query("UPDATE referrals SET totalCommissionCoins = totalCommissionCoins + :coins WHERE referrerCode = :code AND referredUserId = :referredUserId")
    suspend fun addCommission(code: String, referredUserId: Long, coins: Int)

    @Query("DELETE FROM referrals WHERE referrerCode = :code OR referredUserId = :userId")
    suspend fun deleteReferralsForUser(code: String, userId: Long)
}
