package com.ecowaste.app.withdrawal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class CardBrand {
    VISA, MASTERCARD, UNKNOWN
}

data class WithdrawalUiState(
    val balance: Double = 1500.0, // Placeholder balance
    val totalWaste: Double = 20.0, // Placeholder waste
    val withdrawalAmount: String = "",
    val isAmountValid: Boolean = true,
    val showWithdrawalDialog: Boolean = false, // Controls the new dialog

    // Card Details
    val accountHolderName: String = "",
    val cardNumber: String = "",
    val cvv: String = "",
    val expiryDate: String = "",
    val cardBrand: CardBrand = CardBrand.UNKNOWN,

    // UI Control
    val showSuccessDialog: Boolean = false,
    val snackbarMessage: String? = null,
    val isWithdrawalInProgress: Boolean = false,
    val formError: String? = null
)

class WithdrawalViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WithdrawalUiState())
    val uiState = _uiState.asStateFlow()

    fun onAmountChanged(amount: String) {
        if (amount.all { it.isDigit() }) {
            _uiState.update { it.copy(withdrawalAmount = amount, isAmountValid = true) }
        }
    }

    fun onQuickAmountSelected(amount: Double) {
        _uiState.update { it.copy(withdrawalAmount = amount.toInt().toString(), isAmountValid = true) }
    }

    fun onProceedClicked() {
        val amount = _uiState.value.withdrawalAmount.toDoubleOrNull() ?: 0.0
        val state = _uiState.value

        // Validation checks
        if (amount > state.balance) {
            _uiState.update { it.copy(isAmountValid = false, formError = "Amount exceeds available balance.") }
            return
        }
        if (amount < 100) {
            _uiState.update { it.copy(isAmountValid = false, formError = "Minimum withdrawal amount is 100 PKR.") }
            return
        }
        if (state.totalWaste < 15) {
             _uiState.update { it.copy(isAmountValid = false, formError = "You need to contribute at least 15kg of waste.") }
            return
        }

        // If all checks pass, show the dialog
        _uiState.update { it.copy(showWithdrawalDialog = true, formError = null, isAmountValid = true) }
    }

    fun onAccountHolderNameChanged(name: String) {
        _uiState.update { it.copy(accountHolderName = name) }
    }

    fun onCardNumberChanged(number: String) {
        if (number.length <= 16 && number.all { it.isDigit() }) {
            val brand = when {
                number.startsWith("4") -> CardBrand.VISA
                number.startsWith("5") -> CardBrand.MASTERCARD
                else -> CardBrand.UNKNOWN
            }
            _uiState.update { it.copy(cardNumber = number, cardBrand = brand) }
        }
    }

    fun onCvvChanged(cvv: String) {
        if (cvv.length <= 3 && cvv.all { it.isDigit() }) {
            _uiState.update { it.copy(cvv = cvv) }
        }
    }

    fun onExpiryDateChanged(date: String) {
        var formattedDate = date.filter { it.isDigit() }
        if (date.length == 3 && !date.contains("/")) { // Auto-add slash after MM
            formattedDate = date.substring(0, 2) + "/" + date.substring(2)
        }
        if (formattedDate.length <= 5) {
            _uiState.update { it.copy(expiryDate = formattedDate) }
        }
    }

    private fun validateCardDetails(): Boolean {
        val state = _uiState.value
        if (state.accountHolderName.isBlank()) {
            _uiState.update { it.copy(formError = "Account holder name cannot be empty.") }
            return false
        }
        if (state.cardNumber.length != 16) {
            _uiState.update { it.copy(formError = "Card number must be 16 digits.") }
            return false
        }
        if (state.cvv.length != 3) {
            _uiState.update { it.copy(formError = "CVV must be 3 digits.") }
            return false
        }
        if (state.expiryDate.length != 5 || !state.expiryDate.contains("/")) { // Basic check
            _uiState.update { it.copy(formError = "Enter a valid expiry date (MM/YY).") }
            return false
        }
        _uiState.update { it.copy(formError = null) }
        return true
    }

    fun onWithdrawClicked() {
        if (!validateCardDetails()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isWithdrawalInProgress = true) }
            delay(1500) // Simulate network call
            val amount = _uiState.value.withdrawalAmount.toDoubleOrNull() ?: 0.0
            val newBalance = _uiState.value.balance - amount

            _uiState.update {
                it.copy(
                    balance = newBalance,
                    showSuccessDialog = true,
                    showWithdrawalDialog = false,
                    withdrawalAmount = "",
                    isWithdrawalInProgress = false,
                    snackbarMessage = "Withdrawal successful! $amount PKR transferred.",
                    // Clear fields after withdrawal
                    accountHolderName = "",
                    cardNumber = "",
                    cvv = "",
                    expiryDate = ""
                )
            }
        }
    }

    fun dismissAllDialogs() {
        _uiState.update { it.copy(showSuccessDialog = false, showWithdrawalDialog = false) }
    }

    fun snackbarShown() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}