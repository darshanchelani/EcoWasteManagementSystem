package com.ecowaste.app.bank

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecowaste.app.data.local.LocalStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Represents the data for a payment transaction
data class PaymentHistoryItem(
    val date: String,
    val amount: String,
    val transactionId: String
)

// Represents the UI state for the Bank Details screen
data class BankDetailsUiState(
    val isLoading: Boolean = true,
    val hasBankDetails: Boolean = false,
    val earnedAmount: String = "0 PKR",
    val accountNumber: String = "",
    val accountTitle: String = "",
    val bankName: String = "",
    val paymentHistory: List<PaymentHistoryItem> = emptyList(),
    val errorMessage: String? = null
)

class BankDetailsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BankDetailsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchBankDetails()
    }

    private fun fetchBankDetails() {
        viewModelScope.launch {
            val uid = LocalStorage.getCurrentUserId() ?: return@launch
            _uiState.update { it.copy(isLoading = true) }
            try {
                val bankInfo = LocalStorage.getBankDetails(uid)
                if (bankInfo != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            hasBankDetails = true,
                            accountNumber = bankInfo.accountNumber,
                            accountTitle = bankInfo.accountTitle,
                            bankName = bankInfo.bankName,
                            earnedAmount = "1500 PKR",
                            paymentHistory = listOf(PaymentHistoryItem("15 Nov 2025", "500 PKR", "#12345"))
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, hasBankDetails = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Failed to load details.") }
            }
        }
    }

    fun saveBankDetails(accountNumber: String, accountTitle: String, bankName: String) {
        // Real-time validation for JazzCash
        if (!bankName.equals("JazzCash", ignoreCase = true)) {
            _uiState.update { it.copy(errorMessage = "Only JazzCash is supported.") }
            return
        }

        viewModelScope.launch {
            val uid = LocalStorage.getCurrentUserId() ?: return@launch
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                LocalStorage.saveBankDetails(uid, accountNumber, accountTitle, bankName)
                // Refresh the state to show the details view
                fetchBankDetails()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Failed to save details.") }
            }
        }
    }
}