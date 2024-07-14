package com.example.foodcompanion
import com.example.foodcompanion.screens.*

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foodcompanion.data.FoodCategory


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

            MainPage(
                onFoodButtonClicked = {foodPageToGoTo: String -> navController.navigate(foodPageToGoTo)})
        }
        composable(route = FoodCategory.Breakfast.name) {
            FoodsPage(FoodCategory.Breakfast.name){
                navController.navigate("Main")
            }
        }
        composable(route = FoodCategory.Lunch.name) {
            FoodsPage(FoodCategory.Lunch.name){
                navController.navigate("Main")
            }
        }
        composable(route = FoodCategory.Dinner.name) {
            FoodsPage(FoodCategory.Dinner.name){
                navController.navigate("Main")
            }
        }

    }
}