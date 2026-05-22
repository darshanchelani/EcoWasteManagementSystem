package com.ecowaste.app.data

data class Pickup(
    val id: String = "",
    val userId: String = "",
    val address: String = "",
    val phone: String = "",
    val status: String = "",
    val timestamp: Long = System.currentTimeMillis()
)