package com.ecowaste.app.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecowaste.app.data.local.LocalStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UserData(
    val name: String = "User",
    val totalWaste: Double = 0.0
)

class UserViewModel : ViewModel() {
    private val _userData = MutableStateFlow(UserData())
    val userData = _userData.asStateFlow()

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        viewModelScope.launch {
            val uid = LocalStorage.getCurrentUserId() ?: return@launch
            try {
                val user = LocalStorage.getUserData(uid)
                val name = user?.name ?: "User"
                val totalWaste = 0.0
                _userData.value = UserData(name = name, totalWaste = totalWaste)
            } catch (e: Exception) {
                // Handle error silently for demo
            }
        }
    }
}