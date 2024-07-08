package com.example.foodcompanion.screens

import androidx.compose.runtime.Composable

enum class FoodTypes {
    Breakfast,
    Lunch,
    Dinner,
    None
}
object FoodType {
    var type: String = FoodTypes.None.name
}

@Composable
fun FoodsPage(
    onNextButtonClicked: () -> Unit = {},
){

}