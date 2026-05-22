package com.ecowaste.app.pickup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecowaste.app.data.local.LocalStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Represents the UI state for the Pickup Request screen
data class PickupUiState(
    val currentStatus: String = "No active requests",
    val isRequesting: Boolean = false, // To control the dialog
    val isLoading: Boolean = true
)

class PickupViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PickupUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchCurrentPickupStatus()
    }

    private fun fetchCurrentPickupStatus() {
        viewModelScope.launch {
            val uid = LocalStorage.getCurrentUserId() ?: return@launch
            _uiState.update { it.copy(isLoading = true) }
            try {
                val latestPickup = LocalStorage.getLatestPickupStatus(uid)
                if (latestPickup == null) {
                    _uiState.update { it.copy(currentStatus = "No active requests", isLoading = false) }
                } else {
                    val status = latestPickup.status
                    val message = when (status.lowercase()) {
                        "pending" -> "Request sent for waste collection. You will be contacted soon for location confirmation."
                        "collected" -> "Your last pickup was completed!"
                        else -> "No active requests"
                    }
                    _uiState.update { it.copy(currentStatus = message, isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(currentStatus = "Error fetching status.", isLoading = false) }
            }
        }
    }

    fun submitPickupRequest(address: String, phone: String) {
        viewModelScope.launch {
            val uid = LocalStorage.getCurrentUserId() ?: return@launch
            try {
                LocalStorage.submitPickup(userId = uid, address = address, phone = phone)
                // Refresh status after submission
                fetchCurrentPickupStatus()
            } catch (e: Exception) {
                // Handle error silently for demo
            }
            // Hide the dialog
            _uiState.update { it.copy(isRequesting = false) }
        }
    }

    fun onNewRequestClicked() {
        _uiState.update { it.copy(isRequesting = true) }
    }

    fun onDismissDialog() {
        _uiState.update { it.copy(isRequesting = false) }
    }
}