package com.ecowaste.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uid: String,
    val name: String,
    val email: String,
    val password: String,
    val role: String = "user"
)

@Entity(tableName = "pickups")
data class PickupEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val address: String,
    val phone: String,
    val status: String = "pending",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "bank_details")
data class BankDetailsEntity(
    @PrimaryKey val userId: String,
    val accountNumber: String,
    val accountTitle: String,
    val bankName: String
)

