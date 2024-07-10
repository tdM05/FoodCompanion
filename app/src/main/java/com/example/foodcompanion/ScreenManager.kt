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
        startDestination = "Login"
    ) {
        composable(route = "Login") {
            LoginPage(
                pageToNavigateTo = {navController.navigate("Main")}
            )
        }
        composable(route = "Main") {

            MainPage(){
                navController.navigate("Foods")
            }
        }
        composable(route = "Foods") {
            FoodsPage(){
                navController.navigate("Main")
            }
        }
    }
}