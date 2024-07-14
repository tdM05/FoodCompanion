package com.example.foodcompanion.data
import com.google.gson.Gson

data class DietOrder(
    val dietOrder: String?,
    val breakfast: Meal,
    val lunch: Meal,
    val dinner: Meal
)

data class Meal(
    val Starch: List<FoodItem>,
    val Fruit: List<FoodItem>,
    val Beverage: List<FoodItem>,
    val Vegetable: List<FoodItem>,
    val Condiment: List<FoodItem>,
    val Entree: List<FoodItem>,
    val Desert: List<FoodItem>
)

data class FoodItem(
    val id: Int,
    val name: String,
    val calories: Double,
    val macros: Macros,
    val servingSize: ServingSize,
    val appliesTo: List<String>
)

data class Macros(
    val carbohydrates: Carbohydrates,
    val protein: Double,
    val fats: Fats
)

data class Carbohydrates(
    val starches: Double,
    val sugars: Double,
    val fiber: Double
)

data class Fats(
    val trans: Double,
    val saturated: Double
)

data class ServingSize(
    val count: Double,
    val unit: String
)

public fun parseJson(jsonString: String): DietOrder {
    val gson = Gson()
    return gson.fromJson(jsonString, DietOrder::class.java)
}