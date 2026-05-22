package com.ecowaste.app.data.local

import android.content.Context
import android.content.SharedPreferences
import java.util.UUID

object LocalStorage {
    private const val PREFS = "ecowaste_prefs"
    private const val KEY_CURRENT_USER = "current_user_id"

    private lateinit var prefs: SharedPreferences
    private lateinit var db: AppDatabase

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        db = AppDatabase.getInstance(context)
    }

    // Auth methods
    suspend fun registerUser(name: String, email: String, password: String): Result<String> {
        // check existing
        val existing = db.userDao().findByEmail(email)
        if (existing != null) return Result.failure(Exception("This email is already registered."))
        val uid = UUID.randomUUID().toString()
        val user = UserEntity(uid = uid, name = name, email = email, password = password)
        db.userDao().insert(user)
        prefs.edit().putString(KEY_CURRENT_USER, uid).apply()
        return Result.success(uid)
    }

    suspend fun loginUser(email: String, password: String): Result<String> {
        val user = db.userDao().findByEmail(email) ?: return Result.failure(Exception("Invalid credentials."))
        return if (user.password == password) {
            prefs.edit().putString(KEY_CURRENT_USER, user.uid).apply()
            Result.success(user.uid)
        } else Result.failure(Exception("Invalid credentials."))
    }

    fun logout() {
        prefs.edit().remove(KEY_CURRENT_USER).apply()
    }

    fun getCurrentUserId(): String? = prefs.getString(KEY_CURRENT_USER, null)

    suspend fun getUserData(uid: String): UserEntity? = db.userDao().getUser(uid)

    // Pickup methods
    suspend fun submitPickup(userId: String, address: String, phone: String) {
        val pickup = PickupEntity(userId = userId, address = address, phone = phone)
        db.pickupDao().insert(pickup)
    }

    suspend fun getLatestPickupStatus(userId: String): PickupEntity? = db.pickupDao().getLatestForUser(userId)

    suspend fun getAllPickups(userId: String): List<PickupEntity> = db.pickupDao().getAllForUser(userId)

    // Bank details
    suspend fun getBankDetails(userId: String): BankDetailsEntity? = db.bankDao().getForUser(userId)

    suspend fun saveBankDetails(userId: String, accountNumber: String, accountTitle: String, bankName: String) {
        val bank = BankDetailsEntity(userId = userId, accountNumber = accountNumber, accountTitle = accountTitle, bankName = bankName)
        db.bankDao().insert(bank)
    }
}

