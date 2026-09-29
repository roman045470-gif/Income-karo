package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.VipRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VipRequestDao {
    @Query("SELECT * FROM vip_requests WHERE userId = :userId ORDER BY timestamp DESC")
    fun getRequestsByUserId(userId: Long): Flow<List<VipRequestEntity>>

    @Query("SELECT * FROM vip_requests WHERE userId = :userId AND status = 'PENDING' LIMIT 1")
    suspend fun getPendingRequestByUserId(userId: Long): VipRequestEntity?

    @Query("SELECT * FROM vip_requests WHERE id = :id LIMIT 1")
    suspend fun getRequestById(id: Long): VipRequestEntity?

    @Query("SELECT * FROM vip_requests ORDER BY timestamp DESC")
    fun getAllVipRequests(): Flow<List<VipRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVipRequest(request: VipRequestEntity): Long

    @Update
    suspend fun updateVipRequest(request: VipRequestEntity)

    @Query("UPDATE vip_requests SET status = :status WHERE id = :id")
    suspend fun updateRequestStatus(id: Long, status: String)
}
