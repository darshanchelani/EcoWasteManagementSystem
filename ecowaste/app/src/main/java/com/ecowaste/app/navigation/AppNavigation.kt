package com.ecowaste.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ecowaste.app.screens.AdminHomeScreen
import com.ecowaste.app.screens.BankDetailsScreen
import com.ecowaste.app.screens.LoginScreen
import com.ecowaste.app.screens.PickupRequestScreen
import com.ecowaste.app.screens.RegisterScreen
import com.ecowaste.app.screens.UserHomeScreen
import com.ecowaste.app.screens.WelcomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") { 
            WelcomeScreen(navController)
        }
        composable("login") { 
            LoginScreen(navController)
        }
        composable("register") { 
            RegisterScreen(navController)
        }
        composable("user_home") {
            UserHomeScreen(navController)
        }
        composable("admin_home") {
            AdminHomeScreen(navController)
        }
        composable("bank_details") {
            BankDetailsScreen(navController)
        }
        composable("pickup_request") {
            PickupRequestScreen(navController)
        }
    }
}