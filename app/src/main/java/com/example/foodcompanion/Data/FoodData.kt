package com.example.foodcompanion.data

import com.google.gson.Gson

class FoodData {
    data class Macro(
        val carbohydrates: Carbohydrates,
        val proteins: Double,
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

    data class Food(
        val id: String,
        val name: String,
        val calories: Int,
        val servingSize: Double,  // Added servingSize
        val macros: Macro
    )

    data class Category(
        val categoryName: String,
        val foods: List<Food>
    )

    data class FoodData(
        val categories: List<Category>
    )

    val entrees = listOf(
        Food(
            id = "1",
            name = "Grilled Chicken",
            calories = 250,
            servingSize = 100.0,
            macros = Macro(
                carbohydrates = Carbohydrates(starches = 0.0, fiber = 0.0, sugars = 0.0),
                proteins = 30.0,
                fats = Fats(trans = 0.0, saturated = 2.0)
            )
        )
    )

    val starches = listOf(
        Food(
            id = "2",
            name = "Brown Rice",
            calories = 150,
            servingSize = 1.0,
            macros = Macro(
                carbohydrates = Carbohydrates(starches = 0.0, fiber = 2.0, sugars = 0.0),
                proteins = 3.0,
                fats = Fats(trans = 0.0, saturated = 0.5)
            )
        )
    )

    val vegetables = listOf(
        Food(
            id = "3",
            name = "Broccoli",
            calories = 50,
            servingSize = 1.0,
            macros = Macro(
                carbohydrates = Carbohydrates(starches = 0.0, fiber = 2.0, sugars = 2.0),
                proteins = 3.0,
                fats = Fats(trans = 0.0, saturated = 0.5)
            )
        )
    )

    val fruits = listOf(
        Food(
            id = "4",
            name = "Apple",
            calories = 80,
            servingSize = 1.0,
            macros = Macro(
                carbohydrates = Carbohydrates(starches = 0.0, fiber = 4.0, sugars = 16.0),
                proteins = 1.0,
                fats = Fats(trans = 0.0, saturated = 0.1)
            )
        )
    )

    val desserts = listOf(
        Food(
            id = "5",
            name = "Chocolate Cake",
            calories = 300,
            servingSize = 1.0,
            macros = Macro(
                carbohydrates = Carbohydrates(starches = 20.0, fiber = 2.0, sugars = 30.0),
                proteins = 5.0,
                fats = Fats(trans = 2.0, saturated = 8.0)
            )
        )
    )

    val beverages = listOf(
        Food(
            id = "6",
            name = "Orange Juice",
            calories = 120,
            servingSize = 1.0,
            macros = Macro(
                carbohydrates = Carbohydrates(starches = 0.0, fiber = 0.0, sugars = 30.0),
                proteins = 1.0,
                fats = Fats(trans = 0.0, saturated = 0.0)
            )
        )
    )

    val condiments = listOf(
        Food(
            id = "7",
            name = "Ketchup",
            calories = 20,
            servingSize = 1.0,  // Example serving size
            macros = Macro(
                carbohydrates = Carbohydrates(starches = 0.0, fiber = 0.0, sugars = 4.0),
                proteins = 0.1,
                fats = Fats(trans = 0.0, saturated = 0.0)
            )
        )
    )

    val foodData = FoodData(
        categories = listOf(
            Category("Entrees", entrees),
            Category("Starches", starches),
            Category("Vegetables", vegetables),
            Category("Fruits", fruits),
            Category("Desserts", desserts),
            Category("Beverages", beverages),
            Category("Condiments", condiments)
        )
    )

    fun fromJson(json: String): FoodData {
        return Gson().fromJson(json, FoodData::class.java)
    }

    fun toJson(foodData: FoodData): String {
        return Gson().toJson(foodData)
    }
}
