package uicommunicator

import com.example.foodcompanion.Food
import com.example.foodcompanion.FoodManager




//This function is called automatically
fun updateMealStatus(updateStatus: (Boolean) -> Unit){
    /* Check whether the meal should be ready or not and pass in true for the parameter updateStatus if it is ready,
     and false otherwise. You should use getMeal to get the current meal to verify if it is ready.

    At the end you should either call updateStatus(true) or updateStatus(false) is the meal is ready or not*/
    //TODO
    updateStatus(true)
}


//This function is called automatically
fun canAddThisFood(food: Food): Pair<Boolean, String> {
    /* Should return whether or not this food can be added for the first part of the pair and
    if the food cannot be added (the first part is false), the second part of the pair should
    have a message as to why the food cannot be added. The second part should be "" if the food can be added
    */
    //TODO
    return Pair(false, "adsfsadfas") //change this!
}



fun getMeal(): List<Food> {
    return FoodManager.myMeal.toList()
}
