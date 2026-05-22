package com.ecowaste.app.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecowaste.app.data.local.LocalStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Represents the different states of an authentication process
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object RegistrationSuccess : AuthState() // New state for successful registration
    data class Authenticated(val isAdmin: Boolean) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    fun registerUser(name: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            if (name.isBlank() || email.isBlank() || password.isBlank()) {
                _authState.value = AuthState.Error("Please fill in all required fields.")
                return@launch
            }
            if (password.length < 8) {
                _authState.value = AuthState.Error("Password must be at least 8 characters long.")
                return@launch
            }
            try {
                val res = LocalStorage.registerUser(name = name, email = email, password = password)
                if (res.isSuccess) {
                    _authState.value = AuthState.RegistrationSuccess
                } else {
                    _authState.value = AuthState.Error(res.exceptionOrNull()?.message ?: "Registration failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "An unknown error occurred.")
            }
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            if (email.isBlank() || password.isBlank()) {
                _authState.value = AuthState.Error("Please enter both email and password.")
                return@launch
            }
            try {
                val res = LocalStorage.loginUser(email = email, password = password)
                if (res.isSuccess) {
                    // For demo: treat all users as non-admin unless role is stored and checked elsewhere
                    _authState.value = AuthState.Authenticated(isAdmin = false)
                } else {
                    _authState.value = AuthState.Error(res.exceptionOrNull()?.message ?: "Login failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "An unknown error occurred.")
            }
        }
    }
}