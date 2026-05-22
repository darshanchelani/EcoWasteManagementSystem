package com.ecowaste.app.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ecowaste.app.R
import com.ecowaste.app.withdrawal.CardBrand
import com.ecowaste.app.withdrawal.WithdrawalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankDetailsScreen(navController: NavController, withdrawalViewModel: WithdrawalViewModel = viewModel()) {
    val uiState by withdrawalViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            withdrawalViewModel.snackbarShown()
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFF4CAF50), Color.White)))
                .padding(it)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BalanceCard(balance = uiState.balance, totalWaste = uiState.totalWaste)
                QuickSelectButtons(withdrawalViewModel)
                AmountInput(uiState, withdrawalViewModel)
                WithdrawalGuidelines()
            }
        }

        if (uiState.showWithdrawalDialog) {
            WithdrawalDialog(uiState, withdrawalViewModel) { withdrawalViewModel.dismissAllDialogs() }
        }

        if (uiState.showSuccessDialog) {
            SuccessDialog(uiState.withdrawalAmount) { withdrawalViewModel.dismissAllDialogs() }
        }
    }
}

@Composable
fun BalanceCard(balance: Double, totalWaste: Double) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(8.dp)) {
        Box(modifier = Modifier.background(Brush.linearGradient(colors = listOf(Color(0xFFFF5722), Color(0xFFF44336))))) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Available Balance", color = Color.White.copy(alpha = 0.9f), fontSize = 18.sp)
                Text("${String.format("%.2f", balance)} PKR", color = Color.White, fontSize = 32.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Total Waste Contributed: ${String.format("%.1f", totalWaste)} kg", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun QuickSelectButtons(viewModel: WithdrawalViewModel) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf(100.0, 200.0, 500.0).forEach { amount ->
            Button(onClick = { viewModel.onQuickAmountSelected(amount) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) {
                Text("${amount.toInt()} PKR")
            }
        }
    }
}

@Composable
fun AmountInput(uiState: com.ecowaste.app.withdrawal.WithdrawalUiState, viewModel: WithdrawalViewModel) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(
            value = uiState.withdrawalAmount,
            onValueChange = { viewModel.onAmountChanged(it) },
            label = { Text("Or enter custom amount (PKR)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = !uiState.isAmountValid,
            supportingText = { uiState.formError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.onProceedClicked() }) {
            Text("Proceed to Withdraw")
        }
    }
}

@Composable
fun WithdrawalGuidelines() {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Withdrawal Guidelines", style = MaterialTheme.typography.titleMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Minimum withdrawal amount: 100 PKR", style = MaterialTheme.typography.bodyMedium)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Minimum contribution: 15 kg of waste", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun WithdrawalDialog(uiState: com.ecowaste.app.withdrawal.WithdrawalUiState, viewModel: WithdrawalViewModel, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Enter Card Details", style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(
                    value = uiState.accountHolderName,
                    onValueChange = { viewModel.onAccountHolderNameChanged(it) },
                    placeholder = { Text("John Doe", color = Color.Gray.copy(alpha = 0.5f)) },
                    label = { Text("Account Holder Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = uiState.cardNumber,
                    onValueChange = { viewModel.onCardNumberChanged(it) },
                    placeholder = { Text("1234 5678 1234 5678", color = Color.Gray.copy(alpha = 0.5f)) },
                    label = { Text("Card Number") },
                    visualTransformation = CardNumberVisualTransformation(),
                    trailingIcon = {
                        val icon = when (uiState.cardBrand) {
                            CardBrand.VISA -> R.drawable.ic_visa
                            CardBrand.MASTERCARD -> R.drawable.ic_mastercard
                            else -> null
                        }
                        if (icon != null) Image(painter = painterResource(id = icon), contentDescription = "Card Brand", modifier = Modifier.height(24.dp))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = uiState.cvv,
                        onValueChange = { viewModel.onCvvChanged(it) },
                        placeholder = { Text("302", color = Color.Gray.copy(alpha = 0.5f)) },
                        label = { Text("CVV") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = PasswordVisualTransformation()
                    )
                    OutlinedTextField(
                        value = uiState.expiryDate,
                        onValueChange = { viewModel.onExpiryDateChanged(it) },
                        placeholder = { Text("12/27", color = Color.Gray.copy(alpha = 0.5f)) },
                        label = { Text("MM/YY") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                if (uiState.formError != null) {
                    Text(text = uiState.formError, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 4.dp), textAlign = TextAlign.Center)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.onWithdrawClicked() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                    shape = RoundedCornerShape(24.dp),
                    enabled = !uiState.isWithdrawalInProgress
                ) {
                    if (uiState.isWithdrawalInProgress) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text("Withdraw", modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }
    }
}

// A VisualTransformation to add spaces to the card number
class CardNumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 16) text.text.substring(0..15) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i % 4 == 3 && i < 15) out += " "
        }
        val creditCardOffsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 3) return offset
                if (offset <= 7) return offset + 1
                if (offset <= 11) return offset + 2
                if (offset <= 16) return offset + 3
                return 19
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 4) return offset
                if (offset <= 9) return offset - 1
                if (offset <= 14) return offset - 2
                if (offset <= 19) return offset - 3
                return 16
            }
        }
        return TransformedText(AnnotatedString(out), creditCardOffsetTranslator)
    }
}

@Composable
fun SuccessDialog(amount: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("✅ Success!") },
        text = { Text("$amount PKR has been successfully transferred to your bank account.") },
        confirmButton = { Button(onClick = onDismiss) { Text("OK") } }
    )
}