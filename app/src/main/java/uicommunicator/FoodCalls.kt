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
    //For food types you should use the FoodType enum to avoid typos because the image will ont render if there is a typo
    /*
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
    */
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
