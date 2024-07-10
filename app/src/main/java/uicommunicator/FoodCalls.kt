package uicommunicator

import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.foodcompanion.Food
import com.example.foodcompanion.FoodManager
import com.example.foodcompanion.R

var filterStarches: Boolean = false
var filterVegetables: Boolean = false
var filterFruits: Boolean = false
var filterdessert: Boolean = false
var filterbeverages: Boolean = false
var filterCondiments: Boolean = false
enum class FoodTypes{
    Starches,
    Vegetables,
    Fruits,
    Dessert,
    Beverages,
    Condiments

}

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
    val imageID = R.drawable.broccoli_78ec54e
    return Food(name, imageID, servingSize, foodCategory, foodDetails)
}

fun importFoodsToThisUser(foods: List<Food>){
    /*
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
    val food2 = getFoodObject(
        "Some other food",
        "4g",
        FoodTypes.Starches.name,
        listOf(
            "Calories: 17568567g",
            "23ssgsgf",
            "More stuff",
            "Even more stuff"
        )
    )
    //Import statement
    importFoodsToThisUser(listOf(food1, food2))

}