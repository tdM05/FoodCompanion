package com.example.foodcompanion
import com.example.foodcompanion.screens.*

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

enum class Screens() {
    Login,
    Main,
    Foods,
}

// All the screens are managed through this
@Composable
fun Main (){
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screens.Login.name
    ) {
        composable(route = Screens.Login.name) {
            LoginPage(
                pageToNavigateTo = {navController.navigate(Screens.Main.name)}
            )
        }
        composable(route = Screens.Main.name) {
            MainPage()
        }
        composable(route = Screens.Foods.name) {
            FoodsPage()
        }
    }
}