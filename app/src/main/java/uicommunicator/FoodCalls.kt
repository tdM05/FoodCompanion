package uicommunicator

import com.example.foodcompanion.data.FoodCategory
import com.example.foodcompanion.data.FoodTypes
import com.example.foodcompanion.Food
import com.example.foodcompanion.FoodManager
import com.example.foodcompanion.R


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
        FoodTypes.Starches.name -> imageID = R.drawable.starches
        FoodTypes.Vegetables.name -> imageID = R.drawable.broccoli_78ec54e
        FoodTypes.Fruits.name -> imageID = R.drawable.fruits
        FoodTypes.Dessert.name -> imageID = R.drawable.desert
        FoodTypes.Beverages.name -> imageID = R.drawable.beverages
        FoodTypes.Condiments.name -> imageID = R.drawable.condiments
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
fun sampleFoodsCall(){
    //For food types you should use the FoodType enum to avoid typos because the image will ont render if there is a typo

    //breakfast options
    val food1 = getFoodObject(
        "Brocolli",
        "16g",
        FoodTypes.Vegetables.name,
        listOf(
            "Calories: 16g",
            "blablabla",
            "More stuff"
        )
    )
    val item1 = Pair(food1, FoodCategory.Breakfast.name)
    val food2 = getFoodObject(
        "Some Condiments",
        "16g",
        FoodTypes.Condiments.name,
        listOf(
            "Calories: 16g",
            "blablabla",
            "More stuff"
        )
    )
    val item2 = Pair(food2, FoodCategory.Breakfast.name)
    val food3 = getFoodObject(
        "Some desert",
        "16g",
        FoodTypes.Dessert.name,
        listOf(
            "Calories: 16g",
            "blablabla",
            "More stuff"
        )
    )
    val item3 = Pair(food3, FoodCategory.Breakfast.name)
    val food4 = getFoodObject(
        "Some Beverage",
        "16g",
        FoodTypes.Beverages.name,
        listOf(
            "Calories: 16g",
            "blablabla",
            "More stuff"
        )
    )
    val item4 = Pair(food4, FoodCategory.Breakfast.name)
    val food5 = getFoodObject(
        "Some Fruits",
        "16g",
        FoodTypes.Fruits.name,
        listOf(
            "Calories: 16g",
            "blablabla",
            "More stuff"
        )
    )
    val item5 = Pair(food5, FoodCategory.Breakfast.name)
    val food6 = getFoodObject(
        "Some Starches",
        "16g",
        FoodTypes.Starches.name,
        listOf(
            "Calories: 16g",
            "blablabla",
            "More stuff"
        )
    )
    val item6 = Pair(food6, FoodCategory.Breakfast.name)


    //Import statement
    importFoodsToThisUser(listOf(item1, item2, item3, item4, item5, item6))

}