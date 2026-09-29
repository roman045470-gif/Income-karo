package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: Long): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserByIdSync(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): UserEntity?

    @Query("SELECT * FROM users WHERE referralCode = :code LIMIT 1")
    suspend fun getUserByReferralCode(code: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET coins = :coins WHERE id = :userId")
    suspend fun updateCoins(userId: Long, coins: Int)

    @Query("UPDATE users SET isVip = :isVip WHERE id = :userId")
    suspend fun updateVipStatus(userId: Long, isVip: Boolean)

    @Query("UPDATE users SET isBanned = :isBanned WHERE id = :userId")
    suspend fun updateBanStatus(userId: Long, isBanned: Boolean)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserById(userId: Long)

    @Query("DELETE FROM transactions WHERE userId = :userId")
    suspend fun deleteTransactionsByUserId(userId: Long)

    @Query("DELETE FROM vip_requests WHERE userId = :userId")
    suspend fun deleteVipRequestsByUserId(userId: Long)
}
