package com.ecowaste.app.screens

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ecowaste.app.user.UserViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun UserHomeScreen(navController: NavController, userViewModel: UserViewModel = viewModel()) {
    val userData by userViewModel.userData.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    val activity = (LocalContext.current as? Activity)
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            AppBottomNavigation(
                navController = navController,
                onLogoutClick = { showLogoutDialog = true },
                onSettingsClick = { showSettingsDialog = true }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Brush.verticalGradient(colors = listOf(Color(0xFFE8F5E9), Color.White)))
                .verticalScroll(rememberScrollState())
        ) {
            TopBalanceCard(userName = userData.name, balance = "500 PKR", navController = navController)
            RecyclingStatsSection(balance = "500 PKR", waste = "${userData.totalWaste} kg")
            ActionGrid(navController = navController, context = context)
            Spacer(modifier = Modifier.height(16.dp))
            ImageSlider()
        }
    }

    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirm = { showLogoutDialog = false; activity?.finishAffinity() },
            onDismiss = { showLogoutDialog = false }
        )
    }

    if (showSettingsDialog) {
        SettingsDialog(onDismiss = { showSettingsDialog = false })
    }
}

@Composable
fun SettingsDialog(onDismiss: () -> Unit) {
    var isDarkMode by remember { mutableStateOf(false) }
    var areNotificationsEnabled by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Settings") },
        text = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text("Dark Mode", modifier = Modifier.weight(1f))
                    Switch(checked = isDarkMode, onCheckedChange = { isDarkMode = it })
                }
                Divider()
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text("Notifications", modifier = Modifier.weight(1f))
                    Switch(checked = areNotificationsEnabled, onCheckedChange = { areNotificationsEnabled = it })
                }
                Divider()
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { /* TODO: Handle permissions */ }
                ) {
                    Text("Media Permissions")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

@Composable
fun TopBalanceCard(userName: String, balance: String, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(colors = listOf(Color(0xFFFF5722), Color(0xFFF44336))))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Welcome Back, $userName! 🌟♻️", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = balance, color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
                Text(text = "Earned from recycling", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.weight(1f))
            }
            Button(
                onClick = { navController.navigate("bank_details") },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Withdraw +")
            }
        }
    }
}

@Composable
fun RecyclingStatsSection(balance: String, waste: String) {
    val balanceValue = balance.split(" ")[0].toFloatOrNull() ?: 0f
    val wasteValue = waste.split(" ")[0].toFloatOrNull() ?: 0f
    val treesSaved = (wasteValue * 0.017).toFloat()

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("Your Recycling Stats", fontSize = 20.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            CircularGauge(label = "Balance", value = balanceValue, maxValue = 1000f, color = Color(0xFF4CAF50), valueText = "$balanceValue PKR")
            CircularGauge(label = "Waste", value = wasteValue, maxValue = 50f, color = Color(0xFF2196F3), valueText = "$wasteValue kg")
            CircularGauge(label = "Trees Saved", value = treesSaved, maxValue = 1f, color = Color(0xFF673AB7), valueText = String.format("%.2f", treesSaved))
        }
    }
}

@Composable
fun CircularGauge(label: String, value: Float, maxValue: Float, color: Color, valueText: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 8.dp)) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(110.dp)) {
            CircularProgressIndicator(
                progress = value / maxValue,
                modifier = Modifier.fillMaxSize(),
                color = color,
                strokeWidth = 8.dp
            )
            Text(text = valueText, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ActionGrid(navController: NavController, context: Context) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ActionCard(modifier = Modifier.weight(1f), title = "Pickup", icon = Icons.Default.LocalShipping, color = Color(0xFF4CAF50)) { navController.navigate("pickup_request") }
            ActionCard(modifier = Modifier.weight(1f), title = "Withdraw", icon = Icons.Default.AccountBalanceWallet, color = Color(0xFFFF5722)) { navController.navigate("bank_details") }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ActionCard(modifier = Modifier.weight(1f), title = "Share", icon = Icons.Default.Share, color = Color(0xFF673AB7)) { shareApp(context) }
            ActionCard(modifier = Modifier.weight(1f), title = "Contact", icon = Icons.Default.ContactMail, color = Color(0xFF2196F3)) { contactSupport(context) }
        }
    }
}

@Composable
fun ActionCard(modifier: Modifier = Modifier, title: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = title, tint = Color.White, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

data class SliderItem(val imageUrl: String, val text: String)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageSlider() {
    val sliderItems = listOf(
        SliderItem(
            "https://images.unsplash.com/photo-1599664223843-8557d03f04e2?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "Build a cleaner environment"
        ),
        SliderItem(
            "https://images.unsplash.com/photo-1611284446314-60a58ac0deb9?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "Turn your trash into cash"
        ),
        SliderItem(
            "https://images.unsplash.com/photo-1472214103451-9374bd1c798e?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "Save your environment"
        )
    )

    val pagerState = rememberPagerState(pageCount = { sliderItems.size })

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
            pagerState.animateScrollToPage(nextPage)
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 16.dp)
    ) { page ->
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = sliderItems[page].imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                )
                Text(
                    text = sliderItems[page].text,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }
        }
    }
}

fun shareApp(context: Context) {
    val appLink = "https://play.google.com/store/apps/details?id=com.ecowaste.app"
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("EcoWaste App Link", appLink)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "App link copied to clipboard!", Toast.LENGTH_SHORT).show()

    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "Check out EcoWaste for recycling rewards!\n$appLink")
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}

fun contactSupport(context: Context) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:help@ecowaste.com")
        putExtra(Intent.EXTRA_SUBJECT, "Support Query for EcoWaste")
        putExtra(Intent.EXTRA_TEXT, "Hello, I need help with...")
    }
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "No email app found.", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun AppBottomNavigation(navController: NavController, onLogoutClick: () -> Unit, onSettingsClick: () -> Unit) {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Home", "Settings", "Logout")
    val icons = listOf(Icons.Filled.Home, Icons.Filled.Settings, Icons.Filled.ExitToApp)

    NavigationBar(
        modifier = Modifier.height(80.dp),
        containerColor = Color.Transparent
    ) {
        Box(modifier = Modifier.background(Brush.horizontalGradient(colors = listOf(Color(0xFFFFEB3B), Color(0xFF4CAF50))))) {
            Row {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = item, modifier = Modifier.size(30.dp)) },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            when (item) {
                                "Home" -> navController.navigate("user_home")
                                "Settings" -> onSettingsClick()
                                "Logout" -> onLogoutClick()
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF673AB7),
                            unselectedIconColor = Color.White,
                            selectedTextColor = Color(0xFF673AB7),
                            unselectedTextColor = Color.White,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun LogoutConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Logout") },
        text = { Text("Are you sure you want to log out and exit the app?") },
        confirmButton = { Button(onClick = onConfirm) { Text("Yes, Logout") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
