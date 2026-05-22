package com.ecowaste.app.data

data class User(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val wallet: Double = 0.0,
    val isAdmin: Boolean = false,
    val jazzCashNumber: String = ""
)