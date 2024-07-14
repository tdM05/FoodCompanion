package uicommunicator


import com.example.foodcompanion.data.FoodCategory
import com.example.foodcompanion.data.FoodTypes
import com.example.foodcompanion.Food
import com.example.foodcompanion.FoodManager
import com.example.foodcompanion.R
import com.example.foodcompanion.data.FoodItem
import org.json.JSONArray
import org.json.JSONObject



fun getFoodObject(
    name: String,
    servingSize: String,
    foodCategory: String,
    foodDetails: List<String> = emptyList()
): Food {
    /*
    Returns a food object that can be used to import foods to the user through the function
    importFoodsToThisUser
    */
    var imageID = 0
    when (foodCategory) {
        FoodTypes.Starches.name -> imageID = R.drawable.starchicon
        FoodTypes.Vegetables.name -> imageID = R.drawable.vegetable
        FoodTypes.Fruits.name -> imageID = R.drawable.fruiticon
        FoodTypes.Dessert.name -> imageID = R.drawable.desserticon
        FoodTypes.Beverages.name -> imageID = R.drawable.beverageicon
        FoodTypes.Condiments.name -> imageID = R.drawable.condimenticon
        FoodTypes.Entrees.name -> imageID = R.drawable.entreeicon
        else -> {
            imageID = R.drawable.broccoli_78ec54e
        }
    }


    return Food(name, imageID, servingSize, foodCategory, foodDetails)
}


fun importFoodsToThisUser(foods: List<Pair<Food, String>>){
    /*
    mealCategory should either be "Breakfast" "Lunch" or "Dinner"


    Imports the food to the client. This should be done right after the server verifies the client
    and no later or else the client may not have any food options.
     */
    for (food in foods){
        FoodManager.foodOptions.add(food)
    }
}


// This function is called in MainActivity temporarily but should be called by the server later
//This function should be deleted later. It is for demonstration purposes
fun FoodsCall(){
    val entrees = getFoodObject("Grilled Chicken",
        "1",
        FoodTypes.Entrees.name,
        listOf(
            "Calories = 250",
            "starches = 0.0",
            "fiber = 0.0",
            "sugars = 0.0",
            "proteins = 30.0",
            "trans = 0.0",
            "saturated = 2.0",
        )
    )
    val item1 = Pair(entrees, FoodCategory.Breakfast.name)


    val starches = getFoodObject("Brown Rice",
        "1",
        FoodTypes.Starches.name,
        listOf(
            "Calories = 150 ",
            "starches = 0.0",
            "fiber = 2.0",
            "sugars = 0.0",
            "proteins = 3.0",
            "trans = 0.0",
            "saturated = 0.5",


            )
    )
    val item2 = Pair(starches, FoodCategory.Breakfast.name)


    val vegetables = getFoodObject("Broccoli",
        "1",
        FoodTypes.Vegetables.name,
        listOf(
            "Calories = 50 ",
            "starches = 0.0",
            "fiber = 2.0",
            "sugars = 2.0",
            "proteins = 3.0",
            "trans = 0.0",
            "saturated = 0.5",


            )
    )
    val item3 = Pair(vegetables, FoodCategory.Breakfast.name)


    val fruits = getFoodObject("Apple",
        "1",
        FoodTypes.Fruits.name,
        listOf(
            "Calories = 80 ",
            "starches = 0.0",
            "fiber = 4.0",
            "sugars = 16.0",
            "proteins = 1.0",
            "trans = 0.0",
            "saturated = 0.1",


            )
    )
    val item4 = Pair(fruits, FoodCategory.Breakfast.name)


    val desserts = getFoodObject("Chocolate Cake",
        "1",
        FoodTypes.Dessert.name,
        listOf(
            "Calories = 300 ",
            "starches = 20.0",
            "fiber = 2.0",
            "sugars = 30.0",
            "proteins = 5.0",
            "trans = 0.0",
            "saturated = 80.",
            )
    )
    val item5 = Pair(fruits, FoodCategory.Breakfast.name)


    val beverages = getFoodObject("Orange Juice",
        "1",
        FoodTypes.Beverages.name,
        listOf(
            "Calories = 120 ",
            "starches = 0.0",
            "fiber = 2.0",
            "sugars = 30.0",
            "proteins = 3.0",
            "trans = 0.0",
            "saturated = 0.5",
            )
    )
    val item6 = Pair(beverages, FoodCategory.Breakfast.name)


    val condiments = getFoodObject("Brown Rice",
        "1",
        FoodTypes.Starches.name,
        listOf(
            "Calories = 20 ",
            "starches = 0.0",
            "fiber = 0.0",
            "sugars = 4.0",
            "proteins = 3.0",
            "trans = 0.0",
            "saturated = 0.5",


            )
    )
    importFoodsToThisUser(listOf(item1, item2, item3, item4, item5, item6))
}

fun mapFoodItemToFood(foodItem: FoodItem): Food {
    val servingSizeString = "${foodItem.servingSize.count} ${foodItem.servingSize.unit}"
    val foodDetails = listOf(
        "Calories = ${foodItem.calories}",
        "Starches = ${foodItem.macros.carbohydrates?.starches}",
        "Fiber = ${foodItem.macros.carbohydrates?.fiber}",
        "Sugars = ${foodItem.macros.carbohydrates?.sugars}",
        "Protein = ${foodItem.macros.protein}",
        "Trans = ${foodItem.macros.fats?.trans}",
        "Saturated = ${foodItem.macros.fats?.saturated}"
    )
    val foodCategory = determineFoodCategory(foodItem)
    return getFoodObject(foodItem.name, servingSizeString, foodCategory, foodDetails)
}

fun determineFoodCategory(foodItem: FoodItem): String {
    return when {
        foodItem.appliesTo.contains("Starch") -> FoodTypes.Starches.name
        foodItem.appliesTo.contains("Fruit") -> FoodTypes.Fruits.name
        foodItem.appliesTo.contains("Vegetable") -> FoodTypes.Vegetables.name
        foodItem.appliesTo.contains("Condiment") -> FoodTypes.Condiments.name
        foodItem.appliesTo.contains("Entree") -> FoodTypes.Entrees.name
        foodItem.appliesTo.contains("Beverage") -> FoodTypes.Beverages.name
        foodItem.appliesTo.contains("Dessert") -> FoodTypes.Dessert.name
        else -> FoodTypes.Entrees.name // Default to Entrees if no category matches
    }
}

