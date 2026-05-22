package com.ecowaste.app.data.local

import androidx.room.*

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    suspend fun getUser(uid: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)
}

@Dao
interface PickupDao {
    @Query("SELECT * FROM pickups WHERE userId = :userId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestForUser(userId: String): PickupEntity?

    @Insert
    suspend fun insert(pickup: PickupEntity)

    @Query("SELECT * FROM pickups WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getAllForUser(userId: String): List<PickupEntity>
}

@Dao
interface BankDao {
    @Query("SELECT * FROM bank_details WHERE userId = :userId LIMIT 1")
    suspend fun getForUser(userId: String): BankDetailsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bank: BankDetailsEntity)
}

