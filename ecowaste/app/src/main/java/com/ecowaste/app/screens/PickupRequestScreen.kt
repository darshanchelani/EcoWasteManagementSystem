package com.ecowaste.app.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ecowaste.app.pickup.PickupViewModel

@Composable
fun PickupRequestScreen(navController: NavController, pickupViewModel: PickupViewModel = viewModel()) {
    val uiState by pickupViewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF673AB7), Color(0xFFFFEB3B))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "Request Waste Pickup ♻️🌍",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Help Save the Planet by Recycling!",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Request Button
            Button(
                onClick = { pickupViewModel.onNewRequestClicked() },
                shape = RoundedCornerShape(24.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Submit a Request for Waste Pickup", modifier = Modifier.padding(12.dp), color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic Status Card
            AnimatedVisibility(visible = !uiState.isLoading && uiState.currentStatus != "No active requests") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Status", tint = Color(0xFF4CAF50))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = uiState.currentStatus, textAlign = TextAlign.Center)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))

            // Rate List Section
            RateListSection()

            Spacer(modifier = Modifier.height(24.dp))

            // Payment and Pickup Status
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                PaymentStatusCard(modifier = Modifier.weight(1f))
                LastPickupStatusCard(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Confirmation Footer
            ConfirmationFooter()
        }

        // Input Dialog
        if (uiState.isRequesting) {
            PickupRequestDialog(
                onDismiss = { pickupViewModel.onDismissDialog() },
                onSubmit = { address, phone -> pickupViewModel.submitPickupRequest(address, phone) }
            )
        }
    }
}

@Composable
private fun PickupRequestDialog(onDismiss: () -> Unit, onSubmit: (String, String) -> Unit) {
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Pickup Details", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Enter your full address") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Enter your contact number") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { onSubmit(address, phone) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
                ) {
                    Text("OK", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun RateListSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Waste Rates", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        RateItemCard("Clean Organic Waste", "100 PKR for 5 kg", Icons.Default.Eco, Brush.horizontalGradient(listOf(Color(0xFF66BB6A), Color(0xFF388E3C))))
        Spacer(modifier = Modifier.height(8.dp))
        RateItemCard("Plastic Bottles", "200 PKR for 10 kg", Icons.Default.WaterDrop, Brush.horizontalGradient(listOf(Color(0xFF42A5F5), Color(0xFF1976D2))))
    }
}

@Composable
fun RateItemCard(title: String, rate: String, icon: androidx.compose.ui.graphics.vector.ImageVector, gradient: Brush) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: Show tips popup */ },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .background(gradient)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = Color.White)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold)
                Text(rate, color = Color.White.copy(alpha = 0.9f))
            }
        }
    }
}

@Composable
fun PaymentStatusCard(modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4))) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Pending Payments", style = MaterialTheme.typography.labelMedium, color = Color.DarkGray)
            Text("300 PKR", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFFFFC107))
        }
    }
}

@Composable
fun LastPickupStatusCard(modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Last Pickup", style = MaterialTheme.typography.labelMedium)
            Text("Completed on 15 Nov", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ConfirmationFooter() {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.PhoneInTalk, contentDescription = "Confirmation", tint = Color.Gray)
            Spacer(modifier = Modifier.width(8.dp))
            Text("We will contact you soon for confirmation.", style = MaterialTheme.typography.bodySmall, fontStyle = FontStyle.Italic, color = Color.Gray)
        }
    }
}